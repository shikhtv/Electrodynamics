package electrodynamics.common.tile;

import electrodynamics.DeferredRegisters;
import electrodynamics.api.tile.ITickableTileBase;
import electrodynamics.api.tile.electric.IElectricTile;
import electrodynamics.api.tile.electric.IPowerProvider;
import electrodynamics.api.tile.electric.IPowerReceiver;
import electrodynamics.api.utilities.CachedTileOutput;
import electrodynamics.api.utilities.TransferPack;
import electrodynamics.common.tile.generic.GenericTileBase;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.RainType;

public class TileAdvancedSolarPanel extends GenericTileBase implements ITickableTileBase, IPowerProvider, IElectricTile {
	public static final TransferPack DEFAULT_OUTPUT_SETTINGS = TransferPack.ampsVoltage(3 * 3.5, 240);

	public TileAdvancedSolarPanel() {
		super(DeferredRegisters.TILE_ADVANCEDSOLARPANEL.get());
	}

	protected CachedTileOutput output;

	@Override
	public void tickServer() {
		if (world.isDaytime() && world.canSeeSky(pos.add(0, 1, 0))) {
			if (output == null) {
				output = new CachedTileOutput(world, new BlockPos(pos).offset(Direction.DOWN));
			}
			if (output.get() instanceof IPowerReceiver) {
				output.<IPowerReceiver>get().receivePower(getOutput(), Direction.UP, false);
			}
		}
	}

	public float getSunBrightness() {
		float mod = 1.0F - (MathHelper.cos(world.func_242415_f(1f) * ((float) Math.PI * 2F)) * 2.0F + 0.2F);
		mod = MathHelper.clamp(mod, 0.0F, 1.0F);
		mod = 1.0F - mod;
		mod = (float) (mod * (1.0D - world.getRainStrength(1f) * 5.0F / 16.0D));
		mod = (float) (mod * (1.0D - world.getThunderStrength(1f) * 5.0F / 16.0D));
		return mod * 0.8F + 0.2F;
	}

	@Override
	public TransferPack extractPower(TransferPack transfer, Direction from, boolean debug) {
		return TransferPack.EMPTY;
	}

	@Override
	public boolean canConnectElectrically(Direction direction) {
		return direction == Direction.DOWN;
	}

	public TransferPack getOutput() {
		Biome b = world.getBiomeManager().getBiome(getPos());
		float tempEff = 0.3F * (0.8F - b.getTemperature(getPos()));
		float humidityEff = -0.3F * (b.getPrecipitation() != RainType.NONE ? b.getDownfall() : 0.0F);
		TransferPack def = TransferPack.ampsVoltage(TileSolarPanel.DEFAULT_OUTPUT.getAmps() * (1 + humidityEff + tempEff) * getSunBrightness() * (world.isRaining() || world.isThundering() ? 0.7f : 1),
				TileSolarPanel.DEFAULT_OUTPUT.getVoltage());

		return TransferPack.ampsVoltage(def.getAmps() * DEFAULT_OUTPUT_SETTINGS.getAmps(), DEFAULT_OUTPUT_SETTINGS.getVoltage());
	}

	@Override
	public double getVoltage(Direction from) {
		return DEFAULT_OUTPUT_SETTINGS.getVoltage();
	}
}
