package com.aakarsh09z.communityappbackend.Payload.Request;

public record GoogleSignRequest (
        String email,
        String picture,
        String name,
        String given_name,
        String family_name,
        String iss,
        String azp,
        Long exp
){}
