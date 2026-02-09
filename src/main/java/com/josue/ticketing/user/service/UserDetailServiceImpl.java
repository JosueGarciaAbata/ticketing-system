package com.josue.ticketing.user.service;

import com.josue.ticketing.user.entities.User;
import com.josue.ticketing.user.entities.UserDetailsImpl;
import com.josue.ticketing.user.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Primary
@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailWithRoles(username).orElseThrow(() -> new UsernameNotFoundException(username));
        UserDetailsImpl userDetailServiceImp = new UserDetailsImpl(user);
        return userDetailServiceImp;
    }
}
