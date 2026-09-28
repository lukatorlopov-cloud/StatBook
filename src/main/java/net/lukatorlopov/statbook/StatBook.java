package net.lukatorlopov.statbook;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatBook implements ModInitializer {
    public static final String MOD_ID = "statbook";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("StatBook initialized");
        StatTracker.load();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("statbook")
                .then(CommandManager.literal("addkill")
                    .then(CommandManager.argument("entity", StringArgumentType.word())
                        .then(CommandManager.argument("weapon", StringArgumentType.word())
                            .executes(context -> {
                                String entity = context.getArgument("entity", String.class);
                                String weapon = context.getArgument("weapon", String.class);
                                StatTracker.recordMobKill(entity, weapon);
                                context.getSource().sendFeedback(() -> Text.literal("Recorded kill: " + entity + " / " + weapon), false);
                                return 1;
                            })
                        )
                        .executes(context -> {
                            String entity = context.getArgument("entity", String.class);
                            StatTracker.recordMobKill(entity, "Unknown");
                            context.getSource().sendFeedback(() -> Text.literal("Recorded kill: " + entity + " / Unknown"), false);
                            return 1;
                        })
                    )
                )
                .then(CommandManager.literal("addblock")
                    .then(CommandManager.argument("block", StringArgumentType.word())
                        .then(CommandManager.argument("tool", StringArgumentType.word())
                            .executes(context -> {
                                String block = context.getArgument("block", String.class);
                                String tool = context.getArgument("tool", String.class);
                                StatTracker.recordBlockBreak(block, tool);
                                context.getSource().sendFeedback(() -> Text.literal("Recorded block: " + block + " / " + tool), false);
                                return 1;
                            })
                        )
                        .executes(context -> {
                            String block = context.getArgument("block", String.class);
                            StatTracker.recordBlockBreak(block, "Unknown");
                            context.getSource().sendFeedback(() -> Text.literal("Recorded block: " + block + " / Unknown"), false);
                            return 1;
                        })
                    )
                )
                .then(CommandManager.literal("clear")
                    .executes(context -> {
                        StatTracker.clear();
                        context.getSource().sendFeedback(() -> Text.literal("StatBook data cleared."), false);
                        return 1;
                    })
                )
            );
        });
    }
}
