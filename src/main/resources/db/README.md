# LabCore 数据库初始化

当前阶段以 `labcore_full_init.sql` 作为唯一初始化入口。后续稳定后再拆分为结构脚本和数据脚本。

## 全新重建

`labcore_full_init.sql` 会创建并选择 `labcore` 数据库，包含完整表结构、学习卡片数据和一条管理员账号数据。

```bash
mysql --binary-mode=1 -uroot -p < src/main/resources/db/labcore_full_init.sql
```

导入后可以执行只读校验：

```bash
mysql -uroot -p labcore < src/main/resources/db/verify_initialization.sql
```

所有检查项应显示 `PASS`。

## 文件说明

- `labcore_full_init.sql`: 当前推荐的一体化初始化脚本。
- `verify_initialization.sql`: 初始化结果只读校验脚本。
- `schema.sql`: 后续拆分使用的建表基线，暂不作为主入口。
- `seed_learning_cards.sql`: 后续拆分使用的学习卡片数据，暂不作为主入口。
- `assignment_module.sql`: 旧环境单独补齐能力考核表时使用。
- `demo/`: 本地页面验收数据，不属于标准初始化。
- `migrations/`: 旧库增量迁移脚本。
- `snapshots/`: 历史快照，仅用于恢复或核对。

## 文件存储边界

- 在线练习模板随后端资源打包，默认位置为 `classpath:learning-templates`。
- 作业提交文件和作业材料是运行期数据，默认写入 `data/assignment-submissions` 和 `data/assignment-materials`。
