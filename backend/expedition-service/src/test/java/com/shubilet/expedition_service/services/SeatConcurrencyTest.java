package com.shubilet.expedition_service.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.shubilet.expedition_service.AbstractPostgresIntegrationTest;
import com.shubilet.expedition_service.common.enums.SeatStatusForModel;
import com.shubilet.expedition_service.models.Seat;
import com.shubilet.expedition_service.repositories.SeatRepository;

/**
 * Proves that the pessimistic row lock on the seat block/book path actually
 * prevents the concurrent-selection race.
 *
 * <p>The test runs against a <b>real PostgreSQL 15</b> instance (via Testcontainers,
 * matching production) because the guarantee under test is a database behaviour:
 * {@code SELECT ... FOR UPDATE} serialising two transactions on the same row.
 * An in-memory H2 database does not reproduce PostgreSQL's row-locking semantics
 * faithfully, so it could not prove or disprove the fix.
 *
 * <p><b>Requires Docker</b> to be running on the machine executing the test.
 *
 * <p>Scenario: N threads each attempt to block the <i>same</i> seat at the same
 * instant, every thread using a distinct customer id. They are released together
 * by a {@link CyclicBarrier} to maximise contention. With the pessimistic lock in
 * place, the transactions serialise: exactly one thread observes the seat as
 * AVAILABLE and wins; every other thread, once it acquires the lock, re-reads the
 * now-BLOCKED row and is rejected. Without the lock (the old read-check-modify-save)
 * multiple threads would read AVAILABLE and all "succeed" — so this assertion is a
 * genuine regression guard.
 */
class SeatConcurrencyTest extends AbstractPostgresIntegrationTest {

    private static final int EXPEDITION_ID = 4242;
    private static final int SEAT_NO = 7;
    private static final int THREADS = 16;

    @Autowired
    private SeatService seatService;

    @Autowired
    private SeatRepository seatRepository;

    @BeforeEach
    void seedSingleAvailableSeat() {
        seatRepository.deleteAll();
        seatRepository.save(new Seat(EXPEDITION_ID, SEAT_NO));
    }

    @Test
    void onlyOneCustomerCanBlockTheSameSeatUnderConcurrency() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        // Line every thread up at the barrier so they all fire blockSeat together.
        CyclicBarrier startLine = new CyclicBarrier(THREADS);

        try {
            List<Callable<Boolean>> attempts = IntStream.rangeClosed(1, THREADS)
                    .<Callable<Boolean>>mapToObj(customerId -> () -> {
                        startLine.await(10, TimeUnit.SECONDS);
                        // Each customer tries to grab the one available seat.
                        return seatService.blockSeat(EXPEDITION_ID, SEAT_NO, customerId);
                    })
                    .toList();

            List<Future<Boolean>> results = pool.invokeAll(attempts, 30, TimeUnit.SECONDS);

            long winners = 0;
            for (Future<Boolean> result : results) {
                if (Boolean.TRUE.equals(result.get())) {
                    winners++;
                }
            }

            // The core guarantee: the seat is sold at most once.
            assertThat(winners)
                    .as("exactly one of %d concurrent customers should win the seat", THREADS)
                    .isEqualTo(1);

            // And the persisted row must reflect a single, coherent BLOCKED hold.
            Seat seat = seatRepository.findByExpeditionIdAndSeatNo(EXPEDITION_ID, SEAT_NO);
            assertThat(seat.getStatus()).isEqualTo(SeatStatusForModel.BLOCKED);
            assertThat(seat.getBlockedBy())
                    .as("the winning customer must own the hold")
                    .isBetween(1, THREADS);
        } finally {
            pool.shutdownNow();
        }
    }
}
