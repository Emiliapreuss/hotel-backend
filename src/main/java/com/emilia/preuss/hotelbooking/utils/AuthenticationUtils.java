package com.emilia.preuss.hotelbooking.utils;

import com.emilia.preuss.hotelbooking.exception.UserNotFoundException;
import com.emilia.preuss.hotelbooking.model.User;
import com.emilia.preuss.hotelbooking.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUtils {

    private final UserService userService;

    public AuthenticationUtils(UserService userService) {
        this.userService = userService;
    }

    public User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
    }

    public String getAuthenticatedUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
