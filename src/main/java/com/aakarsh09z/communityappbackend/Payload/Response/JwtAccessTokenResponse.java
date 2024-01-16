package com.aakarsh09z.communityappbackend.Payload.Response;

public record JwtAccessTokenResponse(String myAccessToken, String fullname, String newUsername) {
}
