package dev.oakleycord.manhunt.game;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.logic.CompassHandler;
import dev.oakleycord.manhunt.game.logic.GameLoop;
import dev.oakleycord.manhunt.game.logic.ScoreboardHandler;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ManHuntGame {

    private final long seed;
    private final World[] worlds;
    private final ScoreboardHandler scoreboardHandler;
    private final CompassHandler compassHandler;
    private final Scoreboard scoreboard;
    private final Team hunters, runners, spectators;
    private final long timeStamp;
    private GameState state;

    private GameLoop gameLoop;

    public ManHuntGame() {
        state = GameState.LOADING;

        this.timeStamp = System.currentTimeMillis();

        this.worlds = new World[3];
        this.seed = new Random().nextLong();
        worlds[0] = new WorldCreator("mh_world_1").seed(seed).createWorld();
        worlds[1] = new WorldCreator("mh_world_2").seed(seed).environment(World.Environment.NETHER).createWorld();
        worlds[2] = new WorldCreator("mh_world_3").seed(seed).environment(World.Environment.THE_END).createWorld();

        for (World world : worlds) {
            world.setAutoSave(false);
            world.getSpawnLocation().getChunk().load();
            world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
        }

        assert Bukkit.getScoreboardManager() != null;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.scoreboardHandler = new ScoreboardHandler(this);
        this.compassHandler = new CompassHandler(this);

        this.hunters = scoreboard.registerNewTeam("Hunters");
        hunters.setAllowFriendlyFire(false);
        hunters.setPrefix(ChatColor.RED + "" + ChatColor.BOLD + "[Hunter] ");

        this.runners = scoreboard.registerNewTeam("Runners");
        runners.setAllowFriendlyFire(false);
        runners.setPrefix(ChatColor.GREEN + "" + ChatColor.BOLD + "[Runner] ");

        this.spectators = scoreboard.registerNewTeam("Spectators");
        spectators.setColor(ChatColor.DARK_GRAY);
    }

    public static boolean isManHuntWorld(World world) {
        return world.getName().startsWith("mh_world_");
    }

    //i fucking love my boyfriend
    //public long howMuchILoveNiko() {return Long.MAX_VALUE;}

    public void pregame() {
        state = GameState.PREGAME;
        for (World world : worlds) {
            world.getWorldBorder().setCenter(world.getSpawnLocation());
            world.getWorldBorder().setSize(30);
            world.setDifficulty(Difficulty.PEACEFUL);
        }
        freezeGame();
        scoreboardHandler.updateScoreboard(0);
    }

    public void postGame(GameTeam winningTeam) {
        state = GameState.POSTGAME;
        freezeGame();
        switch (winningTeam) {
            case HUNTERS -> getPlayers().forEach(player -> {
                player.sendTitle(ChatColor.RED + "The Hunters Win!", "", 10, 20, 10);
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
            });
            case RUNNERS -> getPlayers().forEach(player -> {
                player.sendTitle(ChatColor.GREEN + "The Runners Win!", "", 10, 20, 10);
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
            });
        }


        Bukkit.getScheduler().runTaskLater(ManHunt.getInstance(), this::destroy, 100);
    }

    public void destroy() {
        ManHunt.getTickingManager().unregister(gameLoop);

        String time = ManHunt.formatTime(System.currentTimeMillis() - this.timeStamp);
        String[] summary = {
                "§e§l<---§6§lGAME SUMMARY§e§l--->",
                "§e§lGame lasted: §b" + time,
                "§e§lWorld Seed: §b" + this.seed
        };

        this.getPlayers().forEach(player -> {
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation().add(0, 1, 0));
            player.sendMessage(summary);
            ManHunt.resetPlayer(player, false);
        });

        for (World world : worlds) {
            String name = world.getName();
            Bukkit.unloadWorld(world, false);
            deleteDir(new File(Bukkit.getWorldContainer(), name));
        }

        ManHunt.GAME = null;
    }

    private void deleteDir(File dir) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                deleteDir(file);
            }
        }
        dir.delete();
    }

    public void resume() {
        state = GameState.INGAME;
        for (World world : worlds) {
            world.getWorldBorder().setSize(300000000);
            world.setDifficulty(Difficulty.HARD);
        }
        unFreezeGame();
        if (gameLoop == null) {
            gameLoop = new GameLoop(this);
            ManHunt.getTickingManager().register(gameLoop);
        }
    }

    private void freezeGame() {
        for (World world : worlds) {
            world.setSpawnFlags(false, false);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.DO_FIRE_TICK, false);
        }
    }

    private void unFreezeGame() {
        for (World world : worlds) {
            world.setSpawnFlags(true, true);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, true);
            world.setGameRule(GameRule.DO_FIRE_TICK, true);
        }
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public World getOverworld() {
        return worlds[0];
    }

    public World getNether() {
        return worlds[1];
    }

    public World getEnd() {
        return worlds[2];
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

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public ScoreboardHandler getScoreboardHandler() {
        return scoreboardHandler;
    }

    public CompassHandler getCompassHandler() {
        return compassHandler;
    }

    public void setTeam(Player player, GameTeam team) {
        Team t = scoreboard.getEntryTeam(player.getName());
        if (t != null) t.removeEntry(player.getName());

        switch (team) {
            case HUNTERS -> {
                hunters.addEntry(player.getName());
                player.setGameMode(GameMode.SURVIVAL);
            }
            case RUNNERS -> {
                runners.addEntry(player.getName());
                player.setGameMode(GameMode.SURVIVAL);
            }
            case SPECTATORS -> {
                spectators.addEntry(player.getName());
                player.setGameMode(GameMode.SPECTATOR);
            }
        }
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        for (World world : worlds)
            players.addAll(world.getPlayers());
        return players;
    }


    public boolean inTeam(Player player) {
        return scoreboard.getEntryTeam(player.getName()) != null;
    }


    public GameState getState() {
        return state;
    }

    public enum GameTeam {
        HUNTERS, RUNNERS, SPECTATORS
    }

    public enum GameState {
        LOADING,
        PREGAME,
        INGAME,
        POSTGAME
    }
}
