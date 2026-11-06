package dev.braian.villagermilkgrowth;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

final class TestPlayers {
    static ServerPlayer create(GameTestHelper helper, GameType mode) {
        var player = helper.makeMockServerPlayerInLevel();
        player.setGameMode(mode);
        return player;
    }
}
