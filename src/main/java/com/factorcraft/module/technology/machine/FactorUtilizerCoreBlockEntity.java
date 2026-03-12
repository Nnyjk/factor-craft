package com.factorcraft.module.technology.machine;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Factor 使用者核心 - 消耗 Factor 制作物品
 */
public class FactorUtilizerCoreBlockEntity extends FactorMachineBlockEntity {
    
    private int craftProgress;
    private double factorCost;
    private String currentRecipe;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    
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
        // 完成合成，产出物品
        if (currentRecipe != null) {
            // 简化实现：根据配方 ID 产出物品
            ItemStack output = resolveRecipeOutput(currentRecipe);
            if (!output.isEmpty()) {
                // 放入输出槽
                ItemStack existing = inventory.get(1);
                if (existing.isEmpty()) {
                    inventory.set(1, output);
                } else if (ItemStack.areItemsEqual(existing, output)) {
                    existing.increment(output.getCount());
                }
            }
        }
        
        currentRecipe = null;
        factorCost = 0;
    }
    
    private ItemStack resolveRecipeOutput(String recipeId) {
        // 简化配方解析 - 实际应从配方管理器获取
        return switch (recipeId) {
            case "factor_shard_t1" -> new ItemStack(net.minecraft.item.Items.AMETHYST_SHARD, 1);
            case "factor_shard_t2" -> new ItemStack(net.minecraft.item.Items.AMETHYST_SHARD, 2);
            case "resonance_core" -> new ItemStack(net.minecraft.item.Items.ECHO_SHARD, 1);
            default -> ItemStack.EMPTY;
        };
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
        Inventories.writeNbt(nbt, inventory, registries);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        craftProgress = nbt.getInt("CraftProgress");
        factorCost = nbt.getDouble("FactorCost");
        String recipe = nbt.getString("CurrentRecipe");
        currentRecipe = recipe.isEmpty() ? null : recipe;
        Inventories.readNbt(nbt, inventory, registries);
    }
    
    public DefaultedList<ItemStack> getInventory() { return inventory; }
}
