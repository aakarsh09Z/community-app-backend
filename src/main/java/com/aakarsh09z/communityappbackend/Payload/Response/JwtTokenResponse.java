package com.aakarsh09z.communityappbackend.Payload.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class JwtTokenResponse {
    private String accessToken;
    private String refreshToken;
    private String userId;
    private String email;
    private String profileImageUrl;
    private Boolean success;
}
