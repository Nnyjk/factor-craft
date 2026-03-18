package com.factorcraft.module.vfx;

import com.factorcraft.module.factor.FactorService;
import com.factorcraft.module.factor.TideStatus;
import com.factorcraft.module.vfx.particle.FactorParticleConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Factor 视觉效果管理器（服务端）
 * 
 * 负责：
 * - 手持 Factor 效果
 * - 使用 Factor 效果
 * - 高浓度区域效果
 * - 强度分级效果
 * 
 * 注意：客户端相关代码在 FactorVisualEffectManagerClient 中
 */
public class FactorVisualEffectManager {
    private static final FactorVisualEffectManager INSTANCE = new FactorVisualEffectManager();
    
    // 玩家手持效果冷却
    private final Map<UUID, Long> heldEffectCooldowns = new HashMap<>();
    
    // 区域效果冷却
    private long lastAreaEffectTick = 0;
    
    // 效果间隔 (ticks)
    private static final int HELD_EFFECT_INTERVAL = 5;
    private static final int AREA_EFFECT_INTERVAL = 20;
    
    // 粒子生成参数
    private static final double HELD_EFFECT_RADIUS = 1.0;
    private static final double AREA_EFFECT_RADIUS = 16.0;
    
    private FactorVisualEffectManager() {}
    
    public static FactorVisualEffectManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * 服务端 tick 处理
     */
    public void tickServer(ServerWorld world) {
        long time = world.getTime();
        
        // 区域效果
        if (time - lastAreaEffectTick >= AREA_EFFECT_INTERVAL) {
            lastAreaEffectTick = time;
            processAreaEffects(world);
        }
        
        // 玩家手持效果
        for (PlayerEntity player : world.getPlayers()) {
            processHeldEffectsServer(world, player, time);
        }
    }
    
    /**
     * 服务端处理手持效果
     */
    private void processHeldEffectsServer(ServerWorld world, PlayerEntity player, long time) {
        UUID playerId = player.getUuid();
        
        // 检查冷却
        Long lastTime = heldEffectCooldowns.get(playerId);
        if (lastTime != null && time - lastTime < HELD_EFFECT_INTERVAL) {
            return;
        }
        
        // 检查主手物品
        ItemStack mainHand = player.getMainHandStack();
        if (!mainHand.isEmpty()) {
            // 检查是否是 Factor 相关物品
            if (isFactorItem(mainHand)) {
                FactorElementType type = getFactorType(mainHand);
                double concentration = FactorService.getInstance().getFactor(world);
                
                spawnHeldEffectParticles(world, player, type, concentration);
                heldEffectCooldowns.put(playerId, time);
            }
        }
    }
    
    /**
     * 生成手持效果粒子
     */
    private void spawnHeldEffectParticles(ServerWorld world, PlayerEntity player,
                                          FactorElementType type, double concentration) {
        if (!FactorParticleConfig.shouldSpawn(0)) return;
        
        Vec3d pos = player.getPos().add(0, player.getStandingEyeHeight() * 0.5, 0);
        int count = type.getParticleCount(concentration, world.random);
        double speed = type.getParticleSpeed(concentration);
        
        for (int i = 0; i < count; i++) {
            double angle = world.random.nextDouble() * Math.PI * 2;
            double radius = HELD_EFFECT_RADIUS * (0.5 + world.random.nextDouble() * 0.5);
            double height = world.random.nextDouble() * 0.5 - 0.25;
            
            double x = pos.x + Math.cos(angle) * radius;
            double y = pos.y + height;
            double z = pos.z + Math.sin(angle) * radius;
            
            // 使用元素类型的主粒子
            world.spawnParticles((ParticleEffect) type.getPrimaryParticle(),
                x, y, z, 1,
                Math.cos(angle) * speed,
                0.02,
                Math.sin(angle) * speed,
                0.01);
        }
    }
    
    /**
     * 处理区域效果
     */
    private void processAreaEffects(ServerWorld world) {
        double concentration = FactorService.getInstance().getFactor(world);
        TideStatus status = TideStatus.fromConcentration(concentration);
        
        // 只在非稳定状态时显示区域效果
        if (status == TideStatus.STABLE) return;
        
        for (PlayerEntity player : world.getPlayers()) {
            BlockPos playerPos = player.getBlockPos();
            FactorElementType type = FactorElementType.fromConcentration(concentration, world.random);
            
            spawnAreaEffectParticles(world, playerPos, type, concentration, status);
        }
    }
    
    /**
     * 生成区域效果粒子
     */
    private void spawnAreaEffectParticles(ServerWorld world, BlockPos center,
                                          FactorElementType type, double concentration,
                                          TideStatus status) {
        if (!FactorParticleConfig.shouldSpawn(0)) return;
        
        int baseCount = getIntensityParticleCount(status);
        int count = FactorParticleConfig.getActualCount(baseCount);
        double speed = type.getParticleSpeed(concentration);
        
        for (int i = 0; i < count; i++) {
            double angle = world.random.nextDouble() * Math.PI * 2;
            double radius = AREA_EFFECT_RADIUS * world.random.nextDouble();
            double height = world.random.nextDouble() * 8;
            
            double x = center.getX() + 0.5 + Math.cos(angle) * radius;
            double y = center.getY() + height;
            double z = center.getZ() + 0.5 + Math.sin(angle) * radius;
            
            // 混合粒子和元素粒子
            if (world.random.nextBoolean()) {
                world.spawnParticles((ParticleEffect) type.getPrimaryParticle(),
                    x, y, z, 1, 0, -0.02, 0, 0.5);
            } else {
                world.spawnParticles((ParticleEffect) type.getSecondaryParticle(),
                    x, y, z, 1, 0, -0.01, 0, 0.3);
            }
        }
        
        // 过载时额外效果
        if (status == TideStatus.OVERLOAD) {
            spawnOverloadEffects(world, center, concentration);
        }
    }
    
    /**
     * 过载特效
     */
    private void spawnOverloadEffects(ServerWorld world, BlockPos center, double concentration) {
        // 闪电效果
        if (world.random.nextDouble() < 0.1) {
            double x = center.getX() + world.random.nextDouble() * 16 - 8;
            double z = center.getZ() + world.random.nextDouble() * 16 - 8;
            
            world.spawnParticles(ParticleTypes.FLASH,
                x, center.getY() + 10, z, 1, 0, 0, 0, 0);
        }
        
        // 能量爆发
        if (world.random.nextDouble() < 0.2) {
            double x = center.getX() + world.random.nextDouble() * 8 - 4;
            double y = center.getY() + world.random.nextDouble() * 4;
            double z = center.getZ() + world.random.nextDouble() * 8 - 4;
            
            world.spawnParticles(ParticleTypes.END_ROD,
                x, y, z, 3, 0.5, 0.5, 0.5, 0.05);
        }
    }
    
    /**
     * 使用 Factor 时的效果（能量束）
     */
    public void spawnUseEffect(ServerWorld world, BlockPos from, BlockPos to,
                               FactorElementType type, double concentration) {
        if (!FactorParticleConfig.ENABLED) return;
        
        Vec3d start = Vec3d.ofCenter(from);
        Vec3d end = Vec3d.ofCenter(to);
        Vec3d direction = end.subtract(start).normalize();
        
        int steps = (int) start.distanceTo(end);
        int count = FactorParticleConfig.getActualCount(Math.max(5, steps / 2));
        
        for (int i = 0; i < count; i++) {
            double progress = (double) i / count;
            Vec3d pos = start.add(direction.multiply(progress * steps));
            
            // 添加随机偏移
            pos = pos.add(
                world.random.nextDouble() * 0.3 - 0.15,
                world.random.nextDouble() * 0.3 - 0.15,
                world.random.nextDouble() * 0.3 - 0.15
            );
            
            world.spawnParticles((ParticleEffect) type.getPrimaryParticle(),
                pos.x, pos.y, pos.z, 1,
                direction.x * 0.1,
                direction.y * 0.1,
                direction.z * 0.1,
                0.02);
        }
        
        // 终点爆发效果
        spawnBurstEffect(world, to, type, concentration);
    }
    
    /**
     * 爆发效果
     */
    public void spawnBurstEffect(ServerWorld world, BlockPos pos,
                                  FactorElementType type, double concentration) {
        if (!FactorParticleConfig.ENABLED) return;
        
        Vec3d center = Vec3d.ofCenter(pos);
        int count = FactorParticleConfig.getActualCount(15);
        
        for (int i = 0; i < count; i++) {
            double angle = world.random.nextDouble() * Math.PI * 2;
            double elevation = world.random.nextDouble() * Math.PI - Math.PI / 2;
            double speed = 0.1 + concentration * 0.1;
            
            double dx = Math.cos(angle) * Math.cos(elevation) * speed;
            double dy = Math.sin(elevation) * speed;
            double dz = Math.sin(angle) * Math.cos(elevation) * speed;
            
            world.spawnParticles((ParticleEffect) type.getSecondaryParticle(),
                center.x, center.y, center.z, 1, dx, dy, dz, 0.1);
        }
    }
    
    /**
     * 获取强度对应的粒子数量
     */
    private int getIntensityParticleCount(TideStatus status) {
        return switch (status) {
            case DEPLETED -> 2;
            case LOW_ENERGY -> 4;
            case STABLE -> 6;
            case HIGH_ENERGY -> 10;
            case OVERLOAD -> 15;
        };
    }
    
    /**
     * 检查是否是 Factor 相关物品
     */
    private boolean isFactorItem(ItemStack stack) {
        // 检查物品 ID 或 Data Component
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
        // 从 Data Component 读取类型，或根据物品特性推断
        var storage = stack.get(com.factorcraft.component.FactorCraftDataComponents.FACTOR_STORAGE);
        if (storage != null) {
            // 可以从 storage 中获取类型信息
            // 目前使用物品名称推断
        }
        
        // 根据物品名称推断
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