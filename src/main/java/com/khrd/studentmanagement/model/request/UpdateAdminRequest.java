package com.khrd.studentmanagement.model.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAdminRequest {
    private String password;
}
