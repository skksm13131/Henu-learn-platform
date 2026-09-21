package com.hwz.admin.service;

import com.hwz.admin.dto.LearningRecordExportRow;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LearningRecordExportServiceTest {

    @Test
    void csvContainsBomAndEscapesValuesForExcel() {
        LearningRecordExportService service = new LearningRecordExportService(null);
        LearningRecordExportRow row = LearningRecordExportRow.builder()
                .username("demo2024_001")
                .displayName("=2+2")
                .realName("李明")
                .grade("2024级")
                .email("student001@labcore.local")
                .itemTitle("提示词,工程")
                .category("智能体")
                .difficulty("简单")
                .firstLearnTime(LocalDateTime.of(2026, 3, 1, 9, 30))
                .completeTime(LocalDateTime.of(2026, 3, 1, 10, 45))
                .learnDurationSec(4500L)
                .completeRemark("理解了\"角色\"设计")
                .build();

        byte[] bytes = service.buildCsv(Collections.singletonList(row));
        String csv = new String(bytes, StandardCharsets.UTF_8);

        assertTrue(csv.startsWith("\uFEFF\"学号\""));
        assertTrue(csv.contains("\"提示词,工程\""));
        assertTrue(csv.contains("\"'=2+2\""));
        assertTrue(csv.contains("\"理解了\"\"角色\"\"设计\""));
        assertTrue(csv.contains("\"1小时15分钟\""));
    }
}
