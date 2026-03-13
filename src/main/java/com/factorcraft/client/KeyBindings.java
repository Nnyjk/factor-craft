package com.factorcraft.client;

import com.factorcraft.module.ui.screen.TraitDisplayScreen;
import com.factorcraft.module.ui.screen.FactorMonitorScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    private static KeyBinding viewTraits;
    private static KeyBinding viewFactor;
    
    public static void register() {
        viewTraits = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.factorcraft.view_traits",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_J,
            "category.factorcraft"
        ));
        
        viewFactor = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.factorcraft.view_factor",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "category.factorcraft"
        ));
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (viewTraits.wasPressed() && client.player != null) {
                ItemStack held = client.player.getMainHandStack();
                client.setScreen(new TraitDisplayScreen(held));
            }
            
            if (viewFactor.wasPressed() && client.player != null && client.world != null) {
                var chunkPos = client.player.getChunkPos();
                client.setScreen(new FactorMonitorScreen(chunkPos));
            }
        });
    }
}