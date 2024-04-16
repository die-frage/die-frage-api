package com.diefrage.student.services;

import com.diefrage.exceptions.TypicalServerException;
import com.diefrage.student.entities.Student;
import com.diefrage.student.entities.requests.StudentRequest;
import com.diefrage.student.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long studentId) {
        Optional<Student> item = studentRepository.findById(studentId);
        if (item.isEmpty()) {
            TypicalServerException.USER_NOT_FOUND.throwException();
        }
        return item.get();
    }

    public Student getStudentByEmail(String email) {
        Optional<Student> item = studentRepository.findByEmail(email);
        if (item.isEmpty()) {
            TypicalServerException.USER_NOT_FOUND.throwException();
        }
        return item.get();
    }

    public Student register(StudentRequest request) {
        if (!isValidEmail(request.getEmail())) {
            TypicalServerException.INVALID_EMAIL_FORMAT.throwException();
        }
        Student student = new Student();
        student.setEmail(request.getEmail());
        student.setName(request.getName());
        student.setGroupNumber(request.getGroup_number());
        studentRepository.save(student);
        return student;
    }

    public Student deleteStudent(Long studentId) {
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        if (studentOptional.isEmpty()) {
            TypicalServerException.USER_NOT_FOUND.throwException();
        }
        studentRepository.deleteById(studentId);
        return studentOptional.get();

    }

    private boolean isValidName(String name) {
        if (name == null) {
            return true;
        }
        String nameRegex = "[a-zA-Zа-яА-Я]{2,100}";
        Pattern pattern = Pattern.compile(nameRegex);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = ".{2,}@.{2,}";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
