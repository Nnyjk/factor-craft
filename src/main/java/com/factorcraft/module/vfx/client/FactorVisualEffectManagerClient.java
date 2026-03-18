package com.factorcraft.module.vfx.client;

import com.factorcraft.module.vfx.FactorElementType;
import com.factorcraft.module.vfx.particle.FactorParticleConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Vec3d;

/**
 * Factor 视觉效果管理器（客户端）
 * 
 * 负责客户端特定的视觉效果：
 * - 手持效果增强渲染
 * - 屏幕效果
 */
public class FactorVisualEffectManagerClient {
    private static final FactorVisualEffectManagerClient INSTANCE = new FactorVisualEffectManagerClient();
    
    // 粒子生成参数
    private static final double HELD_EFFECT_RADIUS = 1.0;
    
    private FactorVisualEffectManagerClient() {}
    
    public static FactorVisualEffectManagerClient getInstance() {
        return INSTANCE;
    }
    
    /**
     * 客户端 tick 处理
     */
    public void tickClient(MinecraftClient client) {
        if (client.world == null || client.player == null) return;
        if (!FactorParticleConfig.ENABLED) return;
        
        ClientWorld world = client.world;
        ClientPlayerEntity player = client.player;
        
        // 客户端手持效果增强
        processHeldEffectsClient(world, player);
        
        // 高浓度区域屏幕效果
        processAreaScreenEffects(world, player);
    }
    
    /**
     * 客户端处理手持效果（更精细）
     */
    private void processHeldEffectsClient(ClientWorld world, ClientPlayerEntity player) {
        ItemStack mainHand = player.getMainHandStack();
        if (!mainHand.isEmpty() && isFactorItem(mainHand)) {
            FactorElementType type = getFactorType(mainHand);
            
            // 客户端额外渲染：手持时微微发光
            spawnClientHeldParticles(world, player, type);
        }
    }
    
    /**
     * 客户端手持粒子效果
     */
    private void spawnClientHeldParticles(ClientWorld world, ClientPlayerEntity player, 
                                          FactorElementType type) {
        Vec3d pos = player.getPos().add(0, player.getStandingEyeHeight() * 0.5, 0);
        double speed = type.getParticleSpeed(0.5);
        
        for (int i = 0; i < 3; i++) {
            double angle = world.random.nextDouble() * Math.PI * 2;
            double radius = HELD_EFFECT_RADIUS * 0.3;
            double height = world.random.nextDouble() * 0.3 - 0.15;
            
            double x = pos.x + Math.cos(angle) * radius;
            double y = pos.y + height;
            double z = pos.z + Math.sin(angle) * radius;
            
            world.addParticle((ParticleEffect) type.getPrimaryParticle(),
                x, y, z,
                Math.cos(angle) * speed * 0.5,
                0.01,
                Math.sin(angle) * speed * 0.5);
        }
    }
    
    /**
     * 处理屏幕效果（客户端）
     */
    private void processAreaScreenEffects(ClientWorld world, ClientPlayerEntity player) {
        // 高浓度区域的屏幕效果由客户端处理
        // 这里可以添加着色器效果或屏幕扭曲
        // 目前使用粒子效果替代
    }
    
    /**
     * 检查是否是 Factor 相关物品
     */
    private boolean isFactorItem(ItemStack stack) {
        String itemId = stack.getItem().toString().toLowerCase();
        return itemId.contains("factor") ||
               itemId.contains("battery") ||
               itemId.contains("scanner") ||
               stack.contains(com.factorcraft.component.FactorCraftDataComponents.FACTOR_STORAGE);
    }
    
    /**
     * 获取物品的 Factor 类型
     */
    private FactorElementType getFactorType(ItemStack stack) {
        String itemName = stack.getItem().toString().toLowerCase();
        if (itemName.contains("fire") || itemName.contains("blaze")) {
            return FactorElementType.FIRE;
        } else if (itemName.contains("water") || itemName.contains("aqua")) {
            return FactorElementType.WATER;
        } else if (itemName.contains("nature") || itemName.contains("nature")) {
            return FactorElementType.NATURE;
        } else if (itemName.contains("void") || itemName.contains("ender")) {
            return FactorElementType.VOID;
        }
        return FactorElementType.ORDER;
    }
}