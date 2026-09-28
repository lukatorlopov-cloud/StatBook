package net.lukatorlopov.statbook;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import java.util.List;

public class StatBookScreen extends Screen {
    private enum Tab { MOBS, BLOCKS }
    private Tab currentTab = Tab.MOBS;
    private TextFieldWidget searchField;
    private ButtonWidget mobsButton, blocksButton;

    public StatBookScreen() { super(Text.translatable("statbook.screen.title")); }

    @Override protected void init() {
        mobsButton = ButtonWidget.builder(Text.translatable("statbook.tabs.mobs"), b -> select(Tab.MOBS)).dimensions(width/2-125, 24, 120, 20).build();
        blocksButton = ButtonWidget.builder(Text.translatable("statbook.tabs.blocks"), b -> select(Tab.BLOCKS)).dimensions(width/2+5, 24, 120, 20).build();
        searchField = new TextFieldWidget(textRenderer, width/2-150, 52, 300, 20, Text.translatable("statbook.search"));
        searchField.setMaxLength(64);
        addDrawableChild(mobsButton); addDrawableChild(blocksButton); addDrawableChild(searchField);
        select(currentTab);
    }

    private void select(Tab tab) { currentTab = tab; if (mobsButton != null) { mobsButton.active = tab != Tab.MOBS; blocksButton.active = tab != Tab.BLOCKS; } }

    @Override public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, Text.translatable("statbook.screen.title"), width/2, 8, 0xFFFFFFFF);
        context.drawText(textRenderer, Text.translatable(currentTab == Tab.MOBS ? "statbook.stats.header.mobs" : "statbook.stats.header.blocks"), width/2-150, 82, 0xFFB8D7FF, false);
        List<StatTracker.StatEntry> entries = currentTab == Tab.MOBS ? StatTracker.getMobEntries(searchField.getText()) : StatTracker.getBlockEntries(searchField.getText());
        int y = 100;
        for (StatTracker.StatEntry entry : entries) {
            if (y > height - 40) break;
            context.drawText(textRenderer, "• " + entry.name, width/2-150, y, 0xFFFFFFFF, false);
            context.drawText(textRenderer, Integer.toString(entry.count), width/2+115, y, 0xFF80F0FF, false);
            context.drawText(textRenderer, entry.detail, width/2-140, y+11, 0xFFB3B3B3, false);
            y += 28;
        }
        super.render(context, mouseX, mouseY, delta);
    }
}
