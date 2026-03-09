package com.factorcraft.module.cycle.block.entity;

/**
 * Cycle 模块 BlockEntity 占位实现
 * 
 * ⚠️ BlockEntity 功能暂时禁用
 * 
 * 原因：Minecraft 1.21.4 的 BlockEntityFactory 是私有接口
 * 无法在外部代码中创建 BlockEntityType
 * 
 * 待解决问题：
 * - BlockEntityType 注册方式
 * - BlockEntity tick 注册
 * - NBT 保存/加载
 */
public class CycleBlockEntities {
    
    /**
     * 注册所有 BlockEntity 类型
     * TODO: 实现 BlockEntity 注册
     */
    public static void register() {
        // 暂时为空，等待 Fabric 官方更新
    }
}
