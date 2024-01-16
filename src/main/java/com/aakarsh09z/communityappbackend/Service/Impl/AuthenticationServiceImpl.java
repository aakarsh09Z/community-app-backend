package com.aakarsh09z.communityappbackend.Service.Impl;

import com.aakarsh09z.communityappbackend.Configuration.AppConstants;
import com.aakarsh09z.communityappbackend.Entity.OtpEntity;
import com.aakarsh09z.communityappbackend.Entity.User;
import com.aakarsh09z.communityappbackend.Exceptions.ResourceNotFoundException;
import com.aakarsh09z.communityappbackend.Payload.Request.*;
import com.aakarsh09z.communityappbackend.Payload.Response.ApiResponse;
import com.aakarsh09z.communityappbackend.Payload.Response.JwtTokenResponse;
import com.aakarsh09z.communityappbackend.Repository.OtpRepository;
import com.aakarsh09z.communityappbackend.Repository.UserRepository;
import com.aakarsh09z.communityappbackend.Service.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private final OtpService otpService;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final GeneratePassword generatePassword;
    private AuthenticationManager authenticationManager;
    @Override
    public ResponseEntity<?> register(RegisterRequest request){
        User user=new User();
        if(userRepository.findByEmail(request.getEmail()).isEmpty()){
//            var userId=userRepository.findByUserId(request.getUserId());
//            if(userId.isPresent() && userId.orElseThrow().getIsVerified()){
//                return new ResponseEntity<>(new ApiResponse("This username is already taken",false),HttpStatus.CONFLICT);
//            }
            user.setFullname(request.getFullname());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setIsVerified(false);
        }
        else{
            user=userRepository.findByEmail(request.getEmail()).orElseThrow(()->new RuntimeException(("user not found in database")+request.getEmail()));

            if(user.getIsVerified()){
                if(user.getPassword().equals("Google")){
                    return new ResponseEntity<>(new ApiResponse("User already registered with google",false),HttpStatus.CONFLICT);
                }
                return new ResponseEntity<>(new ApiResponse("User already registered",false),HttpStatus.CONFLICT);
            }
            user.setFullname(request.getFullname());
            user.setEmail(request.getEmail());
            user.setPassword((request.getPassword()));
            user.setIsVerified(false);
        }
//        String OTP= GenerateOtp.generateOtp();
//        LocalDateTime expirationTime=LocalDateTime.now().plusMinutes(AppConstants.OTP_EXPIRATION_MINUTE);
//
//        OtpEntity otpEntity = new OtpEntity();
//        otpEntity.setOtp(OTP);
//        otpEntity.setEmail(request.getEmail());
//        otpEntity.setExpirationTime(expirationTime);
//        otpRepository.save(otpEntity);
//        emailService.sendOtpEmail(request.getEmail(),OTP);

        userRepository.save(user);
        return new ResponseEntity<>(new ApiResponse("fullname:" + user.getFullname() + " " + "email:" + user.getEmail(), true), HttpStatus.OK);
    }
    @Override
    public ResponseEntity<?> verifyToRegister(VerifyToRegisterRequest request){
        String email = request.getEmail().trim().toLowerCase();
        //check if otp is generated with the given email
        if(otpRepository.findByEmail(email).isEmpty()){
            return new ResponseEntity<>(new ApiResponse("No OTP generated",false),HttpStatus.NOT_FOUND);
        }
        //check if otp entered is correct
        String OTP = otpService.getOtpByEmail(request.getEmail());
        if(!(request.getOtp().equals(OTP))){
            return new ResponseEntity<>(new ApiResponse("Incorrect OTP",false),HttpStatus.NOT_ACCEPTABLE);
        }
        //check if otp is expired-->10min(expiration time)
        OtpEntity otpUser = otpRepository.findByEmail(request.getEmail()).orElseThrow();
        if(LocalDateTime.now().isAfter(otpUser.getExpirationTime())){
            return new ResponseEntity<>(new ApiResponse("OTP expired",false),HttpStatus.CONFLICT);
        }
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        //generate token and user is verified
        JwtTokenResponse response = this.jwtTokenGenerator.generateToken(request.getEmail());
        user.setIsVerified(true);
        userRepository.save(user);
        response.setUserId(user.getUserId());
        response.setEmail(request.getEmail());
        response.setProfileImageUrl(user.getProfileImageUrl());
        response.setSuccess(true);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @Override
    public ResponseEntity<?> login(LoginRequest request){
//        if(request.getEmail()==null && request.getPassword()==null){
//            return new ResponseEntity<>(new ApiResponse("Enter email or username",false),HttpStatus.BAD_REQUEST);
//        }
        if(userRepository.findByEmail(request.getEmail()).isEmpty()){
            return new ResponseEntity<>(new ApiResponse("User not registered",false),HttpStatus.BAD_REQUEST);
        }
//        if(userRepository.findByEmail(request.getEmail()).isPresent())
        User user=userRepository.findByEmail(request.getEmail()).orElseThrow(()->new RuntimeException(("User not found in database")+request.getEmail()));
//        else
//            user=userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new ResourceNotFoundException(request.getUsername(), "Email: " + request.getEmail(), 0));
//        System.out.println(user);
//        try{
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(),request.getPassword()));
//        }
//        catch(Exception e){
//            return new ResponseEntity<>(new ApiResponse("Invalid Credentials",false),HttpStatus.UNAUTHORIZED);
//        }
        if(!(passwordEncoder.matches(request.getPassword(),user.getPassword()))){
            return new ResponseEntity<>(new ApiResponse("Invalid Credentials",false),HttpStatus.UNAUTHORIZED);
        }
        JwtTokenResponse response = this.jwtTokenGenerator.generateToken(request.getEmail());
        response.setUserId(user.getUserId());
        response.setEmail(request.getEmail());
        response.setProfileImageUrl(user.getProfileImageUrl());
        response.setSuccess(true);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @Override
    public ResponseEntity<?> forgot_password(ForgotPasswordRequest request){
        //if email is not registered
        if(userRepository.findByEmail(request.getEmail()).isEmpty()){
            return new ResponseEntity<>(new ApiResponse("Invalid Email",false),HttpStatus.CONFLICT);
        }
        //check if the registered user is verified
        User user=userRepository.findByEmail(request.getEmail()).orElseThrow();
        if(!user.getIsVerified()){
            return new ResponseEntity<>(new ApiResponse("User not verified",false),HttpStatus.CONFLICT);
        }

        LocalDateTime expirationTime=LocalDateTime.now().plusMinutes(AppConstants.OTP_EXPIRATION_MINUTE);
        //send otp to verify
        String OTP=GenerateOtp.generateOtp();
        OtpEntity otpEntity = new OtpEntity();
        otpEntity.setOtp(OTP);
        otpEntity.setEmail(request.getEmail());
        otpEntity.setExpirationTime(expirationTime);
        otpRepository.save(otpEntity);
        emailService.sendOtpEmail(request.getEmail(), OTP);
        return new ResponseEntity<>(new ApiResponse("Check your email for OTP",true),HttpStatus.OK);
    }
    @Override
    public ResponseEntity<?> verifyToResetPassword(VerifyToResetPasswordRequest request){
        String OTP = otpService.getOtpByEmail(request.getEmail());
        OtpEntity otpUser = otpRepository.findByEmail(request.getEmail()).orElseThrow();
        if(LocalDateTime.now().isAfter(otpUser.getExpirationTime())){
            return new ResponseEntity<>(new ApiResponse("OTP expired",false),HttpStatus.CONFLICT);
        }
        if(request.getOtp().equals(OTP)) {
            return new ResponseEntity<>(new ApiResponse("OTP is successfully verified",true),HttpStatus.OK);
        }
        else{
        return new ResponseEntity<>(new ApiResponse("Incorrect OTP",false),HttpStatus.CONFLICT);
        }
    }
    @Override
    public ResponseEntity<?> resetPassword(ResetPasswordRequest request){
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return new ResponseEntity<>(new ApiResponse("Password has been reset successfully",true),HttpStatus.OK);
    }
    @Override
    public ResponseEntity<?> resendOtp(ForgotPasswordRequest request){
        LocalDateTime expirationTime=LocalDateTime.now().plusMinutes(AppConstants.OTP_EXPIRATION_MINUTE);
        var otpUser = otpRepository.findByEmail(request.getEmail()).orElseThrow();

        if(LocalDateTime.now().isBefore(otpUser.getExpirationTime().minusMinutes(9))){
            return new ResponseEntity<>(new ApiResponse("Please wait 1 min before sending another OTP",false),HttpStatus.TOO_EARLY);
        }

        String OTP=GenerateOtp.generateOtp();
        OtpEntity otpEntity = new OtpEntity();
        otpEntity.setOtp(OTP);
        otpEntity.setEmail(request.getEmail());
        otpEntity.setExpirationTime(expirationTime);
        otpRepository.save(otpEntity);
        emailService.sendOtpEmail(request.getEmail(), OTP);

        return new ResponseEntity<>(new ApiResponse("OTP resent",true),HttpStatus.OK);
    }
    @Override
    public ResponseEntity<?> selectAvatar(SelectAvatarRequest request){
        if(userRepository.findByEmail(request.getEmail()).isEmpty()){
            return new ResponseEntity<>(new ApiResponse("User not found with email:"+request.getEmail(),false),HttpStatus.CONFLICT);
        }
        User user=userRepository.findByEmail(request.getEmail()).orElseThrow(()->new RuntimeException(("User not found in database")+request.getEmail()));
        user.setUserId(request.getUserId());
        user.setProfileImageUrl(request.getProfileImageUrl());
        userRepository.save(user);

        String OTP= GenerateOtp.generateOtp();
        LocalDateTime expirationTime=LocalDateTime.now().plusMinutes(AppConstants.OTP_EXPIRATION_MINUTE);

        OtpEntity otpEntity = new OtpEntity();
        otpEntity.setOtp(OTP);
        otpEntity.setEmail(request.getEmail());
        otpEntity.setExpirationTime(expirationTime);
        otpRepository.save(otpEntity);
        emailService.sendOtpEmail(request.getEmail(),OTP);
        return new ResponseEntity<>(new ApiResponse("Check your email for otp",true),HttpStatus.OK);
    }
    public ResponseEntity<?>oauthGoogle(String Token){
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=" + Token))
                    .GET()
                    .build();
            System.out.println(request);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response);
            GoogleSignRequest googleSignRequest = mapper.readValue(response.body(), GoogleSignRequest.class);
            if(googleSignRequest!=null){
                if(this.verifyGoogleToken(googleSignRequest)){
                    request = HttpRequest.newBuilder()
                        .uri(URI.create("https://www.googleapis.com/oauth2/v3/userinfo?access_token=" + Token))
                        .GET()
                        .build();
                    response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    googleSignRequest = mapper.readValue(response.body(), GoogleSignRequest.class);
                    User user;
                    String email= googleSignRequest.email();
                    String password=generatePassword.generateRandomPassword();
                    if (userRepository.findByEmail(email).isPresent()) {
                        user = this.userRepository.findByEmail(email).orElseThrow(() ->new ResourceNotFoundException("User", "Email: " + email, 0));
                        if(user.getIsVerified()) {
                            JwtTokenResponse jwtTokenResponse = this.jwtTokenGenerator.generateToken(email);
                            jwtTokenResponse.setUserId(user.getUserId());
                            jwtTokenResponse.setEmail(email);
                            jwtTokenResponse.setProfileImageUrl(user.getProfileImageUrl());
                            jwtTokenResponse.setSuccess(true);
                            return new ResponseEntity<>(jwtTokenResponse, HttpStatus.OK);
                        } else{
                            user.setFullname(googleSignRequest.name());
                            user.setPassword(passwordEncoder.encode(password));
                            emailService.sendPasswordEmail(email,password);
                            user.setUserId(googleSignRequest.given_name()+GenerateOtp.generateOtp());
                            user.setProfileImageUrl(googleSignRequest.picture());
                            userRepository.save(user);
                            JwtTokenResponse jwtTokenResponse = this.jwtTokenGenerator.generateToken(email);
                            user.setIsVerified(true);
                            userRepository.save(user);
                            jwtTokenResponse.setUserId(user.getUserId());
                            jwtTokenResponse.setEmail(email);
                            jwtTokenResponse.setProfileImageUrl(user.getProfileImageUrl());
                            jwtTokenResponse.setSuccess(true);
                            return new ResponseEntity<>(jwtTokenResponse,HttpStatus.OK);
                        }
                    } else {
                        user = new User();
                        user.setEmail(email);
                        user.setFullname(googleSignRequest.name());
                        user.setPassword(passwordEncoder.encode(password));
                        emailService.sendPasswordEmail(email,password);
                        user.setUserId(googleSignRequest.given_name()+GenerateOtp.generateOtp());
                        user.setProfileImageUrl(googleSignRequest.picture());
                        userRepository.save(user);
                        JwtTokenResponse jwtTokenResponse = this.jwtTokenGenerator.generateToken(email);
                        user.setIsVerified(true);
                        userRepository.save(user);
                        jwtTokenResponse.setUserId(user.getUserId());
                        jwtTokenResponse.setEmail(email);
                        jwtTokenResponse.setProfileImageUrl(user.getProfileImageUrl());
                         jwtTokenResponse.setSuccess(true);
                        return new ResponseEntity<>(jwtTokenResponse,HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(new ApiResponse("The token is either expired or not authorized", false), HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(new ApiResponse("Invalid Action", false), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ApiResponse("Invalid token", false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    private Boolean verifyGoogleToken(GoogleSignRequest request) {
        return ((request.azp().equals(AppConstants.GOOGLE_CLIENT_ID1) || request.azp().equals(AppConstants.GOOGLE_CLIENT_ID2))  && request.exp() * 1000L >= System.currentTimeMillis());
    }
}
