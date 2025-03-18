package com.khrd.studentmanagement.controller;

import com.khrd.studentmanagement.exception.NotFoundException;
import com.khrd.studentmanagement.jwt.JwtService;
import com.khrd.studentmanagement.model.entity.Admin;
import com.khrd.studentmanagement.model.entity.Role;
import com.khrd.studentmanagement.model.request.AdminRequest;
import com.khrd.studentmanagement.model.request.AuthRequest;
import com.khrd.studentmanagement.model.request.UpdateAdminRequest;
import com.khrd.studentmanagement.model.response.AdminResponse;
import com.khrd.studentmanagement.model.response.ApiResponse;
import com.khrd.studentmanagement.model.response.AuthResponse;
import com.khrd.studentmanagement.model.response.StudentResponse;
import com.khrd.studentmanagement.repository.AdminRepository;
import com.khrd.studentmanagement.service.AdminService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})

public class AuthController {
    private final AdminService adminService;
    private final AdminRepository adminRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AdminResponse>> register(@RequestBody AdminRequest adminRequest) {
        try {
            AdminResponse user = adminService.register(adminRequest);

            ApiResponse<AdminResponse> response = ApiResponse.<AdminResponse>builder()
                    .message("Register successfully")
                    .statusCode(HttpStatus.CREATED.value())
                    .httpStatus(HttpStatus.CREATED)
                    .payload(user)
                    .timestamp(new Timestamp(System.currentTimeMillis()))
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<AdminResponse> response = ApiResponse.<AdminResponse>builder()
                    .message(e.getMessage())
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .timestamp(new Timestamp(System.currentTimeMillis()))
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            UserDetails userApp = adminService.loadUserByUsername(username);
            if (userApp == null) {
                throw new BadRequestException("Wrong Email");
            }
            if (!passwordEncoder.matches(password, userApp.getPassword())) {
                throw new BadRequestException("Wrong Password");
            }
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody @Valid AuthRequest authRequest) throws Exception {
        try {
            authenticate(authRequest.getEmail(), authRequest.getPassword());
        } catch (Exception e) {
            throw new NotFoundException("Invalid email or password.");
        }

        UserDetails userDetails = adminService.loadUserByUsername(authRequest.getEmail());

        // Generate JWT token
        String token = jwtService.generateToken(userDetails);

        AuthResponse authResponse = new AuthResponse(userDetails, token);
        return ResponseEntity.ok(authResponse);
    }

    @PutMapping("/update/{email}")
    public ResponseEntity<ApiResponse<AdminResponse>> update(@PathVariable String email, @RequestBody UpdateAdminRequest updateAdminRequest) {
        AdminResponse update = adminService.updateAdminByEmail(email,updateAdminRequest);
        if ( update != null) {
            ApiResponse<AdminResponse> response = ApiResponse.<AdminResponse>builder()
                    .message("Update successfully")
                    .statusCode(HttpStatus.OK.value())
                    .httpStatus(HttpStatus.OK)
                    .payload(update)
                    .timestamp(new Timestamp(System.currentTimeMillis()))
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<AdminResponse> response = ApiResponse.<AdminResponse>builder()
                    .message("Update not successfully")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .payload(update)
                    .timestamp(new Timestamp(System.currentTimeMillis()))
                    .build();
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminResponse>> getAdminById(UUID id) {
        Optional<Admin> admin = adminRepository.findById(id);

        if (admin.isPresent()) {
            AdminResponse data = modelMapper.map(admin, AdminResponse.class);
            ApiResponse<AdminResponse> response = ApiResponse.<AdminResponse>builder()
                    .message("Admin found")
                    .statusCode(HttpStatus.OK.value())
                    .httpStatus(HttpStatus.OK)
                    .payload(data)
                    .timestamp(new Timestamp(System.currentTimeMillis()))
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<AdminResponse> response = ApiResponse.<AdminResponse>builder()
                    .message("Admin not found")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .timestamp(new Timestamp(System.currentTimeMillis()))
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/getAllAdmin")
    public ResponseEntity<ApiResponse<List<StudentResponse>>>getAllAdmins() {
        List<StudentResponse>admin=adminService.getAllAdmin();
        if (admin!=null) {
            ApiResponse<List<StudentResponse>> response = ApiResponse.<List<StudentResponse>>builder()
                    .message("Admin found")
                    .statusCode(HttpStatus.OK.value())
                    .httpStatus(HttpStatus.OK)
                    .payload(admin)
                    .timestamp(new Timestamp(System.currentTimeMillis()))
                    .build();
            return ResponseEntity.ok(response);
        }else {
            ApiResponse<List<StudentResponse>> response = ApiResponse.<List<StudentResponse>>builder()
                    .message("Admin not found")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .payload(admin)
                    .timestamp(new Timestamp(System.currentTimeMillis()))
                    .build();
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/email")
    public ResponseEntity<ApiResponse<StudentResponse>>forgetPassword(@PathVariable String email, String password) {
        StudentResponse admin = adminService.forgetPassword(email,password);
        if (admin!=null) {
            ApiResponse<StudentResponse> response = ApiResponse.<StudentResponse>builder()
                    .message("Admin updated successfully")
                    .statusCode(HttpStatus.OK.value())
                    .httpStatus(HttpStatus.OK)
                    .payload(admin)
                    .timestamp(new Timestamp(System.currentTimeMillis()))
                    .build();
            return ResponseEntity.ok(response);
        }else {
            ApiResponse<StudentResponse> response = ApiResponse.<StudentResponse>builder()
                    .message("Admin not update success")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .payload(admin)
                    .timestamp(new Timestamp(System.currentTimeMillis()))
                    .build();
            return ResponseEntity.ok(response);
        }
    }


}

