package com.khrd.studentmanagement.service;

import com.khrd.studentmanagement.model.request.AdminRequest;
import com.khrd.studentmanagement.model.request.UpdateAdminRequest;
import com.khrd.studentmanagement.model.response.AdminResponse;
import com.khrd.studentmanagement.model.response.StudentResponse;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AdminService extends UserDetailsService {
    AdminResponse register(AdminRequest adminRequest);

    AdminResponse updateAdminByEmail(String email, UpdateAdminRequest updateAdminRequest);

    List<StudentResponse> getAllAdmin();

    StudentResponse forgetPassword(String email, String password);
}
