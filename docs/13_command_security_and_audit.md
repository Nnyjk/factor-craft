# 13. 命令权限与审计标准（M0.2）

## 目标

为 M0 命令 MVP 提供统一安全基线，确保命令执行具备“可控、可拦截、可追踪”。

## 权限策略

- 每条命令必须声明 `permission` 字段。
- 权限节点建议命名：`factorcraft.command.<domain>.<action>`。
- 默认最小权限原则：
  - 调试/运维类命令：`OPERATOR`
  - 普通玩家可用命令：`PLAYER`
- 未配置权限或权限字段非法的命令，启动期直接阻断。

## 审计字段规范

每次命令执行建议至少记录以下字段：

- `command`: 命令 ID
- `executor`: 执行者（玩家名/系统）
- `success`: 是否成功
- `code`: 结果码（如 `OK` / `COMMAND_NOT_FOUND` / `RATE_LIMITED`）
- `message`: 结果消息
- `executedAtMillis`: 执行时间戳（毫秒）

## 结果码建议

- `OK`: 执行成功
- `EMPTY_COMMAND`: 空命令
- `COMMAND_NOT_FOUND`: 未匹配命令
- `COMMAND_DISABLED`: 命令被禁用
- `HANDLER_NOT_FOUND`: 处理器未注册
- `RATE_LIMITED`: 触发限流
- `EXECUTION_EXCEPTION`: 处理器内部异常

## 留存策略

- 内存留存最近 500 条审计记录。
- 生产环境建议将审计日志接入外部日志系统（ELK/Loki/ClickHouse 等）。
- 审计日志默认保留 30 天（服务端策略，可按运营要求调整）。

## 错误处理策略

- 命令执行阶段：
  - 参数/配置错误直接返回失败结果码，不抛出未处理异常。
  - handler 异常统一收敛为 `EXECUTION_EXCEPTION` 并写审计。
- 事件总线阶段（同步）：
  - 默认 `LOG_AND_CONTINUE`。
  - 核心流程可切换 `FAIL_FAST`。
