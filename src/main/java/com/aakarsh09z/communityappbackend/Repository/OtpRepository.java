package com.aakarsh09z.communityappbackend.Repository;

import com.aakarsh09z.communityappbackend.Entity.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity,Long> {
    Optional<OtpEntity> findByEmail(String email);
}
