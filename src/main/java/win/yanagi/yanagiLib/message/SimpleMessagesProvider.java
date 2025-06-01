package win.yanagi.yanagiLib.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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

public class SimpleMessagesProvider extends AbstractMessageProvider {
    private final JavaPlugin plugin;
    private final String resourceFileName;
    private FileConfiguration messageConfig;

    public SimpleMessagesProvider(@NotNull JavaPlugin plugin, @NotNull String resourceFileName) {
        this.plugin = plugin;
        this.resourceFileName = resourceFileName;
    }

    // メッセージファイルをロードして登録
    @Override
    public void load() {
        clear();

        File file = new File(plugin.getDataFolder(), resourceFileName);
        if (!file.exists()) {
            plugin.saveResource(resourceFileName, false);
        }

        messageConfig = YamlConfiguration.loadConfiguration(file);
    }

    // 登録したメッセージ情報をクリア
    @Override
    public void clear() {
        messageConfig = null;
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
}
