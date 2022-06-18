package dev.oakleycord.manhunt.game;

import dev.oakleycord.manhunt.SpeedRuns;
import dev.oakleycord.manhunt.game.boards.SoloBoard;
import dev.oakleycord.manhunt.game.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.plusmc.pluslib.bukkit.managed.PlusBoard;
import org.plusmc.pluslib.bukkit.util.BungeeUtil;

import java.util.UUID;

public class SoloRun extends AbstractRun {
    private final PlusBoard board;
    private UUID playerUUID;


    public SoloRun() {
        super();
        this.board = new SoloBoard(this);
        playerUUID = null;
        SpeedRuns.getBoardHandler().addBoard(board);
    }

    @Override
    public void tick(long tick) {
    }

    @Override
    public @NotNull PlusBoard getPlusBoard() {
        return board;
    }


    @Override
    public void onPlayerJoin(Player player) {
        super.onPlayerJoin(player);
        if (getPlayers().size() > 1) {
            player.sendMessage("Only 1 player is allowed in this game.");
            BungeeUtil.connectServer(player, SpeedRuns.getInstance().lobbyServer);
        } else if (playerUUID == null) {
            this.playerUUID = player.getUniqueId();
        }
    }

    public Player getPlayer() {
        return (Player) getOfflinePlayer();
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(playerUUID);
    }


    @Override
    public void postGame() {
        PlayerUtil.finishedRun(getPlayer(), getGameMode(), getTime());
        super.postGame();
    }


}
