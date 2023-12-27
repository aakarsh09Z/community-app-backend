package com.aakarsh09z.communityappbackend.Payload.Request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VerifyToRegisterRequest {
    @Email
    private String email;
    @Min(value=1000, message="OTP should be 4 digit number")
    @Digits(message="OTP should be 4 digit number", fraction = 0, integer = 4)
    private String otp;
}
