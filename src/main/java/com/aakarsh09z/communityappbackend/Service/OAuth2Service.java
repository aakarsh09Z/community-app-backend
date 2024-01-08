package com.aakarsh09z.communityappbackend.Service;

import com.aakarsh09z.communityappbackend.Entity.User;
import com.aakarsh09z.communityappbackend.Repository.UserRepository;
import com.aakarsh09z.communityappbackend.Security.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2Service {
    private final UserRepository userRepository;
    public String googleLoginCallback(Authentication authentication){
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {

            String email = oauthToken.getPrincipal().getAttribute("email");

            User user =new User();
            if (userRepository.findByEmail(email).isPresent()) {
                user=userRepository.findByEmail(email).orElseThrow();
            }
                user.setEmail(email);
                user.setFullname(oauthToken.getPrincipal().getAttribute("name"));
                user.setIsVerified(true);
                userRepository.save(user);

        }
        return "redirect:/dashboard";
    }
}
