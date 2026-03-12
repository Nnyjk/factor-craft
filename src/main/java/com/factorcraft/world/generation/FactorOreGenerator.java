package com.factorcraft.world.generation;

import com.factorcraft.module.factor.management.ChunkFactorManager;
import com.factorcraft.module.factor.state.ChunkFactorState;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;

import java.util.Random;

/**
 * Factor 矿脉生成器
 * 在新区块生成时初始化 Factor 状态
 */
public class FactorOreGenerator {
    
    // 生成配置
    private static final double BASE_SPAWN_CHANCE = 0.15;
    private static final double HIGH_CONCENTRATION_CHANCE = 0.05;
    private static final double HIGH_CONCENTRATION_MIN = 100.0;
    private static final double NORMAL_CONCENTRATION_MIN = 50.0;
    private static final double NORMAL_CONCENTRATION_MAX = 80.0;
    
    /**
     * 为区块生成初始 Factor 浓度
     */
    public static void generateForChunk(StructureWorldAccess world, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        
        // 检查是否已存在
        if (ChunkFactorManager.getState(chunkPos).isPresent()) {
            return;
        }
        
        Random random = new Random(chunkPos.x * 31L + chunkPos.z * 17L);
        double concentration = calculateConcentration(world, chunkPos, random);
        
        // 创建区块状态
        ChunkFactorState state = new ChunkFactorState(concentration);
        // TODO: 实现区块状态存储
    }
    
    /**
     * 计算区块 Factor 浓度
     */
    private static double calculateConcentration(StructureWorldAccess world, ChunkPos pos, Random random) {
        // 基础浓度
        double base = 40.0 + random.nextDouble() * 30.0;
        
        // 群系影响
        String biomeId = world.getBiome(pos.getStartPos()).getKey().map(k -> k.getValue().toString()).orElse("unknown");
        base += getBiomeModifier(biomeId);
        
        // 高浓度区域（稀有）
        if (random.nextDouble() < HIGH_CONCENTRATION_CHANCE) {
            base = HIGH_CONCENTRATION_MIN + random.nextDouble() * 50.0;
        }
        
        // 正常随机波动
        if (random.nextDouble() < BASE_SPAWN_CHANCE) {
            base = NORMAL_CONCENTRATION_MIN + random.nextDouble() * (NORMAL_CONCENTRATION_MAX - NORMAL_CONCENTRATION_MIN);
        }
        
        return Math.max(10.0, Math.min(150.0, base));
    }
    
    /**
     * 获取群系影响值
     */
    private static double getBiomeModifier(String biomeId) {
        // 简化版：根据群系 ID 调整
        if (biomeId.contains("nether")) return 30.0;
        if (biomeId.contains("end")) return 50.0;
        if (biomeId.contains("desert")) return -10.0;
        if (biomeId.contains("ocean")) return 5.0;
        if (biomeId.contains("mountain")) return 15.0;
        return 0.0;
    }
    
    /**
     * 检查是否应该生成 Factor 晶体矿脉
     */
    public static boolean shouldSpawnCrystalVein(Random random, double concentration) {
        // 高浓度区域更容易生成
        double chance = BASE_SPAWN_CHANCE * (1.0 + (concentration / 100.0));
        return random.nextDouble() < chance;
    }
}