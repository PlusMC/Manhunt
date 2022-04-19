package dev.oakleycord.manhunt.game;

import dev.oakleycord.manhunt.game.logic.Logic;
import dev.oakleycord.manhunt.game.logic.gamemodes.Classic;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;

public enum GameMode {
    CLASSIC(Classic.class);

    final Class<? extends Logic> logic;

    GameMode(Class<? extends Logic> logic) {
        this.logic = logic;
    }

    public Logic getLogic(MHGame game) {
        try {
            Constructor<? extends Logic> constructor = this.logic.getConstructor(MHGame.class);
            return constructor.newInstance(game);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.broadcastMessage("UNABLE TO GET LOGIC FOR GAMEMODE USING CLASSIC% INSTEAD...");
            return new Classic(game);
        }
    }
}
