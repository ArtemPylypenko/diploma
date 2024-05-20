package com.study.diploma.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HistoryDTO {
    public String name;
    public String createdAt;

    public String returnedAt;

}
