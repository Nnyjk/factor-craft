package com.factorcraft.module.creature.mutation;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.factor.FactorService;
import com.factorcraft.module.factor.TideStatus;
import com.factorcraft.module.vfx.particle.FactorParticleTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.*;

/**
 * 生物变异管理器
 * 
 * 管理世界中所有生物的变异状态
 */
public class MutationManager {
    
    /** Factor 服务 */
    private final FactorService factorService;
    
    /** 变异状态缓存 */
    private final Map<UUID, MutatedCreatureState> creatureStates = new HashMap<>();
    
    /** 清理间隔（ticks） */
    private static final int CLEANUP_INTERVAL = 100;
    
    /** 粒子生成间隔（ticks） */
    private static final int PARTICLE_INTERVAL = 20;
    
    /** 上次清理时间 */
    private long lastCleanupTime = 0;
    
    /** 上次粒子生成时间 */
    private long lastParticleTime = 0;
    
    public MutationManager() {
        this.factorService = FactorService.getInstance();
    }
    
    /**
     * Tick 更新
     */
    public void tick(ServerWorld world) {
        long currentTime = world.getTime();
        
        // 定期清理过期状态
        if (currentTime - lastCleanupTime > CLEANUP_INTERVAL) {
            cleanupExpired(world);
            lastCleanupTime = currentTime;
        }
        
        // 定期生成变异粒子效果
        if (currentTime - lastParticleTime > PARTICLE_INTERVAL) {
            spawnMutationParticles(world);
            lastParticleTime = currentTime;
        }
        
        // 更新所有变异生物的状态
        for (MutatedCreatureState state : creatureStates.values()) {
            if (state.isExpired(currentTime)) {
                // 过期，移除状态
                creatureStates.remove(state.getCreatureId());
            }
        }
    }
    
    /**
     * 检查并应用变异
     */
    public void tryApplyMutation(LivingEntity creature, ServerWorld world) {
        if (world.isClient) return;
        
        // 获取世界 Factor 浓度
        double concentration = getWorldConcentration(world, creature.getBlockPos());
        TideStatus status = TideStatus.fromConcentration(concentration);
        
        // 只有高浓度区域才可能变异
        if (status != TideStatus.HIGH_ENERGY && status != TideStatus.OVERLOAD) {
            return;
        }
        
        // 检查是否已有变异
        MutatedCreatureState state = creatureStates.get(creature.getUuid());
        if (state != null && state.hasMutations()) {
            // 已有变异，不再重复应用
            return;
        }
        
        // 计算变异概率
        double baseChance = status == TideStatus.OVERLOAD ? 0.60 : 0.30;
        double randomRoll = world.getRandom().nextDouble();
        
        if (randomRoll <= baseChance) {
            // 触发变异
            applyRandomMutation(creature, world, status);
        }
    }
    
    /**
     * 应用随机变异
     */
    private void applyRandomMutation(LivingEntity creature, ServerWorld world, TideStatus status) {
        Random random = world.getRandom();
        
        // 根据浓度决定稀有变异概率
        boolean allowRare = status == TideStatus.OVERLOAD && random.nextDouble() < 0.20;
        
        // 获取可用变异列表
        List<MutationEffect> availableMutations = allowRare 
            ? new ArrayList<>(MutationRegistry.getAll()) 
            : MutationRegistry.getByRarity(false);
        
        if (availableMutations.isEmpty()) {
            return;
        }
        
        // 随机选择一个变异
        MutationEffect mutation = availableMutations.get(random.nextInt(availableMutations.size()));
        
        // 创建变异状态
        MutatedCreatureState state = creatureStates.computeIfAbsent(
            creature.getUuid(), 
            id -> new MutatedCreatureState(id, world.getTime())
        );
        
        // 应用变异（永久或临时）
        boolean permanent = mutation.isRare() && status == TideStatus.OVERLOAD;
        long duration = permanent ? 0 : 24000L; // 永久或 1 游戏日
        
        state.addMutation(Identifier.of(FactorCraftMod.MOD_ID, mutation.id()), duration, permanent);
        
        // 应用效果
        applyMutationEffects(creature, mutation);
        
        FactorCraftMod.LOGGER.debug("Applied mutation {} to creature {}", mutation.id(), creature.getType());
    }
    
    /**
     * 应用变异效果到生物
     */
    private void applyMutationEffects(LivingEntity creature, MutationEffect mutation) {
        // 应用状态效果
        for (StatusEffectInstance effect : mutation.effects()) {
            creature.addStatusEffect(effect);
        }
        
        // 修改属性（Minecraft 1.21.4 API）
        applyAttributeModifier(creature, EntityAttributes.ATTACK_DAMAGE, mutation.damageModifier());
        applyAttributeModifier(creature, EntityAttributes.MAX_HEALTH, mutation.healthModifier());
        applyAttributeModifier(creature, EntityAttributes.MOVEMENT_SPEED, mutation.speedModifier());
    }
    
    /**
     * 应用属性修正
     */
    private void applyAttributeModifier(LivingEntity creature, net.minecraft.registry.entry.RegistryEntry<net.minecraft.entity.attribute.EntityAttribute> attribute, double modifier) {
        EntityAttributeInstance instance = creature.getAttributeInstance(attribute);
        if (instance != null) {
            // 移除旧的修正（如果有）
            // 注意：实际实现需要跟踪 UUID 以避免重复应用
            instance.setBaseValue(instance.getBaseValue() * modifier);
        }
    }
    
    /**
     * 移除变异效果
     */
    public void removeMutations(LivingEntity creature) {
        MutatedCreatureState state = creatureStates.remove(creature.getUuid());
        if (state != null) {
            // TODO: 恢复原始属性
            FactorCraftMod.LOGGER.debug("Removed mutations from creature {}", creature.getType());
        }
    }
    
    /**
     * 获取生物的变异状态
     */
    public Optional<MutatedCreatureState> getMutationState(UUID creatureId) {
        return Optional.ofNullable(creatureStates.get(creatureId));
    }
    
    /**
     * 获取所有变异生物
     */
    public Collection<MutatedCreatureState> getAllMutatedCreatures() {
        return Collections.unmodifiableCollection(creatureStates.values());
    }
    
    /**
     * 清理过期状态
     */
    private void cleanupExpired(ServerWorld world) {
        long currentTime = world.getTime();
        creatureStates.entrySet().removeIf(entry -> 
            entry.getValue().isExpired(currentTime)
        );
    }
    
    /**
     * 获取世界 Factor 浓度
     */
    private double getWorldConcentration(ServerWorld world, net.minecraft.util.math.Vec3i pos) {
        // 使用 FactorService 获取真实浓度
        try {
            return factorService.getFactor(world);
        } catch (Exception e) {
            FactorCraftMod.LOGGER.warn("Failed to get factor concentration", e);
            return 0.5; // 默认稳定浓度
        }
    }
    
    /**
     * 生成变异生物粒子效果
     */
    private void spawnMutationParticles(ServerWorld world) {
        // 简化实现：遍历世界中的所有生物
        for (MutatedCreatureState state : creatureStates.values()) {
            if (!state.hasMutations()) {
                continue;
            }
            
            // 尝试通过 UUID 获取生物
            Entity entity = world.getEntity(state.getCreatureId());
            if (entity instanceof LivingEntity creature) {
                // 为每个变异生成粒子
                for (Identifier mutationId : state.getActiveMutations()) {
                    MutationRegistry.get(mutationId).ifPresent(mutation -> {
                        spawnCreatureParticles(world, creature, mutation);
                    });
                }
            }
        }
    }
    
    /**
     * 为单个生物生成粒子
     */
    private void spawnCreatureParticles(ServerWorld world, LivingEntity creature, MutationEffect mutation) {
        if (world.isClient) {
            return;
        }
        
        Random random = world.getRandom();
        Vec3d pos = creature.getPos();
        
        // 生成环绕粒子
        int particleCount = 3;
        for (int i = 0; i < particleCount; i++) {
            double angle = (i / (double) particleCount) * Math.PI * 2 + random.nextDouble() * 0.5;
            double radius = 0.5 + random.nextDouble() * 0.3;
            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;
            double offsetY = 0.5 + random.nextDouble() * 1.0;
            
            Vec3d particlePos = pos.add(offsetX, offsetY, offsetZ);
            
            // 使用 Factor 粒子效果
            world.spawnParticles(
                FactorParticleTypes.EXTRACTION,
                particlePos.x, particlePos.y, particlePos.z,
                1,
                offsetX * 0.02, 0.01, offsetZ * 0.02, 0.0
            );
        }
    }
    
    /**
     * 清除所有状态（用于测试或重置）
     */
    public void clear() {
        creatureStates.clear();
    }
    
    /**
     * 获取变异生物数量
     */
    public int getMutatedCreatureCount() {
        return creatureStates.size();
    }
}
