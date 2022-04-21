package dev.oakleycord.manhunt.game.enums;

import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.logic.Logic;
import dev.oakleycord.manhunt.game.logic.modes.Classic;
import dev.oakleycord.manhunt.game.logic.modes.EnderEye;
import dev.oakleycord.manhunt.game.logic.modes.Portal;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;

public enum Mode {
    CLASSIC(Classic.class), PORTAL(Portal.class), ENDEREYE(EnderEye.class);

    final Class<? extends Logic> logic;

    Mode(Class<? extends Logic> logic) {
        this.logic = logic;
    }

    public Logic getLogic(MHGame game) {
        try {
            Constructor<? extends Logic> constructor = this.logic.getDeclaredConstructor(MHGame.class);
            constructor.setAccessible(true);
            return constructor.newInstance(game);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.broadcastMessage("UNABLE TO GET LOGIC FOR GAMEMODE USING CLASSIC% INSTEAD...");
            return new Classic(game);
        }
    }
}
