package com.aakarsh09z.communityappbackend.Security;

import com.aakarsh09z.communityappbackend.Configuration.AppConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtHelper {

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(AppConstants.secret).parseClaimsJws(token).getBody();
    }
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateAccessToken(claims, userDetails.getUsername());
    }
    private String doGenerateAccessToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+ AppConstants.JWT_ACCESS_TOKEN_VALIDITY *1000 ))
                .signWith(SignatureAlgorithm.HS512, AppConstants.secret).compact();
    }
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateRefreshToken(claims, userDetails.getUsername());
    }
    private String doGenerateRefreshToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setSubject("refresh_"+subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + AppConstants.JWT_REFRESH_TOKEN_VALIDITY *1000 ))
                .signWith(SignatureAlgorithm.HS512, AppConstants.secret).compact();
    }
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    public Boolean validateRefreshToken(String token, UserDetails userDetails){
        final String username = getUsernameFromToken(token).substring(8);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
