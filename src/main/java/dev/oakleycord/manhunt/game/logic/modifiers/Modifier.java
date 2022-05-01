package dev.oakleycord.manhunt.game.logic.modifiers;

import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.logic.Logic;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.lang.reflect.Constructor;

public enum Modifier {
    TEAM_SWAP(TeamSwap.class, Material.HEART_OF_THE_SEA, "TS", "Team Swap", "§fEvery 1 to 3 minutes, everyone will swap teams!"),
    BLOCK_RAIN(BlockRain.class, Material.ANVIL, "BR", "Block Rain", "§fIt's raining blocks! How fun!", "§fContainers will have random loot, keep a look out!"),
    KING_SLIME(KingSlime.class, Material.SLIME_BLOCK, "KS", "King Slime", "§fKing Slime appears!", "§fYou better watch out! It'll destroy everything in its path!"),
    QUICK_GAME(QuickGame.class, Material.SUGAR, "QG", "Quick Game", "§fEveryone will get tools and armor!");

    public final String sortName;
    public final String name;
    public final String[] description;
    public final Material icon;

    public final Class<? extends Logic> logic;

    Modifier(Class<? extends Logic> logic, Material icon, String sortName, String name, String... description) {
        this.icon = icon;
        this.name = name;
        this.description = description;
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
