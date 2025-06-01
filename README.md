# YanagiLib
自分用に作成したPaperプラグインの作成を補助するユーティリティライブラリ

## 特徴
- プレイヤーの状態の管理が（一部）楽に
- ワールドの管理が（一部）楽に
- メッセージのYamlファイルでの管理をまとめて担当する

## インストール
### Maven
```xml
<project>
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>com.github.soreiine</groupId>
            <artifactId>YanagiLib</artifactId>
            <version>0.1</version>
        </dependency>
    </dependencies>
</project>
```

## 使い方

### 例: プレイヤーのインベントリ内のアイテムを削除する
`player.getInventory().clear();`だけでは削除できないアイテムもまとめて削除します
```java
Players.clearPlayerInventory(player);
```

### 例: プラグイン内の言語分けされたメッセージファイルの登録・使用
以下の例では、英語をデフォルト言語にし、追加で日本語を登録しています。

ファイルは`plugins/lang`にコピーされます。

また、メッセージはMiniMessage形式で装飾され、`MessageKey.RELOADED`の中の`<players-online>`がオンラインのプレイヤー数に置き換わり取得されます
```java
LocalizedMessagesProvider localizedMessagesProvider = new LocalizedMessagesProvider(plugin, "lang");

localizedMessagesProvider
        .registerDefault(Locale.ENGLISH, "en-US.yml")
        .register(Locale.JAPANESE, "ja-JP.yml")
        .load();

Component message = localizedMessagesProvider.getComponent(MessageKey.RELOADED, Locale.JAPANESE, "players-online", Bukkit.getOnlinePlayers().size());
```