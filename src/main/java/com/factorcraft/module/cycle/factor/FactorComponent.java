package com.factorcraft.module.cycle.factor;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Factor 组件接口
 * 
 * 定义不同类型 Factor 的通用行为
 * 实现此接口的类可以作为特殊 Factor 类型
 */
public interface FactorComponent {
    
    /**
     * 获取 Factor ID
     * 
     * @return Factor 的唯一标识符
     */
    String getId();
    
    /**
     * 获取扩散速率
     * 
     * @return 每秒扩散的 Factor 量
     */
    int getDiffusionRate();
    
    /**
     * 获取浓度阈值
     * 
     * @return 触发特殊效果的浓度阈值
     */
    double getConcentrationThreshold();
    
    /**
     * 应用环境效果
     * 
     * @param world 世界
     * @param pos 位置
     * @param concentration 当前浓度
     */
    void applyEnvironmentEffect(World world, BlockPos pos, double concentration);
    
    /**
     * 应用实体效果
     * 
     * @param entity 受影响的实体
     * @param concentration 当前浓度
     */
    void applyEntityEffect(Entity entity, double concentration);
}
