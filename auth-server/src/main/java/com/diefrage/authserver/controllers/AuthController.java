package com.diefrage.authserver.controllers;

import com.diefrage.authserver.entities.requests.SignInRequest;
import com.diefrage.authserver.entities.requests.SignUpRequest;
import com.diefrage.authserver.entities.responses.JwtAuthenticationResponse;
import com.diefrage.authserver.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
public class AuthController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody SignUpRequest request) {
        return authenticationService.signUp(request);
    }

    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody SignInRequest request) {
        return authenticationService.signIn(request);
    }

    @Operation(summary = "Валидация токена для аутентификации")
    @GetMapping("/validate")
    public String validateToken(@RequestParam(name = "token") String token) {
        return authenticationService.validateToken(token);
    }
}
