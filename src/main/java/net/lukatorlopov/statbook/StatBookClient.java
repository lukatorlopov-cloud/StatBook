package net.lukatorlopov.statbook;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.client.player.ClientBlockBreakEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class StatBookClient implements ClientModInitializer {
    private static final String KEY_CATEGORY = "key.category.statbook";
    private final Set<UUID> observedDeadEntities = new HashSet<>();

    @Override
    public void onInitializeClient() {
        StatTracker.load();

        KeyBinding openBook = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.statbook.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_APOSTROPHE, KEY_CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openBook.wasPressed()) client.setScreen(new StatBookScreen());
            observeKilledEntities(client);
        });

        ClientBlockBreakEvents.AFTER.register((world, player, pos, state) -> {
            if (!world.isClient() || player != MinecraftClient.getInstance().player) return;
            ItemStack tool = player.getMainHandStack();
            StatTracker.recordBlockBreak(state.getBlock().getName().getString(),
                tool.isEmpty() ? "Hand" : tool.getName().getString());
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            observedDeadEntities.clear();
            StatTracker.load();
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> StatTracker.save());
    }

    private void observeKilledEntities(MinecraftClient client) {
        World world = client.world;
        PlayerEntity player = client.player;
        if (world == null || player == null) return;

        for (Entity entity : world.iterateEntities()) {
            if (!(entity instanceof LivingEntity living) || entity == player || !living.isDead()) continue;
            if (!observedDeadEntities.add(entity.getUuid())) continue;
            if (living.getLastAttacker() == player || living.getAttacker() == player) {
                ItemStack weapon = player.getMainHandStack();
                StatTracker.recordMobKill(living.getType().getName().getString(),
                    weapon.isEmpty() ? "Hand" : weapon.getName().getString());
            }
        }
    }
}
