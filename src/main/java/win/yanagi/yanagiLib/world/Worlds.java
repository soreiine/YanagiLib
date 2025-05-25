package win.yanagi.yanagiLib.world;

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
    public static void deleteWorld(@NotNull World world) throws IOException {
        File file = world.getWorldFolder();

        FileUtils.deleteDirectory(file);
    }
}
