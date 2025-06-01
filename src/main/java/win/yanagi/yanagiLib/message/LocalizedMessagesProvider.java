package win.yanagi.yanagiLib.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class LocalizedMessagesProvider extends AbstractMessageProvider {
    private final JavaPlugin plugin;
    private final String folderName;
    private final Map<Locale, String> localeConfigNameMap;
    private final Map<Locale, FileConfiguration> localeConfigMap;
    private Locale defaultLocale = null;

    public LocalizedMessagesProvider(@NotNull JavaPlugin plugin, @NotNull String folderName) {
        this.plugin = plugin;
        this.folderName = folderName;
        this.localeConfigNameMap = new HashMap<>();
        this.localeConfigMap = new HashMap<>();
    }

    // メッセージファイルをロードして登録
    @Override
    public void load() {
        clear();

        if (localeConfigNameMap.isEmpty()) {
            plugin.getLogger().warning("No locale files registered. Skipping message loading.");
            return;
        }

        if (defaultLocale == null) {
            plugin.getLogger().warning("No default locale has been registered. Skipping message loading.");
            return;
        }

        File langFolder = new File(plugin.getDataFolder(), folderName);
        if (!langFolder.exists() && !langFolder.mkdirs()) {
            plugin.getLogger().warning("Failed to create a lang folder. Language features will not work properly.");
            return;
        }

        localeConfigNameMap.forEach((locale, configName) -> {
            File file = new File(plugin.getDataFolder(), folderName + File.separator + configName);
            if (!file.exists()) {
                plugin.saveResource(folderName + File.separator + configName, false);
            }

            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            localeConfigMap.put(locale, config);
        });
    }

    // 登録したメッセージ情報をクリア
    @Override
    public void clear() {
        localeConfigNameMap.clear();
        localeConfigMap.clear();
    }

    public @NotNull LocalizedMessagesProvider registerDefault(@NotNull Locale locale, @NotNull String resourceFileName) {
        this.defaultLocale = locale;
        register(locale, resourceFileName);

        return this;
    }

    public @NotNull LocalizedMessagesProvider register(@NotNull Locale locale, @NotNull String resourceFileName) {
        localeConfigNameMap.put(locale, resourceFileName);

        return this;
    }

    public @Nullable FileConfiguration getConfig(@NotNull Locale locale) {
        if (localeConfigMap.containsKey(locale)) {
            locale = defaultLocale;
        }

        return localeConfigMap.get(locale);
    }

    public @NotNull Locale getPlayerLocale(@NotNull Player player) {
        Locale playerLocale = player.locale();
        if (localeConfigMap.containsKey(playerLocale)) {
            return playerLocale;
        }

        return defaultLocale;
    }

    // メッセージファイルの値を変換なしでString型として取得
    public @NotNull String getRawString(@NotNull MessageKey key, @NotNull Locale locale) {
        FileConfiguration config = getConfig(locale);
        if (config == null) {
            return "";
        }

        String message = config.getString(key.getKey());

        return Objects.requireNonNullElse(message, "");
    }

    // メッセージファイルの値を変換なしでComponent型として取得
    public @NotNull Component getRawComponent(@NotNull MessageKey key, @NotNull Locale locale) {
        String message = getRawString(key, locale);

        return Component.text(message);
    }

    // メッセージファイルの値を変換ありでString型として取得
    public @NotNull String getString(@NotNull MessageKey key, @NotNull Locale locale, @NotNull Object... replacers) {
        String message = getRawString(key, locale);
        TagResolver tagResolver = getTagResolver(replacers);

        Component messageComponent = MiniMessage.miniMessage().deserialize(message, tagResolver);

        return LegacyComponentSerializer.legacySection().serialize(messageComponent);
    }

    // メッセージファイルの値を変換ありでComponent型として取得
    public @NotNull Component getComponent(@NotNull MessageKey key, @NotNull Locale locale, @NotNull Object... replacers) {
        String message = getRawString(key, locale);
        TagResolver tagResolver = getTagResolver(replacers);

        return MiniMessage.miniMessage().deserialize(message, tagResolver);
    }

    // メッセージファイルの値をターゲットに送信
    public void send(@NotNull CommandSender target, @NotNull MessageKey key, @NotNull Object... replacers) {
        Locale locale = (target instanceof Player player) ? getPlayerLocale(player) : defaultLocale;

        Component message = getComponent(key, locale, replacers);
        if (message == Component.empty()) {
            return;
        }

        target.sendMessage(message);
    }

    // メッセージファイルの値を複数のターゲットに送信
    public void send(@NotNull Collection<CommandSender> targets, @NotNull MessageKey key, @NotNull Object... replacers) {
        Map<Locale, List<CommandSender>> localeGroupMap = new HashMap<>();

        targets.forEach(target -> {
            Locale locale = (target instanceof Player player) ? getPlayerLocale(player) : defaultLocale;
            localeGroupMap.computeIfAbsent(locale, k -> new ArrayList<>()).add(target);
        });

        localeGroupMap.entrySet().forEach(entry -> {
            Locale locale = entry.getKey();
            List<CommandSender> groupedTargets = entry.getValue();

            Component message = getComponent(key, locale, replacers);
            if (message == Component.empty()) {
                return;
            }

            groupedTargets.forEach(target -> {
                target.sendMessage(message);
            });
        });
    }
}
