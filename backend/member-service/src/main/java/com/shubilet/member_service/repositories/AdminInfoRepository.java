package com.shubilet.member_service.repositories;

import com.shubilet.member_service.models.AdminInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminInfoRepository extends JpaRepository<AdminInfo, Long> {
}
