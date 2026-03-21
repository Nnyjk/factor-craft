package com.factorcraft.factor.synthesis;

import com.factorcraft.factor.Factor;
import com.factorcraft.factor.FactorRegistry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Factor 合成执行器
 * 
 * 执行合成操作，处理概率判定和失败返还
 */
public class FactorSynthesizer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("FactorSynthesizer");
    private static final Random RANDOM = new Random();
    
    // 合成结果
    public enum SynthesisResult {
        SUCCESS,        // 合成成功
        FAILED_DESTROY, // 合成失败，输入已销毁
        FAILED_RETURN,  // 合成失败，输入已返还
        INVALID_RECIPE, // 无效配方
        MISSING_INPUT,  // 缺少输入
        INVALID_INPUT   // 输入不匹配
    }
    
    // 合成输出
    public static class SynthesisOutput {
        private final SynthesisResult result;
        private final List<Factor> outputs;
        private final List<Factor> returned; // 失败返还的 Factor
        private final String message;
        
        private SynthesisOutput(SynthesisResult result, List<Factor> outputs, List<Factor> returned, String message) {
            this.result = result;
            this.outputs = outputs != null ? Collections.unmodifiableList(outputs) : Collections.emptyList();
            this.returned = returned != null ? Collections.unmodifiableList(returned) : Collections.emptyList();
            this.message = message;
        }
        
        public SynthesisResult getResult() {
            return result;
        }
        
        public List<Factor> getOutputs() {
            return outputs;
        }
        
        public List<Factor> getReturned() {
            return returned;
        }
        
        public boolean isSuccess() {
            return result == SynthesisResult.SUCCESS;
        }
        
        public String getMessage() {
            return message;
        }
        
        // 静态工厂方法
        public static SynthesisOutput success(List<Factor> outputs) {
            return new SynthesisOutput(SynthesisResult.SUCCESS, outputs, null, "Synthesis successful");
        }
        
        public static SynthesisOutput failedDestroy(String message) {
            return new SynthesisOutput(SynthesisResult.FAILED_DESTROY, null, null, message);
        }
        
        public static SynthesisOutput failedReturn(List<Factor> returned, String message) {
            return new SynthesisOutput(SynthesisResult.FAILED_RETURN, null, returned, message);
        }
        
        public static SynthesisOutput invalidRecipe(String message) {
            return new SynthesisOutput(SynthesisResult.INVALID_RECIPE, null, null, message);
        }
        
        public static SynthesisOutput missingInput(String message) {
            return new SynthesisOutput(SynthesisResult.MISSING_INPUT, null, null, message);
        }
        
        public static SynthesisOutput invalidInput(String message) {
            return new SynthesisOutput(SynthesisResult.INVALID_INPUT, null, null, message);
        }
    }
    
    // ========== 合成验证 ==========
    
    /**
     * 验证输入是否匹配配方要求
     * 
     * @param recipe 合成配方
     * @param inputs 输入的 Factor 列表
     * @return 验证结果
     */
    public static SynthesisOutput validateInputs(FactorSynthesisRecipe recipe, List<Factor> inputs) {
        if (recipe == null) {
            return SynthesisOutput.invalidRecipe("Recipe cannot be null");
        }
        
        if (inputs == null || inputs.isEmpty()) {
            return SynthesisOutput.missingInput("No inputs provided");
        }
        
        // 检查每个输入要求
        List<FactorIngredient> requirements = recipe.getInputs();
        List<Factor> availableInputs = new ArrayList<>(inputs);
        
        for (FactorIngredient requirement : requirements) {
            int needed = requirement.getCount();
            int found = 0;
            
            Iterator<Factor> iterator = availableInputs.iterator();
            while (iterator.hasNext() && found < needed) {
                Factor input = iterator.next();
                if (requirement.matches(input)) {
                    found++;
                    iterator.remove(); // 标记为已使用
                }
            }
            
            if (found < needed) {
                return SynthesisOutput.invalidInput(
                    String.format("Not enough matching inputs for %s (needed %d, found %d)", 
                        requirement.getFactorId(), needed, found));
            }
        }
        
        return null; // 验证通过
    }
    
    /**
     * 检查输入是否匹配配方
     */
    public static boolean canSynthesize(FactorSynthesisRecipe recipe, List<Factor> inputs) {
        SynthesisOutput validation = validateInputs(recipe, inputs);
        return validation == null;
    }
    
    // ========== 合成执行 ==========
    
    /**
     * 执行合成操作
     * 
     * @param recipe 合成配方
     * @param inputs 输入的 Factor 列表（将被消耗）
     * @return 合成结果
     */
    public static SynthesisOutput synthesize(FactorSynthesisRecipe recipe, List<Factor> inputs) {
        return synthesize(recipe, inputs, RANDOM);
    }
    
    /**
     * 执行合成操作（带随机源）
     * 
     * @param recipe 合成配方
     * @param inputs 输入的 Factor 列表（将被消耗）
     * @param random 随机源
     * @return 合成结果
     */
    public static SynthesisOutput synthesize(FactorSynthesisRecipe recipe, List<Factor> inputs, Random random) {
        // 验证输入
        SynthesisOutput validation = validateInputs(recipe, inputs);
        if (validation != null) {
            return validation;
        }
        
        // 检查输出 Factor 是否已注册
        FactorRegistry factorRegistry = FactorRegistry.getInstance();
        Identifier outputId = recipe.getOutputFactorId();
        Optional<Factor> templateFactor = factorRegistry.get(outputId);
        
        if (templateFactor.isEmpty()) {
            return SynthesisOutput.invalidRecipe("Output factor not registered: " + outputId);
        }
        
        // 概率判定
        double roll = random.nextDouble();
        boolean success = roll < recipe.getSuccessRate();
        
        LOGGER.debug("Synthesis roll: {} vs {} -> {}", roll, recipe.getSuccessRate(), success ? "SUCCESS" : "FAIL");
        
        if (success) {
            // 合成成功
            List<Factor> outputs = new ArrayList<>();
            Factor template = templateFactor.get();
            
            int outputCount = recipe.getOutputCount();
            for (int i = 0; i < outputCount; i++) {
                // 创建新的 Factor（基于模板，可能调整属性）
                Factor output = createOutputFactor(template, recipe);
                outputs.add(output);
            }
            
            return SynthesisOutput.success(outputs);
        } else {
            // 合成失败
            return handleFailure(recipe, inputs);
        }
    }
    
    // ========== 失败处理 ==========
    
    /**
     * 处理合成失败
     */
    private static SynthesisOutput handleFailure(FactorSynthesisRecipe recipe, List<Factor> inputs) {
        switch (recipe.getFailureBehavior()) {
            case DESTROY:
                LOGGER.debug("Synthesis failed - inputs destroyed");
                return SynthesisOutput.failedDestroy("Synthesis failed - all inputs destroyed");
                
            case RETURN_ALL:
                LOGGER.debug("Synthesis failed - returning all inputs");
                return SynthesisOutput.failedReturn(new ArrayList<>(inputs), "Synthesis failed - inputs returned");
                
            case RETURN_HALF:
                List<Factor> toReturn = selectHalfRandom(inputs);
                LOGGER.debug("Synthesis failed - returning {} of {} inputs", toReturn.size(), inputs.size());
                return SynthesisOutput.failedReturn(toReturn, "Synthesis failed - partial inputs returned");
                
            default:
                return SynthesisOutput.failedDestroy("Synthesis failed");
        }
    }
    
    /**
     * 随机选择一半输入返还
     */
    private static List<Factor> selectHalfRandom(List<Factor> inputs) {
        List<Factor> shuffled = new ArrayList<>(inputs);
        Collections.shuffle(shuffled, RANDOM);
        
        int count = (shuffled.size() + 1) / 2; // 向上取整
        return new ArrayList<>(shuffled.subList(0, count));
    }
    
    // ========== 输出 Factor 创建 ==========
    
    /**
     * 创建输出 Factor
     * 基于 FactorRegistry 中的模板创建，可能添加合成加成
     */
    private static Factor createOutputFactor(Factor template, FactorSynthesisRecipe recipe) {
        // 目前直接返回模板副本
        // 未来可以根据配方添加额外属性或修改
        return template;
    }
    
    // ========== 辅助方法 ==========
    
    /**
     * 计算合成成功率（考虑加成）
     * 未来可以添加基于玩家属性、结构等的加成
     */
    public static double calculateSuccessRate(FactorSynthesisRecipe recipe, 
                                               double playerBonus,
                                               double structureBonus) {
        double baseRate = recipe.getSuccessRate();
        return Math.min(1.0, baseRate * (1 + playerBonus + structureBonus));
    }
}