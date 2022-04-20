package dev.oakleycord.manhunt.game.enums;

import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.logic.Logic;
import dev.oakleycord.manhunt.game.logic.modifiers.Empty;
import dev.oakleycord.manhunt.game.logic.modifiers.TeamSwap;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;

public enum Modifier {
    TEAMSWAP(TeamSwap.class, "TS");

    public final String sortName;
    private final Class<? extends Logic> logic;

    Modifier(Class<? extends Logic> logic, String sortName) {
        this.logic = logic;
        this.sortName = sortName;
    }

    public Logic getLogic(MHGame game) {
        try {
            Constructor<? extends Logic> constructor = this.logic.getDeclaredConstructor(MHGame.class);
            constructor.setAccessible(true);
            return constructor.newInstance(game);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.broadcastMessage("UNABLE TO GET LOGIC FOR MODIFIER USING EMPTY INSTEAD...");
            return new Empty(game);
        }
    }
}
