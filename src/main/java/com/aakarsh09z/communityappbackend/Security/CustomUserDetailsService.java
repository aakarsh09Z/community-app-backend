package com.aakarsh09z.communityappbackend.Security;
import com.aakarsh09z.communityappbackend.Exceptions.ResourceNotFoundException;
import com.aakarsh09z.communityappbackend.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DialectOverride;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User ", "email : "+username, 0));
    }
}
