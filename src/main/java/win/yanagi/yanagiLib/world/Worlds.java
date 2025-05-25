package win.yanagi.yanagiLib.world;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.codehaus.plexus.util.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public final class Worlds {
    // クラスのインスタンス化を防止
    private Worlds() {
    }

    // 指定したワールドを削除
    public static boolean deleteWorld(@NotNull World world) {
        File file = world.getWorldFolder();

        boolean unloadSuccessful = Bukkit.unloadWorld(world, false);
        if (!unloadSuccessful) {
            return false;
        }

        try {
            FileUtils.deleteDirectory(file);
        } catch (IOException exception) {
            return false;
        }

        return true;
    }
}
