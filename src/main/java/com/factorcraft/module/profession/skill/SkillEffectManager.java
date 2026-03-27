package com.factorcraft.module.profession.skill;

import com.factorcraft.api.IFactorContainer;
import com.factorcraft.module.profession.model.PlayerProfessionData;
import com.factorcraft.module.profession.api.ProfessionAPI;
import com.factorcraft.module.technology.machine.MachineBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.util.math.ChunkPos;

import java.util.*;

/**
 * 技能效果管理器
 * 
 * 管理玩家技能的持续效果和区域效果
 */
public class SkillEffectManager {
    
    // 玩家激活的技能效果 (UUID -> 效果列表)
    private static final Map<UUID, List<ActiveSkillEffect>> ACTIVE_EFFECTS = new HashMap<>();
    
    /**
     * 激活技能效果
     */
    public static void activateEffect(ServerPlayerEntity player, String skillId, int durationTicks) {
        UUID playerId = player.getUuid();
        ACTIVE_EFFECTS.computeIfAbsent(playerId, k -> new ArrayList<>())
            .add(new ActiveSkillEffect(skillId, player.getServerWorld().getTime() + durationTicks));
    }
    
    /**
     * 检查技能效果是否激活
     */
    public static boolean isEffectActive(ServerPlayerEntity player, String skillId) {
        List<ActiveSkillEffect> effects = ACTIVE_EFFECTS.get(player.getUuid());
        if (effects == null) return false;
        
        long currentTime = player.getServerWorld().getTime();
        return effects.stream()
            .anyMatch(e -> e.skillId.equals(skillId) && e.endTime > currentTime);
    }
    
    /**
     * 清理过期效果
     */
    public static void tick(ServerPlayerEntity player) {
        List<ActiveSkillEffect> effects = ACTIVE_EFFECTS.get(player.getUuid());
        if (effects == null) return;
        
        long currentTime = player.getServerWorld().getTime();
        effects.removeIf(e -> e.endTime <= currentTime);
    }
    
    /**
     * 获取范围内方块实体
     */
    private static List<BlockEntity> getBlockEntitiesInRange(ServerWorld world, BlockPos center, int range) {
        List<BlockEntity> result = new ArrayList<>();
        Box box = new Box(center).expand(range);
        
        // 计算需要遍历的区块范围
        int minChunkX = (int) box.minX >> 4;
        int maxChunkX = (int) box.maxX >> 4;
        int minChunkZ = (int) box.minZ >> 4;
        int maxChunkZ = (int) box.maxZ >> 4;
        
        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                WorldChunk chunk = world.getChunk(cx, cz);
                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    if (box.contains(be.getPos().getX(), be.getPos().getY(), be.getPos().getZ())) {
                        result.add(be);
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * 执行能量爆发 - 为周围机器充满Factor
     */
    public static int executeEnergyBurst(ServerPlayerEntity player, int range) {
        ServerWorld world = player.getServerWorld();
        BlockPos center = player.getBlockPos();
        
        int chargedMachines = 0;
        
        for (BlockEntity be : getBlockEntitiesInRange(world, center, range)) {
            if (be instanceof IFactorContainer container) {
                double needed = container.getMaxFactorStorage() - container.getFactorStorage();
                if (needed > 0) {
                    container.addFactor(needed);
                    chargedMachines++;
                }
            }
        }
        
        return chargedMachines;
    }
    
    /**
     * 执行超频 - 返回受影响的机器数量
     */
    public static int executeOverclock(ServerPlayerEntity player, int range, int duration) {
        ServerWorld world = player.getServerWorld();
        BlockPos center = player.getBlockPos();
        
        int affectedMachines = 0;
        
        for (BlockEntity be : getBlockEntitiesInRange(world, center, range)) {
            if (be instanceof MachineBlockEntity) {
                affectedMachines++;
            }
        }
        
        // 激活超频效果
        if (affectedMachines > 0) {
            activateEffect(player, "overclock", duration);
        }
        
        return affectedMachines;
    }
    
    /**
     * 激活远程构建
     */
    public static void activateRemoteBuild(ServerPlayerEntity player, int duration) {
        activateEffect(player, "remote_build", duration);
    }
    
    /**
     * 激活工厂意志
     */
    public static void activateFactoryWill(ServerPlayerEntity player, int duration) {
        activateEffect(player, "factory_will", duration);
    }
    
    /**
     * 检查玩家是否可以远程构建
     */
    public static boolean canRemoteBuild(ServerPlayerEntity player) {
        return isEffectActive(player, "remote_build");
    }
    
    /**
     * 检查机器是否受工厂意志影响
     */
    public static boolean isFactoryWillActive(ServerPlayerEntity player) {
        return isEffectActive(player, "factory_will");
    }
    
    /**
     * 活跃技能效果记录
     */
    private static class ActiveSkillEffect {
        final String skillId;
        final long endTime;
        
        ActiveSkillEffect(String skillId, long endTime) {
            this.skillId = skillId;
            this.endTime = endTime;
        }
    }
}