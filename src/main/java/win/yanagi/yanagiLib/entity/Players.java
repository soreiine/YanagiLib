package win.yanagi.yanagiLib.entity;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

public final class Players {
    // クラスのインスタンス化を防止
    private Players() {
    }

    // プレイヤーのインベントリ内のアイテムを削除するメソッド
    public static void clearPlayerInventory(@NotNull Player player) {
        // プレイヤーのインベントリのアイテムを削除
        player.getInventory().clear();

        // カーソルで持ち上げてるアイテムを追加削除
        player.setItemOnCursor(null);
        // クラフトグリッドのアイテムを追加削除（要検証）
        InventoryView openInventory = player.getOpenInventory();
        if (openInventory.getType() == InventoryType.CRAFTING) {
            for (int i = 0; i <= 4; i++) {
                openInventory.setItem(i, null);
            }
        }
    }

    // プレイヤーの体力をリセット
    public static void resetPlayerHealth(@NotNull Player player) {
        double health;

        // プレイヤーの最大体力を取得
        AttributeInstance maxHealthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        health = maxHealthAttribute != null ? maxHealthAttribute.getValue() : 20.0;

        // 取得した最大体力にセット
        player.setHealth(health);
    }

    // プレイヤーの満腹度をリセット
    public static void resetPlayerFoodLevel(@NotNull Player player) {
        player.setFoodLevel(20);
    }

    // プレイヤーの隠し満腹度をリセット
    public static void resetPlayerSaturation(@NotNull Player player) {
        player.setSaturation(20);
    }

    // プレイヤーの満腹度消費度をリセット
    public static void resetPlayerExhaustion(@NotNull Player player) {
        player.setExhaustion(0);
    }

    // プレイヤーの体力と満腹度全般をリセット
    public static void resetPlayerHealthAndHunger(@NotNull Player player) {
        resetPlayerHealth(player);
        resetPlayerFoodLevel(player);
        resetPlayerSaturation(player);
        resetPlayerExhaustion(player);
    }
}
