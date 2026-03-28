package com.factorcraft.module.cycle.energy.item;

import com.factorcraft.component.FactorCraftDataComponents;
import com.factorcraft.component.type.FactorInjection;
import com.factorcraft.FactorCraftMod;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * Factor 注射器
 * 
 * 用于为工具/装备注入 Factor，获得临时特殊能力
 * 
 * 使用方式：
 * 1. 手持注射器
 * 2. 右键点击要注射的工具/装备
 * 3. 消耗注射器耐久
 * 4. 工具获得临时增益效果
 */
public class FactorInjectorItem extends Item {
    
    public static final RegistryKey<Item> FACTOR_INJECTOR_KEY = RegistryKey.of(
        RegistryKeys.ITEM,
        Identifier.of(FactorCraftMod.MOD_ID, "factor_injector")
    );
    
    private static final int MAX_DURABILITY = 64;
    private static final int BASE_DURATION = 6000; // 5 分钟 = 6000 ticks
    
    public FactorInjectorItem() {
        super(new Item.Settings()
            .registryKey(FACTOR_INJECTOR_KEY)
            .maxCount(1)
            .maxDamage(MAX_DURABILITY));
    }
    
    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        
        // 检查注射器耐久
        if (stack.getDamage() >= MAX_DURABILITY - 1) {
            player.sendMessage(Text.translatable("item.factorcraft.factor_injector.empty"), true);
            return ActionResult.FAIL;
        }
        
        // 检查另一只手的物品
        Hand otherHand = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
        ItemStack targetStack = player.getStackInHand(otherHand);
        
        if (targetStack.isEmpty()) {
            player.sendMessage(Text.translatable("item.factorcraft.factor_injector.no_target"), true);
            return ActionResult.FAIL;
        }
        
        // 检查目标物品是否为工具/装备
        int boostType = getBoostType(targetStack);
        if (boostType < 0) {
            player.sendMessage(Text.translatable("item.factorcraft.factor_injector.invalid_target"), true);
            return ActionResult.FAIL;
        }
        
        // 创建注射组件
        FactorInjection injection = new FactorInjection(
            world.getTime(),
            BASE_DURATION,
            boostType
        );
        
        // 应用注射效果
        targetStack.set(FactorCraftDataComponents.FACTOR_INJECTION, injection);
        
        // 消耗注射器耐久
        stack.damage(1, player, hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        
        // 发送成功消息
        player.sendMessage(Text.translatable("item.factorcraft.factor_injector.success", 
            getBoostTypeName(boostType)), true);
        
        return ActionResult.SUCCESS;
    }
    
    /**
     * 获取增益类型
     * @return 0=工具，1=武器，2=盔甲，-1=无效
     */
    private int getBoostType(ItemStack stack) {
        Item item = stack.getItem();
        
        // 检查是否为工具 (镐、斧、铲、锄)
        if (item instanceof PickaxeItem || item instanceof AxeItem || 
            item instanceof ShovelItem || item instanceof HoeItem) {
            return 0;
        }
        
        // 检查是否为武器
        if (item instanceof SwordItem || item instanceof BowItem || item instanceof CrossbowItem) {
            return 1;
        }
        
        // 检查是否为盔甲
        if (item instanceof ArmorItem) {
            return 2;
        }
        
        return -1;
    }
    
    /**
     * 获取增益类型名称
     */
    private String getBoostTypeName(int type) {
        return switch (type) {
            case 0 -> "tool";
            case 1 -> "weapon";
            case 2 -> "armor";
            default -> "unknown";
        };
    }
}
