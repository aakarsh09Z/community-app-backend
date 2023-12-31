package com.aakarsh09z.communityappbackend.Service;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class GenerateOtp {
    private static final String CHARACTERS = "123456789";
    private static final int OTP_LENGTH = 4;

    public static String generateOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < OTP_LENGTH; i++) {
            char randomChar = CHARACTERS.charAt(random.nextInt(CHARACTERS.length()));
            otp.append(randomChar);
        }

        return otp.toString();
    }
}
