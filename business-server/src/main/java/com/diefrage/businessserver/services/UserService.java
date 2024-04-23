package com.diefrage.businessserver.services;

import com.diefrage.businessserver.entities.User;
import com.diefrage.businessserver.repositories.UserRepository;
import com.diefrage.exceptions.TypicalServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SurveyService surveyService;

    public User getUserById(User userRequest, Long id) {
        if (!Objects.equals(userRequest.getId(), id)) {
            TypicalServerException.USER_NOT_FOUND.throwException();
        }
        Optional<User> item = userRepository.findById(id);
        if (item.isEmpty()) {
            TypicalServerException.USER_NOT_FOUND.throwException();
        }
        return item.get();
    }

    public User getUserByEmail(User userRequest, String email) {
        if (!Objects.equals(userRequest.getEmail(), email)) {
            TypicalServerException.USER_NOT_FOUND.throwException();
        }
        return getUserByEmail(email);
    }

    public User getUserByEmail(String email) {
        Optional<User> item = userRepository.findByEmail(email);
        if (item.isEmpty()) {
            TypicalServerException.USER_NOT_FOUND.throwException();
        }
        return item.get();
    }

    public User updateUserById(User userRequest, Long id, String firstName, String lastName, String patronymic, String email, String password) {
        if (!Objects.equals(userRequest.getId(), id)) {
            TypicalServerException.USER_NOT_FOUND.throwException();
        }

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent() && !Objects.equals(optionalUser.get().getId(), id)) {
            TypicalServerException.USER_ALREADY_EXISTS.throwException();
        }

        if (!isValidEmail(email)) {
            TypicalServerException.INVALID_EMAIL_FORMAT.throwException();
        }

        if (!isValidPassword(password)) {
            TypicalServerException.INVALID_PASSWORD_FORMAT.throwException();
        }

        if (!isValidName(firstName)) {
            TypicalServerException.INVALID_NAME_FORMAT.throwException();
        }

        if (!isValidName(lastName)) {
            TypicalServerException.INVALID_NAME_FORMAT.throwException();
        }

        if (patronymic == null || patronymic.equals("")) {
        } else if (!isValidName(patronymic)) {
            TypicalServerException.INVALID_NAME_FORMAT.throwException();
        }

        var user = User.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .patronymic(patronymic)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();

        return update(user);
    }

    public User deleteUserById(User userRequest, Long id) {
        if (!Objects.equals(userRequest.getId(), id)) {
            TypicalServerException.USER_NOT_FOUND.throwException();
        }

        Optional<User> item = userRepository.findById(id);
        if (item.isEmpty()) {
            TypicalServerException.USER_NOT_FOUND.throwException();
        }
        User user = item.get();

        surveyService.deleteAllSurveys(user.getId());
        userRepository.deleteById(id);
        return user;
    }

    public User update(User user) {
        if (!userRepository.existsById(user.getId())) {
            TypicalServerException.USER_NOT_FOUND.throwException();
        }
        return userRepository.save(user);
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
