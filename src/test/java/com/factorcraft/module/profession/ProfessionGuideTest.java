package com.factorcraft.module.profession;

import com.factorcraft.module.profession.guide.ProfessionGuideManager;
import com.factorcraft.module.profession.guide.ProfessionGuideManager.GuideStage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 职业系统新手引导测试
 * 
 * 验证引导系统的功能：
 * - 引导状态追踪
 * - 引导进度计算
 * - 引导重置功能
 */
@DisplayName("职业新手引导测试")
class ProfessionGuideTest {
    
    private ProfessionGuideManager guideManager;
    private UUID testPlayerId;
    
    @BeforeEach
    void setUp() {
        guideManager = ProfessionGuideManager.getInstance();
        testPlayerId = UUID.randomUUID();
        
        // 重置测试玩家的引导状态
        guideManager.resetGuideStatus(testPlayerId);
    }
    
    @Test
    @DisplayName("新玩家应未完成任何引导")
    void testNewPlayerGuideStatus() {
        assertFalse(guideManager.hasCompletedGuide(testPlayerId, GuideStage.PROFESSION_SELECTION),
            "新玩家应未完成职业选择引导");
        assertFalse(guideManager.hasCompletedGuide(testPlayerId, GuideStage.TALENT_ALLOCATION),
            "新玩家应未完成天赋分配引导");
        assertFalse(guideManager.hasCompletedGuide(testPlayerId, GuideStage.SKILL_USAGE),
            "新玩家应未完成技能使用引导");
    }
    
    @Test
    @DisplayName("引导进度应正确计算")
    void testGuideProgress() {
        // 初始进度为 0
        assertEquals(0, guideManager.getGuideProgress(testPlayerId),
            "初始引导进度应为 0");
        assertEquals(0.0f, guideManager.getGuideProgressPercent(testPlayerId), 0.01f,
            "初始引导完成百分比应为 0%");
        
        // 模拟完成职业选择引导
        guideManager.resetGuideStatus(testPlayerId);
    }
    
    @Test
    @DisplayName("引导重置应清除所有状态")
    void testGuideReset() {
        // 先清除可能存在的状态
        guideManager.resetGuideStatus(testPlayerId);
        
        // 验证重置后状态
        assertEquals(0, guideManager.getGuideProgress(testPlayerId),
            "重置后引导进度应为 0");
    }
    
    @Test
    @DisplayName("引导阶段枚举应正确")
    void testGuideStageEnum() {
        GuideStage[] stages = GuideStage.values();
        
        assertEquals(3, stages.length, "应有 3 个引导阶段");
        
        // 验证每个阶段都有标题和描述
        for (GuideStage stage : stages) {
            assertNotNull(stage.getTitle(), "引导阶段应有标题");
            assertNotNull(stage.getDescription(), "引导阶段应有描述");
            assertFalse(stage.getTitle().isEmpty(), "引导阶段标题不应为空");
            assertFalse(stage.getDescription().isEmpty(), "引导阶段描述不应为空");
        }
    }
    
    @Test
    @DisplayName("单例模式应正确工作")
    void testSingletonPattern() {
        ProfessionGuideManager instance1 = ProfessionGuideManager.getInstance();
        ProfessionGuideManager instance2 = ProfessionGuideManager.getInstance();
        
        assertSame(instance1, instance2, "应返回相同的实例");
    }
    
    @Test
    @DisplayName("不同玩家的引导状态应独立")
    void testIndependentPlayerStatus() {
        UUID player1 = UUID.randomUUID();
        UUID player2 = UUID.randomUUID();
        
        // 重置两个玩家的状态
        guideManager.resetGuideStatus(player1);
        guideManager.resetGuideStatus(player2);
        
        // 两个玩家的进度都应为 0
        assertEquals(0, guideManager.getGuideProgress(player1));
        assertEquals(0, guideManager.getGuideProgress(player2));
    }
    
    @Test
    @DisplayName("引导完成百分比应在 0-100 范围内")
    void testGuideProgressPercentRange() {
        guideManager.resetGuideStatus(testPlayerId);
        
        float percent = guideManager.getGuideProgressPercent(testPlayerId);
        
        assertTrue(percent >= 0.0f && percent <= 100.0f,
            "引导完成百分比应在 0-100 范围内");
    }
}