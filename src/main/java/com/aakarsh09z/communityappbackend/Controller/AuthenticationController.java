package com.aakarsh09z.communityappbackend.Controller;

import com.aakarsh09z.communityappbackend.Entity.User;
import com.aakarsh09z.communityappbackend.Payload.Request.*;
import com.aakarsh09z.communityappbackend.Service.AuthenticationService;
import com.aakarsh09z.communityappbackend.Service.JwtTokenGenerator;
import com.aakarsh09z.communityappbackend.Service.OAuth2Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final JwtTokenGenerator jwtTokenGenerator;
    private final AuthenticationService authenticationService;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService;
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
    @GetMapping("/login/google")
    public Map<String, Object> currentUser(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        if (oAuth2AuthenticationToken != null) {
            return oAuth2AuthenticationToken.getPrincipal().getAttributes();
        } else {
            // Handle the case when oAuth2AuthenticationToken is null
            return Collections.emptyMap();
        }
    }
//    @GetMapping
//    public String handleGoogleCallback(OAuth2AuthenticationToken authenticationToken) {
//        OAuth2User oAuth2User = oauth2UserService.loadUser(authenticationToken.getPrincipal().getAttribute("email"));
//
//        Authentication authentication = new OAuth2AuthenticationToken(
//                authenticationToken.getAuthorizedClientRegistrationId(),
//                authenticationToken.getPrincipal(),
//                oAuth2User.getAuthorities(),
//                authenticationToken.getName()
//        );
//
//        // Set the Authentication in the SecurityContextHolder
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // Redirect to a welcome page or any desired URL
//        return "redirect:/welcome";
//    }
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
    @GetMapping("/regenerateToken")
    public ResponseEntity<?> refreshToken(@RequestParam String token) {
        return this.jwtTokenGenerator.generateRefreshToken(token);
    }
}