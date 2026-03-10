package com.factorcraft.module.technology.multiblock;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;

/**
 * 祭坛结构定义
 */
public class AltarStructure {
    
    private final Identifier id;
    private final String type; // "extractor", "emitter", "utilizer"
    private final int tier;
    private final List<BlockPos> relativePositions;
    private final List<Identifier> requiredBlocks;
    
    public AltarStructure(Identifier id, String type, int tier) {
        this.id = id;
        this.type = type;
        this.tier = tier;
        this.relativePositions = List.of();
        this.requiredBlocks = List.of();
    }
    
    public Identifier getId() { return id; }
    public String getType() { return type; }
    public int getTier() { return tier; }
    public List<BlockPos> getRelativePositions() { return relativePositions; }
    public List<Identifier> getRequiredBlocks() { return requiredBlocks; }
    
    /**
     * 检查某个位置是否符合结构
     */
    public boolean matches(net.minecraft.world.World world, BlockPos corePos) {
        // TODO: 实现结构检测逻辑
        return true;
    }
}
