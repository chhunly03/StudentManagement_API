package com.khrd.studentmanagement.service.AdminImpl;

import com.khrd.studentmanagement.model.entity.Admin;
import com.khrd.studentmanagement.model.entity.Role;
import com.khrd.studentmanagement.model.request.AdminRequest;
import com.khrd.studentmanagement.model.request.UpdateAdminRequest;
import com.khrd.studentmanagement.model.response.AdminResponse;
import com.khrd.studentmanagement.model.response.ApiResponse;
import com.khrd.studentmanagement.model.response.StudentResponse;
import com.khrd.studentmanagement.repository.AdminRepository;
import com.khrd.studentmanagement.service.AdminService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminImpl implements AdminService {
    private final AdminRepository adminRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public AdminResponse register(AdminRequest adminRequest) {
        Admin existingAdmin = adminRepository.findAdminByEmail(adminRequest.getEmail());
        if (existingAdmin != null) {
            throw new IllegalArgumentException("This email already exists");
        }

        Admin admin = new Admin();
        admin.setFirstName(adminRequest.getFirstName());
        admin.setLastName(adminRequest.getLastName());
        admin.setEmail(adminRequest.getEmail());
        admin.setPassword(passwordEncoder.encode(adminRequest.getPassword()));
        admin.setGender(adminRequest.getGender());
        admin.setRole(adminRequest.getRole());

        Admin savedAdmin = adminRepository.save(admin);
        return modelMapper.map(savedAdmin, AdminResponse.class);
    }



    @Override
    public AdminResponse updateAdminByEmail(String email, UpdateAdminRequest updateAdminRequest) {
        Optional<Admin> Optional = adminRepository.findByEmail(email);
        if (Optional.isPresent()) {
            Admin admin = Optional.get();
            if (updateAdminRequest.getPassword() != null) {
                admin.setPassword(passwordEncoder.encode(updateAdminRequest.getPassword()));
            }
            admin = adminRepository.save(admin);
            return modelMapper.map(admin, AdminResponse.class);
        }else {
            System.out.println("No such admin");
            return null;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Admin> admin = adminRepository.findByEmail(email);
        return admin.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Override
    public List<StudentResponse> getAllAdmin() {
        List<StudentResponse> students = adminRepository.findAllByRole(Role.ADMIN)
                .stream()
                .map(StudentResponse::from)
                .collect(Collectors.toList());

        return students;
    }

    @Override
    public StudentResponse forgetPassword(String email, String password) {
        Admin admin = adminRepository.updateAdminRoleByEmail(email,Role.ADMIN);
        if (admin == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }else {
            admin.setPassword(passwordEncoder.encode(password));
            adminRepository.save(admin);
            return modelMapper.map(admin, StudentResponse.class);
        }
    }

}
