package com.factorcraft.module.technology.item;

import com.factorcraft.component.FactorCraftDataComponents;
import com.factorcraft.component.type.FactorStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Factor 电池 - 便携式 Factor 存储设备
 * 
 * 支持 5 个等级：
 * - T1: 1,000 容量
 * - T2: 5,000 容量
 * - T3: 25,000 容量
 * - T4: 100,000 容量
 * - T5: 1,000,000 容量
 * 
 * 使用方式：
 * - Shift+右键机器：从机器抽取 Factor
 * - 右键机器：向机器注入 Factor
 * - 手持显示当前存储量和类型
 */
public class FactorBatteryItem extends Item {
    
    public final BatteryTier tier;
    
    public FactorBatteryItem(BatteryTier tier, Settings settings) {
        super(settings.maxCount(1));
        this.tier = tier;
    }
    
    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        
        // 显示电池信息
        displayBatteryInfo(user, stack);
        
        return ActionResult.SUCCESS;
    }
    
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        
        if (!(world instanceof ServerWorld serverWorld)) {
            return ActionResult.PASS;
        }
        
        // Shift+右键：从机器抽取 Factor
        if (player.isSneaking()) {
            // TODO: 实现从相邻机器抽取 Factor
            player.sendMessage(Text.literal("从机器抽取 Factor...").formatted(Formatting.YELLOW), false);
            return ActionResult.SUCCESS;
        }
        
        // 右键：向机器注入 Factor
        // TODO: 实现向相邻机器注入 Factor
        player.sendMessage(Text.literal("向机器注入 Factor...").formatted(Formatting.YELLOW), false);
        return ActionResult.SUCCESS;
    }
    
    /**
     * 向电池充能
     * 
     * @param stack 电池物品
     * @param amount 充能量
     * @return 实际充入的量
     */
    public double charge(ItemStack stack, double amount) {
        FactorStorage storage = stack.get(FactorCraftDataComponents.FACTOR_STORAGE);
        if (storage == null) {
            storage = FactorStorage.empty();
        }
        FactorStorage updated = storage.add(amount, tier.maxCapacity);
        stack.set(FactorCraftDataComponents.FACTOR_STORAGE, updated);
        return updated.amount() - storage.amount();
    }
    
    /**
     * 从电池放能
     * 
     * @param stack 电池物品
     * @param amount 放能量
     * @return 实际放出的量
     */
    public double discharge(ItemStack stack, double amount) {
        FactorStorage storage = stack.get(FactorCraftDataComponents.FACTOR_STORAGE);
        if (storage == null) {
            return 0.0;
        }
        FactorStorage updated = storage.remove(amount);
        stack.set(FactorCraftDataComponents.FACTOR_STORAGE, updated);
        return storage.amount() - updated.amount();
    }
    
    /**
     * 获取当前存储量
     * 
     * @param stack 电池物品
     * @return 当前 Factor 量
     */
    public double getStoredAmount(ItemStack stack) {
        FactorStorage storage = stack.get(FactorCraftDataComponents.FACTOR_STORAGE);
        if (storage == null) {
            return 0.0;
        }
        return storage.amount();
    }
    
    /**
     * 获取剩余容量
     * 
     * @param stack 电池物品
     * @return 剩余容量
     */
    public double getRemainingCapacity(ItemStack stack) {
        return tier.maxCapacity - getStoredAmount(stack);
    }
    
    /**
     * 显示电池信息
     */
    private void displayBatteryInfo(PlayerEntity player, ItemStack stack) {
        FactorStorage storage = stack.get(FactorCraftDataComponents.FACTOR_STORAGE);
        if (storage == null) {
            storage = FactorStorage.empty();
        }
        
        double percentage = storage.getPercentage(tier.maxCapacity);
        Formatting percentColor = getPercentageColor(percentage);
        
        player.sendMessage(Text.literal("=== Factor 电池 ===").formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("等级：" + tier.displayName).formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("容量：" + formatNumber(tier.maxCapacity)), false);
        player.sendMessage(Text.literal("存储：" + formatNumber(storage.amount()) + " / " + formatNumber(tier.maxCapacity)).formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("电量：" + String.format("%.1f", percentage) + "%").formatted(percentColor), false);
        player.sendMessage(Text.literal("").formatted(Formatting.RESET), false);
        player.sendMessage(Text.literal("提示：").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("  - Shift+右键机器：抽取 Factor").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("  - 右键机器：注入 Factor").formatted(Formatting.GRAY), false);
    }
    
    /**
     * 根据电量百分比获取颜色
     */
    private Formatting getPercentageColor(double percentage) {
        if (percentage < 20.0) {
            return Formatting.RED;
        } else if (percentage < 40.0) {
            return Formatting.GOLD;
        } else if (percentage < 60.0) {
            return Formatting.YELLOW;
        } else if (percentage < 80.0) {
            return Formatting.GREEN;
        } else {
            return Formatting.DARK_GREEN;
        }
    }
    
    /**
     * 格式化数字（带 K/M 单位）
     */
    private String formatNumber(double value) {
        if (value >= 1_000_000) {
            return String.format("%.2fM", value / 1_000_000);
        } else if (value >= 1_000) {
            return String.format("%.2fK", value / 1_000);
        } else {
            return String.format("%.0f", value);
        }
    }
    
    /**
     * 电池等级
     */
    public enum BatteryTier {
        T1("T1 基础电池", 1_000),
        T2("T2 进阶电池", 5_000),
        T3("T3 高级电池", 25_000),
        T4("T4 精英电池", 100_000),
        T5("T5 终极电池", 1_000_000);
        
        public final String displayName;
        public final double maxCapacity;
        
        BatteryTier(String displayName, double maxCapacity) {
            this.displayName = displayName;
            this.maxCapacity = maxCapacity;
        }
    }
}
