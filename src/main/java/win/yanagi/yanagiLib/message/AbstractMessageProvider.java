package win.yanagi.yanagiLib.message;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractMessageProvider {
    // メッセージファイルをロードして登録
    public abstract void load();

    // 登録したメッセージ情報をクリア
    public abstract void clear();

    // TargetResolverを取得
    protected @NotNull TagResolver getTagResolver(@NotNull Object... replacers) {
        TagResolver.Builder resolverBuilder = TagResolver.builder();

        for (int i = 0; i < replacers.length; i += 2) {
            if (i + 1 < replacers.length) {
                resolverBuilder.resolver(Placeholder.unparsed(String.valueOf(replacers[i]), String.valueOf(replacers[i + 1])));
            }
        }

        return resolverBuilder.build();
    }
}
