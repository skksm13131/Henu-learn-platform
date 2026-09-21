package com.hwz.admin.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class LearningContentSchemaMigrationRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public LearningContentSchemaMigrationRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        ensureColumn("learning_item", "status",
                "ALTER TABLE learning_item ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED' AFTER features");
        ensureColumn("learning_item", "template_path",
                "ALTER TABLE learning_item ADD COLUMN template_path VARCHAR(255) NULL AFTER status");
        ensureColumn("learning_item", "author_id",
                "ALTER TABLE learning_item ADD COLUMN author_id BIGINT NULL AFTER template_path");
        ensureColumn("learning_item", "published_at",
                "ALTER TABLE learning_item ADD COLUMN published_at DATETIME NULL AFTER author_id");
        ensureColumn("learning_item", "created_at",
                "ALTER TABLE learning_item ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER published_at");
        ensureColumn("learning_item", "updated_at",
                "ALTER TABLE learning_item ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at");

        jdbcTemplate.update("UPDATE learning_item SET status = 'PUBLISHED' WHERE status IS NULL OR status = ''");
    }

    private void ensureColumn(String tableName, String columnName, String ddl) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?",
                Integer.class,
                tableName,
                columnName
        );
        if (count != null && count == 0) {
            jdbcTemplate.execute(ddl);
        }
    }
}
