package com.factorcraft.module.technology.machine;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Factor 使用者核心 - 消耗 Factor 制作物品
 */
public class FactorUtilizerCoreBlockEntity extends FactorMachineBlockEntity {
    
    private int craftProgress;
    private double factorCost;
    private String currentRecipe;
    
    public FactorUtilizerCoreBlockEntity(BlockPos pos, BlockState state) {
        super(null, pos, state);
        this.craftProgress = 0;
        this.factorCost = 0.0;
        this.currentRecipe = null;
    }
    
    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;
        
        if (currentRecipe != null && factorCost > 0) {
            craftProgress += 1;
            if (craftProgress >= 100) {
                craftProgress = 0;
                completeCrafting();
            }
        }
        
        markDirty();
    }
    
    private void completeCrafting() {
        // TODO: 完成合成，产出物品
        currentRecipe = null;
        factorCost = 0;
    }
    
    public void startCrafting(String recipeId, double cost) {
        this.currentRecipe = recipeId;
        this.factorCost = cost;
        this.craftProgress = 0;
    }
    
    public String getCurrentRecipe() { return currentRecipe; }
    public double getFactorCost() { return factorCost; }
    public int getCraftProgress() { return craftProgress; }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putInt("CraftProgress", craftProgress);
        nbt.putDouble("FactorCost", factorCost);
        nbt.putString("CurrentRecipe", currentRecipe != null ? currentRecipe : "");
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        craftProgress = nbt.getInt("CraftProgress");
        factorCost = nbt.getDouble("FactorCost");
        String recipe = nbt.getString("CurrentRecipe");
        currentRecipe = recipe.isEmpty() ? null : recipe;
    }
}
