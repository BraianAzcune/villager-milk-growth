package dev.braian.villagermilkgrowth.client.mixin;

import dev.braian.villagermilkgrowth.client.EfectosObservados;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundManager.class)
abstract class SonidoObserverMixin {
    @Inject(method = "play", at = @At("RETURN"))
    private void observarSonido(SoundInstance sound, CallbackInfoReturnable<SoundEngine.PlayResult> ci) {
        if (sound.getIdentifier().equals(SoundEvents.GENERIC_DRINK.value().location())
            && ci.getReturnValue() == SoundEngine.PlayResult.STARTED) {
            EfectosObservados.sonidosIniciados++;
        }
    }
}
