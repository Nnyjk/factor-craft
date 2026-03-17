package com.factorcraft.config;

import com.factorcraft.FactorCraftMod;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 配置验证器
 * 
 * 验证 JSON 配置文件的结构和内容
 */
public class ConfigValidator {
    
    /**
     * 验证结果
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        private final List<String> warnings;
        
        public ValidationResult(boolean valid, List<String> errors, List<String> warnings) {
            this.valid = valid;
            this.errors = errors;
            this.warnings = warnings;
        }
        
        public boolean isValid() { return valid; }
        public List<String> getErrors() { return errors; }
        public List<String> getWarnings() { return warnings; }
        
        public static ValidationResult success() {
            return new ValidationResult(true, new ArrayList<>(), new ArrayList<>());
        }
        
        public static ValidationResult failure(String error) {
            List<String> errors = new ArrayList<>();
            errors.add(error);
            return new ValidationResult(false, errors, new ArrayList<>());
        }
    }
    
    /**
     * 验证配置版本字段
     */
    public ValidationResult validateVersion(JsonObject config, String configName) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        if (!config.has("version")) {
            errors.add("配置 '" + configName + "' 缺少必填字段：version");
            return new ValidationResult(false, errors, warnings);
        }
        
        String version = config.get("version").getAsString();
        if (!version.matches("\\d+\\.\\d+\\.\\d+")) {
            errors.add("配置 '" + configName + "' 的版本号格式错误：" + version + "（应为 major.minor.patch）");
            return new ValidationResult(false, errors, warnings);
        }
        
        // 检查 schema 字段（可选）
        if (config.has("schema")) {
            String schema = config.get("schema").getAsString();
            if (!schema.contains(":")) {
                warnings.add("配置 '" + configName + "' 的 schema 格式建议为：modid:type/version");
            }
        } else {
            warnings.add("配置 '" + configName + "' 缺少 schema 字段（推荐添加）");
        }
        
        return new ValidationResult(true, errors, warnings);
    }
    
    /**
     * 验证必填字段
     */
    public ValidationResult validateRequiredFields(JsonObject config, String configName, List<String> requiredFields) {
        List<String> errors = new ArrayList<>();
        
        for (String field : requiredFields) {
            if (!config.has(field)) {
                errors.add("配置 '" + configName + "' 缺少必填字段：" + field);
            }
        }
        
        if (errors.isEmpty()) {
            return ValidationResult.success();
        }
        return new ValidationResult(false, errors, new ArrayList<>());
    }
    
    /**
     * 验证数值范围
     */
    public ValidationResult validateRange(JsonObject config, String configName, String field, 
                                        double min, double max, double defaultValue) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        if (!config.has(field)) {
            warnings.add("配置 '" + configName + "' 缺少字段 '" + field + "'，使用默认值：" + defaultValue);
            return new ValidationResult(true, errors, warnings);
        }
        
        JsonElement element = config.get(field);
        if (!element.isJsonPrimitive()) {
            errors.add("配置 '" + configName + "' 的字段 '" + field + "' 类型错误（应为数字）");
            return new ValidationResult(false, errors, warnings);
        }
        
        double value = element.getAsDouble();
        if (value < min || value > max) {
            errors.add("配置 '" + configName + "' 的字段 '" + field + "' 超出范围 [" + min + ", " + max + "]：当前值=" + value);
        }
        
        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }
    
    /**
     * 验证整数范围
     */
    public ValidationResult validateIntRange(JsonObject config, String configName, String field, 
                                           int min, int max, int defaultValue) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        if (!config.has(field)) {
            warnings.add("配置 '" + configName + "' 缺少字段 '" + field + "'，使用默认值：" + defaultValue);
            return new ValidationResult(true, errors, warnings);
        }
        
        JsonElement element = config.get(field);
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
            errors.add("配置 '" + configName + "' 的字段 '" + field + "' 类型错误（应为整数）");
            return new ValidationResult(false, errors, warnings);
        }
        
        int value = element.getAsInt();
        if (value < min || value > max) {
            errors.add("配置 '" + configName + "' 的字段 '" + field + "' 超出范围 [" + min + ", " + max + "]：当前值=" + value);
        }
        
        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }
    
    /**
     * 验证字符串枚举值
     */
    public ValidationResult validateEnum(JsonObject config, String configName, String field, 
                                       String[] allowedValues, String defaultValue) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        if (!config.has(field)) {
            warnings.add("配置 '" + configName + "' 缺少字段 '" + field + "'，使用默认值：" + defaultValue);
            return new ValidationResult(true, errors, warnings);
        }
        
        String value = config.get(field).getAsString();
        boolean valid = false;
        for (String allowed : allowedValues) {
            if (allowed.equals(value)) {
                valid = true;
                break;
            }
        }
        
        if (!valid) {
            errors.add("配置 '" + configName + "' 的字段 '" + field + "' 值无效：" + value + 
                      "（允许值：" + String.join(", ", allowedValues) + "）");
        }
        
        return new ValidationResult(valid, errors, warnings);
    }
    
    /**
     * 验证数组
     */
    public ValidationResult validateArray(JsonObject config, String configName, String field, 
                                        boolean required, int minLength, int maxLength) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        if (!config.has(field)) {
            if (required) {
                errors.add("配置 '" + configName + "' 缺少必填数组字段：" + field);
                return new ValidationResult(false, errors, warnings);
            }
            return ValidationResult.success();
        }
        
        if (!config.get(field).isJsonArray()) {
            errors.add("配置 '" + configName + "' 的字段 '" + field + "' 类型错误（应为数组）");
            return new ValidationResult(false, errors, warnings);
        }
        
        int size = config.getAsJsonArray(field).size();
        if (size < minLength || size > maxLength) {
            errors.add("配置 '" + configName + "' 的数组字段 '" + field + "' 长度超出范围 [" + 
                      minLength + ", " + maxLength + "]：当前长度=" + size);
        }
        
        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }
    
    /**
     * 验证对象结构
     */
    public ValidationResult validateObject(JsonObject config, String configName, String field, 
                                         boolean required, Map<String, Class<?>> fieldTypes) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        if (!config.has(field)) {
            if (required) {
                errors.add("配置 '" + configName + "' 缺少必填对象字段：" + field);
                return new ValidationResult(false, errors, warnings);
            }
            return ValidationResult.success();
        }
        
        if (!config.get(field).isJsonObject()) {
            errors.add("配置 '" + configName + "' 的字段 '" + field + "' 类型错误（应为对象）");
            return new ValidationResult(false, errors, warnings);
        }
        
        JsonObject obj = config.getAsJsonObject(field);
        for (Map.Entry<String, Class<?>> entry : fieldTypes.entrySet()) {
            String subField = entry.getKey();
            Class<?> expectedType = entry.getValue();
            
            if (!obj.has(subField)) {
                errors.add("配置 '" + configName + "' 的对象字段 '" + field + "' 缺少子字段：" + subField);
                continue;
            }
            
            JsonElement subElement = obj.get(subField);
            if (!isTypeMatch(subElement, expectedType)) {
                errors.add("配置 '" + configName + "' 的字段 '" + field + "." + subField + 
                          "' 类型错误（应为 " + expectedType.getSimpleName() + "）");
            }
        }
        
        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }
    
    private boolean isTypeMatch(JsonElement element, Class<?> expectedType) {
        if (expectedType == String.class) {
            return element.isJsonPrimitive() && element.getAsJsonPrimitive().isString();
        } else if (expectedType == Integer.class || expectedType == int.class) {
            return element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber();
        } else if (expectedType == Double.class || expectedType == double.class) {
            return element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber();
        } else if (expectedType == Boolean.class || expectedType == boolean.class) {
            return element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean();
        } else if (expectedType == JsonObject.class) {
            return element.isJsonObject();
        } else if (expectedType == JsonArray.class) {
            return element.isJsonArray();
        }
        return true; // 未知类型默认通过
    }
    
    /**
     * 完整验证配置
     */
    public ValidationResult validate(JsonObject config, String configName, ValidationSchema schema) {
        List<String> allErrors = new ArrayList<>();
        List<String> allWarnings = new ArrayList<>();
        
        // 验证版本
        ValidationResult versionResult = validateVersion(config, configName);
        allErrors.addAll(versionResult.getErrors());
        allWarnings.addAll(versionResult.getWarnings());
        
        // 验证必填字段
        if (schema.requiredFields != null && !schema.requiredFields.isEmpty()) {
            ValidationResult fieldsResult = validateRequiredFields(config, configName, schema.requiredFields);
            allErrors.addAll(fieldsResult.getErrors());
        }
        
        // 验证数值范围
        if (schema.rangeChecks != null) {
            for (RangeCheck check : schema.rangeChecks) {
                ValidationResult rangeResult = validateRange(config, configName, check.field, 
                                                           check.min, check.max, check.defaultValue);
                allErrors.addAll(rangeResult.getErrors());
                allWarnings.addAll(rangeResult.getWarnings());
            }
        }
        
        return new ValidationResult(allErrors.isEmpty(), allErrors, allWarnings);
    }
    
    /**
     * 验证 Schema 定义
     */
    public static class ValidationSchema {
        public List<String> requiredFields = new ArrayList<>();
        public List<RangeCheck> rangeChecks = new ArrayList<>();
        
        public ValidationSchema addRequiredField(String field) {
            requiredFields.add(field);
            return this;
        }
        
        public ValidationSchema addRangeCheck(String field, double min, double max, double defaultValue) {
            rangeChecks.add(new RangeCheck(field, min, max, defaultValue));
            return this;
        }
    }
    
    public static class RangeCheck {
        public final String field;
        public final double min;
        public final double max;
        public final double defaultValue;
        
        public RangeCheck(String field, double min, double max, double defaultValue) {
            this.field = field;
            this.min = min;
            this.max = max;
            this.defaultValue = defaultValue;
        }
    }
}
