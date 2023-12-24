package com.aakarsh09z.communityappbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class CommunityAppBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityAppBackendApplication.class, args);
    }

}
