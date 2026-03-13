package com.factorcraft.verification;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 快速验证器 - 不启动 Minecraft，检查静态资源完整性
 * 
 * 运行: ./gradlew runQuickTest
 */
public class QuickVerifier {
    
    private static final String MOD_ID = "factorcraft";
    private static int errors = 0;
    private static int warnings = 0;
    
    public static void main(String[] args) {
        System.out.println("=== Factor Craft Quick Verifier ===\n");
        
        String basePath = System.getProperty("user.dir", ".");
        
        // 1. 检查 fabric.mod.json
        checkFabricModJson(basePath);
        
        // 2. 检查方块注册与资源匹配
        checkBlockResources(basePath);
        
        // 3. 检查物品注册与资源匹配
        checkItemResources(basePath);
        
        // 4. 检查语言文件
        checkLanguageFiles(basePath);
        
        // 5. 检查配置文件
        checkConfigFiles(basePath);
        
        // 输出结果
        System.out.println("\n=== 验证结果 ===");
        System.out.println("错误: " + errors);
        System.out.println("警告: " + warnings);
        
        if (errors > 0) {
            System.out.println("\n❌ 验证失败!");
            System.exit(1);
        } else {
            System.out.println("\n✅ 验证通过!");
        }
    }
    
    private static void checkFabricModJson(String basePath) {
        System.out.println("\n[1/5] 检查 fabric.mod.json...");
        
        try {
            Path modJsonPath = Path.of(basePath, "src/main/resources/fabric.mod.json");
            if (!Files.exists(modJsonPath)) {
                error("fabric.mod.json 不存在");
                return;
            }
            
            String content = Files.readString(modJsonPath);
            
            // 检查 ID
            if (!content.contains("\"id\": \"factorcraft\"")) {
                error("MOD_ID 应为 'factorcraft'");
            }
            
            // 检查入口点
            if (!content.contains("com.factorcraft.FactorCraftMod")) {
                error("缺少主入口点");
            }
            if (!content.contains("com.factorcraft.FactorCraftClient")) {
                warning("缺少客户端入口点");
            }
            
            System.out.println("  ✓ fabric.mod.json 配置正确");
        } catch (Exception e) {
            error("读取 fabric.mod.json 失败: " + e.getMessage());
        }
    }
    
    private static void checkBlockResources(String basePath) {
        System.out.println("\n[2/5] 检查方块资源...");
        
        // 从代码提取注册的方块
        Set<String> registeredBlocks = extractBlocksFromCode(basePath);
        
        // 检查资源文件
        String assetsPath = basePath + "/src/main/resources/assets/factorcraft";
        
        int missingBlockstates = 0;
        int missingModels = 0;
        int missingItemModels = 0;
        
        for (String blockId : registeredBlocks) {
            // blockstates
            if (!Files.exists(Path.of(assetsPath, "blockstates", blockId + ".json"))) {
                error("缺少 blockstates: " + blockId);
                missingBlockstates++;
            }
            // block model
            if (!Files.exists(Path.of(assetsPath, "models/block", blockId + ".json"))) {
                error("缺少 block model: " + blockId);
                missingModels++;
            }
            // item model
            if (!Files.exists(Path.of(assetsPath, "models/item", blockId + ".json"))) {
                error("缺少 item model: " + blockId);
                missingItemModels++;
            }
        }
        
        System.out.println("  已注册方块: " + registeredBlocks.size());
        if (missingBlockstates == 0 && missingModels == 0 && missingItemModels == 0) {
            System.out.println("  ✓ 所有方块资源完整");
        }
    }
    
    private static void checkItemResources(String basePath) {
        System.out.println("\n[3/5] 检查物品资源...");
        
        Set<String> registeredItems = extractItemsFromCode(basePath);
        
        String assetsPath = basePath + "/src/main/resources/assets/factorcraft";
        
        int missingModels = 0;
        for (String itemId : registeredItems) {
            if (!Files.exists(Path.of(assetsPath, "models/item", itemId + ".json"))) {
                error("缺少 item model: " + itemId);
                missingModels++;
            }
        }
        
        System.out.println("  已注册物品: " + registeredItems.size());
        if (missingModels == 0) {
            System.out.println("  ✓ 所有物品资源完整");
        }
    }
    
    private static void checkLanguageFiles(String basePath) {
        System.out.println("\n[4/5] 检查语言文件...");
        
        String langPath = basePath + "/src/main/resources/assets/factorcraft/lang";
        
        if (!Files.exists(Path.of(langPath, "en_us.json"))) {
            error("缺少 en_us.json");
        } else {
            System.out.println("  ✓ en_us.json 存在");
        }
        
        if (!Files.exists(Path.of(langPath, "zh_cn.json"))) {
            warning("缺少 zh_cn.json (可选)");
        } else {
            System.out.println("  ✓ zh_cn.json 存在");
        }
    }
    
    private static void checkConfigFiles(String basePath) {
        System.out.println("\n[5/5] 检查配置文件...");
        
        String configPath = basePath + "/src/main/resources/config";
        String[] requiredConfigs = {"materials.json", "material_production.json", "structure_unlocks.json"};
        
        for (String config : requiredConfigs) {
            if (!Files.exists(Path.of(configPath, config))) {
                error("缺少配置文件: " + config);
            } else {
                System.out.println("  ✓ " + config);
            }
        }
    }
    
    // 从代码提取方块注册
    private static Set<String> extractBlocksFromCode(String basePath) {
        Set<String> blocks = new HashSet<>();
        
        // 检查 technology 模块的 ModBlocks
        try {
            Path modBlocksPath = Path.of(basePath, "src/main/java/com/factorcraft/module/technology/block/ModBlocks.java");
            if (Files.exists(modBlocksPath)) {
                String content = Files.readString(modBlocksPath);
                Pattern pattern = Pattern.compile("\"([a-z_]+)\"");
                Matcher matcher = pattern.matcher(content);
                while (matcher.find()) {
                    String id = matcher.group(1);
                    if (!id.equals("factorcraft")) {
                        blocks.add(id);
                    }
                }
            }
        } catch (Exception e) {
            warning("无法解析 ModBlocks.java: " + e.getMessage());
        }
        
        return blocks;
    }
    
    // 从代码提取物品注册
    private static Set<String> extractItemsFromCode(String basePath) {
        Set<String> items = new HashSet<>();
        
        try {
            Path modItemsPath = Path.of(basePath, "src/main/java/com/factorcraft/module/technology/item/ModItems.java");
            if (Files.exists(modItemsPath)) {
                String content = Files.readString(modItemsPath);
                Pattern pattern = Pattern.compile("\"([a-z_]+)\"");
                Matcher matcher = pattern.matcher(content);
                while (matcher.find()) {
                    String id = matcher.group(1);
                    if (!id.equals("factorcraft")) {
                        items.add(id);
                    }
                }
            }
        } catch (Exception e) {
            warning("无法解析 ModItems.java: " + e.getMessage());
        }
        
        return items;
    }
    
    private static void error(String message) {
        System.out.println("  ❌ " + message);
        errors++;
    }
    
    private static void warning(String message) {
        System.out.println("  ⚠️  " + message);
        warnings++;
    }
}