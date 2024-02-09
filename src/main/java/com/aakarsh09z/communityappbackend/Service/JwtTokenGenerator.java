package com.aakarsh09z.communityappbackend.Service;

import com.aakarsh09z.communityappbackend.Entity.User;
import com.aakarsh09z.communityappbackend.Payload.Response.ApiResponse;
import com.aakarsh09z.communityappbackend.Payload.Response.JwtAccessTokenResponse;
import com.aakarsh09z.communityappbackend.Payload.Response.JwtTokenResponse;
import com.aakarsh09z.communityappbackend.Repository.UserRepository;
import com.aakarsh09z.communityappbackend.Security.JwtHelper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Service
@RequiredArgsConstructor
public class JwtTokenGenerator {
    private final AuthenticationManager authenticationManager;
    private final JwtHelper jwtHelper;
    private final UserDetailsService userDetailsService;
    private  final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
//    public boolean authenticate(String username, String password) {
//        String encodedPassword = passwordEncoder.encode(password);
//        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, encodedPassword);
//        try {
//            this.authenticationManager.authenticate(authenticationToken);
//            return true;
//        } catch (BadCredentialsException var5) {
//            System.out.println("Invalid Password");
//            return false;
//        }
//    }
    public JwtTokenResponse generateToken(String email){
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
        String myAccessToken = this.jwtHelper.generateAccessToken(userDetails);
        String myRefreshToken = this.jwtHelper.generateRefreshToken(userDetails);
        JwtTokenResponse response = new JwtTokenResponse();
        response.setAccessToken(myAccessToken);
        response.setRefreshToken(myRefreshToken);
        return response;
    }
//    public JwtTokenResponse generateToken(String email, String Password){
//        if (this.authenticate(email, Password)) {
//            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
//            String myAccessToken = this.jwtHelper.generateAccessToken(userDetails);
//            String myRefreshToken = this.jwtHelper.generateRefreshToken(userDetails);
//            JwtTokenResponse response = new JwtTokenResponse();
//            response.setAccessToken(myAccessToken);
//            response.setRefreshToken(myRefreshToken);
//            return response;
//        } else {
//            return null;
//        }
//    }
    public ResponseEntity<?> generateRefreshToken(String token){
        if(token != null){
            try {
                String username = this.jwtHelper.getUsernameFromToken(token);
                if(username.startsWith("refresh_")) {
                    String newUsername = username.substring(8);
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(newUsername);
                    User user = userRepository.findByEmail(newUsername).orElseThrow(() -> new NoSuchElementException("User does not exist :"+ newUsername));
                    if (this.jwtHelper.validateRefreshToken(token, userDetails)) {
                        String accessToken = this.jwtHelper.generateAccessToken(userDetails);
                        return new ResponseEntity<>(new JwtAccessTokenResponse(accessToken, user.getFullname(), newUsername,true),OK);
                    }
                    else {
                        return new ResponseEntity<>(new ApiResponse("Refresh Token Expired!!", false), HttpStatus.REQUEST_TIMEOUT);
                    }
                }
                else{
                    return new ResponseEntity<>(new ApiResponse("Not a Refresh Token", false), BAD_REQUEST);
                }
            }
            catch(IllegalArgumentException e){
                return new ResponseEntity<>(new ApiResponse("Unable to get the JWT token!!", false), HttpStatus.BAD_REQUEST);
            }
            catch(ExpiredJwtException e){
                return new ResponseEntity<>(new ApiResponse("Refresh Token Expired!!", false), HttpStatus.REQUEST_TIMEOUT);
            }
            catch(MalformedJwtException e){
                return new ResponseEntity<>(new ApiResponse("Invalid jwt token", false), BAD_REQUEST);
            }
        }
        else {
            return new ResponseEntity<>(new ApiResponse("Invalid jwt token", false), BAD_REQUEST);
        }
    }
}
