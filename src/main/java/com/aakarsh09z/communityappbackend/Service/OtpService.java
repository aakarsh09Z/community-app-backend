package com.aakarsh09z.communityappbackend.Service;

import com.aakarsh09z.communityappbackend.Entity.OtpEntity;
import com.aakarsh09z.communityappbackend.Repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final OtpRepository otpRepository;
    public String getOtpByEmail(String email) {
        OtpEntity otpEntity = otpRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("OTP not found for email: " + email));

        return otpEntity.getOtp();
    }
}
