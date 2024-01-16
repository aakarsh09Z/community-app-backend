package com.aakarsh09z.communityappbackend.Service;

import com.aakarsh09z.communityappbackend.Payload.Request.*;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    public ResponseEntity<?> register(RegisterRequest request);
    public ResponseEntity<?> verifyToRegister(VerifyToRegisterRequest request);
    public ResponseEntity<?> login(LoginRequest request);
    public ResponseEntity<?> forgot_password(ForgotPasswordRequest request);
    public ResponseEntity<?> verifyToResetPassword(VerifyToResetPasswordRequest request);
    public ResponseEntity<?> resetPassword(ResetPasswordRequest request);
    public ResponseEntity<?> resendOtp(ForgotPasswordRequest request);
}
