package com.khrd.studentmanagement.controller;

import com.khrd.studentmanagement.model.request.StudentRequest;
import com.khrd.studentmanagement.model.response.ApiResponse;
import com.khrd.studentmanagement.model.response.StudentResponse;
import com.khrd.studentmanagement.service.StudentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/students")
@AllArgsConstructor
public class StudentController {
    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentById(@PathVariable UUID id) {
        return studentService.getStudentById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudentByUUID(
            @PathVariable UUID id,
            @Valid @RequestBody StudentRequest studentRequest) {
        return studentService.updateStudentByUUID(id, studentRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable UUID id) {
        return studentService.deleteStudent(id);
    }
}
