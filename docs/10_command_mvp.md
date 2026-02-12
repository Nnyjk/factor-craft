# 10. 命令系统 MVP（基础地基）

## 目标

在不引入高自由度脚本系统的前提下，提供可配置、可维护、可扩展、可审计的命令基础设施。

## 设计原则

- 低自由度：命令只绑定受控 `handlerId`，不支持任意脚本执行。
- 强约束：统一 `CommandSpec` + `CommandSpecValidator`。
- 可扩展：扩展包可注册命令定义和处理器。
- 可观测：执行结果统一为 `CommandExecutionResult`，并预留 `CommandExecutedEvent`。

## 核心结构

- `CommandModule`：模块入口。
- `CommandRegistrar` / `CommandApi` / `CommandHandler`：接入与查询接口。
- `CommandSpec` / `CommandArgSpec`：命令标准模型。
- `CommandRegistry`：命令/别名/处理器统一注册中心。
- `CommandSpecValidator`：命令配置校验器。

## 标准字段（CommandSpec）

- `commandId`：唯一标识（`namespace:path`）。
- `sourcePackId`：来源包标识。
- `handlerId`：受控处理器 ID。
- `aliases`：别名列表。
- `permission`：权限节点。
- `scope`：命令作用域（SERVER/PLAYER/OPERATOR）。
- `cooldownTicks`：冷却。
- `rateLimitPerMinute`：限流。
- `enabled`：启用开关。
- `tags`：扩展标签。
- `args`：参数定义。

## 生命周期（建议）

1. 启动时加载命令配置（后续可接入 dynamic JSON）。
2. 通过 `CommandSpecValidator` 校验。
3. 注册到 `CommandRegistry`。
4. 执行时：命令查找 -> 处理器查找 -> 执行 -> 结果落审计/事件。

## 示例（配置草案）

```json
{
  "commandId": "factor_craft:factor_debug",
  "sourcePackId": "factor_craft",
  "handlerId": "factor.debug",
  "aliases": ["fc_factor_debug"],
  "description": "输出当前世界因子信息",
  "permission": "factorcraft.command.factor.debug",
  "scope": "OPERATOR",
  "cooldownTicks": 20,
  "rateLimitPerMinute": 30,
  "enabled": true,
  "tags": ["debug", "factor"],
  "args": [
    {
      "name": "dimension",
      "type": "DIMENSION",
      "required": false,
      "description": "维度ID",
      "defaultValue": "minecraft:overworld"
    }
  ]
}
```
