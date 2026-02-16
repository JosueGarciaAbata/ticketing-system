package com.josue.ticketing.user.service;

import com.josue.ticketing.user.dtos.RegisterRequest;
import com.josue.ticketing.user.dtos.RegisterResponse;
import com.josue.ticketing.user.entities.Role;
import com.josue.ticketing.user.entities.User;
import com.josue.ticketing.user.repos.RoleRepository;
import com.josue.ticketing.user.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public RegisterResponse register(RegisterRequest registerRequest, String roleName) {

        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new RuntimeException("Email ya registrado.");
        }

        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new RuntimeException("Rol invalido"));

        User user = new User();
        user.setEmail(registerRequest.email());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setFirstName(registerRequest.firstName());
        user.setLastName(registerRequest.lastName());
        user.setRoles(Set.of(role));

        User saved = userRepository.save(user);

        return RegisterResponse.builder()
                .email(saved.getEmail())
                .fullName(saved.getFirstName() + " " + saved.getLastName())
                .build();
    }
}
