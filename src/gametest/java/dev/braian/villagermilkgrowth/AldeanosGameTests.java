package dev.braian.villagermilkgrowth;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;

public class AldeanosGameTests {
    @GameTest
    public void cuatroCubosCrecenUnRecienNacido(GameTestHelper helper) {
        Villager aldeano = helper.spawnWithNoFreeWill(EntityTypes.VILLAGER, 1, 1, 1);
        Player jugador = TestPlayers.create(helper, GameType.SURVIVAL);
        aldeano.setAge(-24000);

        for (int cubo = 1; cubo <= 4; cubo++) {
            jugador.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.MILK_BUCKET));
            aldeano.mobInteract(jugador, InteractionHand.MAIN_HAND);
            helper.assertValueEqual(-24000 + cubo * 6000, aldeano.getAge(), "Cada cubo adelanta 6000 ticks");
            helper.assertTrue(jugador.getItemInHand(InteractionHand.MAIN_HAND).is(Items.BUCKET), "Se devuelve el cubo vacio");
        }
        helper.assertFalse(aldeano.isBaby(), "El cuarto cubo convierte al aldeano en adulto");
        helper.succeed();
    }

    @GameTest
    public void edadParcialYManoSecundaria(GameTestHelper helper) {
        Villager aldeano = helper.spawnWithNoFreeWill(EntityTypes.VILLAGER, 1, 1, 1);
        Player jugador = TestPlayers.create(helper, GameType.SURVIVAL);
        aldeano.setAge(-10500);
        jugador.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.MILK_BUCKET));
        aldeano.mobInteract(jugador, InteractionHand.OFF_HAND);
        helper.assertValueEqual(-4500, aldeano.getAge(), "Se adelanta la edad parcial");
        helper.assertTrue(jugador.getItemInHand(InteractionHand.OFF_HAND).is(Items.BUCKET), "El cubo vuelve a la mano usada");
        jugador.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.MILK_BUCKET));
        aldeano.mobInteract(jugador, InteractionHand.OFF_HAND);
        helper.assertValueEqual(0, aldeano.getAge(), "El crecimiento termina en cero");
        helper.succeed();
    }

    @GameTest
    public void creativoConservaLecheSinCrearCubo(GameTestHelper helper) {
        Villager aldeano = helper.spawnWithNoFreeWill(EntityTypes.VILLAGER, 1, 1, 1);
        Player jugador = TestPlayers.create(helper, GameType.CREATIVE);
        aldeano.setAge(-24000);
        jugador.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.MILK_BUCKET));
        aldeano.mobInteract(jugador, InteractionHand.MAIN_HAND);
        helper.assertValueEqual(-18000, aldeano.getAge(), "La leche tambien funciona en creativo");
        helper.assertTrue(jugador.getItemInHand(InteractionHand.MAIN_HAND).is(Items.MILK_BUCKET), "La leche se conserva");
        helper.assertFalse(jugador.getInventory().contains(new ItemStack(Items.BUCKET)), "No se crea un cubo vacio");
        helper.succeed();
    }

    @GameTest
    public void adultoYOtroItemNoCambianEdadNiInventario(GameTestHelper helper) {
        Villager aldeano = helper.spawnWithNoFreeWill(EntityTypes.VILLAGER, 1, 1, 1);
        Player jugador = TestPlayers.create(helper, GameType.SURVIVAL);
        aldeano.setAge(-24000);
        jugador.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.APPLE));
        aldeano.mobInteract(jugador, InteractionHand.MAIN_HAND);
        helper.assertValueEqual(-24000, aldeano.getAge(), "Otro item no adelanta la edad");
        helper.assertTrue(jugador.getItemInHand(InteractionHand.MAIN_HAND).is(Items.APPLE), "Otro item se conserva");
        aldeano.setAge(0);
        jugador.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.MILK_BUCKET));
        aldeano.mobInteract(jugador, InteractionHand.MAIN_HAND);
        helper.assertValueEqual(0, aldeano.getAge(), "La leche no cambia la edad de un adulto");
        helper.assertTrue(jugador.getItemInHand(InteractionHand.MAIN_HAND).is(Items.MILK_BUCKET), "El adulto no consume leche");
        helper.succeed();
    }

    @GameTest
    public void cubosApiladosDevuelvenCuboAlInventario(GameTestHelper helper) {
        Villager aldeano = helper.spawnWithNoFreeWill(EntityTypes.VILLAGER, 1, 1, 1);
        Player jugador = TestPlayers.create(helper, GameType.SURVIVAL);
        aldeano.setAge(-24000);
        jugador.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.MILK_BUCKET, 2));
        aldeano.mobInteract(jugador, InteractionHand.MAIN_HAND);
        helper.assertValueEqual(1, jugador.getItemInHand(InteractionHand.MAIN_HAND).getCount(), "Se consume solo un cubo de leche");
        helper.assertTrue(jugador.getInventory().contains(new ItemStack(Items.BUCKET)), "El cubo vacio va al inventario");
        helper.succeed();
    }
}

