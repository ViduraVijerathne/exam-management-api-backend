package com.vidura.exam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SubjectDTO extends DTO{
    private Long id;
    @NotBlank(message = "please enter subject name")
    @Size(min = 1, max = 100 ,message = "subject name  length should be between 1 - 100")
    private String name;
    private boolean isActive = true;
}
