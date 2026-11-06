package dev.braian.villagermilkgrowth.client;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.EntityHitResult;

public class AldeanosClientGameTest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
        try (var mundo = context.worldBuilder().create()) {
            mundo.getServer().runCommand("fill -5 99 -5 5 99 5 minecraft:stone");
            mundo.getServer().runCommand("fill -5 100 -5 5 105 5 minecraft:air");
            mundo.getServer().runCommand("time set day");
            mundo.getServer().runCommand("weather clear");
            UUID id = mundo.getServer().computeOnServer(server -> {
                var level = server.overworld();
                var player = server.getPlayerList().getPlayers().getFirst();
                player.setGameMode(GameType.SURVIVAL);
                player.teleportTo(level, 0.5, 100, -2.0, Set.of(), 0, 0, true);
                player.getInventory().clearContent();
                player.getInventory().setSelectedSlot(0);
                Villager aldeano = EntityTypes.VILLAGER.create(level, EntitySpawnReason.COMMAND);
                aldeano.setPos(0.5, 100, 0.5);
                aldeano.setNoAi(true);
                aldeano.setAge(-24000);
                level.addFreshEntity(aldeano);
                return aldeano.getUUID();
            });
            mundo.getConnection().waitForChunksRender();
            context.getInput().lookAt(new BlockPos(0, 100, 0));
            context.waitFor(mc -> mc.hitResult instanceof EntityHitResult hit && hit.getEntity().getUUID().equals(id));
            context.takeScreenshot("01-aldeano-bebe");

            for (int uso = 1; uso <= 4; uso++) {
                final int numero = uso;
                int edadAntes = mundo.getServer().computeOnServer(server -> {
                    var player = server.getPlayerList().getPlayers().getFirst();
                    player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.MILK_BUCKET));
                    return ((Villager) server.overworld().getEntity(id)).getAge();
                });
                context.waitFor(mc -> mc.player.getMainHandItem().is(Items.MILK_BUCKET));
                context.runOnClient(mc -> EfectosObservados.reset());
                context.getInput().pressMouse(com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_RIGHT);
                context.waitFor(mc -> mc.player.getMainHandItem().is(Items.BUCKET));

                mundo.getServer().runOnServer(server -> {
                    int edad = ((Villager) server.overworld().getEntity(id)).getAge();
                    // Natural aging continues while a real client renders and exchanges packets.
                    int esperado = Math.min(0, edadAntes + 6000);
                    check(edad >= esperado && edad <= Math.min(0, esperado + 100),
                        "Cubo " + numero + ": crecimiento distinto de 6000 ticks; antes=" + edadAntes + ", despues=" + edad);
                    check(server.getPlayerList().getPlayers().getFirst().getMainHandItem().is(Items.BUCKET) && server.getPlayerList().getPlayers().getFirst().getMainHandItem().getCount() == 1,
                        "El servidor debe devolver el cubo vacio");
                });
                int cantidad = uso == 4 ? 12 : 6;
                context.waitFor(mc -> EfectosObservados.sonidosIniciados >= 1 && !EfectosObservados.particulas.isEmpty());
                context.runOnClient(mc -> {
                    check(EfectosObservados.particulas.equals(List.of(cantidad)),
                        "Debe recibirse una sola rafaga de " + cantidad + ": " + EfectosObservados.particulas);
                    check(EfectosObservados.sonidosIniciados == 1, "Debe iniciarse un unico sonido de bebida");
                    check(mc.player.getMainHandItem().getCount() == 1, "El cliente debe recibir exactamente un cubo");
                    check(mc.gui.screen() == null, "Dar leche no debe abrir comercio");
                    System.out.println("Villager Milk Growth client test: uso=" + numero + ", cubo=1, particulas="
                        + EfectosObservados.particulas + ", sonidosIniciados=" + EfectosObservados.sonidosIniciados);
                });
                context.takeScreenshot("0" + (uso + 1) + "-leche-" + uso);
            }
            context.runOnClient(mc -> {
                var aldeano = (Villager) ((EntityHitResult) mc.hitResult).getEntity();
                check(!aldeano.isBaby(), "El cliente debe mostrar un adulto tras cuatro cubos");
            });
            mundo.getServer().runOnServer(server ->
                check(!((Villager) server.overworld().getEntity(id)).isBaby(), "El servidor debe tener un adulto"));
            context.takeScreenshot("06-aldeano-adulto");
        }
    }

    private static void check(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }
}
