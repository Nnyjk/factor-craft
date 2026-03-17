# 机器动画系统

## 概述

机器动画系统为 Factor Craft 中的机器 BlockEntity 提供视觉动画效果，包括旋转、浮动、脉冲等。

## 配置

编辑 `config/factorcraft/machine_animation.json`:

```json
{
  "enabled": true,
  "renderDistance": 64.0,
  "updateFrequency": 1,
  "debugMode": false
}
```

- `enabled`: 启用/禁用所有动画
- `renderDistance`: 渲染距离（方块），超过此距离不渲染动画
- `updateFrequency`: 动画更新频率（tick），值越大更新越慢
- `debugMode`: 调试模式，显示额外信息

## 动画类型

### 1. 旋转动画 (Rotation)
机器核心方块绕 Y 轴旋转，工作时速度加快。

### 2. 浮动动画 (Floating)
机器上下浮动，营造悬浮效果。

### 3. 脉冲缩放 (Pulse Scale)
机器大小周期性变化，工作时更明显。

## 机器动画效果

| 机器 | 旋转 | 浮动 | 脉冲 | 特殊效果 |
|------|------|------|------|----------|
| 提取器核心 | ✓ (工作 x3) | ✓ (工作) | ✓ (工作) | 能量脉冲 |
| 合成器核心 | ✓ (工作加速) | ✓ | ✓ (完成) | 进度光环 |
| 培育器核心 | ✓ (注入) | ✓ (注入) | ✓ (反馈) | 特性粒子 |
| 传递器 | ✓ (传输) | ✓ | ✓ (传输) | 能量束 |

## 扩展

### 添加新机器渲染器

1. 继承 `MachineBlockEntityRenderer<T>`:

```java
public class MyMachineRenderer extends MachineBlockEntityRenderer<MyMachineBlockEntity> {
    public MyMachineRenderer(Context context) {
        super(context);
    }
    
    @Override
    protected void applyAnimations(MyMachineBlockEntity entity, float tickDelta, MatrixStack matrices) {
        long time = getMachineTime(entity);
        applyRotation(matrices, tickDelta, time, 0.02f);
        applyFloating(matrices, tickDelta, time, 0.03f, 0.015f);
    }
    
    @Override
    protected void renderModel(MyMachineBlockEntity entity, float tickDelta, MatrixStack matrices,
                               VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // 渲染模型
    }
}
```

2. 在 `FactorCraftClient.registerBlockEntityRenderers()` 中注册:

```java
BlockEntityRendererRegistry.register(ModMachines.MY_MACHINE, MyMachineRenderer::new);
```

## 性能优化

- 距离裁剪：超过配置距离不渲染
- 更新频率控制：可配置更新间隔
- 条件渲染：仅在工作时渲染复杂效果

## 调试

启用 `debugMode` 后，动画系统会显示：
- 当前渲染距离
- 动画状态
- 性能统计

## 注意事项

- 动画仅在客户端渲染
- 不影响游戏逻辑
- 可通过配置文件禁用以提升性能
