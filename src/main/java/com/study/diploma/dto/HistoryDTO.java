package com.study.diploma.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HistoryDTO {
    private String name;
    private String createdAt;
    private String returnedAt;
}
