package com.vidura.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PaginationResponse<T> extends  DTO{
    private int pageNumber = 1;
    private int limit = 10;
    private RetrieveStatus retrieveStatus;
    private List<T> data;
}
