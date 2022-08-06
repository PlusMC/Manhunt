package dev.oakleycord.manhunt.game.assets.boards;

import dev.oakleycord.manhunt.game.SoloRun;
import org.plusmc.pluslib.bukkit.managed.PlusBoard;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.GOLD;

public class SoloBoard extends PlusBoard {
    SoloRun game;

    public SoloBoard(SoloRun game) {
        super(GOLD + "SpeedRun Solo");
        this.game = game;
    }

    @Override
    public List<String> getEntries(long tick) {
        List<String> entries = new ArrayList<>();
        entries.add("Time: §b%time%");
        entries.add("Mode: §b%mode%");
        return entries;
    }

    @Override
    public boolean useVariables() {
        return true;
    }
}
