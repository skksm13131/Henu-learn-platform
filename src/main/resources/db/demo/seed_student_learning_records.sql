-- Anonymous local demo data for learning-record pages and exports.
-- Creates 45 demo students and deterministic, realistic-looking learning records.
-- Safe to rerun: only records owned by usernames prefixed with demo2024_ are rebuilt.

USE `labcore`;
SET NAMES utf8mb4;

INSERT INTO `sys_user`
  (`username`, `password`, `display_name`, `email`, `real_name`, `grade`, `role`, `status`, `created_time`, `updated_time`)
VALUES
  ('demo2024_001', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '李明', 'student001@labcore.local', '李明', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:00:00', NOW()),
  ('demo2024_002', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '王芳', 'student002@labcore.local', '王芳', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:02:00', NOW()),
  ('demo2024_003', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '张伟', 'student003@labcore.local', '张伟', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:04:00', NOW()),
  ('demo2024_004', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '刘洋', 'student004@labcore.local', '刘洋', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:06:00', NOW()),
  ('demo2024_005', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '陈晨', 'student005@labcore.local', '陈晨', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:08:00', NOW()),
  ('demo2024_006', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '杨帆', 'student006@labcore.local', '杨帆', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:10:00', NOW()),
  ('demo2024_007', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '赵静', 'student007@labcore.local', '赵静', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:12:00', NOW()),
  ('demo2024_008', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '黄杰', 'student008@labcore.local', '黄杰', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:14:00', NOW()),
  ('demo2024_009', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '周宁', 'student009@labcore.local', '周宁', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:16:00', NOW()),
  ('demo2024_010', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '吴桐', 'student010@labcore.local', '吴桐', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:18:00', NOW()),
  ('demo2024_011', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '徐欣', 'student011@labcore.local', '徐欣', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:20:00', NOW()),
  ('demo2024_012', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '孙浩', 'student012@labcore.local', '孙浩', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:22:00', NOW()),
  ('demo2024_013', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '胡悦', 'student013@labcore.local', '胡悦', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:24:00', NOW()),
  ('demo2024_014', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '朱凯', 'student014@labcore.local', '朱凯', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:26:00', NOW()),
  ('demo2024_015', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '高洁', 'student015@labcore.local', '高洁', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:28:00', NOW()),
  ('demo2024_016', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '林涛', 'student016@labcore.local', '林涛', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:30:00', NOW()),
  ('demo2024_017', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '何雨', 'student017@labcore.local', '何雨', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:32:00', NOW()),
  ('demo2024_018', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '郭子涵', 'student018@labcore.local', '郭子涵', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:34:00', NOW()),
  ('demo2024_019', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '马文博', 'student019@labcore.local', '马文博', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:36:00', NOW()),
  ('demo2024_020', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '罗思远', 'student020@labcore.local', '罗思远', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:38:00', NOW()),
  ('demo2024_021', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '梁佳', 'student021@labcore.local', '梁佳', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:40:00', NOW()),
  ('demo2024_022', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '宋宇', 'student022@labcore.local', '宋宇', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:42:00', NOW()),
  ('demo2024_023', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '郑楠', 'student023@labcore.local', '郑楠', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:44:00', NOW()),
  ('demo2024_024', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '谢琳', 'student024@labcore.local', '谢琳', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:46:00', NOW()),
  ('demo2024_025', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '韩旭', 'student025@labcore.local', '韩旭', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:48:00', NOW()),
  ('demo2024_026', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '唐琪', 'student026@labcore.local', '唐琪', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:50:00', NOW()),
  ('demo2024_027', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '冯硕', 'student027@labcore.local', '冯硕', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:52:00', NOW()),
  ('demo2024_028', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '于萌', 'student028@labcore.local', '于萌', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:54:00', NOW()),
  ('demo2024_029', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '董航', 'student029@labcore.local', '董航', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:56:00', NOW()),
  ('demo2024_030', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '萧然', 'student030@labcore.local', '萧然', '2024级', 'USER', 'ACTIVE', '2026-02-10 09:58:00', NOW()),
  ('demo2024_031', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '程璐', 'student031@labcore.local', '程璐', '2023级', 'USER', 'ACTIVE', '2026-02-10 10:00:00', NOW()),
  ('demo2024_032', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '曹磊', 'student032@labcore.local', '曹磊', '2023级', 'USER', 'ACTIVE', '2026-02-10 10:02:00', NOW()),
  ('demo2024_033', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '袁媛', 'student033@labcore.local', '袁媛', '2023级', 'USER', 'ACTIVE', '2026-02-10 10:04:00', NOW()),
  ('demo2024_034', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '邓超', 'student034@labcore.local', '邓超', '2023级', 'USER', 'ACTIVE', '2026-02-10 10:06:00', NOW()),
  ('demo2024_035', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '许诺', 'student035@labcore.local', '许诺', '2023级', 'USER', 'ACTIVE', '2026-02-10 10:08:00', NOW()),
  ('demo2024_036', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '傅强', 'student036@labcore.local', '傅强', '2023级', 'USER', 'ACTIVE', '2026-02-10 10:10:00', NOW()),
  ('demo2024_037', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '沈佳怡', 'student037@labcore.local', '沈佳怡', '2023级', 'USER', 'ACTIVE', '2026-02-10 10:12:00', NOW()),
  ('demo2024_038', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '曾浩然', 'student038@labcore.local', '曾浩然', '2023级', 'USER', 'ACTIVE', '2026-02-10 10:14:00', NOW()),
  ('demo2024_039', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '彭雪', 'student039@labcore.local', '彭雪', '2025级', 'USER', 'ACTIVE', '2026-02-10 10:16:00', NOW()),
  ('demo2024_040', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '吕哲', 'student040@labcore.local', '吕哲', '2025级', 'USER', 'ACTIVE', '2026-02-10 10:18:00', NOW()),
  ('demo2024_041', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '苏晴', 'student041@labcore.local', '苏晴', '2025级', 'USER', 'ACTIVE', '2026-02-10 10:20:00', NOW()),
  ('demo2024_042', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '卢俊', 'student042@labcore.local', '卢俊', '2025级', 'USER', 'ACTIVE', '2026-02-10 10:22:00', NOW()),
  ('demo2024_043', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '蒋欣然', 'student043@labcore.local', '蒋欣然', '2025级', 'USER', 'ACTIVE', '2026-02-10 10:24:00', NOW()),
  ('demo2024_044', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '蔡志远', 'student044@labcore.local', '蔡志远', '2025级', 'USER', 'ACTIVE', '2026-02-10 10:26:00', NOW()),
  ('demo2024_045', '$2b$10$yNdBxDrbzOFyTTTY7QiazeUhggUQ4jnutm/qGgxbgclL/GB6dwMVi', '邱雨薇', 'student045@labcore.local', '邱雨薇', '2025级', 'USER', 'ACTIVE', '2026-02-10 10:28:00', NOW())
ON DUPLICATE KEY UPDATE
  `display_name` = VALUES(`display_name`),
  `email` = VALUES(`email`),
  `real_name` = VALUES(`real_name`),
  `grade` = VALUES(`grade`),
  `role` = 'USER',
  `status` = 'ACTIVE',
  `updated_time` = NOW();

UPDATE `sys_user`
SET `last_login_time` = DATE_ADD(
      DATE_ADD('2026-07-01 08:00:00', INTERVAL MOD(CAST(SUBSTRING(`username`, 10) AS UNSIGNED) * 7, 12) DAY),
      INTERVAL MOD(CAST(SUBSTRING(`username`, 10) AS UNSIGNED) * 3, 11) HOUR
    )
WHERE `username` LIKE 'demo2024\_%';

DELETE lr
FROM `learning_record` lr
JOIN `sys_user` u ON u.`user_id` = lr.`user_id`
WHERE u.`username` LIKE 'demo2024\_%';

INSERT INTO `learning_record`
  (`user_id`, `item_pk`, `first_learn_time`, `complete_time`, `complete_remark`,
   `learn_duration_sec`, `created_at`, `updated_at`)
SELECT
  generated_rows.`user_id`,
  generated_rows.`item_pk`,
  generated_rows.`first_learn_time`,
  CASE WHEN generated_rows.`completion_score` < generated_rows.`completion_threshold`
    THEN DATE_ADD(generated_rows.`first_learn_time`, INTERVAL generated_rows.`learn_duration_sec` SECOND)
    ELSE NULL
  END,
  CASE WHEN generated_rows.`completion_score` >= generated_rows.`completion_threshold` THEN NULL
    WHEN MOD(generated_rows.`student_no` + generated_rows.`item_pk`, 5) = 0 THEN '完成实验并理解了核心步骤'
    WHEN MOD(generated_rows.`student_no` + generated_rows.`item_pk`, 5) = 1 THEN '代码运行通过，关键结果已记录'
    WHEN MOD(generated_rows.`student_no` + generated_rows.`item_pk`, 5) = 2 THEN '掌握了基本原理，后续继续复习'
    WHEN MOD(generated_rows.`student_no` + generated_rows.`item_pk`, 5) = 3 THEN '完成练习，对参数影响有了直观认识'
    ELSE '按要求完成学习任务'
  END,
  generated_rows.`learn_duration_sec`,
  generated_rows.`first_learn_time`,
  DATE_ADD(generated_rows.`first_learn_time`, INTERVAL generated_rows.`learn_duration_sec` SECOND)
FROM (
  SELECT
    students.`user_id`,
    students.`student_no`,
    li.`item_pk`,
    DATE_ADD(
      DATE_ADD('2026-02-17 08:20:00', INTERVAL MOD(students.`student_no` * 11 + li.`item_pk` * 5, 120) DAY),
      INTERVAL MOD(students.`student_no` * 37 + li.`item_pk` * 23, 720) MINUTE
    ) AS `first_learn_time`,
    1200 + MOD(students.`student_no` * 421 + li.`item_pk` * 613, 7800) AS `learn_duration_sec`,
    MOD(students.`student_no` * 19 + li.`item_pk` * 23, 100) AS `completion_score`,
    60 + MOD(students.`student_no` * 7, 31) AS `completion_threshold`
  FROM (
    SELECT `user_id`, CAST(SUBSTRING(`username`, 10) AS UNSIGNED) AS `student_no`
    FROM `sys_user`
    WHERE `username` LIKE 'demo2024\_%'
  ) students
  JOIN `learning_item` li
    ON li.`status` = 'PUBLISHED'
   AND li.`item_pk` <= 18 + MOD(students.`student_no` * 13, 24)
   AND MOD(li.`item_pk` + students.`student_no` * 3, 7) <> 0
) generated_rows;

SELECT COUNT(*) AS `demo_student_count`
FROM `sys_user`
WHERE `username` LIKE 'demo2024\_%';

SELECT COUNT(*) AS `demo_learning_record_count`,
       SUM(`complete_time` IS NOT NULL) AS `completed_count`,
       SUM(`complete_time` IS NULL) AS `in_progress_count`
FROM `learning_record` lr
JOIN `sys_user` u ON u.`user_id` = lr.`user_id`
WHERE u.`username` LIKE 'demo2024\_%';
