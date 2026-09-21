package com.hwz.assignment.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AssignmentSchemaMigrationRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public AssignmentSchemaMigrationRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS assignment (" +
                "assignment_id BIGINT NOT NULL AUTO_INCREMENT," +
                "title VARCHAR(255) NOT NULL," +
                "description TEXT NULL," +
                "category VARCHAR(100) NULL," +
                "deadline DATETIME NULL," +
                "total_score DECIMAL(6,2) NOT NULL DEFAULT 100.00," +
                "status VARCHAR(20) NOT NULL DEFAULT 'DRAFT'," +
                "created_by BIGINT NULL," +
                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "PRIMARY KEY (assignment_id)," +
                "KEY idx_assignment_status (status)," +
                "KEY idx_assignment_deadline (deadline)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS assignment_question (" +
                "question_id BIGINT NOT NULL AUTO_INCREMENT," +
                "assignment_id BIGINT NOT NULL," +
                "title VARCHAR(255) NOT NULL," +
                "content TEXT NULL," +
                "score DECIMAL(6,2) NOT NULL DEFAULT 0.00," +
                "sort_order INT NOT NULL DEFAULT 0," +
                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "PRIMARY KEY (question_id)," +
                "KEY idx_assignment_question_assignment (assignment_id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS assignment_submission (" +
                "submission_id BIGINT NOT NULL AUTO_INCREMENT," +
                "assignment_id BIGINT NOT NULL," +
                "student_id BIGINT NOT NULL," +
                "status VARCHAR(20) NOT NULL DEFAULT 'DRAFT'," +
                "answer_text TEXT NULL," +
                "submitted_at DATETIME NULL," +
                "graded_at DATETIME NULL," +
                "score DECIMAL(6,2) NULL," +
                "feedback TEXT NULL," +
                "graded_by BIGINT NULL," +
                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "PRIMARY KEY (submission_id)," +
                "UNIQUE KEY uk_assignment_student (assignment_id, student_id)," +
                "KEY idx_assignment_submission_assignment (assignment_id)," +
                "KEY idx_assignment_submission_student (student_id)," +
                "KEY idx_assignment_submission_status (status)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");

        Integer answerTextColumnCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.COLUMNS " +
                        "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'assignment_submission' " +
                        "AND COLUMN_NAME = 'answer_text'",
                Integer.class);
        if (answerTextColumnCount != null && answerTextColumnCount == 0) {
            jdbcTemplate.execute("ALTER TABLE assignment_submission ADD COLUMN answer_text TEXT NULL AFTER status");
        }

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS assignment_submission_file (" +
                "file_id BIGINT NOT NULL AUTO_INCREMENT," +
                "submission_id BIGINT NOT NULL," +
                "file_type VARCHAR(20) NOT NULL," +
                "original_name VARCHAR(255) NOT NULL," +
                "stored_path VARCHAR(500) NOT NULL," +
                "mime_type VARCHAR(100) NULL," +
                "file_size BIGINT NOT NULL DEFAULT 0," +
                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "PRIMARY KEY (file_id)," +
                "KEY idx_assignment_file_submission (submission_id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS assignment_material (" +
                "material_id BIGINT NOT NULL AUTO_INCREMENT," +
                "assignment_id BIGINT NOT NULL," +
                "material_type VARCHAR(20) NOT NULL," +
                "title VARCHAR(255) NULL," +
                "original_name VARCHAR(255) NOT NULL," +
                "stored_path VARCHAR(500) NOT NULL," +
                "mime_type VARCHAR(100) NULL," +
                "file_size BIGINT NOT NULL DEFAULT 0," +
                "sort_order INT NOT NULL DEFAULT 0," +
                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "PRIMARY KEY (material_id)," +
                "KEY idx_assignment_material_assignment (assignment_id)," +
                "KEY idx_assignment_material_type (material_type)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
    }
}
