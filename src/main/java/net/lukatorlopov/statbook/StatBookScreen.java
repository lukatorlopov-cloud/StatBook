package net.lukatorlopov.statbook;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Locale;

public class StatBookScreen extends Screen {
    private enum Tab { MOBS, BLOCKS }

    private Tab currentTab = Tab.MOBS;
    private TextFieldWidget searchField;

    public StatBookScreen() {
        super(Text.translatable("statbook.screen.title"));
    }

    @Override
    protected void init() {
        super.init();

        ButtonWidget mobsButton = ButtonWidget.builder(Text.translatable("statbook.tabs.mobs"), button -> currentTab = Tab.MOBS)
            .dimensions(width / 2 - 120, 20, 110, 20)
            .build();

        ButtonWidget blocksButton = ButtonWidget.builder(Text.translatable("statbook.tabs.blocks"), button -> currentTab = Tab.BLOCKS)
            .dimensions(width / 2 + 10, 20, 110, 20)
            .build();

        searchField = new TextFieldWidget(textRenderer, width / 2 - 150, 52, 300, 18, Text.translatable("statbook.search"));
        searchField.setMaxLength(64);

        addDrawableChild(mobsButton);
        addDrawableChild(blocksButton);
        addSelectableChild(searchField);
        addDrawableChild(searchField);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(textRenderer, Text.translatable("statbook.screen.title"), width / 2, 6, 0xFFFFFFFF);

        if (searchField != null) {
            searchField.render(context, mouseX, mouseY, delta);
        }

        List<StatTracker.StatEntry> entries = currentTab == Tab.MOBS
            ? StatTracker.getMobEntries(searchField == null ? "" : searchField.getText())
            : StatTracker.getBlockEntries(searchField == null ? "" : searchField.getText());

        String headerKey = currentTab == Tab.MOBS ? "statbook.stats.header.mobs" : "statbook.stats.header.blocks";
        context.drawText(textRenderer, Text.translatable(headerKey), width / 2 - 150, 82, 0xFFB8D7FF, false);

        int y = 100;
        for (StatTracker.StatEntry entry : entries) {
            if (y > height - 35) {
                break;
            }

            context.drawText(textRenderer, "• " + entry.name, width / 2 - 150, y, 0xFFFFFFFF, false);
            context.drawText(textRenderer, String.valueOf(entry.count), width / 2 + 110, y, 0xFF80F0FF, false);
            if (entry.detail != null && !entry.detail.isBlank()) {
                context.drawText(textRenderer, entry.detail, width / 2 - 150, y + 10, 0xFFB3B3B3, false);
                y += 18;
            }
            y += 14;
        }

        super.render(context, mouseX, mouseY, delta);
    }
}
