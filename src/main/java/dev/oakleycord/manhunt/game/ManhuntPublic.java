package dev.oakleycord.manhunt.game;

import dev.oakleycord.manhunt.game.boards.ManHuntPublicBoard;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.plusmc.pluslib.bukkit.handlers.VariableHandler;
import org.plusmc.pluslibcore.reflection.bungeebukkit.config.ConfigEntry;
import org.plusmc.pluslibcore.reflection.bungeebukkit.config.InjectableConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.md_5.bungee.api.ChatColor.AQUA;
import static net.md_5.bungee.api.ChatColor.GOLD;

public class ManhuntPublic extends ManHunt {
    private boolean starting = false;

    private @ConfigEntry int maxPlayers = 6;
    private @ConfigEntry double percentRunners = 0.5;
    private int startTicks = 30 * 20; //30 seconds

    public ManhuntPublic(InjectableConfig config) {
        super();
        config.inject(this);
        setBoard(new ManHuntPublicBoard(this, getScoreboard()));
    }

    public boolean isStarting() {
        return starting;
    }

    @Override
    public void startGame() {
        List<Player> players = new ArrayList<>(getPlayers());
        Collections.shuffle(players);
        for (int i = 0; i < players.size(); i++) {
            if (i < players.size() * percentRunners) {
                setTeam(players.get(i), MHTeam.RUNNERS);
            } else setTeam(players.get(i), MHTeam.HUNTERS);
        }
        super.startGame();
    }

    @Override
    public void tick(long tick) {
        super.tick(tick);
        if (getState() != GameState.PREGAME) return;
        if (!starting) return;
        if (startTicks > 0) {
            startTicks--;

            if (startTicks % 20 != 0)
                return;


            int seconds = (int) ((startTicks - 100) / 20.0);

            if (seconds == 10 || seconds <= 5)
                playHotBar();

        } else {
            this.startGame();
            starting = false;
        }
    }

    @Override
    public void onPlayerJoin(Player player) {
        super.onPlayerJoin(player);
        updateStartTicks();
    }

    @Override
    public void onPlayerQuit(Player player) {
        super.onPlayerQuit(player);
        updateStartTicks(true);
    }

    public void updateStartTicks() {
        updateStartTicks(false);
    }

    public void updateStartTicks(boolean leave) {
        if (getState() != GameState.PREGAME) return;
        int amountOfPlayers = getPlayers().size();
        if (leave)
            amountOfPlayers--;

        if (amountOfPlayers >= 2 && !starting) {
            starting = true;
            startTicks = 30 * 20;
            playHotBar();

        } else if (amountOfPlayers < 2) starting = false;

        if (amountOfPlayers > maxPlayers * 0.5f && startTicks > 10 * 20) {
            startTicks = 10 * 20;
            playHotBar();
        }
    }

    public void playHotBar() {
        TextComponent message = new TextComponent("Starting in: ");
        TextComponent seconds = new TextComponent(startTicks / 20 + "s");
        message.setColor(GOLD);
        seconds.setColor(AQUA);
        message.addExtra(seconds);
        getPlayers().forEach(player -> {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
            player.spigot().sendMessage(ChatMessageType.SYSTEM, message);

            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
        });
    }

    @Override
    public void updateVariables() {
        super.updateVariables();
        VariableHandler.setVariable("maxPlayers", String.valueOf(maxPlayers));
        VariableHandler.setVariable("startTime", startTicks / 20 + "s");
    }
}
