package com.factorcraft;

import com.factorcraft.dynamic.DynamicBundle;
import com.factorcraft.dynamic.DynamicContentManager;
import net.fabricmc.api.ClientModInitializer;

public class FactorCraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DynamicBundle bundle = DynamicContentManager.getInstance().current();
        FactorCraftMod.LOGGER.info(
                "Factor Craft 客户端初始化完成。textures={}, models={}, languages={}, commands={}",
                bundle.textures().size(),
                bundle.models().size(),
                bundle.languages().size(),
                bundle.commands().size()
        );
    }
}
