package com.factorcraft.sound;

import com.factorcraft.registry.ModSounds;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * 音效播放辅助类
 * 
 * 提供统一的音效播放接口，支持：
 * - 方块位置播放
 * - 坐标位置播放
 * - 客户端/服务端安全调用
 */
public class SoundHelper {
    
    // ==================== 机器音效 ====================
    
    public static void playExtractorWork(World world, BlockPos pos) {
        playBlockSound(world, pos, ModSounds.EXTRACTOR_WORK, SoundCategory.BLOCKS, 0.5f, 1.0f);
    }
    
    public static void playSynthesizerWork(World world, BlockPos pos) {
        playBlockSound(world, pos, ModSounds.SYNTHESIZER_WORK, SoundCategory.BLOCKS, 0.5f, 1.0f);
    }
    
    public static void playConsumerWork(World world, BlockPos pos) {
        playBlockSound(world, pos, ModSounds.CONSUMER_WORK, SoundCategory.BLOCKS, 0.5f, 1.0f);
    }
    
    public static void playTransmitterWork(World world, BlockPos pos) {
        playBlockSound(world, pos, ModSounds.TRANSMITTER_WORK, SoundCategory.BLOCKS, 0.3f, 1.0f);
    }
    
    public static void playCultivatorWork(World world, BlockPos pos) {
        playBlockSound(world, pos, ModSounds.CULTIVATOR_WORK, SoundCategory.BLOCKS, 0.5f, 1.0f);
    }
    
    public static void playBreederWork(World world, BlockPos pos) {
        playBlockSound(world, pos, ModSounds.BREEDER_WORK, SoundCategory.BLOCKS, 0.5f, 1.0f);
    }
    
    // ==================== UI 音效 ====================
    
    public static void playUiButtonClick(World world, BlockPos pos) {
        playBlockSound(world, pos, ModSounds.UI_BUTTON_CLICK, SoundCategory.MASTER, 0.8f, 1.2f);
    }
    
    public static void playItemPickup(World world, BlockPos pos) {
        playBlockSound(world, pos, ModSounds.ITEM_PICKUP, SoundCategory.PLAYERS, 0.6f, 1.0f);
    }
    
    public static void playRecipeUnlock(World world, BlockPos pos) {
        playBlockSound(world, pos, ModSounds.RECIPE_UNLOCK, SoundCategory.PLAYERS, 0.7f, 1.0f);
    }
    
    public static void playFactorCollect(World world, BlockPos pos) {
        playBlockSound(world, pos, ModSounds.FACTOR_COLLECT, SoundCategory.AMBIENT, 0.6f, 1.0f);
    }
    
    // ==================== 成就音效 ====================
    
    public static void playAchievementUnlock(World world, BlockPos pos) {
        playBlockSound(world, pos, ModSounds.ACHIEVEMENT_UNLOCK, SoundCategory.PLAYERS, 0.8f, 1.0f);
    }
    
    public static void playQuestComplete(World world, BlockPos pos) {
        playBlockSound(world, pos, ModSounds.QUEST_COMPLETE, SoundCategory.PLAYERS, 0.8f, 1.0f);
    }
    
    public static void playLevelUp(World world, BlockPos pos) {
        playBlockSound(world, pos, ModSounds.LEVEL_UP, SoundCategory.PLAYERS, 0.75f, 1.0f);
    }
    
    // ==================== 环境音效 ====================
    
    public static void playFactorAmbient(World world, BlockPos pos) {
        playBlockSound(world, pos, ModSounds.FACTOR_AMBIENT, SoundCategory.AMBIENT, 0.3f, 1.0f);
    }
    
    public static void playFactorFlow(World world, BlockPos pos) {
        playBlockSound(world, pos, ModSounds.FACTOR_FLOW, SoundCategory.AMBIENT, 0.4f, 1.0f);
    }
    
    // ==================== 核心播放方法 ====================
    
    public static void playBlockSound(@Nullable World world, @Nullable BlockPos pos, 
                                       @Nullable SoundEvent sound, 
                                       SoundCategory category, float volume, float pitch) {
        if (world == null || world.isClient || pos == null || sound == null) {
            return;
        }
        world.playSound(null, pos, sound, category, volume, pitch);
    }
    
    public static void playSoundAt(@Nullable World world, double x, double y, double z, 
                                   @Nullable SoundEvent sound, SoundCategory category, 
                                   float volume, float pitch) {
        if (world == null || world.isClient || sound == null) {
            return;
        }
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.playSound(null, x, y, z, sound, category, volume, pitch);
        }
    }
    
    public static void playSoundAt(@Nullable World world, @Nullable Vec3d pos, 
                                   @Nullable SoundEvent sound, 
                                   SoundCategory category, float volume, float pitch) {
        if (pos == null) return;
        playSoundAt(world, pos.x, pos.y, pos.z, sound, category, volume, pitch);
    }
    
    public static void playMachineWork(World world, BlockPos pos, String machineType) {
        if (machineType == null) return;
        switch (machineType.toLowerCase()) {
            case "extractor" -> playExtractorWork(world, pos);
            case "synthesizer" -> playSynthesizerWork(world, pos);
            case "consumer" -> playConsumerWork(world, pos);
            case "transmitter" -> playTransmitterWork(world, pos);
            case "cultivator" -> playCultivatorWork(world, pos);
            case "breeder" -> playBreederWork(world, pos);
            default -> playBlockSound(world, pos, ModSounds.EXTRACTOR_WORK, SoundCategory.BLOCKS, 0.5f, 1.0f);
        }
    }
}