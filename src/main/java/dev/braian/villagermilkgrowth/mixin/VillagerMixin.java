package dev.braian.villagermilkgrowth.mixin;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Villager.class)
abstract class VillagerMixin {
    @Unique
    private static final int CRECIMIENTO_POR_CUBO = 5 * 60 * 20;

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void alimentarConLeche(
        Player player,
        InteractionHand hand,
        CallbackInfoReturnable<InteractionResult> callback
    ) {
        Villager aldeano = (Villager) (Object) this;
        ItemStack leche = player.getItemInHand(hand);

        if (!aldeano.canAgeUp() || !leche.is(Items.MILK_BUCKET)) {
            return;
        }

        callback.setReturnValue(InteractionResult.SUCCESS);

        if (!(aldeano.level() instanceof ServerLevel level)) {
            return;
        }

        aldeano.setAge(Math.min(0, aldeano.getAge() + CRECIMIENTO_POR_CUBO));
        mostrarEfectosDeAlimentacion(level, aldeano);

        if (player.hasInfiniteMaterials()) {
            return;
        }

        player.setItemInHand(
            hand,
            ItemUtils.createFilledResult(leche, player, new ItemStack(Items.BUCKET))
        );
    }

    @Unique
    private static void mostrarEfectosDeAlimentacion(ServerLevel level, Villager aldeano) {
        level.sendParticles(
            ParticleTypes.HAPPY_VILLAGER,
            aldeano.getX(), aldeano.getY() + aldeano.getBbHeight() * 0.5, aldeano.getZ(),
            aldeano.isBaby() ? 6 : 12,
            aldeano.getBbWidth() * 0.5, aldeano.getBbHeight() * 0.25, aldeano.getBbWidth() * 0.5,
            0.0
        );
        aldeano.playSound(SoundEvents.GENERIC_DRINK.value(), 0.35F, 1.1F);
    }
}
