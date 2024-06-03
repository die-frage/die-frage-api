package com.diefrage.businessserver.controllers;

import com.diefrage.businessserver.dto.StudentDTO;
import com.diefrage.businessserver.entities.Student;
import com.diefrage.businessserver.requests.StudentRequest;
import com.diefrage.businessserver.services.StudentService;
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
    @Operation(summary = "Получение информацию о студенте")
    public StudentDTO getStudentById(@PathVariable(value = "student_id") Long studentId) {
        return StudentDTO.fromStudent(studentService.getStudentById(studentId));
    }

    @GetMapping("/")
    @Operation(summary = "Получение информации о студенте")
    public StudentDTO getStudentByEmail(@RequestParam(value = "email") String email) {
        return StudentDTO.fromStudent(studentService.getStudentByEmail(email));
    }

    @GetMapping("/by_chat_id/{chat_id}")
    @Operation(summary = "Получение информации о студенте")
    public StudentDTO getStudentByChatId(@PathVariable(value = "chat_id") String chatId) {
        return StudentDTO.fromStudent(studentService.getStudentByChatId(chatId));
    }

    @PostMapping("/registration")
    @Operation(summary = "Создание новой записи о студенте")
    public StudentDTO registration(@RequestBody StudentRequest request) {
        return StudentDTO.fromStudent(studentService.register(request));
    }

    @DeleteMapping("/delete/{student_id}")
    @Operation(summary = "Удаление записи о студенте")
    public StudentDTO deleteStudent(@PathVariable(value = "student_id") Long studentId) {
        Student student = studentService.deleteStudent(studentId);
        return StudentDTO.fromStudent(student);
    }

    @PutMapping("/update")
    @Operation(summary = "Изменение данных о студенте")
    public StudentDTO updateStudent(@RequestBody StudentRequest request) {
        return StudentDTO.fromStudent(studentService.update(request));
    }
}
