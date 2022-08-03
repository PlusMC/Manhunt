package dev.oakleycord.manhunt.game;


import dev.oakleycord.manhunt.game.boards.ManhuntBoard;
import dev.oakleycord.manhunt.game.logic.handlers.CompassHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.plusmc.pluslib.bukkit.handlers.VariableHandler;

public class ManHunt extends AbstractRun {
    private final Team hunters;
    private final Team runners;
    private final Team spectators;

    private final CompassHandler compassHandler;

    public ManHunt() {
        super();
        this.compassHandler = new CompassHandler(this);

        this.hunters = getScoreboard().registerNewTeam("Hunters");
        hunters.setAllowFriendlyFire(false);
        hunters.setPrefix(ChatColor.RED + "" + ChatColor.BOLD + "[Hunter] ");

        this.runners = getScoreboard().registerNewTeam("Runners");
        runners.setAllowFriendlyFire(false);
        runners.setPrefix(ChatColor.GREEN + "" + ChatColor.BOLD + "[Runner] ");

        this.spectators = getScoreboard().registerNewTeam("Spectators");
        spectators.setPrefix(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "[Spectator] ");
        spectators.setColor(ChatColor.DARK_GRAY);
    }

    public void setTeam(Player player, MHTeam team) {
        Team t = getScoreboard().getEntryTeam(player.getName());
        if (t != null) t.removeEntry(player.getName());

        switch (team) {
            case HUNTERS -> {
                hunters.addEntry(player.getName());
                player.setGameMode(org.bukkit.GameMode.SURVIVAL);
            }
            case RUNNERS -> {
                runners.addEntry(player.getName());
                player.setGameMode(org.bukkit.GameMode.SURVIVAL);
            }
            case SPECTATORS -> {
                spectators.addEntry(player.getName());
                player.setGameMode(org.bukkit.GameMode.SPECTATOR);
            }
        }
    }

    public MHTeam getGameTeam(Player player) {
        if (getTeam(player).equals(hunters))
            return MHTeam.HUNTERS;
        else if (getTeam(player).equals(runners))
            return MHTeam.RUNNERS;
        else return MHTeam.SPECTATORS;
    }


    public Team getHunters() {
        return hunters;
    }

    public Team getRunners() {
        return runners;
    }

    public Team getSpectators() {
        return spectators;
    }

    public boolean hasTeam(Player player) {
        return getScoreboard().getEntryTeam(player.getName()) != null;
    }

    public void win(MHTeam winningTeam) {
        getPlayers().forEach(player -> {

            if (winningTeam == ManHunt.MHTeam.HUNTERS) {
                player.sendTitle(ChatColor.RED + "Hunters Win!", "", 10, 20, 10);
            } else if (winningTeam == ManHunt.MHTeam.RUNNERS) {
                player.sendTitle(ChatColor.GREEN + "Runners Win!", "", 10, 20, 10);
            }

            if (getGameTeam(player) == winningTeam) {
                player.incrementWins(winningTeam);
                player.rewardPoints(getTeam(player).equals(hunters) ? 75 : 200, "§aGame Won");
            } else if (getGameTeam(player) == winningTeam.getOpponent() || player.wasRunner()) {
                player.incrementLoses(winningTeam.getOpponent());
                player.rewardPoints(25, "§aParticipation");
            }
        });

        postGame();
    }


    @Override
    public void onPlayerJoin(Player player) {
        if (!this.hasPlayerJoined(player)) {
            if (getState() == GameState.PREGAME) {
                setTeam(player, MHTeam.HUNTERS);
            } else setTeam(player, ManHunt.MHTeam.SPECTATORS);
        }
        super.onPlayerJoin(player);
    }

    @Override
    public void tick(long tick) {
        compassHandler.getTimings().startTiming();
        compassHandler.tick(tick);
        compassHandler.getTimings().stopTiming();

        if (runners.getSize() == 0)
            win(ManHunt.MHTeam.HUNTERS);
    }

    @Override
    public void updateVariables() {
        super.updateVariables();
        VariableHandler.setVariable("hunterAmount", String.valueOf(getHunters().getEntries().size()));
        VariableHandler.setVariable("runnerAmount", String.valueOf(getRunners().getEntries().size()));
        VariableHandler.setVariable("spectatorAmount", String.valueOf(getSpectators().getEntries().size()));
    }


    @Override
    public void startGame() {
        super.startGame();
        setBoard(new ManhuntBoard(this, getScoreboard()));
    }

    public enum MHTeam {
        HUNTERS,
        RUNNERS,
        SPECTATORS;

        public MHTeam getOpponent() {
            return switch (this) {
                case HUNTERS -> RUNNERS;
                case RUNNERS -> HUNTERS;
                case SPECTATORS -> SPECTATORS;
            };
        }
    }
}
