package com.factorcraft.module.profession.balance;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * 职业数值平衡配置
 * 
 * 管理各职业的数值调整，确保职业强度差异 ≤15%
 * 
 * 平衡原则：
 * 1. 各职业发育速度差异不超过 15%
 * 2. 支持至少 3 种不同 build 玩法
 * 3. 技能冷却和消耗平衡
 * 4. 天赋效果比例合理
 */
public class ProfessionBalanceConfig {
    
    private static Path configPath = null;
    
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    
    private static ProfessionBalanceConfig INSTANCE;
    
    /**
     * 获取配置文件路径（延迟初始化）
     */
    private static Path getConfigPath() {
        if (configPath == null) {
            try {
                configPath = FabricLoader.getInstance()
                        .getConfigDir()
                        .resolve("factorcraft")
                        .resolve("profession_balance.json");
            } catch (Exception e) {
                // 测试环境或 FabricLoader 未初始化时，使用临时目录
                configPath = Path.of(System.getProperty("java.io.tmpdir"))
                        .resolve("factorcraft")
                        .resolve("profession_balance.json");
            }
        }
        return configPath;
    }
    
    // 职业基础属性倍率配置
    @SerializedName("profession_attributes")
    private Map<String, ProfessionAttributes> professionAttributes = new HashMap<>();
    
    // 技能平衡配置
    @SerializedName("skill_balance")
    private SkillBalanceConfig skillBalance = new SkillBalanceConfig();
    
    // 天赋平衡配置
    @SerializedName("talent_balance")
    private TalentBalanceConfig talentBalance = new TalentBalanceConfig();
    
    // 全局倍率
    @SerializedName("global_multiplier")
    private double globalMultiplier = 1.0;
    
    /**
     * 职业属性配置
     */
    public static class ProfessionAttributes {
        @SerializedName("development_speed")
        private double developmentSpeed = 1.0;
        
        @SerializedName("factor_efficiency")
        private double factorEfficiency = 1.0;
        
        @SerializedName("skill_power")
        private double skillPower = 1.0;
        
        @SerializedName("survival_rate")
        private double survivalRate = 1.0;
        
        @SerializedName("resource_gathering")
        private double resourceGathering = 1.0;
        
        public double getDevelopmentSpeed() { return developmentSpeed; }
        public double getFactorEfficiency() { return factorEfficiency; }
        public double getSkillPower() { return skillPower; }
        public double getSurvivalRate() { return survivalRate; }
        public double getResourceGathering() { return resourceGathering; }
    }
    
    /**
     * 技能平衡配置
     */
    public static class SkillBalanceConfig {
        @SerializedName("cooldown_multiplier")
        private double cooldownMultiplier = 1.0;
        
        @SerializedName("factor_cost_multiplier")
        private double factorCostMultiplier = 1.0;
        
        @SerializedName("effect_duration_multiplier")
        private double effectDurationMultiplier = 1.0;
        
        @SerializedName("damage_multiplier")
        private double damageMultiplier = 1.0;
        
        public double getCooldownMultiplier() { return cooldownMultiplier; }
        public double getFactorCostMultiplier() { return factorCostMultiplier; }
        public double getEffectDurationMultiplier() { return effectDurationMultiplier; }
        public double getDamageMultiplier() { return damageMultiplier; }
    }
    
    /**
     * 天赋平衡配置
     */
    public static class TalentBalanceConfig {
        @SerializedName("max_talent_points_per_level")
        private int maxTalentPointsPerLevel = 1;
        
        @SerializedName("talent_effect_cap")
        private double talentEffectCap = 0.5; // 单一天赋最大效果上限 50%
        
        @SerializedName("talent_synergy_bonus")
        private double talentSynergyBonus = 0.1; // 天赋协同加成 10%
        
        public int getMaxTalentPointsPerLevel() { return maxTalentPointsPerLevel; }
        public double getTalentEffectCap() { return talentEffectCap; }
        public double getTalentSynergyBonus() { return talentSynergyBonus; }
    }
    
    private ProfessionBalanceConfig() {
        // 初始化默认职业属性
        initDefaultAttributes();
    }
    
    private void initDefaultAttributes() {
        // Factor工程师 - 发育速度略快，但技能伤害一般
        ProfessionAttributes engineer = new ProfessionAttributes();
        engineer.developmentSpeed = 1.08;    // +8% 发育速度
        engineer.factorEfficiency = 1.12;    // +12% Factor效率
        engineer.skillPower = 0.95;          // -5% 技能威力
        engineer.survivalRate = 1.0;        // 基准生存率
        engineer.resourceGathering = 1.05;  // +5% 资源收集
        professionAttributes.put("engineer", engineer);
        
        // 能量培育师 - 发育速度均衡，资源收集略强
        ProfessionAttributes cultivator = new ProfessionAttributes();
        cultivator.developmentSpeed = 1.0;    // 基准发育速度
        cultivator.factorEfficiency = 1.05;   // +5% Factor效率
        cultivator.skillPower = 1.0;          // 基准技能威力
        cultivator.survivalRate = 1.05;      // +5% 生存率
        cultivator.resourceGathering = 1.12; // +12% 资源收集
        professionAttributes.put("cultivator", cultivator);
        
        // 潮汐探索者 - 发育速度略慢，但技能威力强
        ProfessionAttributes explorer = new ProfessionAttributes();
        explorer.developmentSpeed = 0.95;    // -5% 发育速度
        explorer.factorEfficiency = 1.0;     // 基准Factor效率
        explorer.skillPower = 1.12;         // +12% 技能威力
        explorer.survivalRate = 1.05;       // +5% 生存率
        explorer.resourceGathering = 1.0;   // 基准资源收集
        professionAttributes.put("explorer", explorer);
        
        // 因子掌控者 - 全能但均衡
        ProfessionAttributes master = new ProfessionAttributes();
        master.developmentSpeed = 1.0;
        master.factorEfficiency = 1.0;
        master.skillPower = 1.0;
        master.survivalRate = 1.0;
        master.resourceGathering = 1.0;
        professionAttributes.put("master", master);
    }
    
    /**
     * 获取配置实例
     */
    public static ProfessionBalanceConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = loadOrCreate();
        }
        return INSTANCE;
    }
    
    /**
     * 加载或创建配置
     */
    private static ProfessionBalanceConfig loadOrCreate() {
        Path path = getConfigPath();
        if (Files.exists(path)) {
            try {
                String json = Files.readString(path);
                return GSON.fromJson(json, ProfessionBalanceConfig.class);
            } catch (IOException e) {
                // 加载失败，使用默认配置
            }
        }
        
        ProfessionBalanceConfig config = new ProfessionBalanceConfig();
        config.save();
        return config;
    }
    
    /**
     * 保存配置
     */
    public void save() {
        try {
            Path path = getConfigPath();
            Files.createDirectories(path.getParent());
            String json = GSON.toJson(this);
            Files.writeString(path, json);
        } catch (IOException e) {
            // 保存失败，静默处理
        }
    }
    
    /**
     * 重新加载配置
     */
    public static void reload() {
        INSTANCE = loadOrCreate();
    }
    
    /**
     * 获取职业属性配置
     */
    public ProfessionAttributes getProfessionAttributes(String professionId) {
        return professionAttributes.getOrDefault(professionId, new ProfessionAttributes());
    }
    
    /**
     * 获取技能平衡配置
     */
    public SkillBalanceConfig getSkillBalance() {
        return skillBalance;
    }
    
    /**
     * 获取天赋平衡配置
     */
    public TalentBalanceConfig getTalentBalance() {
        return talentBalance;
    }
    
    /**
     * 获取全局倍率
     */
    public double getGlobalMultiplier() {
        return globalMultiplier;
    }
    
    /**
     * 验证职业平衡是否符合要求（差异 ≤15%）
     */
    public boolean validateBalance() {
        double maxDevSpeed = 0;
        double minDevSpeed = Double.MAX_VALUE;
        
        for (ProfessionAttributes attrs : professionAttributes.values()) {
            if (attrs.developmentSpeed > maxDevSpeed) {
                maxDevSpeed = attrs.developmentSpeed;
            }
            if (attrs.developmentSpeed < minDevSpeed) {
                minDevSpeed = attrs.developmentSpeed;
            }
        }
        
        // 计算差异百分比
        double difference = (maxDevSpeed - minDevSpeed) / minDevSpeed * 100;
        
        return difference <= 15.0;
    }
    
    /**
     * 获取平衡差异报告
     */
    public String getBalanceReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== 职业平衡报告 ===\n\n");
        
        for (Map.Entry<String, ProfessionAttributes> entry : professionAttributes.entrySet()) {
            ProfessionAttributes attrs = entry.getValue();
            report.append(String.format("%s:\n", entry.getKey()));
            report.append(String.format("  发育速度: %.2f%%\n", attrs.developmentSpeed * 100 - 100));
            report.append(String.format("  Factor效率: %.2f%%\n", attrs.factorEfficiency * 100 - 100));
            report.append(String.format("  技能威力: %.2f%%\n", attrs.skillPower * 100 - 100));
            report.append(String.format("  生存率: %.2f%%\n", attrs.survivalRate * 100 - 100));
            report.append(String.format("  资源收集: %.2f%%\n\n", attrs.resourceGathering * 100 - 100));
        }
        
        report.append(String.format("平衡验证: %s\n", validateBalance() ? "✓ 通过" : "✗ 未通过"));
        
        return report.toString();
    }
}