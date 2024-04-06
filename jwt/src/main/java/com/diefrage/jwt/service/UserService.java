package com.diefrage.jwt.service;

import com.diefrage.exceptions.TypicalServerException;
import com.diefrage.jwt.entity.User;
import com.diefrage.jwt.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public User update(User user) {
        if (!userRepository.existsById(user.getId())) {
            TypicalServerException.USER_NOT_FOUND.throwException();
        }
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        User user = getCurrentUser();
        Optional<User> item = userRepository.findById(id);
        if (item.isEmpty()) {
            TypicalServerException.USER_NOT_FOUND.throwException();
        }
        User possibleUser = item.get();
        if (!Objects.equals(user.getId(), possibleUser.getId())) {
            TypicalServerException.USER_NOT_FOUND.throwException();
        }
        return possibleUser;
    }

    public User getUserByEmail(String email) {
        User user = getCurrentUser();
        Optional<User> item = userRepository.findByEmail(email);
        if (item.isEmpty()) {
            TypicalServerException.USER_NOT_FOUND.throwException();
        }
        User possibleUser = item.get();
        if (!Objects.equals(user.getId(), possibleUser.getId())) {
            TypicalServerException.USER_NOT_FOUND.throwException();
        }
        return possibleUser;
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

    private boolean isValidPassword(String password) {
        return password.length() >= 8;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = ".{2,}@.{2,}";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidName(String name) {
        if (name == null) {
            return true;
        }
        String nameRegex = "[a-zA-Zа-яА-Я\\s]{1,100}";
        Pattern pattern = Pattern.compile(nameRegex);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }
}