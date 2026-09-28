package net.lukatorlopov.statbook;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public final class StatTracker {
    private static final Map<String, MobStat> mobStats = new LinkedHashMap<>();
    private static final Map<String, BlockStat> blockStats = new LinkedHashMap<>();

    private StatTracker() {
    }

    public static void load() {
        // Initial load is intentionally empty. The book can be populated via commands or future sync logic.
    }

    public static void clear() {
        mobStats.clear();
        blockStats.clear();
    }

    public static void recordMobKill(String mobName, String weaponName) {
        String normalizedName = normalize(mobName);
        MobStat stat = mobStats.computeIfAbsent(normalizedName, ignored -> new MobStat());
        stat.count++;
        if (weaponName != null && !weaponName.isBlank()) {
            stat.weaponCounts.merge(weaponName, 1, Integer::sum);
            stat.favoriteWeapon = pickFavorite(stat.weaponCounts);
        }
    }

    public static void recordBlockBreak(String blockName, String toolName) {
        String normalizedName = normalize(blockName);
        BlockStat stat = blockStats.computeIfAbsent(normalizedName, ignored -> new BlockStat());
        stat.count++;
        if (toolName != null && !toolName.isBlank()) {
            stat.toolCounts.merge(toolName, 1, Integer::sum);
            stat.favoriteTool = pickFavorite(stat.toolCounts);
        }
    }

    public static List<StatEntry> getMobEntries(String filterText) {
        return collectEntries(mobStats, filterText, stat -> new StatEntry(stat.name, stat.count, stat.favoriteWeapon == null ? "Favorite weapon: Unknown" : "Favorite weapon: " + stat.favoriteWeapon));
    }

    public static List<StatEntry> getBlockEntries(String filterText) {
        return collectEntries(blockStats, filterText, stat -> new StatEntry(stat.name, stat.count, stat.favoriteTool == null ? "Tool: Unknown" : "Tool: " + stat.favoriteTool));
    }

    private static <T> List<StatEntry> collectEntries(Map<String, T> stats, String filterText, Function<T, StatEntry> mapper) {
        List<StatEntry> entries = new ArrayList<>();
        String trimmed = filterText == null ? "" : filterText.trim().toLowerCase(Locale.ROOT);

        for (Map.Entry<String, T> entry : stats.entrySet()) {
            String name = entry.getKey();
            if (!trimmed.isEmpty() && !name.toLowerCase(Locale.ROOT).contains(trimmed)) {
                continue;
            }
            entries.add(mapper.apply(entry.getValue()));
        }

        entries.sort(Comparator.comparingInt((StatEntry value) -> value.count).reversed());
        return entries;
    }

    private static String normalize(String value) {
        if (value == null) {
            return "Unknown";
        }
        String result = value.trim();
        if (result.isEmpty()) {
            return "Unknown";
        }
        return result.replace('_', ' ');
    }

    private static String pickFavorite(Map<String, Integer> counts) {
        return counts.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .findFirst()
            .map(Map.Entry::getKey)
            .orElse("Unknown");
    }

    public static final class StatEntry {
        public final String name;
        public final int count;
        public final String detail;

        public StatEntry(String name, int count, String detail) {
            this.name = name;
            this.count = count;
            this.detail = detail;
        }
    }

    private static final class MobStat {
        private int count;
        private final Map<String, Integer> weaponCounts = new HashMap<>();
        private String favoriteWeapon = "Unknown";
        private final String name;

        private MobStat() {
            this.name = "";
        }
    }

    private static final class BlockStat {
        private int count;
        private final Map<String, Integer> toolCounts = new HashMap<>();
        private String favoriteTool = "Unknown";
        private final String name;

        private BlockStat() {
            this.name = "";
        }
    }
}
