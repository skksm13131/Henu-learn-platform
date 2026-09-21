package com.hwz.admin.service;

import com.hwz.admin.dto.LearningRecordExportRow;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class LearningRecordExportService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String BASE_QUERY =
            "SELECT u.username, u.display_name, u.real_name, u.grade, u.email, " +
            "li.title AS item_title, li.category, li.difficulty, " +
            "lr.first_learn_time, lr.complete_time, lr.learn_duration_sec, lr.complete_remark " +
            "FROM learning_record lr " +
            "JOIN sys_user u ON u.user_id = lr.user_id " +
            "JOIN learning_item li ON li.item_pk = lr.item_pk " +
            "WHERE u.role = 'USER'";

    private final JdbcTemplate jdbcTemplate;

    public LearningRecordExportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public byte[] exportCsv(String keyword) {
        return exportCsv(keyword, null, Collections.emptyList());
    }

    public byte[] exportCsv(String keyword, Long userId) {
        return exportCsv(keyword, userId, Collections.emptyList());
    }

    public byte[] exportCsv(String keyword, Long userId, List<Long> userIds) {
        StringBuilder sql = new StringBuilder(BASE_QUERY);
        List<Object> arguments = new ArrayList<>();
        List<Long> selectedUserIds = normalizeUserIds(userId, userIds);
        if (!selectedUserIds.isEmpty()) {
            validateStudents(selectedUserIds);
            sql.append(" AND u.user_id IN (")
                    .append(String.join(",", Collections.nCopies(selectedUserIds.size(), "?")))
                    .append(")");
            arguments.addAll(selectedUserIds);
        }
        if (StringUtils.hasText(keyword)) {
            String pattern = "%" + keyword.trim() + "%";
            sql.append(" AND (u.username LIKE ? OR u.display_name LIKE ? OR u.real_name LIKE ? OR u.email LIKE ?)");
            arguments.add(pattern);
            arguments.add(pattern);
            arguments.add(pattern);
            arguments.add(pattern);
        }
        sql.append(" ORDER BY u.username, lr.first_learn_time, li.item_pk");

        List<LearningRecordExportRow> rows = jdbcTemplate.query(
                sql.toString(),
                (resultSet, rowNum) -> LearningRecordExportRow.builder()
                        .username(resultSet.getString("username"))
                        .displayName(resultSet.getString("display_name"))
                        .realName(resultSet.getString("real_name"))
                        .grade(resultSet.getString("grade"))
                        .email(resultSet.getString("email"))
                        .itemTitle(resultSet.getString("item_title"))
                        .category(resultSet.getString("category"))
                        .difficulty(resultSet.getString("difficulty"))
                        .firstLearnTime(toLocalDateTime(resultSet.getTimestamp("first_learn_time")))
                        .completeTime(toLocalDateTime(resultSet.getTimestamp("complete_time")))
                        .learnDurationSec(resultSet.getLong("learn_duration_sec"))
                        .completeRemark(resultSet.getString("complete_remark"))
                        .build(),
                arguments.toArray()
        );
        return buildCsv(rows);
    }

    private List<Long> normalizeUserIds(Long userId, List<Long> userIds) {
        if (userId != null && userIds != null && !userIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不能同时指定单个学生和多个学生");
        }
        Set<Long> normalized = new LinkedHashSet<>();
        if (userId != null) {
            normalized.add(userId);
        }
        if (userIds != null) {
            normalized.addAll(userIds);
        }
        if (normalized.size() > 1000 || normalized.stream().anyMatch(id -> id == null || id <= 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "选择的学生不正确");
        }
        return new ArrayList<>(normalized);
    }

    private void validateStudents(List<Long> userIds) {
        String placeholders = String.join(",", Collections.nCopies(userIds.size(), "?"));
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sys_user WHERE role = 'USER' AND user_id IN (" + placeholders + ")",
                Integer.class,
                userIds.toArray());
        if (count == null || count != userIds.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "部分学生不存在");
        }
    }

    byte[] buildCsv(List<LearningRecordExportRow> rows) {
        StringBuilder csv = new StringBuilder(1024 + rows.size() * 180);
        csv.append('\uFEFF');
        appendRow(csv, "学号", "显示名", "姓名", "年级", "邮箱", "学习项目", "分类", "难度",
                "状态", "首次学习时间", "完成时间", "学习时长（秒）", "学习时长", "完成备注");
        for (LearningRecordExportRow row : rows) {
            appendRow(csv,
                    row.getUsername(),
                    row.getDisplayName(),
                    row.getRealName(),
                    row.getGrade(),
                    row.getEmail(),
                    row.getItemTitle(),
                    row.getCategory(),
                    row.getDifficulty(),
                    row.getCompleteTime() == null ? "学习中" : "已完成",
                    formatDateTime(row.getFirstLearnTime()),
                    formatDateTime(row.getCompleteTime()),
                    String.valueOf(row.getLearnDurationSec() == null ? 0L : row.getLearnDurationSec()),
                    formatDuration(row.getLearnDurationSec()),
                    row.getCompleteRemark());
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static void appendRow(StringBuilder csv, String... values) {
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                csv.append(',');
            }
            csv.append(escape(values[i]));
        }
        csv.append("\r\n");
    }

    private static String escape(String value) {
        String safeValue = value == null ? "" : value;
        if (!safeValue.isEmpty() && "=+-@\t\r".indexOf(safeValue.charAt(0)) >= 0) {
            safeValue = "'" + safeValue;
        }
        return '"' + safeValue.replace("\"", "\"\"") + '"';
    }

    private static String formatDateTime(LocalDateTime value) {
        return value == null ? "" : DATE_TIME_FORMATTER.format(value);
    }

    private static String formatDuration(Long durationSec) {
        long seconds = durationSec == null ? 0L : durationSec;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        if (hours > 0) {
            return hours + "小时" + minutes + "分钟";
        }
        return minutes + "分钟";
    }

    private static LocalDateTime toLocalDateTime(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }
}
