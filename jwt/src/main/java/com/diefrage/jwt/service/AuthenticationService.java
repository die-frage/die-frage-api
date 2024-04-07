package com.diefrage.jwt.service;

import com.diefrage.exceptions.TypicalServerException;
import com.diefrage.jwt.entity.Role;
import com.diefrage.jwt.entity.User;
import com.diefrage.jwt.entity.request.SignInRequest;
import com.diefrage.jwt.entity.request.SignUpRequest;
import com.diefrage.jwt.entity.response.JwtAuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        String patronymic = request.getPatronymic();
        if (patronymic == null) patronymic = "";
        validateSignUp(request);

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .patronymic(patronymic)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        userService.create(user);

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    public JwtAuthenticationResponse signIn(SignInRequest request) {
        validateSignIn(request);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
            ));
        } catch (BadCredentialsException ex) {
            TypicalServerException.USER_NOT_FOUND.throwException();
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));
        var user = userService
                .userDetailsService()
                .loadUserByUsername(request.getEmail());

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    public String validateToken(String token) {
        try {
            jwtService.validateToken(token);
            return jwtService.extractUserName(token);
        } catch (Exception e) {
            TypicalServerException.USER_NOT_FOUND.throwException();
        }
        return "";
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

    public void validateSignUp(SignUpRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        String firstName = request.getFirstName();
        String lastName = request.getLastName();
        String patronymic = request.getPatronymic();

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
    }

    public void validateSignIn(SignInRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        if (!isValidEmail(email)) {
            System.out.println("1");
            TypicalServerException.INVALID_EMAIL_FORMAT.throwException();
        }
        if (!isValidPassword(password)) {
            System.out.println("2");
            TypicalServerException.INVALID_PASSWORD_FORMAT.throwException();
        }
    }


}
