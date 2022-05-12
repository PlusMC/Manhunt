package dev.oakleycord.manhunt.game;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.boards.ManhuntBoard;
import dev.oakleycord.manhunt.game.events.MHEvents;
import dev.oakleycord.manhunt.game.logic.GameLoop;
import dev.oakleycord.manhunt.game.logic.Logic;
import dev.oakleycord.manhunt.game.logic.handlers.CompassHandler;
import dev.oakleycord.manhunt.game.logic.modes.Mode;
import dev.oakleycord.manhunt.game.logic.modifiers.Modifier;
import dev.oakleycord.manhunt.game.util.OtherUtil;
import dev.oakleycord.manhunt.game.util.PlayerUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.plusmc.pluslib.bukkit.handlers.MultiWorldHandler;
import org.plusmc.pluslib.bukkit.managing.BaseManager;

import java.util.*;

public class MHGame {
    private final long seed;

    private final ManhuntBoard board;
    private final CompassHandler compassHandler;
    private final MultiWorldHandler worldHandler;

    private final Team hunters;
    private final Team runners;
    private final Team spectators;
    private final List<Modifier> modifiers;
    private final List<Logic> modifierLogic;
    private long timeStamp;
    private long endTimeStamp;
    private Mode mode;
    private Logic gameModeLogic;
    private GameState state;
    private GameLoop gameLoop;

    public MHGame() {
        this.state = GameState.LOADING;
        this.mode = Mode.CLASSIC;
        this.gameModeLogic = mode.getLogic(this);

        this.modifierLogic = new ArrayList<>();

        this.modifiers = new ArrayList<>();

        this.timeStamp = System.currentTimeMillis();

        this.seed = new Random().nextLong();

        Bukkit.broadcastMessage("§6Loading worlds (§e1§6/§e3§6)...");
        World overworld = new WorldCreator("mh_world_1").seed(seed).createWorld();
        Bukkit.broadcastMessage("§6Loading worlds (§e2§6/§e3§6)...");
        World nether = new WorldCreator("mh_world_2").seed(seed).environment(World.Environment.NETHER).createWorld();
        Bukkit.broadcastMessage("§6Loading worlds (§e3§6/§e3§6)...");
        World end = new WorldCreator("mh_world_3").seed(seed).environment(World.Environment.THE_END).createWorld();
        worldHandler = new MultiWorldHandler(ManHunt.getInstance(), overworld, nether, end);
        worldHandler.registerEvents(new MHEvents(this));
        worldHandler.listenForPortal(true);

        for (World world : worldHandler.getWorlds()) {
            world.setAutoSave(false);
            world.getSpawnLocation().getChunk().load();
            world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
            world.setGameRule(GameRule.DO_INSOMNIA, false);
        }

        assert Bukkit.getScoreboardManager() != null;
        this.board = new ManhuntBoard(this);
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

        ManHunt.getBoardHandler().addBoard(board);
    }

    public Scoreboard getScoreboard() {
        return board.getScoreboard();
    }

    public void pregame() {
        state = GameState.PREGAME;
        for (World world : worldHandler.getWorlds()) {
            world.getWorldBorder().setCenter(world.getSpawnLocation());
            world.getWorldBorder().setSize(30);
            world.setDifficulty(Difficulty.PEACEFUL);
        }
        freeze();
        board.tick(0);
    }

    private void freeze() {
        for (World world : worldHandler.getWorlds()) {
            world.setSpawnFlags(false, false);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.DO_FIRE_TICK, false);
        }
    }

    public void startGame() {
        state = GameState.INGAME;
        this.timeStamp = System.currentTimeMillis();

        for (World world : worldHandler.getWorlds()) {
            world.getWorldBorder().setSize(300000000);
            world.setDifficulty(Difficulty.HARD);
        }

        unfreeze();

        if (gameLoop == null) {
            gameLoop = new GameLoop(this);
            BaseManager.registerAny(gameLoop, ManHunt.getInstance());
        }

        modifierLogic.forEach(Logic::load);
        gameModeLogic.load();

        getPlayers().forEach(player -> {
            player.sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "Game Started!", "", 10, 20, 10);
            PlayerUtil.resetPlayer(player);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 1);
        });
    }

    private void unfreeze() {
        for (World world : worldHandler.getWorlds()) {
            world.setSpawnFlags(true, true);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, true);
            world.setGameRule(GameRule.DO_FIRE_TICK, true);
        }
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        for (World world : worldHandler.getWorlds())
            players.addAll(world.getPlayers());
        return players;
    }

    public void postGame(GameTeam winningTeam) {
        state = GameState.POSTGAME;
        endTimeStamp = System.currentTimeMillis();
        freeze();

        if (winningTeam == GameTeam.HUNTERS) {
            getPlayers().forEach(player ->
                    player.sendTitle(ChatColor.RED + "Hunters Win!", "", 10, 20, 10)
            );
        } else if (winningTeam == GameTeam.RUNNERS) {
            getPlayers().forEach(player ->
                    player.sendTitle(ChatColor.GREEN + "Runners Win!", "", 10, 20, 10)
            );
        }

        gameModeLogic.unload();
        modifierLogic.forEach(Logic::unload);
        BaseManager.unregisterAny(gameLoop, ManHunt.getInstance());

        getPlayers().forEach(player -> {
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
            player.sendMessage("§8§lReturning to lobby in 10 seconds...");
        });
        Bukkit.getScheduler().runTaskLater(ManHunt.getInstance(), () -> this.destroy(winningTeam), 200);
    }

    public void destroy(GameTeam winningTeam) {
        ManHunt.getBoardHandler().removeBoard(board);

        String time = OtherUtil.formatTime(endTimeStamp - this.timeStamp);
        String[] summary = new String[5];
        summary[0] = "§e§l<---§6§lGAME SUMMARY§e§l--->";
        summary[1] = "§e§lGame lasted: §b" + time;
        summary[2] = "§e§lWorld Seed: §b" + this.seed;
        summary[3] = "§e§lMode: §b" + this.mode.name() + "%";

        if (!modifiers.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            modifiers.forEach(modifier -> sb.append(modifier.sortName).append(", "));
            sb.delete(sb.length() - 2, sb.length());
            summary[4] = "§e§lModifiers: §b" + sb;
        }

        this.getPlayers().forEach(player -> {
            player.sendMessage(Arrays.stream(summary).filter(Objects::nonNull).toArray(String[]::new));
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation().add(0.5, 1, 0.5));

            if (getGameTeam(player) == winningTeam) {
                PlayerUtil.incrementWins(player, winningTeam);
                PlayerUtil.rewardPoints(player, getTeam(player).equals(hunters) ? 75 : 200, "§aGame Won");
            } else if (getGameTeam(player) == winningTeam.getOpponent() || PlayerUtil.wasRunner(player)) {
                PlayerUtil.incrementLoses(player, winningTeam.getOpponent());
                PlayerUtil.rewardPoints(player, 25, "§aParticipation");
            }

            PlayerUtil.resetPlayer(player);
        });

        worldHandler.delete();
        ManHunt.removeGame();
    }

    public GameTeam getGameTeam(Player player) {
        if (getTeam(player).equals(hunters))
            return GameTeam.HUNTERS;
        else if (getTeam(player).equals(runners))
            return GameTeam.RUNNERS;
        else return GameTeam.SPECTATORS;
    }

    public Team getTeam(Player player) {
        return getScoreboard().getEntryTeam(player.getName());
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public MultiWorldHandler getWorldHandler() {
        return worldHandler;
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

    public void setTeam(Player player, GameTeam team) {
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

    public boolean hasTeam(Player player) {
        return getScoreboard().getEntryTeam(player.getName()) != null;
    }

    public ManhuntBoard getScoreboardHandler() {
        return board;
    }

    public CompassHandler getCompassHandler() {
        return compassHandler;
    }

    public GameState getState() {
        return state;
    }


    @NotNull
    public Mode getGameMode() {
        return mode;
    }

    public void setGameMode(Mode mode) {
        if (this.gameModeLogic != null)
            this.gameModeLogic.unload(); // unload old logic
        this.mode = mode;
        this.gameModeLogic = mode.getLogic(this);
        if (state == GameState.INGAME)
            this.gameModeLogic.load();
    }

    @Nullable
    public Logic getGameModeLogic() {
        return gameModeLogic;
    }


    public List<Modifier> getModifiers() {
        return modifiers;
    }

    public void addModifier(Modifier modifier) {
        if (modifiers.contains(modifier)) return;
        modifiers.add(modifier);
        Logic logic = modifier.getLogic(this);
        modifierLogic.add(logic);
        if (state == GameState.INGAME)
            logic.load();
    }

    public void removeModifier(Modifier modifier) {
        if (!modifiers.contains(modifier)) return;
        modifiers.remove(modifier);
        modifierLogic.removeIf(logic -> {
            if (logic.getClass() == modifier.logic) {
                logic.unload();
                return true;
            }
            return false;
        });
    }

    public List<Logic> getModifierLogic() {
        return List.copyOf(modifierLogic);
    }
}
