package dev.oakleycord.manhunt.game;

import org.jetbrains.annotations.NotNull;
import org.plusmc.pluslib.bukkit.managed.PlusBoard;

import java.util.ArrayList;
import java.util.List;

public class SoloRun extends AbstractRun {

    @Override
    public void tick(long tick) {

    }

    @Override
    public @NotNull PlusBoard getPlusBoard() {
        return new PlusBoard("") {
            @Override
            public List<String> getEntries(long tick) {
                return new ArrayList<>();
            }
        };
    }

    @Override
    public void updateVariables() {

    }
}
