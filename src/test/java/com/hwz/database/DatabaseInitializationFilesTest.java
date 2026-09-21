package com.hwz.database;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseInitializationFilesTest {

    @Test
    void fullInitializationTargetsLabcoreAndContainsAdminSeed() throws Exception {
        String sql = String.join("\n", readResource("db/labcore_full_init.sql"));

        assertTrue(sql.contains("CREATE DATABASE IF NOT EXISTS `labcore`"));
        assertTrue(sql.contains("USE `labcore`;"));
        assertTrue(sql.contains("INSERT INTO `sys_user`"));
        assertTrue(sql.contains("'admin'"));
        assertFalse(sql.contains("labcore_test"));
        assertFalse(sql.contains("is_contributor"));
    }

    @Test
    void bundledLearningTemplatesExistForInitializedAgentCards() throws Exception {
        for (int itemId = 53; itemId <= 57; itemId++) {
            assertNotNull(getClass().getClassLoader()
                    .getResourceAsStream("learning-templates/item-" + itemId + "/template.ipynb"));
        }
    }

    private List<String> readResource(String path) throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream(path);
        assertNotNull(input, "Missing classpath resource: " + path);
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }
}
