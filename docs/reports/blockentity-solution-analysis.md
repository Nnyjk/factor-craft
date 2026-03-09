# BlockEntity 注册问题分析与解决

> **日期：** 2026-03-09  
> **问题：** Minecraft 1.21.4 BlockEntity 注册  
> **状态：** ✅ 已解决

---

## 🔍 问题分析

### 为什么一定要用 BlockEntity？

**需要 BlockEntity 的场景：**
1. ✅ 存储额外 NBT 数据（Factor 数值、进度等）
2. ✅ 每 tick 执行逻辑（处理合成、传输等）
3. ✅ GUI 交互（玩家操作界面）
4. ✅ 网络同步（客户端 - 服务端数据同步）

**我们的 Factor 循环系统：**
- 需要存储 Factor 数值 → ✅ 需要 BlockEntity
- 需要 tick 处理进度 → ✅ 需要 BlockEntity
- 需要 GUI 操作 → ✅ 需要 BlockEntity
- 需要网络同步 → ✅ 需要 BlockEntity

**结论：** BlockEntity 是必要的，无法用 BlockState 替代。

---

## ❌ 错误的尝试

### 尝试 1: 直接使用 BlockEntityType 构造函数

```java
// ❌ 错误：构造函数是私有的
new BlockEntityType<>(factory, blocks, null)
```

**错误信息：**
```
BlockEntityType(net.minecraft.block.entity.BlockEntityType$BlockEntityFactory,java.util.Set<net.minecraft.block.Block>)
has private access in BlockEntityType
```

### 尝试 2: 使用 BlockEntityType.create()

```java
// ❌ 错误：方法是私有的
BlockEntityType.create(factory, blocks)
```

**错误信息：**
```
<T>create(String,net.minecraft.block.entity.BlockEntityType$BlockEntityFactory,
net.minecraft.block.Block...) has private access in BlockEntityType
```

### 尝试 3: 使用 Object 参数绕过

```java
// ❌ 错误：lambda 无法推断类型
private static BlockEntityType<T> create(Object factory, Block... blocks)
```

**错误信息：**
```
incompatible types: Object is not a functional interface
```

### 尝试 4: 暂时禁用 BlockEntity

```java
// ⚠️ 临时方案：功能不完整
super(null, pos, state)
```

**问题：** 无法运行，会在游戏启动时崩溃。

---

## ✅ 正确的解决方案

### Fabric API 提供了 FabricBlockEntityTypeBuilder

**正确的导入：**
```java
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
```

**正确的注册方式：**
```java
public class CycleBlockEntities {
    public static final BlockEntityType<FactorSinkBlockEntity> FACTOR_SINK;
    
    static {
        // 使用 Fabric 提供的 Builder
        FACTOR_SINK = FabricBlockEntityTypeBuilder.create(
            FactorSinkBlockEntity::new,  // 工厂方法
            CycleBlocks.getFactorSink()  // 关联的方块
        ).build(null);
    }
    
    public static void register() {
        // 手动注册到 Registry（Fabric 不会自动注册）
        Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(ModId.MOD_ID, "factor_sink"),
            FACTOR_SINK
        );
    }
}
```

---

## 📝 关键知识点

### 1. FabricBlockEntityTypeBuilder 的优势

- ✅ **公开 API** - Fabric 官方支持
- ✅ **类型安全** - 泛型正确推断
- ✅ **易于使用** - Builder 模式
- ✅ **向后兼容** - Fabric 保证稳定性

### 2. 为什么需要手动注册？

```java
// FabricBlockEntityTypeBuilder.build() 不会自动注册
BlockEntityType<T> type = FabricBlockEntityTypeBuilder.create(...)
    .build(null);

// 必须手动调用 Registry.register()
Registry.register(Registries.BLOCK_ENTITY_TYPE, id, type);
```

**原因：** Fabric 遵循 Minecraft 的注册机制，不自动注册自定义内容。

### 3. 工厂方法的两种写法

**方法引用（推荐）：**
```java
FabricBlockEntityTypeBuilder.create(
    FactorSinkBlockEntity::new,  // 简洁
    blocks
)
```

**Lambda 表达式：**
```java
FabricBlockEntityTypeBuilder.create(
    (pos, state) -> new FactorSinkBlockEntity(pos, state),  // 明确
    blocks
)
```

### 4. .build() 参数

```java
.build(null)  // DataType<?> 参数，可为 null
```

**说明：** 第二个参数是 Mojang 的 DataFixer 类型，Mod 通常不需要。

---

## 🎯 完整的 BlockEntity 实现模板

### CycleBlockEntities.java

```java
package com.example.mod.block.entity;

import com.example.mod.ExampleMod;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<ExampleBlockEntity> EXAMPLE;
    
    static {
        EXAMPLE = FabricBlockEntityTypeBuilder.create(
            ExampleBlockEntity::new,
            ModBlocks.EXAMPLE
        ).build(null);
    }
    
    public static void register() {
        Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(ExampleMod.MOD_ID, "example"),
            EXAMPLE
        );
    }
}
```

### ExampleBlockEntity.java

```java
package com.example.mod.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ExampleBlockEntity extends BlockEntity {
    
    public ExampleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EXAMPLE, pos, state);
    }
    
    public static void tick(World world, BlockPos pos, 
                           BlockState state, ExampleBlockEntity entity) {
        if (world.isClient) return;
        // tick 逻辑
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        // 保存数据
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        // 加载数据
    }
    
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
    
    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}
```

### CycleBlocks.java

```java
package com.example.mod.block;

import com.example.mod.block.entity.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;

public class ModBlocks {
    
    public static <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Block block, BlockEntityType<T> type) {
        if (block == EXAMPLE && type == ModBlockEntities.EXAMPLE) {
            return (world, pos, state, blockEntity) -> 
                ExampleBlockEntity.tick(world, pos, state, 
                    (ExampleBlockEntity) blockEntity);
        }
        return null;
    }
}
```

---

## 🚨 常见错误

### 错误 1: 忘记手动注册

```java
// ❌ 错误：只创建不注册
public static final BlockEntityType<ExampleBlockEntity> EXAMPLE = 
    FabricBlockEntityTypeBuilder.create(...).build(null);

// ✅ 正确：创建后注册
public static void register() {
    Registry.register(Registries.BLOCK_ENTITY_TYPE, id, EXAMPLE);
}
```

### 错误 2: 循环依赖

```java
// ❌ 错误：互相引用
public class ModBlocks {
    public static final Block EXAMPLE = register(...);
    public static final BlockEntityType<?> TYPE = ModBlockEntities.EXAMPLE;
}

public class ModBlockEntities {
    public static final BlockEntityType<ExampleBlockEntity> EXAMPLE = 
        create(ModBlocks.EXAMPLE);  // 循环！
}

// ✅ 正确：使用延迟初始化
public class ModBlocks {
    public static Block getExample() {
        if (example == null) {
            example = register(...);
        }
        return example;
    }
}
```

### 错误 3: 忘记在 Mod 入口调用 register()

```java
// ❌ 错误：没有调用
public void onInitialize() {
    ModBlocks.register();
    // 忘记调用 ModBlockEntities.register()
}

// ✅ 正确：都调用
public void onInitialize() {
    ModBlocks.register();
    ModBlockEntities.register();
}
```

---

## 📊 对比总结

| 方案 | 状态 | 原因 |
|------|------|------|
| `new BlockEntityType()` | ❌ | 构造函数私有 |
| `BlockEntityType.create()` | ❌ | 方法私有 |
| `BlockEntityType.Builder` | ❌ | 不存在 |
| **`FabricBlockEntityTypeBuilder`** | ✅ | **Fabric 官方 API** |

---

## 🎓 经验教训

### 1. 不要过早放弃

- 第一次尝试失败后，应该继续查找 Fabric API
- 成熟的框架一定提供了正确的方式
- 我的错误：假设"无法实现"而不是"我没找到"

### 2. 善用反编译工具

- 使用 `javap` 查看类结构
- 查看 Fabric API 的 JAR 文件
- 找到 `FabricBlockEntityTypeBuilder` 只花了 5 分钟

### 3. 相信开源社区

- Fabric 是成熟的 Mod 加载器
- 一定有社区验证过的最佳实践
- 我应该先查文档/示例，而不是自己摸索

### 4. 文档要准确

- 我之前写的"BlockEntityFactory 是私有接口，无法实现"是错误的
- Fabric 提供了 `FabricBlockEntityTypeBuilder.Factory` 公开接口
- 已更新 fabric-best-practices Skill

---

## 📚 参考资源

- [Fabric Object Builder API](https://docs.fabricmc.net/1.21.4/develop/objects)
- [FabricBlockEntityTypeBuilder Javadoc](https://maven.fabricmc.net/docs/fabric-api-0.119.2+1.21.4/net/fabricmc/fabric/api/object/builder/v1/block/entity/FabricBlockEntityTypeBuilder.html)
- [Fabric Example Mod](https://github.com/FabricMC/fabric-example-mod)

---

> **问题解决时间：** 2 小时  
> **关键发现：** FabricBlockEntityTypeBuilder  
> **状态：** ✅ 完全解决，代码已提交
