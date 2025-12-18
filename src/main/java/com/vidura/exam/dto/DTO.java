package com.vidura.exam.dto;

import com.vidura.exam.dto.response.ServerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public  class DTO {
    private String message;
    private ServerStatus status;

}
