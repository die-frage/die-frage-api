package com.diefrage.students;

import com.diefrage.students.entities.Student;
import com.diefrage.students.entities.StudentDTO;
import com.diefrage.students.entities.StudentSignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/{student_id}")
    @Operation(summary = "Получение информацию о пользователе")
    public StudentDTO getStudentById(@PathVariable(value = "student_id") Long studentId) {
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
            @RequestBody StudentSignUpRequest request) {
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