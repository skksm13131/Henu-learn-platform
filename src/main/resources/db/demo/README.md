# 演示数据

本目录中的 SQL 只用于本地页面验收，不属于 LabCore 标准初始化流程，禁止在生产数据库执行。

## 学生学习记录

`seed_student_learning_records.sql` 会创建 45 个匿名演示学生账号，并生成确定性的学习记录。
脚本可重复执行，只会重建 `demo2024_` 前缀账号的数据。

```bash
mysql --binary-mode=1 -uroot -p labcore < seed_student_learning_records.sql
```

演示账号统一密码为 `DemoStudent@123`。
