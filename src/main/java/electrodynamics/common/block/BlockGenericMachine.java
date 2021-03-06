package electrodynamics.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class BlockGenericMachine extends Block {
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

	public BlockGenericMachine() {
		super(Properties.create(Material.IRON).hardnessAndResistance(3.5F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).notSolid());
		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH));
	}

	@Deprecated
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof IInventory) {
			InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) tile);
		}
		super.onReplaced(state, worldIn, pos, newState, isMoving);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		return 0;
	}

	@Override
	public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return 1;
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (worldIn.isRemote) {
			return ActionResultType.SUCCESS;
		} else {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof INamedContainerProvider) {
				player.openContainer((INamedContainerProvider) tileentity);
			}
			player.addStat(Stats.INTERACT_WITH_FURNACE);
			return ActionResultType.CONSUME;
		}
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Deprecated
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
}
