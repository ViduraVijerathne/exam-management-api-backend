package com.vidura.exam.dto.response;

import com.vidura.exam.dto.DTO;
import com.vidura.exam.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginResponse extends DTO {
   private String email;
    private String token;
    private Role role;
    private Boolean isEmailVerified;
    private Boolean isProfileCompleted;

}