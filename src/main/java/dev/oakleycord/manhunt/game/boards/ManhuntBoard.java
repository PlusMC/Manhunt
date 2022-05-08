package dev.oakleycord.manhunt.game.boards;

import dev.oakleycord.manhunt.game.GameState;
import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.util.OtherUtil;
import org.plusmc.pluslib.bukkit.managed.PlusBoard;

import java.util.ArrayList;
import java.util.List;

public class ManhuntBoard extends PlusBoard {
    private final MHGame game;

    public ManhuntBoard(MHGame game) {
        super("§6§l§n§oManHunt");
        this.game = game;
    }

    @Override
    public List<String> getEntries(long tick) {
        List<String> entries = new ArrayList<>();
        entries.add("");
        entries.add("Players: §b" + game.getPlayers().size());
        entries.add("Hunters: §b" + game.getHunters().getSize());
        entries.add("Runners Alive: §b" + game.getRunners().getSize());

        if (game.getState() == GameState.INGAME)
            entries.add("Time: §b" + OtherUtil.formatTime(System.currentTimeMillis() - game.getTimeStamp()));

        entries.add("Mode: §b" + game.getGameMode().name() + "%");

        if (!game.getModifiers().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            game.getModifiers().forEach(modifier -> sb.append(modifier.sortName).append(", "));
            sb.delete(sb.length() - 2, sb.length());
            entries.add("Modifiers: §b" + sb);
        }

        entries.add("");
        entries.add("§8§lGameState: " + game.getState());
        return entries;
    }

}
