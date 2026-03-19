package com.factorcraft.module.loot.handler;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.loot.ResonanceCoreItem;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraft.util.math.random.Random;

/**
 * 方块掉落处理器 - 处理共振核心掉落
 */
public final class BlockDropHandler {
    
    private static final double RESONANCE_CORE_CHANCE = 0.02;
    
    private BlockDropHandler() {}
    
    public static void register() {
        FactorCraftMod.LOGGER.info("[FactorCraft:Loot] 注册方块掉落处理器");
        
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (world.isClient()) return;
            handleBlockBreak((ServerWorld) world, player, pos, state);
        });
    }
    
    private static void handleBlockBreak(ServerWorld world, PlayerEntity player, BlockPos pos, net.minecraft.block.BlockState state) {
        String blockId = state.getBlock().toString();
        Random random = world.getRandom();
        
        boolean isSpecial = blockId.contains("factor_ore") || 
                           blockId.contains("resonance") || 
                           blockId.contains("crystalline");
        
        double chance = isSpecial ? RESONANCE_CORE_CHANCE * 3 : RESONANCE_CORE_CHANCE * 0.1;
        
        if (random.nextDouble() < chance) {
            ItemStack core = ResonanceCoreItem.createCore(1);
            ItemEntity item = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, core);
            world.spawnEntity(item);
        }
    }
}