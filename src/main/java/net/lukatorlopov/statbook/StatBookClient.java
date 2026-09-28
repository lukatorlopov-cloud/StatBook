package net.lukatorlopov.statbook;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class StatBookClient implements ClientModInitializer {
    private static final String KEY_CATEGORY = "key.category.statbook";

    @Override
    public void onInitializeClient() {
        KeyBinding openBook = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.statbook.open", InputUtil.Type.KEYSYM, InputUtil.fromTranslationKey("key.keyboard.b").getCode(), KEY_CATEGORY)
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openBook.wasPressed()) {
                client.setScreen(new StatBookScreen());
            }
        });
    }
}
