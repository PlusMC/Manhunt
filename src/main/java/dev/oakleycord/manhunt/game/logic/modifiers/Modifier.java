package dev.oakleycord.manhunt.game.logic.modifiers;

import dev.oakleycord.manhunt.game.AbstractRun;
import dev.oakleycord.manhunt.game.logic.Logic;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.lang.reflect.Constructor;

public enum Modifier {
    TEAM_SWAP(TeamSwap.class, Material.HEART_OF_THE_SEA, true, "TS", "Team Swap", "§fEvery 1 to 3 minutes, everyone will swap teams!"),
    BLOCK_RAIN(BlockRain.class, Material.ANVIL, "BR", "Block Rain", "§fIt's raining blocks! How fun!", "§fContainers will have random loot, keep a look out!"),
    //KING_SLIME(KingSlime.class, Material.SLIME_BLOCK, "KS", "King Slime", "§fKing Slime appears!", "§fYou better watch out! It'll destroy everything in its path!"),
    QUICK_GAME(QuickGame.class, Material.SUGAR, true, "QG", "Quick Game", "§fEveryone will get tools and armor!");

    public final String sortName;
    public final String itemName;
    public final Material icon;
    public final Class<? extends Logic> logic;
    private final String[] description;
    public final boolean manHuntOnly;

    Modifier(Class<? extends Logic> logic, Material icon, String sortName, String itemName, String... description) {
        this.icon = icon;
        this.itemName = itemName;
        this.description = description;
        this.logic = logic;
        this.sortName = sortName;
        this.manHuntOnly = false;
    }

    Modifier(Class<? extends Logic> logic, Material icon, boolean manHuntOnly, String sortName, String itemName, String... description) {
        this.icon = icon;
        this.itemName = itemName;
        this.description = description;
        this.logic = logic;
        this.sortName = sortName;
        this.manHuntOnly = manHuntOnly;
    }


    public Logic getLogic(AbstractRun game) {
        try {
            Constructor<? extends Logic> constructor = this.logic.getDeclaredConstructor(AbstractRun.class);
            constructor.setAccessible(true);
            return constructor.newInstance(game);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.broadcastMessage("UNABLE TO GET LOGIC FOR MODIFIER USING EMPTY INSTEAD...");
            return new Empty(game);
        }
    }

    public String[] getDescription() {
        return description.clone();
    }
}
