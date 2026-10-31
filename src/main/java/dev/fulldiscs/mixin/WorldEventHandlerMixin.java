package dev.fulldiscs.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.fulldiscs.JukeboxTracker;
import net.minecraft.client.world.WorldEventHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEvents;

@Mixin(WorldEventHandler.class)
public abstract class WorldEventHandlerMixin {
	@Inject(method = "processWorldEvent", at = @At("HEAD"), cancellable = true)
	private void fulldiscs$onWorldEvent(int eventId, BlockPos pos, int data, CallbackInfo ci) {
		JukeboxTracker.rememberHandler((WorldEventHandler) (Object) this);

		if (eventId == WorldEvents.JUKEBOX_STARTS_PLAYING) {
			JukeboxTracker.onSongStarted(pos);
		} else if (eventId == WorldEvents.JUKEBOX_STOPS_PLAYING
				&& !JukeboxTracker.isBypassing()
				&& JukeboxTracker.shouldHold(pos)) {
			ci.cancel();
		}
	}
}
