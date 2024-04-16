package com.diefrage.student.controllers;

import com.diefrage.student.services.StudentService;
import com.diefrage.student.entities.Student;
import com.diefrage.student.entities.StudentDTO;
import com.diefrage.student.entities.requests.StudentRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/{student_id}")
    @Operation(summary = "Получение информацию о пользователе")
    public StudentDTO getStudentById(@PathVariable(value = "student_id") Long studentId) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String username = request.getHeader("X-Username");
        System.out.println(username);
        return StudentDTO.fromStudent(studentService.getStudentById(studentId));
    }

    @GetMapping("/")
    @Operation(summary = "Получение информацию о пользователе")
    public StudentDTO getStudentByEmail(@RequestParam(value = "email") String email) {
        return StudentDTO.fromStudent(studentService.getStudentByEmail(email));
    }

    @PostMapping("/registration")
    @Operation(summary = "Создание новой записи о студенте")
    public StudentDTO registration(
            @RequestBody StudentRequest request) {
        return StudentDTO.fromStudent(studentService.register(request));
    }

    @DeleteMapping("/delete/{student_id}")
    @Operation(summary = "Удаление записи о студенте")
    public StudentDTO deleteStudent(
            @PathVariable(value = "student_id") Long studentId) {
        Student student = studentService.deleteStudent(studentId);
        return StudentDTO.fromStudent(student);
    }
}
