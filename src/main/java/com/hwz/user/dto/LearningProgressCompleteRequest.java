package com.hwz.user.dto;

import lombok.Data;

@Data
public class LearningProgressCompleteRequest {
    private Long itemPk;
    private String completeRemark;
}
