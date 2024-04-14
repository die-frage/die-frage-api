package com.diefrage.authserver.service;

import com.diefrage.authserver.entity.User;
import com.diefrage.authserver.repository.UserRepository;
import com.diefrage.exceptions.TypicalServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User create(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            TypicalServerException.USER_ALREADY_EXISTS.throwException();
        }
        return save(user);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public UserDetailsService userDetailsService() {
        return this::getByEmail;
    }

    public User getCurrentUser() {
        // Получение имени пользователя из контекста Spring Security
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByEmail(username);
    }

    private User getByEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            TypicalServerException.USER_NOT_FOUND.throwException();
        }
        return optionalUser.get();
    }

}