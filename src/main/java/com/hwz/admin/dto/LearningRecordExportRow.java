package com.hwz.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LearningRecordExportRow {

    private String username;
    private String displayName;
    private String realName;
    private String grade;
    private String email;
    private String itemTitle;
    private String category;
    private String difficulty;
    private LocalDateTime firstLearnTime;
    private LocalDateTime completeTime;
    private Long learnDurationSec;
    private String completeRemark;
}
