package com.khrd.studentmanagement.service;

import com.khrd.studentmanagement.model.request.StudentRequest;
import com.khrd.studentmanagement.model.response.ApiResponse;
import com.khrd.studentmanagement.model.response.StudentResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface StudentService {
    ResponseEntity<ApiResponse<List<StudentResponse>>> getAllStudents();

    ResponseEntity<ApiResponse<StudentResponse>> getStudentById(UUID id);

    ResponseEntity<ApiResponse<StudentResponse>> updateStudentByUUID(UUID id, StudentRequest studentRequest);

    ResponseEntity<ApiResponse<Void>> deleteStudent(UUID id);
}
