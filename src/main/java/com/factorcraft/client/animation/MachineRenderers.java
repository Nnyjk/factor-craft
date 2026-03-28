package com.factorcraft.client.animation;

import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

import java.util.HashMap;
import java.util.Map;

/**
 * 机器渲染器注册表
 * 
 * 管理所有机器类型的 BlockEntityRenderer 注册
 */
public class MachineRenderers {
    
    private static final Map<BlockEntityType<?>, BlockEntityRenderer<?>> RENDERERS = new HashMap<>();
    
    /**
     * 注册机器渲染器
     */
    public static void register() {
        // 渲染器在 FactorCraftClient 中通过 BlockEntityRendererRegistry 注册
        // 这里仅作为渲染器类型的集中管理
    }
    
    /**
     * 获取指定 BlockEntityType 的渲染器
     */
    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> BlockEntityRenderer<T> getRenderer(BlockEntityType<T> type) {
        return (BlockEntityRenderer<T>) RENDERERS.get(type);
    }
}
