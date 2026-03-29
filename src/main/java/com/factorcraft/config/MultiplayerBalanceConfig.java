package com.factorcraft.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;

/**
 * R3.4 多人平衡配置
 * 
 * 管理多人游戏平衡性相关的配置参数
 * 支持服务器管理员动态调整
 */
public class MultiplayerBalanceConfig {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiplayerBalanceConfig.class);
    private static final String CONFIG_FILE = "config/multiplayer_balance.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    private static MultiplayerBalanceConfig instance;
    
    /** 是否启用多人平衡 */
    public boolean enabled = true;
    
    /** 每玩家浓度倍数 (默认 0.2，即每多一个玩家增加 20% 浓度) */
    public double concentrationMultiplierPerPlayer = 0.2;
    
    /** 机器效率递减率 (默认 0.1，即每台额外机器降低 10% 效率) */
    public double machineEfficiencyDecayRate = 0.1;
    
    /** 资源声明持续时间 (秒，默认 30 秒) */
    public int resourceClaimDuration = 30;
    
    /** 半径内最大机器数 (默认 16 台) */
    public int maxMachinesInRadius = 16;
    
    /** 机器效率计算半径 (区块，默认 2) */
    public int machineCalculationRadius = 2;
    
    /** 最小效率倍数 (默认 0.5，即最低 50% 效率) */
    public double minEfficiencyMultiplier = 0.5;
    
    /** 最大玩家数阈值 (超过此数量不再增加难度，默认 10) */
    public int maxPlayerThreshold = 10;
    
    private MultiplayerBalanceConfig() {
    }
    
    /**
     * 获取配置实例
     */
    public static MultiplayerBalanceConfig getInstance() {
        if (instance == null) {
            instance = new MultiplayerBalanceConfig();
            instance.load();
        }
        return instance;
    }
    
    /**
     * 加载配置文件
     */
    public void load() {
        Path configPath = Paths.get(CONFIG_FILE);
        
        if (!Files.exists(configPath)) {
            LOGGER.info("多人平衡配置文件不存在，创建默认配置");
            save();
            return;
        }
        
        try (Reader reader = Files.newBufferedReader(configPath)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            
            if (json.has("enabled")) {
                enabled = json.get("enabled").getAsBoolean();
            }
            if (json.has("concentrationMultiplierPerPlayer")) {
                concentrationMultiplierPerPlayer = json.get("concentrationMultiplierPerPlayer").getAsDouble();
            }
            if (json.has("machineEfficiencyDecayRate")) {
                machineEfficiencyDecayRate = json.get("machineEfficiencyDecayRate").getAsDouble();
            }
            if (json.has("resourceClaimDuration")) {
                resourceClaimDuration = json.get("resourceClaimDuration").getAsInt();
            }
            if (json.has("maxMachinesInRadius")) {
                maxMachinesInRadius = json.get("maxMachinesInRadius").getAsInt();
            }
            if (json.has("machineCalculationRadius")) {
                machineCalculationRadius = json.get("machineCalculationRadius").getAsInt();
            }
            if (json.has("minEfficiencyMultiplier")) {
                minEfficiencyMultiplier = json.get("minEfficiencyMultiplier").getAsDouble();
            }
            if (json.has("maxPlayerThreshold")) {
                maxPlayerThreshold = json.get("maxPlayerThreshold").getAsInt();
            }
            
            LOGGER.info("多人平衡配置文件加载成功");
        } catch (IOException e) {
            LOGGER.error("加载多人平衡配置文件失败", e);
            save();
        }
    }
    
    /**
     * 保存配置文件
     */
    public void save() {
        Path configPath = Paths.get(CONFIG_FILE);
        
        try {
            Files.createDirectories(configPath.getParent());
            
            JsonObject json = new JsonObject();
            json.addProperty("enabled", enabled);
            json.addProperty("concentrationMultiplierPerPlayer", concentrationMultiplierPerPlayer);
            json.addProperty("machineEfficiencyDecayRate", machineEfficiencyDecayRate);
            json.addProperty("resourceClaimDuration", resourceClaimDuration);
            json.addProperty("maxMachinesInRadius", maxMachinesInRadius);
            json.addProperty("machineCalculationRadius", machineCalculationRadius);
            json.addProperty("minEfficiencyMultiplier", minEfficiencyMultiplier);
            json.addProperty("maxPlayerThreshold", maxPlayerThreshold);
            
            try (Writer writer = Files.newBufferedWriter(configPath)) {
                GSON.toJson(json, writer);
            }
            
            LOGGER.info("多人平衡配置文件保存成功");
        } catch (IOException e) {
            LOGGER.error("保存多人平衡配置文件失败", e);
        }
    }
    
    /**
     * 重新加载配置
     */
    public void reload() {
        load();
    }
    
    // Getters
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public double getConcentrationMultiplierPerPlayer() {
        return concentrationMultiplierPerPlayer;
    }
    
    public double getMachineEfficiencyDecayRate() {
        return machineEfficiencyDecayRate;
    }
    
    public int getResourceClaimDuration() {
        return resourceClaimDuration;
    }
    
    public int getMaxMachinesInRadius() {
        return maxMachinesInRadius;
    }
    
    public int getMachineCalculationRadius() {
        return machineCalculationRadius;
    }
    
    public double getMinEfficiencyMultiplier() {
        return minEfficiencyMultiplier;
    }
    
    public int getMaxPlayerThreshold() {
        return maxPlayerThreshold;
    }
}
