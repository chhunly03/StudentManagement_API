package com.khrd.studentmanagement.service.studentImpl;

import com.khrd.studentmanagement.model.entity.Admin;
import com.khrd.studentmanagement.model.entity.Role;
import com.khrd.studentmanagement.model.request.StudentRequest;
import com.khrd.studentmanagement.model.response.ApiResponse;
import com.khrd.studentmanagement.model.response.StudentResponse;
import com.khrd.studentmanagement.repository.AdminRepository;
import com.khrd.studentmanagement.service.StudentService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StudentImpl implements StudentService {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AdminRepository adminRepository;

    @Override
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAllStudents() {
        List<StudentResponse> students = adminRepository.findAllByRole(Role.STUDENT)
                .stream()
                .map(StudentResponse::from)
                .collect(Collectors.toList());

        ApiResponse<List<StudentResponse>> response = ApiResponse.<List<StudentResponse>>builder()
                .message(students.isEmpty() ? "No students found" : "Fetched all students successfully")
                .statusCode(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .payload(students)
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .build();

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentById(UUID id) {
        return adminRepository.findByIdAndRole(id, Role.STUDENT)
                .map(student -> ResponseEntity.ok(ApiResponse.<StudentResponse>builder()
                        .message("Student found")
                        .statusCode(HttpStatus.OK.value())
                        .httpStatus(HttpStatus.OK)
                        .payload(StudentResponse.from(student))
                        .timestamp(new Timestamp(System.currentTimeMillis()))
                        .build()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ApiResponse.<StudentResponse>builder()
                                .message("Student not found")
                                .statusCode(HttpStatus.NOT_FOUND.value())
                                .httpStatus(HttpStatus.NOT_FOUND)
                                .timestamp(new Timestamp(System.currentTimeMillis()))
                                .build()));
    }

    @Override
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudentByUUID(UUID id, StudentRequest studentRequest) {
        Optional<Admin> studentOptional = adminRepository.findByIdAndRole(id, Role.STUDENT);

        if (studentOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.<StudentResponse>builder()
                            .message("Student not found")
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .httpStatus(HttpStatus.NOT_FOUND)
                            .timestamp(new Timestamp(System.currentTimeMillis()))
                            .build()
            );
        }

        Admin student = studentOptional.get();
        student.setFirstName(studentRequest.getFirstName());
        student.setLastName(studentRequest.getLastName());
        student.setGender(studentRequest.getGender());
        student.setEmail(studentRequest.getEmail());

        if (studentRequest.getPassword() != null && !studentRequest.getPassword().isBlank()) {
            student.setPassword(passwordEncoder.encode(studentRequest.getPassword()));
        }

        adminRepository.save(student);

        return ResponseEntity.ok(
                ApiResponse.<StudentResponse>builder()
                        .message("Student updated successfully")
                        .statusCode(HttpStatus.OK.value())
                        .httpStatus(HttpStatus.OK)
                        .payload(StudentResponse.from(student))
                        .timestamp(new Timestamp(System.currentTimeMillis()))
                        .build()
        );
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteStudent(UUID id) {
        Optional<Admin> studentOptional = adminRepository.findByIdAndRole(id, Role.STUDENT);

        if (studentOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.<Void>builder()
                            .message("Student not found")
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .httpStatus(HttpStatus.NOT_FOUND)
                            .timestamp(new Timestamp(System.currentTimeMillis()))
                            .build()
            );
        }

        adminRepository.deleteById(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("Student deleted successfully")
                        .statusCode(HttpStatus.OK.value())
                        .httpStatus(HttpStatus.OK)
                        .timestamp(new Timestamp(System.currentTimeMillis()))
                        .build()
        );
    }
}
