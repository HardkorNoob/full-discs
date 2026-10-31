package dev.fulldiscs.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.fulldiscs.JukeboxTracker;
import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
	@Inject(method = "tick", at = @At("TAIL"))
	private void fulldiscs$tick(CallbackInfo ci) {
		JukeboxTracker.tick((MinecraftClient) (Object) this);
	}
}
