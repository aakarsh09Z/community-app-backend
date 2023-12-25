package com.aakarsh09z.communityappbackend.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table(name = "OTP")
@Entity
public class OtpEntity {
    private String otp;
    @Id
    private String email;
    private LocalDateTime ExpirationTime;
}
