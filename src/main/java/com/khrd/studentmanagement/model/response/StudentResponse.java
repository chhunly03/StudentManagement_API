package com.khrd.studentmanagement.model.response;

import com.khrd.studentmanagement.model.entity.Admin;
import com.khrd.studentmanagement.model.entity.Role;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String gender;
    private String email;
    private Role role;

    public static StudentResponse from(Admin student) {
        return StudentResponse.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .gender(student.getGender())
                .email(student.getEmail())
                .role(student.getRole())
                .build();
    }
}
