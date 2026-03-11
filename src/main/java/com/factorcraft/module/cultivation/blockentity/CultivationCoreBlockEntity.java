package com.factorcraft.module.cultivation.blockentity;

import com.factorcraft.module.factor.management.ChunkFactorManager;
import com.factorcraft.module.material.trait.TraitInstance;
import com.factorcraft.module.material.trait.TraitService;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.List;

public class CultivationCoreBlockEntity extends BlockEntity {
    private ItemStack targetItem = ItemStack.EMPTY;
    private int processTime = 0;
    private static final int MAX_PROCESS_TIME = 200;
    private static final double FACTOR_COST_BASE = 10.0;
    
    public CultivationCoreBlockEntity(BlockPos pos, BlockState state) {
        super(null, pos, state);
    }
    
    public void setTargetItem(ItemStack stack) {
        this.targetItem = stack.copy();
        this.processTime = 0;
        markDirty();
    }
    
    public ItemStack getTargetItem() {
        return targetItem;
    }
    
    public static void tick(World world, BlockPos pos, BlockState state, CultivationCoreBlockEntity blockEntity) {
        if (world.isClient || blockEntity.targetItem.isEmpty()) return;
        
        blockEntity.processTime++;
        
        if (blockEntity.processTime >= MAX_PROCESS_TIME) {
            blockEntity.performInjection(world, pos);
            blockEntity.processTime = 0;
            blockEntity.markDirty();
        }
    }
    
    private void performInjection(World world, BlockPos pos) {
        if (targetItem.isEmpty()) return;
        
        ChunkPos chunkPos = new ChunkPos(pos);
        double availableFactor = ChunkFactorManager.getState(chunkPos)
            .map(state -> state.getCurrentConcentration())
            .orElse(0.0);
        
        if (availableFactor < FACTOR_COST_BASE) return;
        
        var random = world.random;
        List<TraitInstance> newTraits = TraitService.generateRandomTraits(1, 1, random, 0.7);
        
        if (!newTraits.isEmpty()) {
            TraitInstance newTrait = newTraits.get(0);
            if (TraitService.addTrait(targetItem, newTrait.traitId(), newTrait.level())) {
                ChunkFactorManager.extractFactor(world, chunkPos, FACTOR_COST_BASE);
            }
        }
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!targetItem.isEmpty()) {
            nbt.put("targetItem", targetItem.toNbt(registryLookup));
        }
        nbt.putInt("processTime", processTime);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("targetItem")) {
            targetItem = ItemStack.fromNbt(registryLookup, nbt.getCompound("targetItem")).orElse(ItemStack.EMPTY);
        }
        processTime = nbt.getInt("processTime");
    }
}