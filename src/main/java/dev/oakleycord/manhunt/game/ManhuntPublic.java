package dev.oakleycord.manhunt.game;

import dev.oakleycord.manhunt.game.boards.ManHuntPublicBoard;
import org.bukkit.entity.Player;
import org.plusmc.pluslib.bukkit.handlers.VariableHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManhuntPublic extends ManHunt {
    private boolean starting = false;
    private final int maxPlayers = 6;
    private int startTicks = 30 * 20; //30 seconds

    public ManhuntPublic() {
        super();
        setBoard(new ManHuntPublicBoard(this, getScoreboard()));
    }

    public boolean isStarting() {
        return starting;
    }

    @Override
    public void startGame() {
        super.startGame();
        float percentRunners = 0.5f;
        List<Player> players = new ArrayList<>(getPlayers());
        Collections.shuffle(players);
        for (int i = 0; i < players.size(); i++) {
            if (i < players.size() * percentRunners) {
                players.get(i).sendTitle("", "You are a runner", 10, 20, 10);
                setTeam(players.get(i), MHTeam.RUNNERS);
            } else {
                players.get(i).sendTitle("", "You are a hunter", 10, 20, 10);
                setTeam(players.get(i), MHTeam.HUNTERS);
            }

        }
    }

    @Override
    public void tick(long tick) {
        super.tick(tick);
        if (getState() == GameState.INGAME) return;
        if (!starting) return;
        if (startTicks > 0) {
            startTicks--;
        } else {
            this.startGame();
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
        updateStartTicks();
    }

    public void updateStartTicks() {
        if (getState() == GameState.INGAME) return;
        int amountOfPlayers = getPlayers().size();
        if (amountOfPlayers < 2 && !starting) {
            starting = true;
            startTicks = 30 * 20;

        } else starting = false;

        if (amountOfPlayers < maxPlayers * 0.5f && startTicks > 10 * 20) //TODO: add hotbar message once triggered with a click sound
            startTicks = 10 * 20;
    }

    @Override
    public void updateVariables() {
        super.updateVariables();
        VariableHandler.setVariable("maxPlayers", String.valueOf(maxPlayers));
        VariableHandler.setVariable("startTime", startTicks / 20 + "s");
    }
}
