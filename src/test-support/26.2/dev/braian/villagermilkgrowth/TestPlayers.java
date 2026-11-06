package dev.braian.villagermilkgrowth;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

final class TestPlayers {
    static Player create(GameTestHelper helper, GameType mode) {
        return helper.makeMockServerPlayer(mode);
    }
}
