# 11. 基础地基补全（M0.1）

## 本次已落地

- 模块生命周期扩展：`initialize / reload / shutdown`。
- 模块依赖声明：`dependencies()` 与启动前校验。
- 动态配置接入 `commands.json`：纳入统一 `DynamicBundle`。

## 关键约束

- 不引入任意脚本执行。
- 仍采用受控 handler 分发。
- 以配置与标准模型驱动，不提前耦合业务实现。

## 后续建议（M0.2）

- 增加事件总线基础契约（优先级、错误处理、同步/异步语义）。
- 增加权限与审计文档标准（命令安全策略）。
- 为 registry/validator 增加纯 JVM 单元测试。
