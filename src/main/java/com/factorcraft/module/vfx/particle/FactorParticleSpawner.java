package com.factorcraft.module.vfx.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

/**
 * Factor 粒子生成器
 * 
 * 提供便捷的粒子生成方法
 */
public class FactorParticleSpawner {
    
    private static final Random random = Random.create();
    
    /**
     * 生成提取器粒子（向上飘散）
     */
    public static void spawnExtractionParticles(ServerWorld world, BlockPos pos, int count, double factorAmount) {
        if (!FactorParticleConfig.shouldSpawn(getDistanceToNearestPlayer(world, pos))) {
            return;
        }
        
        int actualCount = FactorParticleConfig.getActualCount(count);
        double speed = 0.05 + (factorAmount / 1000.0) * 0.1;
        
        for (int i = 0; i < actualCount; i++) {
            double offsetX = random.nextDouble() * 0.8 - 0.4;
            double offsetY = random.nextDouble() * 0.5;
            double offsetZ = random.nextDouble() * 0.8 - 0.4;
            
            Vec3d particlePos = Vec3d.of(pos).add(0.5, 0.5, 0.5).add(offsetX, offsetY, offsetZ);
            
            world.spawnParticles(
                FactorParticleTypes.EXTRACTION,
                particlePos.x, particlePos.y, particlePos.z,
                1,
                offsetX * 0.1, speed, offsetZ * 0.1,
                0.0
            );
        }
    }
    
    /**
     * 生成合成器粒子（向中心汇聚）
     */
    public static void spawnSynthesisParticles(ServerWorld world, BlockPos pos, int count, double progress) {
        if (!FactorParticleConfig.shouldSpawn(getDistanceToNearestPlayer(world, pos))) {
            return;
        }
        
        int actualCount = FactorParticleConfig.getActualCount(count);
        double speed = 0.02 + progress * 0.08;
        
        for (int i = 0; i < actualCount; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double radius = 0.5 + random.nextDouble() * 0.5;
            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;
            double offsetY = random.nextDouble() * 0.8;
            
            Vec3d particlePos = Vec3d.of(pos).add(0.5, 0.5, 0.5).add(offsetX, offsetY, offsetZ);
            
            world.spawnParticles(
                FactorParticleTypes.SYNTHESIS,
                particlePos.x, particlePos.y, particlePos.z,
                1,
                -offsetX * speed, -0.02, -offsetZ * speed,
                0.0
            );
        }
    }
    
    /**
     * 生成传递器粒子（束流效果）
     */
    public static void spawnTransmissionParticles(ServerWorld world, BlockPos from, BlockPos to, int count) {
        if (!FactorParticleConfig.shouldSpawn(getDistanceToNearestPlayer(world, from))) {
            return;
        }
        
        int actualCount = FactorParticleConfig.getActualCount(count);
        Vec3d start = Vec3d.of(from).add(0.5, 0.5, 0.5);
        Vec3d end = Vec3d.of(to).add(0.5, 0.5, 0.5);
        
        for (int i = 0; i < actualCount; i++) {
            double t = random.nextDouble();
            Vec3d particlePos = start.lerp(end, t);
            
            double spread = 0.1;
            world.spawnParticles(
                FactorParticleTypes.TRANSMISSION,
                particlePos.x, particlePos.y, particlePos.z,
                1,
                spread, spread, spread,
                0.0
            );
        }
    }
    
    /**
     * 生成培育器粒子（环绕效果）
     */
    public static void spawnCultivationParticles(ServerWorld world, BlockPos pos, int count, boolean success) {
        if (!FactorParticleConfig.shouldSpawn(getDistanceToNearestPlayer(world, pos))) {
            return;
        }
        
        int actualCount = FactorParticleConfig.getActualCount(count);
        double speed = 0.03;
        
        for (int i = 0; i < actualCount; i++) {
            double angle = (i / (double) actualCount) * Math.PI * 2;
            double radius = 0.6;
            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;
            double offsetY = random.nextDouble() * 0.6;
            
            Vec3d particlePos = Vec3d.of(pos).add(0.5, 0.5, 0.5).add(offsetX, offsetY, offsetZ);
            
            // 成功：绿色粒子，失败：红色粒子
            double velocityY = success ? 0.02 : -0.02;
            
            world.spawnParticles(
                FactorParticleTypes.CULTIVATION,
                particlePos.x, particlePos.y, particlePos.z,
                1,
                offsetX * speed, velocityY, offsetZ * speed,
                0.0
            );
        }
    }
    
    /**
     * 生成消耗器粒子（燃烧效果）
     */
    public static void spawnConsumptionParticles(ServerWorld world, BlockPos pos, int count) {
        if (!FactorParticleConfig.shouldSpawn(getDistanceToNearestPlayer(world, pos))) {
            return;
        }
        
        int actualCount = FactorParticleConfig.getActualCount(count);
        
        for (int i = 0; i < actualCount; i++) {
            double offsetX = random.nextDouble() * 0.6 - 0.3;
            double offsetY = random.nextDouble() * 0.8;
            double offsetZ = random.nextDouble() * 0.6 - 0.3;
            
            Vec3d particlePos = Vec3d.of(pos).add(0.5, 0.5, 0.5).add(offsetX, offsetY, offsetZ);
            
            world.spawnParticles(
                FactorParticleTypes.CONSUMPTION,
                particlePos.x, particlePos.y, particlePos.z,
                1,
                offsetX * 0.05, 0.08, offsetZ * 0.05,
                0.0
            );
        }
    }
    
    /**
     * 获取距离最近的玩家距离
     */
    private static double getDistanceToNearestPlayer(ServerWorld world, BlockPos pos) {
        Vec3d center = pos.toCenterPos();
        return world.getPlayers().stream()
            .mapToDouble(player -> player.getPos().distanceTo(center))
            .min()
            .orElse(Double.MAX_VALUE);
    }
    
    // ==================== 客户端兼容方法 ====================
    
    /**
     * 生成提取器粒子（客户端版本）
     */
    public static void spawnExtractionParticles(ClientWorld world, BlockPos pos, int count, double factorAmount) {
        if (!FactorParticleConfig.shouldSpawn(getDistanceToNearestPlayer(world, pos))) {
            return;
        }
        
        int actualCount = FactorParticleConfig.getActualCount(count);
        double speed = 0.05 + (factorAmount / 1000.0) * 0.1;
        
        for (int i = 0; i < actualCount; i++) {
            double offsetX = random.nextDouble() * 0.8 - 0.4;
            double offsetY = random.nextDouble() * 0.5;
            double offsetZ = random.nextDouble() * 0.8 - 0.4;
            
            Vec3d particlePos = Vec3d.of(pos).add(0.5, 0.5, 0.5).add(offsetX, offsetY, offsetZ);
            
            world.addParticle(
                FactorParticleTypes.EXTRACTION,
                particlePos.x, particlePos.y, particlePos.z,
                offsetX * speed, offsetY * speed + 0.05, offsetZ * speed
            );
        }
    }
    
    /**
     * 生成合成器粒子（客户端版本）
     */
    public static void spawnSynthesisParticles(ClientWorld world, BlockPos pos, int count, double factorAmount) {
        if (!FactorParticleConfig.shouldSpawn(getDistanceToNearestPlayer(world, pos))) {
            return;
        }
        
        int actualCount = FactorParticleConfig.getActualCount(count);
        double speed = 0.08 + (factorAmount / 1000.0) * 0.05;
        
        for (int i = 0; i < actualCount; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double radius = 0.5;
            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;
            double offsetY = random.nextDouble() * 0.3;
            
            Vec3d particlePos = Vec3d.of(pos).add(0.5, 0.5, 0.5).add(offsetX, offsetY, offsetZ);
            
            // 向中心汇聚的效果
            world.addParticle(
                FactorParticleTypes.SYNTHESIS,
                particlePos.x, particlePos.y, particlePos.z,
                -offsetX * speed, -offsetY * speed, -offsetZ * speed
            );
        }
    }
    
    /**
     * 生成转换器粒子（客户端版本）
     */
    public static void spawnTransmissionParticles(ClientWorld world, BlockPos pos, int count, double factorAmount) {
        if (!FactorParticleConfig.shouldSpawn(getDistanceToNearestPlayer(world, pos))) {
            return;
        }
        
        int actualCount = FactorParticleConfig.getActualCount(count);
        double speed = 0.06;
        
        for (int i = 0; i < actualCount; i++) {
            double offsetX = random.nextDouble() * 0.6 - 0.3;
            double offsetY = random.nextDouble() * 0.6;
            double offsetZ = random.nextDouble() * 0.6 - 0.3;
            
            Vec3d particlePos = Vec3d.of(pos).add(0.5, 0.5, 0.5).add(offsetX, offsetY, offsetZ);
            
            world.addParticle(
                FactorParticleTypes.TRANSMISSION,
                particlePos.x, particlePos.y, particlePos.z,
                offsetX * speed, offsetY * speed * 0.5, offsetZ * speed
            );
        }
    }
    
    /**
     * 获取距离最近的玩家距离（客户端版本）
     */
    private static double getDistanceToNearestPlayer(ClientWorld world, BlockPos pos) {
        Vec3d center = pos.toCenterPos();
        return world.getPlayers().stream()
            .mapToDouble(player -> player.getPos().distanceTo(center))
            .min()
            .orElse(Double.MAX_VALUE);
    }
}
