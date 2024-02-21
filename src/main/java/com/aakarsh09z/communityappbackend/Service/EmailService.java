package com.aakarsh09z.communityappbackend.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    public void sendOtpEmail(String toEmail, String OTP) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(toEmail);
        helper.setSubject("OTP Verification");
        String htmlContent = "<html><body>" +
                "<p style=\"font-size: 18px; color:#fff\">Your OTP for email verification is:</p>" +
                "<div>" +
                "<h2 style=\"font-size: 36px; margin: 0; text-align:center; font-weight:900;\">" + OTP + "</h2>" +
                "</div>" +
                "<p>Regards,<br/>Connectify</p>" +
                "</body></html>";
        helper.setText(htmlContent, true);
        helper.setFrom(new InternetAddress("connectify.community@gmail.com"));
        javaMailSender.send(message);
    }
    public void sendPasswordEmail(String toEmail, String Password)throws MessagingException{
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(toEmail);
        helper.setSubject("New Password");
        String htmlContent = "<html><body>" +
                "<p style=\"font-size: 18px; color:#fff\">Your password for direct login is:</p>" +
                "<div>" +
                "<h2 style=\"font-size: 36px; margin: 0; text-align:center; font-weight:900;\">" + Password + "</h2>" +
                "</div>" +
                "<p>Regards,<br/>Connectify</p>" +
                "</body></html>";
        helper.setText(htmlContent, true);
        helper.setFrom(new InternetAddress("connectify.community@gmail.com"));
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