package com.diefrage.professor.controllers;

import com.diefrage.professor.entities.User;
import com.diefrage.professor.entities.UserDTO;
import com.diefrage.professor.services.UserService;
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
            @PathVariable(value = "id") Long id) {
        return UserDTO.fromUser(userService.getUserById(id));
    }

    @GetMapping("by_email/{email}")
    @Operation(summary = "Получение информацию о пользователе")
    public UserDTO getProfessorByEmail(
            @PathVariable(value = "email") String email) {
        return UserDTO.fromUser(userService.getUserByEmail(email));
    }

    @PutMapping("/credentials/{id}")
    @Operation(summary = "Обновление информацию о пользователе")
    public UserDTO updateCredentials(
            @PathVariable(value = "id") Long id,
            @RequestParam(value = "firstName") String firstName,
            @RequestParam(value = "lastName") String lastName,
            @RequestParam(value = "patronymic", required = false) String patronymic,
            @RequestParam(value = "email") String email,
            @RequestParam(value = "password") String password) {
        User prof = userService.updateUserById(id, firstName, lastName, patronymic, email, password);
        return UserDTO.fromUser(prof);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Удаление пользователя")
    public UserDTO deleteProfessor(
            @PathVariable(value = "id") Long id) {
        return UserDTO.fromUser(userService.deleteUserById(id));
    }
}