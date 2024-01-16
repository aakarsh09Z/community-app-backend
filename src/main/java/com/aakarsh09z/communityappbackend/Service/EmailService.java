package com.aakarsh09z.communityappbackend.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    public void sendOtpEmail(String toEmail, String OTP) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("OTP Verification");
        message.setText("Your OTP for email verification is: " + OTP);
        message.setFrom("bitebliss.app@gmail.com");
        javaMailSender.send(message);
    }
    public void sendPasswordEmail(String toEmail, String Password){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("New Password");
        message.setText("Your password for direct login is: " + Password);
        message.setFrom("bitebliss.app@gmail.com");
        javaMailSender.send(message);
    }
}