package com.aakarsh09z.communityappbackend.Controller;

import com.aakarsh09z.communityappbackend.Payload.Request.*;
import com.aakarsh09z.communityappbackend.Service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @GetMapping("/test")
    public String test(){
        return "This is working";
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request){
        return this.authenticationService.register(request);
    }
    @PostMapping("/verify-registration")
    public ResponseEntity<?> verifyToRegister(@Valid @RequestBody VerifyToRegisterRequest request){
        return this.authenticationService.verifyToRegister(request);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        return this.authenticationService.login(request);
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgot_password(@RequestBody ForgotPasswordRequest request){
        return this.authenticationService.forgot_password(request);
    }
    @PostMapping("/verify-reset-password")
    public ResponseEntity<?> verifyToResetPassword(@Valid @RequestBody VerifyToResetPasswordRequest request){
        return this.authenticationService.verifyToResetPassword(request);
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request){
        return this.authenticationService.resetPassword(request);
    }
    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody ForgotPasswordRequest request){
        return this.authenticationService.resendOtp(request);
    }
}