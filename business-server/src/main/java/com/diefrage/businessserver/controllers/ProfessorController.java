package com.diefrage.businessserver.controllers;

import com.diefrage.businessserver.dto.UserDTO;
import com.diefrage.businessserver.entities.User;
import com.diefrage.businessserver.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/professor")
@RequiredArgsConstructor
public class ProfessorController {

    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "Получение информацию о пользователе")
    public UserDTO getProfessorById(
            @PathVariable(value = "id") Long id,
            @RequestHeader(value = "X-Username") String username) {
        User userRequest = userService.getUserByEmail(username);
        return UserDTO.fromUser(userService.getUserById(userRequest, id));
    }

    @GetMapping("by_email/{email}")
    @Operation(summary = "Получение информацию о пользователе")
    public UserDTO getProfessorByEmail(
            @PathVariable(value = "email") String email,
            @RequestHeader(value = "X-Username") String username) {
        User userRequest = userService.getUserByEmail(username);
        return UserDTO.fromUser(userService.getUserByEmail(userRequest, email));
    }

    @PutMapping("/credentials/{id}")
    @Operation(summary = "Обновление информацию о пользователе")
    public UserDTO updateCredentials(
            @PathVariable(value = "id") Long id,
            @RequestParam(value = "firstName") String firstName,
            @RequestParam(value = "lastName") String lastName,
            @RequestParam(value = "patronymic", required = false) String patronymic,
            @RequestParam(value = "email") String email,
            @RequestParam(value = "password") String password,
            @RequestHeader(value = "X-Username") String username) {
        User userRequest = userService.getUserByEmail(username);
        User prof = userService.updateUserById(userRequest, id, firstName, lastName, patronymic, email, password);
        return UserDTO.fromUser(prof);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Удаление пользователя")
    public UserDTO deleteProfessor(
            @PathVariable(value = "id") Long id,
            @RequestHeader(value = "X-Username") String username) {
        User userRequest = userService.getUserByEmail(username);
        return UserDTO.fromUser(userService.deleteUserById(userRequest, id));
    }
}
