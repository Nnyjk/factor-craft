package com.factorcraft;

import com.factorcraft.module.cycle.CycleModule;
import com.factorcraft.module.factor.FactorSystemModule;
import com.factorcraft.module.combat.CombatModule;
import com.factorcraft.module.creature.CreatureModule;
import com.factorcraft.module.loot.LootModule;
import com.factorcraft.module.network.NetworkModule;
import com.factorcraft.module.ui.UIModule;
import com.factorcraft.module.quest.QuestModule;
import com.factorcraft.module.technology.TechnologyModule;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factor Craft 主 Mod 类
 */
public class FactorCraftMod implements ModInitializer {
    
    public static final String MOD_ID = "factorcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Factor Craft Mod...");
        
        // 初始化所有模块
        FactorSystemModule.getInstance().initialize();
        CycleModule.getInstance().initialize();
        CombatModule.getInstance().initialize();
        CreatureModule.getInstance().initialize();
        LootModule.getInstance().initialize();
        NetworkModule.getInstance().initialize();
        UIModule.getInstance().initialize();
        QuestModule.getInstance().initialize();
        TechnologyModule.getInstance().initialize();
        
        LOGGER.info("Factor Craft Mod initialized successfully!");
    }
}
