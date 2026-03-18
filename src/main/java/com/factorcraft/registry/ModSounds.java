package com.factorcraft.registry;

import com.factorcraft.FactorCraftMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 音效注册类
 * 
 * 音效分类:
 * - 机器音效: 机器工作时的循环音效
 * - UI 音效: 界面交互音效
 * - 成就音效: 任务完成/成就解锁提示音
 * - 环境音效: Factor 区域环境音
 */
public class ModSounds {
    
    // ==================== 注册表 ====================
    
    private static final Map<Identifier, SoundEvent> SOUND_EVENTS = new LinkedHashMap<>();
    
    // ==================== 机器音效 ====================
    
    /** 提取器工作音效 */
    public static final SoundEvent EXTRACTOR_WORK = register("extractor_work");
    
    /** 合成器工作音效 */
    public static final SoundEvent SYNTHESIZER_WORK = register("synthesizer_work");
    
    /** 消耗器工作音效 */
    public static final SoundEvent CONSUMER_WORK = register("consumer_work");
    
    /** 传递器工作音效 */
    public static final SoundEvent TRANSMITTER_WORK = register("transmitter_work");
    
    /** 培育器工作音效 */
    public static final SoundEvent CULTIVATOR_WORK = register("cultivator_work");
    
    /** 繁殖器工作音效 */
    public static final SoundEvent BREEDER_WORK = register("breeder_work");
    
    // ==================== UI 音效 ====================
    
    /** UI 按钮点击 */
    public static final SoundEvent UI_BUTTON_CLICK = register("ui_button_click");
    
    /** 物品拾取 */
    public static final SoundEvent ITEM_PICKUP = register("item_pickup");
    
    /** 配方解锁 */
    public static final SoundEvent RECIPE_UNLOCK = register("recipe_unlock");
    
    /** Factor 收集 */
    public static final SoundEvent FACTOR_COLLECT = register("factor_collect");
    
    // ==================== 成就音效 ====================
    
    /** 成就解锁 */
    public static final SoundEvent ACHIEVEMENT_UNLOCK = register("achievement_unlock");
    
    /** 任务完成 */
    public static final SoundEvent QUEST_COMPLETE = register("quest_complete");
    
    /** 等级提升 */
    public static final SoundEvent LEVEL_UP = register("level_up");
    
    // ==================== 环境音效 ====================
    
    /** 高浓度 Factor 区域环境音 */
    public static final SoundEvent FACTOR_AMBIENT = register("factor_ambient");
    
    /** Factor 流动声 */
    public static final SoundEvent FACTOR_FLOW = register("factor_flow");
    
    // ==================== 注册表（移至文件顶部）====================
    
    /**
     * 注册音效事件
     */
    private static SoundEvent register(String name) {
        Identifier id = Identifier.of(FactorCraftMod.MOD_ID, name);
        SoundEvent soundEvent = SoundEvent.of(id);
        SOUND_EVENTS.put(id, soundEvent);
        return soundEvent;
    }
    
    /**
     * 初始化并注册所有音效
     */
    public static void initialize() {
        for (Map.Entry<Identifier, SoundEvent> entry : SOUND_EVENTS.entrySet()) {
            Registry.register(Registries.SOUND_EVENT, entry.getKey(), entry.getValue());
        }
        FactorCraftMod.LOGGER.info("[FactorCraft] 注册 {} 个音效事件", SOUND_EVENTS.size());
    }
    
    /**
     * 获取所有注册的音效（用于调试）
     */
    public static Map<Identifier, SoundEvent> getAllSounds() {
        return new LinkedHashMap<>(SOUND_EVENTS);
    }
}