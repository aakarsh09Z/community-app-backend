package com.aakarsh09z.communityappbackend.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class GeneralController {
    public String test(){
        return "This is working";
    }
}
