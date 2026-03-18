package com.factorcraft.module.quest.condition;

import com.factorcraft.module.factor.management.ChunkFactorManager;
import com.factorcraft.module.factor.state.ChunkFactorState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;

import java.util.Optional;

/**
 * Factor 区域探索条件 - 检测玩家是否进入指定浓度的 Factor 区域
 */
public class FactorExploreCondition implements QuestCondition {
    
    private final double minConcentration;
    private final int requiredChunks;
    private int exploredChunks;
    
    /**
     * 创建探索条件
     * @param minConcentration 最小浓度要求
     * @param requiredChunks 需要探索的区块数量
     */
    public FactorExploreCondition(double minConcentration, int requiredChunks) {
        this.minConcentration = minConcentration;
        this.requiredChunks = requiredChunks;
        this.exploredChunks = 0;
    }
    
    @Override
    public QuestConditionType getType() {
        return QuestConditionType.FACTOR_EXPLORE;
    }
    
    @Override
    public boolean check(PlayerEntity player, Object context) {
        return exploredChunks >= requiredChunks;
    }
    
    @Override
    public float getProgress(PlayerEntity player, Object context) {
        return Math.min(1.0f, (float) exploredChunks / requiredChunks);
    }
    
    @Override
    public NbtCompound toNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        nbt.putDouble("min_concentration", minConcentration);
        nbt.putInt("required", requiredChunks);
        nbt.putInt("explored", exploredChunks);
        return nbt;
    }
    
    /**
     * 检查并记录区块探索
     * @param world 世界
     * @param chunkPos 区块坐标
     * @return 是否发现符合条件的区块
     */
    public boolean checkChunk(ServerWorld world, ChunkPos chunkPos) {
        if (world == null) return false;
        
        // 通过 ChunkFactorManager 获取区块 Factor 状态
        Optional<ChunkFactorState> stateOpt = ChunkFactorManager.getState(chunkPos);
        if (stateOpt.isPresent()) {
            ChunkFactorState state = stateOpt.get();
            double concentration = state.getCurrentConcentration();
            if (concentration >= minConcentration) {
                exploredChunks++;
                return true;
            }
        }
        return false;
    }
    
    public double getMinConcentration() { return minConcentration; }
    public int getRequiredChunks() { return requiredChunks; }
    public int getExploredChunks() { return exploredChunks; }
}