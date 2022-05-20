package dev.oakleycord.manhunt.game.logic.modes;

import dev.oakleycord.manhunt.game.AbstractRun;
import dev.oakleycord.manhunt.game.logic.Logic;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public enum Mode {
    CLASSIC(Classic.class, Material.DRAGON_HEAD, "§fRunners must reach the end and kill the Ender Dragon before the Hunters kills them!", "§fTruly a classic."),
    PORTAL(Portal.class, Material.NETHERRACK, "§fRunners must build and go through a Nether Portal before the Hunters kills them!", "§fYIPEEEEE! I have no idea why i'm exited!"),
    ENDEREYE(EnderEye.class, Material.ENDER_EYE, "§fRunners must reach the nether and craft an Ender Eye before the Hunters kills them!", "§fWHAAAAAT?");

    public final Material icon;
    final Class<? extends Logic> logic;
    private final String[] description;

    Mode(Class<? extends Logic> logic, Material icon, String... description) {
        this.icon = icon;
        this.description = description;
        this.logic = logic;
    }

    public Logic getLogic(AbstractRun game) {
        try {
            Constructor<? extends Logic> constructor = this.logic.getDeclaredConstructor(AbstractRun.class);
            constructor.setAccessible(true);
            return constructor.newInstance(game);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.broadcastMessage("UNABLE TO GET LOGIC FOR GAMEMODE USING CLASSIC% INSTEAD...");
            return new Classic(game);
        }
    }

    public String[] getDescription() {
        return Arrays.copyOf(description, description.length);
    }
}
