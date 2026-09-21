package com.hwz.user.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TrendPointDTO {
    private LocalDate date;

    private Long completedCount;
}
