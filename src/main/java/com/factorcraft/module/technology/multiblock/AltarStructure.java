package com.factorcraft.module.technology.multiblock;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

/**
 * 祭坛结构定义
 * 
 * 表示一个完整的多方块结构
 */
public class AltarStructure {
    
    private final Identifier id;
    private final String type; // "extractor", "emitter", "utilizer"
    private final int tier;
    private final Map<BlockPos, String> blockPositions; // 相对位置 -> 方块 ID
    private final Map<String, Object> properties;
    
    public AltarStructure(Identifier id, String type, int tier, 
                         Map<BlockPos, String> blockPositions,
                         Map<String, Object> properties) {
        this.id = id;
        this.type = type;
        this.tier = tier;
        this.blockPositions = blockPositions;
        this.properties = properties;
    }
    
    // Getters
    public Identifier getId() { return id; }
    public String getType() { return type; }
    public int getTier() { return tier; }
    public Map<BlockPos, String> getBlockPositions() { return blockPositions; }
    public Map<String, Object> getProperties() { return properties; }
    
    /**
     * 获取属性值
     */
    public double getProperty(String key, double defaultValue) {
        Object value = properties.get(key);
        return value != null ? ((Number) value).doubleValue() : defaultValue;
    }
    
    /**
     * 检查某个位置是否符合结构
     * 
     * @param world 世界
     * @param corePos 核心方块位置
     * @return 是否匹配
     */
    public boolean matches(World world, BlockPos corePos) {
        if (world == null || corePos == null) return false;
        
        for (Map.Entry<BlockPos, String> entry : blockPositions.entrySet()) {
            BlockPos relativePos = entry.getKey();
            String expectedBlockId = entry.getValue();
            
            // 计算实际位置
            BlockPos actualPos = corePos.add(relativePos);
            
            // 获取实际方块
            Block actualBlock = world.getBlockState(actualPos).getBlock();
            Identifier actualBlockId = Registries.BLOCK.getId(actualBlock);
            
            // 检查是否匹配
            if (!actualBlockId.toString().equals(expectedBlockId)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 获取结构的所有相对位置
     */
    public Iterable<BlockPos> getRelativePositions() {
        return blockPositions.keySet();
    }
    
    /**
     * 获取指定相对位置所需的方块 ID
     */
    public String getRequiredBlockId(BlockPos relativePos) {
        return blockPositions.get(relativePos);
    }
    
    @Override
    public String toString() {
        return String.format("AltarStructure[id=%s, type=%s, tier=%d, blocks=%d]", 
            id, type, tier, blockPositions.size());
    }
}