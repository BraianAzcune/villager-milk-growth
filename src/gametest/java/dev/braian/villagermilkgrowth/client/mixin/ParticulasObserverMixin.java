package dev.braian.villagermilkgrowth.client.mixin;

import dev.braian.villagermilkgrowth.client.EfectosObservados;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
abstract class ParticulasObserverMixin {
    @Inject(method = "handleParticleEvent", at = @At("RETURN"))
    private void observarParticulas(ClientboundLevelParticlesPacket packet, CallbackInfo ci) {
        if (packet.getParticle().getType() == ParticleTypes.HAPPY_VILLAGER) {
            EfectosObservados.particulas.add(packet.getCount());
        }
    }
}
