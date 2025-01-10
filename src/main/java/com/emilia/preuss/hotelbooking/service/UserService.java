package com.emilia.preuss.hotelbooking.service;

import com.emilia.preuss.hotelbooking.model.User;
import com.emilia.preuss.hotelbooking.repository.UserRepository;
import com.emilia.preuss.hotelbooking.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    // Register a new user with hashed password
    public void registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Hash password
        user.setRoles(Collections.singletonList("ROLE_USER")); // Default role
        userRepository.save(user);
    }

    // Authenticate the user and return a JWT token
    public String loginUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isEmpty()) {
            throw new BadCredentialsException("Invalid email or password");
        }

        // Check hashed password
        if (!passwordEncoder.matches(user.getPassword(), existingUser.get().getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        // Authenticate using AuthenticationManager (optional, ensures role-based security)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
        );

        // Generate JWT token
        return jwtTokenProvider.generateToken(authentication);
    }
}
