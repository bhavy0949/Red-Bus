package com.shubilet.expedition_service.scheduling;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.shubilet.expedition_service.repositories.SeatRepository;

/**
 * Periodically releases seat holds whose block window has lapsed.
 *
 * <p>A seat is held ("BLOCKED") for a fixed window (2 minutes) during checkout.
 * {@code Seat.getStatus()}/{@code isBooked()} already treat an expired hold as
 * AVAILABLE on read, so booking correctness never depended on active cleanup.
 * However, if a customer abandons checkout and nobody reads that seat again, the
 * persisted {@code status} column stays "BLOCKED" indefinitely — which is
 * misleading for raw SQL queries, company/admin dashboards, and reporting.
 *
 * <p>This sweeper closes that gap by bulk-resetting expired holds back to
 * AVAILABLE on a fixed interval, keeping the persisted state consistent with
 * what the entity reports at read time.
 *
 * <p><b>Multi-replica note:</b> expedition-service is HPA-scaled, so every replica
 * runs this job. That is safe here because the underlying UPDATE is idempotent and
 * only ever touches already-expired holds — the worst case is two pods issuing the
 * same no-op UPDATE. If the work ever became expensive, this should be gated behind
 * a distributed lock (e.g. ShedLock) so only one pod runs it per tick.
 */
@Component
public class SeatBlockExpiryScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SeatBlockExpiryScheduler.class);

    private final SeatRepository seatRepository;

    public SeatBlockExpiryScheduler(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    /**
     * Runs every 60 seconds, releasing any seat hold that has already expired.
     * The interval is well below the 2-minute hold window, so a lapsed hold is
     * reflected in the persisted status within at most a minute of expiry.
     */
    @Scheduled(fixedDelayString = "${seat.block.sweeper.interval-ms:60000}")
    @Transactional
    public void releaseExpiredBlocks() {
        int released = seatRepository.releaseExpiredBlocks(Instant.now());
        if (released > 0) {
            logger.info("Seat block sweeper released {} expired seat hold(s).", released);
        }
    }
}
