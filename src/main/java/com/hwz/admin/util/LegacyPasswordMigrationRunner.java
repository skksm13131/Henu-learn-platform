package com.hwz.admin.util;

import com.hwz.common.entity.User;
import com.hwz.common.mapper.UserMapper;
import com.hwz.common.util.PasswordSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class LegacyPasswordMigrationRunner implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public LegacyPasswordMigrationRunner(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        try {
            List<User> users = userMapper.selectList(null);
            int migrated = 0;
            for (User user : users) {
                if (user == null || !StringUtils.hasText(user.getPassword()) || PasswordSupport.isEncoded(user.getPassword())) {
                    continue;
                }
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.setUpdatedTime(LocalDateTime.now());
                userMapper.updateById(user);
                migrated++;
            }
            if (migrated > 0) {
                log.info("Migrated {} legacy plaintext passwords to BCrypt", migrated);
            }
        } catch (Exception e) {
            log.error("Failed to migrate legacy passwords", e);
        }
    }
}
