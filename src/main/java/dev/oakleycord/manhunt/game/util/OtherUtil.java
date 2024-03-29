package dev.oakleycord.manhunt.game.util;

import dev.oakleycord.manhunt.SpeedRuns;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class OtherUtil {
    public static final Enchantment EMPTY_ENCHANT = new EmptyEnchant(new NamespacedKey(SpeedRuns.getInstance(), "empty"));

    private OtherUtil() {
        throw new IllegalStateException("Utility class");
    }


    public static String formatTime(long time) {
        //format time (ms) to hh:mm:ss
        long seconds = time / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
    }

    public static boolean isWorldSR(World world) {
        return world.getName().startsWith("sr_world_");
    }


    private static class EmptyEnchant extends Enchantment {

        public EmptyEnchant(@NotNull NamespacedKey key) {
            super(key);
        }

        @NotNull
        @Override
        public String getName() {
            return "";
        }

        @Override
        public int getMaxLevel() {
            return 0;
        }

        @Override
        public int getStartLevel() {
            return 0;
        }

        @NotNull
        @Override
        public EnchantmentTarget getItemTarget() {
            return EnchantmentTarget.ARMOR;
        }

        @Override
        public boolean isTreasure() {
            return false;
        }

        @Override
        public boolean isCursed() {
            return false;
        }

        @Override
        public boolean conflictsWith(@NotNull Enchantment other) {
            return false;
        }

        @Override
        public boolean canEnchantItem(@NotNull ItemStack item) {
            return false;
        }
    }
}
