package com.factorcraft.client.animation;

import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

import java.util.HashMap;
import java.util.Map;

/**
 * 机器 BlockEntity 渲染器通用实现
 * 
 * 为所有机器类型提供统一的渲染器注册和管理
 */
public class MachineBlockEntityRenderer {
    
    private static final Map<BlockEntityType<?>, BlockEntityRenderer<?>> RENDERERS = new HashMap<>();
    
    /**
     * 注册机器渲染器
     * 在 FactorCraftClient 中通过 BlockEntityRendererRegistry 调用
     */
    public static void registerRenderers() {
        // 渲染器在 FactorCraftClient 中注册
        // 这里仅作为集中管理入口
    }
    
    /**
     * 获取指定 BlockEntityType 的渲染器
     */
    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> BlockEntityRenderer<T> getRenderer(BlockEntityType<T> type) {
        return (BlockEntityRenderer<T>) RENDERERS.get(type);
    }
    
    /**
     * 注册渲染器到内部映射
     */
    public static <T extends BlockEntity> void register(BlockEntityType<T> type, BlockEntityRenderer<T> renderer) {
        RENDERERS.put(type, renderer);
    }
}
