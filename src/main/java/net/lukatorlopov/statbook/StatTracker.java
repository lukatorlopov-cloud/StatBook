package net.lukatorlopov.statbook;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public final class StatTracker {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type FILE_TYPE = new TypeToken<SaveData>() {}.getType();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("statbook.json");
    private static final Map<String, MobStat> mobStats = new LinkedHashMap<>();
    private static final Map<String, BlockStat> blockStats = new LinkedHashMap<>();
    private static boolean loaded;

    private StatTracker() {}

    public static synchronized void load() {
        if (loaded) return;
        loaded = true;
        try (Reader reader = Files.newBufferedReader(FILE)) {
            SaveData data = GSON.fromJson(reader, FILE_TYPE);
            if (data != null) {
                if (data.mobs != null) mobStats.putAll(data.mobs);
                if (data.blocks != null) blockStats.putAll(data.blocks);
            }
        } catch (Exception ignored) { }
    }

    public static synchronized void save() {
        try {
            Files.createDirectories(FILE.getParent());
            SaveData data = new SaveData();
            data.mobs = mobStats;
            data.blocks = blockStats;
            try (Writer writer = Files.newBufferedWriter(FILE)) { GSON.toJson(data, FILE_TYPE, writer); }
        } catch (Exception ignored) { }
    }

    public static synchronized void clear() { mobStats.clear(); blockStats.clear(); save(); }

    public static synchronized void recordMobKill(String name, String weapon) {
        MobStat stat = mobStats.computeIfAbsent(normalize(name), key -> new MobStat());
        stat.count++;
        stat.weaponCounts.merge(normalize(weapon), 1, Integer::sum);
        stat.favoriteWeapon = favorite(stat.weaponCounts);
        save();
    }

    public static synchronized void recordBlockBreak(String name, String tool) {
        BlockStat stat = blockStats.computeIfAbsent(normalize(name), key -> new BlockStat());
        stat.count++;
        stat.toolCounts.merge(normalize(tool), 1, Integer::sum);
        stat.favoriteTool = favorite(stat.toolCounts);
        save();
    }

    public static synchronized List<StatEntry> getMobEntries(String filter) {
        return entries(mobStats, filter, stat -> new StatEntry(stat.name, stat.count, "Favorite weapon: " + stat.favoriteWeapon));
    }

    public static synchronized List<StatEntry> getBlockEntries(String filter) {
        return entries(blockStats, filter, stat -> new StatEntry(stat.name, stat.count, "Tool: " + stat.favoriteTool));
    }

    private static <T extends NamedStat> List<StatEntry> entries(Map<String, T> values, String filter, Function<T, StatEntry> mapper) {
        String query = filter == null ? "" : filter.trim().toLowerCase(Locale.ROOT);
        List<StatEntry> result = new ArrayList<>();
        values.values().stream()
            .filter(value -> query.isEmpty() || value.name.toLowerCase(Locale.ROOT).contains(query))
            .map(mapper).forEach(result::add);
        result.sort(Comparator.comparingInt((StatEntry entry) -> entry.count).reversed().thenComparing(entry -> entry.name));
        return result;
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) return "Unknown";
        return value.trim().replace('_', ' ');
    }

    private static String favorite(Map<String, Integer> values) {
        return values.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("Unknown");
    }

    public static final class StatEntry {
        public final String name; public final int count; public final String detail;
        public StatEntry(String name, int count, String detail) { this.name = name; this.count = count; this.detail = detail; }
    }
    private interface NamedStat { String getName(); }
    private static final class MobStat implements NamedStat {
        int count; String favoriteWeapon = "Unknown"; Map<String,Integer> weaponCounts = new HashMap<>(); String name;
        public String getName() { return name; }
    }
    private static final class BlockStat implements NamedStat {
        int count; String favoriteTool = "Unknown"; Map<String,Integer> toolCounts = new HashMap<>(); String name;
        public String getName() { return name; }
    }
    private static final class SaveData { Map<String,MobStat> mobs = new LinkedHashMap<>(); Map<String,BlockStat> blocks = new LinkedHashMap<>(); }
}
