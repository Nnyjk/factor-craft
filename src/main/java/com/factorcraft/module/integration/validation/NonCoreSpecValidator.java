package com.factorcraft.module.integration.validation;

import com.factorcraft.module.integration.model.NonCoreContentSpec;

import java.util.regex.Pattern;

/**
 * 统一校验标准，确保第三方包接入数据一致。
 */
public final class NonCoreSpecValidator {
    private static final Pattern CONTENT_ID_PATTERN = Pattern.compile("^[a-z0-9_.-]+:[a-z0-9_/.-]+$");

    private NonCoreSpecValidator() {}

    public static void validate(NonCoreContentSpec spec) {
        require(spec != null, "spec must not be null");
        require(nonEmpty(spec.contentId()), "contentId must not be empty");
        require(CONTENT_ID_PATTERN.matcher(spec.contentId()).matches(), "contentId must be namespace:path");
        require(nonEmpty(spec.sourcePackId()), "sourcePackId must not be empty");
        require(nonEmpty(spec.displayName()), "displayName must not be empty");
        require(spec.tierWindow() != null, "tierWindow must not be null");
        require(spec.conductivityCost() >= 0, "conductivityCost must be >= 0");
    }

    private static boolean nonEmpty(String value) {
        return value != null && !value.isBlank();
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
}
