package dev.oakleycord.manhunt.game;

import dev.oakleycord.manhunt.SpeedRuns;
import dev.oakleycord.manhunt.game.events.RunEvents;
import dev.oakleycord.manhunt.game.logic.GameLoop;
import dev.oakleycord.manhunt.game.logic.Logic;
import dev.oakleycord.manhunt.game.logic.modes.Mode;
import dev.oakleycord.manhunt.game.logic.modifiers.Modifier;
import dev.oakleycord.manhunt.game.util.OtherUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.plusmc.pluslib.bukkit.handlers.MultiWorldHandler;
import org.plusmc.pluslib.bukkit.handlers.VariableHandler;
import org.plusmc.pluslib.bukkit.managed.PlusBoard;
import org.plusmc.pluslib.bukkit.managing.BaseManager;

import java.util.*;


public abstract class AbstractRun {
    private final long seed;

    private final MultiWorldHandler worldHandler;

    private final List<Modifier> modifiers;
    private final List<Logic> modifierLogic;
    private boolean hadModifiers;
    private long timeStamp;
    private long endTimeStamp;
    private Mode mode;
    private Logic gameModeLogic;
    private GameState state;
    private GameLoop gameLoop;
    private final List<UUID> joinedPlayers;

    protected AbstractRun() {
        this.state = GameState.LOADING;
        this.mode = Mode.CLASSIC;
        this.gameModeLogic = mode.getLogic(this);

        this.joinedPlayers = new ArrayList<>();

        this.modifierLogic = new ArrayList<>();
        this.modifiers = new ArrayList<>();
        this.hadModifiers = false;

        this.timeStamp = System.currentTimeMillis();

        this.seed = new Random().nextLong();

        Bukkit.broadcastMessage("§6Loading worlds (§e1§6/§e3§6)...");
        World overworld = new WorldCreator("sr_world_1").seed(seed).createWorld();
        Bukkit.broadcastMessage("§6Loading worlds (§e2§6/§e3§6)...");
        World nether = new WorldCreator("sr_world_2").seed(seed).environment(World.Environment.NETHER).createWorld();
        Bukkit.broadcastMessage("§6Loading worlds (§e3§6/§e3§6)...");
        World end = new WorldCreator("sr_world_3").seed(seed).environment(World.Environment.THE_END).createWorld();
        worldHandler = new MultiWorldHandler(SpeedRuns.getInstance(), overworld, nether, end);
        worldHandler.registerEvents(new RunEvents(this));
        worldHandler.listenForPortal(true);

        for (World world : worldHandler.getWorlds()) {
            world.setAutoSave(false);
            world.getSpawnLocation().getChunk().load();
            world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
            world.setGameRule(GameRule.DO_INSOMNIA, false);
        }
    }

    public void pregame() {
        state = GameState.PREGAME;
        for (World world : worldHandler.getWorlds()) {
            world.getWorldBorder().setCenter(world.getSpawnLocation());
            world.getWorldBorder().setSize(30);
            world.setDifficulty(Difficulty.PEACEFUL);
        }
        freeze();
        if (gameLoop == null) {
            gameLoop = new GameLoop(this);
            BaseManager.registerAny(gameLoop, SpeedRuns.getInstance());
        }
        getPlusBoard().tick(0);
    }

    public boolean hadModifiers() {
        return hadModifiers;
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


        modifierLogic.forEach(Logic::load);
        gameModeLogic.load();

        getPlayers().forEach(player -> {
            player.sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "Game Started!", "", 10, 20, 10);
            player.reset();
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

    public abstract void tick(long tick);

    public void postGame() {
        state = GameState.POSTGAME;
        endTimeStamp = System.currentTimeMillis();
        freeze();

        gameModeLogic.unload();
        modifierLogic.forEach(Logic::unload);
        BaseManager.unregisterAny(gameLoop, SpeedRuns.getInstance());

        getPlayers().forEach(player -> {
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
            player.sendMessage("§8§lReturning to lobby in 10 seconds...");
        });
        updateVariables();
        Bukkit.getScheduler().runTaskLater(SpeedRuns.getInstance(), this::destroy, 200);
    }

    public Scoreboard getScoreboard() {
        return getPlusBoard().getScoreboard();
    }

    public void destroy() {
        SpeedRuns.getBoardHandler().removeBoard(getPlusBoard());

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

            player.reset();
        });

        worldHandler.delete();
        SpeedRuns.removeGame();
    }

    public Team getTeam(Player player) {
        return getScoreboard().getEntryTeam(player.getName());
    }

    public MultiWorldHandler getWorldHandler() {
        return worldHandler;
    }

    @NotNull
    public abstract PlusBoard getPlusBoard();

    @Nullable
    public Logic getGameModeLogic() {
        return gameModeLogic;
    }

    public void addModifier(Modifier modifier) {
        hadModifiers = true;
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

    public void updateVariables() {
        VariableHandler.setVariable("mode", getGameMode().name() + "%");
        VariableHandler.setVariable("playerAmount", String.valueOf(getPlayers().size()));
        VariableHandler.setVariable("time", OtherUtil.formatTime(System.currentTimeMillis() - getTimeStamp()));
        if (!getModifiers().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            getModifiers().forEach(modifier -> sb.append(modifier.sortName).append(", "));
            sb.delete(sb.length() - 2, sb.length());
            VariableHandler.setVariable("modifiers", sb.toString());
        }
        VariableHandler.setVariable("gameState", getState().name());
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

    public void onPlayerJoin(Player player) {
        player.setScoreboard(this.getScoreboard());
        if (!this.hasPlayerJoined(player)) {
            player.resetAdvancements();
            player.reset();
            joinedPlayers.add(player.getUniqueId());
        }
        this.getPlusBoard().tick(0);
    }

    public boolean hasPlayerJoined(Player player) {
        return joinedPlayers.contains(player.getUniqueId());
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public List<Modifier> getModifiers() {
        return modifiers;
    }

    public GameState getState() {
        return state;
    }

    public long getTime() {
        return endTimeStamp != 0 ? endTimeStamp - timeStamp : System.currentTimeMillis() - timeStamp;
    }

    public List<Logic> getModifierLogic() {
        return List.copyOf(modifierLogic);
    }
}
