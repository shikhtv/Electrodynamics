package physica.core.common.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import physica.CoreReferences;
import physica.api.core.abstraction.recipe.IRecipeRegister;
import physica.api.core.utilities.IBaseUtilities;
import physica.core.common.CoreBlockRegister;
import physica.core.common.CoreTabRegister;
import physica.core.common.tile.TileCoalGenerator;
import physica.library.block.BlockBaseContainer;

public class BlockCoalGenerator extends BlockBaseContainer implements IBaseUtilities, IRecipeRegister {
	@SideOnly(Side.CLIENT)
	private IIcon	iconFacing;
	@SideOnly(Side.CLIENT)
	private IIcon	iconFacingRunning;
	@SideOnly(Side.CLIENT)
	private IIcon	iconOutput;

	public BlockCoalGenerator() {
		super(Material.iron);
		setHardness(1);
		setResistance(5);
		setHarvestLevel("pickaxe", 2);
		setCreativeTab(CoreTabRegister.coreTab);
		setBlockName(CoreReferences.PREFIX + "coalGenerator");
		setBlockTextureName(CoreReferences.PREFIX + "coalGenerator");
		addToRegister("Core", this);
	}

	@Override
	public void registerBlockIcons(IIconRegister reg)
	{
		blockIcon = reg.registerIcon(getTextureName());
		iconFacing = reg.registerIcon(getTextureName() + "Facing");
		iconFacingRunning = reg.registerIcon(getTextureName() + "FacingRunning");
		iconOutput = reg.registerIcon(getTextureName() + "Output");
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z)
	{
		TileCoalGenerator tile = (TileCoalGenerator) world.getTileEntity(x, y, z);
		return tile.generate > 0 ? Blocks.lava.getLightValue() : 0;
	}

	@Override
	public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side)
	{
		TileEntity tile = access.getTileEntity(x, y, z);
		if (tile instanceof TileCoalGenerator)
		{
			ForgeDirection facing = ((TileCoalGenerator) tile).getFacing();
			if (side == facing.ordinal())
			{
				return ((TileCoalGenerator) tile).generate > 0 ? iconFacingRunning : iconFacing;
			} else if (side == facing.getOpposite().ordinal())
			{
				return iconOutput;
			}
		}
		return blockIcon;
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		if (side == 4)
		{
			return iconFacing;
		} else
		{
			return blockIcon;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileCoalGenerator)
		{
			TileCoalGenerator generator = (TileCoalGenerator) tile;
			if (generator.generate > 0)
			{
				int ordinal = generator.getFacing().ordinal();
				float xLoc = (float) x + 0.5F;
				float yLoc = (float) y + 4.0f / 16.0f + rand.nextFloat() * 6.0F / 16.0F;
				float zLoc = (float) z + 0.5F;
				float offset1 = 0.52F;
				float offset2 = rand.nextFloat() * 0.6F - 0.3F;
				if (ordinal == 4)
				{
					world.spawnParticle("smoke", (double) (xLoc - offset1), (double) yLoc, (double) (zLoc + offset2), 0.0D, 0.0D, 0.0D);
					world.spawnParticle("flame", (double) (xLoc - offset1), (double) yLoc, (double) (zLoc + offset2), 0.0D, 0.0D, 0.0D);
				} else if (ordinal == 5)
				{
					world.spawnParticle("smoke", (double) (xLoc + offset1), (double) yLoc, (double) (zLoc + offset2), 0.0D, 0.0D, 0.0D);
					world.spawnParticle("flame", (double) (xLoc + offset1), (double) yLoc, (double) (zLoc + offset2), 0.0D, 0.0D, 0.0D);
				} else if (ordinal == 2)
				{
					world.spawnParticle("smoke", (double) (xLoc + offset2), (double) yLoc, (double) (zLoc - offset1), 0.0D, 0.0D, 0.0D);
					world.spawnParticle("flame", (double) (xLoc + offset2), (double) yLoc, (double) (zLoc - offset1), 0.0D, 0.0D, 0.0D);
				} else if (ordinal == 3)
				{
					world.spawnParticle("smoke", (double) (xLoc + offset2), (double) yLoc, (double) (zLoc + offset1), 0.0D, 0.0D, 0.0D);
					world.spawnParticle("flame", (double) (xLoc + offset2), (double) yLoc, (double) (zLoc + offset1), 0.0D, 0.0D, 0.0D);
				}
			}
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileCoalGenerator();
	}

	@Override
	public void registerRecipes()
	{
		addRecipe(CoreBlockRegister.blockCoalGenerator, "ISI", "CFC", "SSS", 'F', Blocks.furnace, 'I', Items.iron_ingot, 'S', "ingotSteel", 'C', Blocks.cobblestone);
	}

}