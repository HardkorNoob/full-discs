package dev.fulldiscs;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.WorldEventHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEvents;

/**
 * Запоминает проигрыватели, у которых игра хотела остановить музыку по таймеру
 * (длина диска из jukebox_song), и не даёт звуку оборваться, пока диск
 * действительно лежит в проигрывателе.
 */
public final class JukeboxTracker {
	private static final Set<BlockPos> HELD = new HashSet<>();
	private static ClientWorld lastWorld;
	private static WorldEventHandler handler;
	private static boolean bypass;

	private JukeboxTracker() {}

	/** Вызывается из миксина на каждое мировое событие, чтобы знать, какой handler использовать. */
	public static void rememberHandler(WorldEventHandler h) {
		handler = h;
	}

	public static boolean isBypassing() {
		return bypass;
	}

	/** Началась новая песня в этой позиции: старая запись больше не нужна. */
	public static void onSongStarted(BlockPos pos) {
		HELD.remove(pos);
	}

	/**
	 * Пришло событие «остановить музыку». Возвращает true, если его нужно отменить:
	 * диск всё ещё в проигрывателе, значит это остановка по таймеру.
	 * Если диск вынули, на клиент почти одновременно приходит обновление блока,
	 * и tick() ниже остановит звук в следующем тике.
	 */
	public static boolean shouldHold(BlockPos pos) {
		ClientWorld world = MinecraftClient.getInstance().world;
		if (world == null || !hasRecord(world.getBlockState(pos))) {
			return false;
		}
		HELD.add(pos.toImmutable());
		return true;
	}

	/** Вызывается каждый клиентский тик. */
	public static void tick(MinecraftClient client) {
		ClientWorld world = client.world;
		if (world != lastWorld) {
			HELD.clear();
			lastWorld = world;
		}
		if (world == null || HELD.isEmpty()) {
			return;
		}
		Set<BlockPos> toStop = null;
		for (BlockPos pos : HELD) {
			if (!hasRecord(world.getBlockState(pos))) {
				if (toStop == null) {
					toStop = new HashSet<>();
				}
				toStop.add(pos);
			}
		}
		if (toStop == null) {
			return;
		}
		HELD.removeAll(toStop);
		if (handler == null) {
			return;
		}
		bypass = true;
		try {
			for (BlockPos pos : toStop) {
				handler.processWorldEvent(WorldEvents.JUKEBOX_STOPS_PLAYING, pos, 0);
			}
		} finally {
			bypass = false;
		}
	}

	private static boolean hasRecord(BlockState state) {
		return state.isOf(Blocks.JUKEBOX) && state.get(JukeboxBlock.HAS_RECORD);
	}
}
