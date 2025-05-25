package win.yanagi.yanagiLib.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.Objects;

public class Messages {
    private final JavaPlugin plugin;
    private final String resourceFileName;
    private FileConfiguration messageConfig;

    public Messages(@NotNull JavaPlugin plugin, @NotNull String resourceFileName) {
        this.plugin = plugin;
        this.resourceFileName = resourceFileName;
    }

    // メッセージファイルをロードして登録
    public void loadConfig() {
        File file = new File(plugin.getDataFolder(), resourceFileName);
        if (!file.exists()) {
            plugin.saveResource(resourceFileName, false);
        }

        messageConfig = YamlConfiguration.loadConfiguration(file);
    }

    // メッセージファイルの値を変換なしでString型として取得
    public @NotNull String getRawString(@NotNull MessageKey key) {
        String message = messageConfig.getString(key.getKey());

        return Objects.requireNonNullElse(message, "");
    }

    // メッセージファイルの値を変換なしでComponent型として取得
    public @NotNull Component getRawComponent(@NotNull MessageKey key) {
        String message = getRawString(key);

        return Component.text(message);
    }

    // メッセージファイルの値を変換ありでString型として取得
    public @NotNull String getString(@NotNull MessageKey key, @NotNull Object... replacers) {
        String message = getRawString(key);
        TagResolver tagResolver = getTagResolver(replacers);

        Component messageComponent = MiniMessage.miniMessage().deserialize(message, tagResolver);

        return LegacyComponentSerializer.legacySection().serialize(messageComponent);
    }

    // メッセージファイルの値を変換ありでComponent型として取得
    public @NotNull Component getComponent(@NotNull MessageKey key, @NotNull Object... replacers) {
        String message = getRawString(key);
        TagResolver tagResolver = getTagResolver(replacers);

        return MiniMessage.miniMessage().deserialize(message, tagResolver);
    }

    // メッセージファイルの値をターゲットに送信
    public void send(@NotNull CommandSender target, @NotNull MessageKey key, @NotNull Object... replacers) {
        Component message = getComponent(key, replacers);
        if (message == Component.empty()) {
            return;
        }

        target.sendMessage(message);
    }

    // メッセージファイルの値を複数のターゲットに送信
    public void send(@NotNull Collection<CommandSender> targets, @NotNull MessageKey key, @NotNull Object... replacers) {
        Component message = getComponent(key, replacers);
        if (message == Component.empty()) {
            return;
        }

        targets.forEach(target -> target.sendMessage(message));
    }

    // TargetResolverを取得
    public TagResolver getTagResolver(@NotNull Object... replacers) {
        TagResolver.Builder resolverBuilder = TagResolver.builder();

        for (int i = 0; i < replacers.length; i += 2) {
            if (i + 1 < replacers.length) {
                resolverBuilder.resolver(Placeholder.unparsed(String.valueOf(replacers[i]), String.valueOf(replacers[i + 1])));
            }
        }

        return resolverBuilder.build();
    }
}
