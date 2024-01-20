package com.aakarsh09z.communityappbackend.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class GeneralController {
    @GetMapping("/test")
    public String test(){
        return "This is working";
    }
}
