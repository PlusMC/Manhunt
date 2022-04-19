package dev.oakleycord.manhunt.game.util;

import org.bukkit.World;

import java.io.File;

public class OtherUtil {
    public static String formatTime(long time) {
        //format time (ms) to hh:mm:ss
        long seconds = time / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
    }

    public static boolean isManHunt(World world) {
        return world.getName().startsWith("mh_world_");
    }

    public static void deleteDir(File dir) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                deleteDir(file);
            }
        }
        dir.delete();
    }
}
