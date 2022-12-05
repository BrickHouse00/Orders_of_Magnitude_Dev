package net.brickhouse.ordersofmagnitude.block.matterinfusion;

import net.brickhouse.ordersofmagnitude.block.blockEntity.matterinfusion.AbstractMatterInfusionBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractMatterInfusionBlock extends BaseEntityBlock {

    private int tier;

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public AbstractMatterInfusionBlock(Properties pProperties, int pTier) {
        super(pProperties);
        tier = pTier;
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> createMatterInfuserTicker(Level pLevel, BlockEntityType<T> pServerType, BlockEntityType<? extends AbstractMatterInfusionBlockEntity> pClientType){
        return pLevel.isClientSide() ? null: createTickerHelper(pServerType, pClientType, AbstractMatterInfusionBlockEntity::serverTick);
    }
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit){
        if(pLevel.isClientSide)
            return InteractionResult.CONSUME;
        else{
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if(blockEntity instanceof AbstractMatterInfusionBlockEntity){
                //craftModule(0.00004, pPlayer);
                NetworkHooks.openGui(((ServerPlayer)pPlayer), (AbstractMatterInfusionBlockEntity)blockEntity, pPos);
            } else {
                throw new IllegalStateException("Our container provider is missing.");
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    //Block Entity Voxel Shape Shit
    private static VoxelShape[] northShape = new VoxelShape[]{
            box(0,0,0,16,3,16),         //0,0,0 -? 16,3,16
            box(4,3,13,14,13,16),       //4,3,13 -> 14, 10, 16
            box(0,3,0,4,13,16),         //0,3,0 -> 4, 10, 16
            //box(4,3,13,14,13,16),   //4,3,13 -> 10,10,3
            box(0,13,0,16,16,16),       //0,13,0 -> 16,3,16
            box(14,3,0, 16,13,16),      //14,3,0 -> 2,10,16
            box(10,16,2, 14,20,6)};     //10,16,2 -> 4,4,4

    private static VoxelShape[] eastShape = new VoxelShape[]{
            box(0,0,0,16,3,16),         //0,0,0 -? 16,3,16 Bottom
            box(0,3,4,3,13,14),         //0,3,4 -> 3, 10, 10  Back
            box(0,3,0,16,13,4),         //0,3,0 -> 16, 10, 4 Right hand face
            //box(4,3,13,14,13,16),   //4,3,13 -> 10,10,3
            box(0,13,0,16,16,16),       //0,13,0 -> 16,3,16 top
            box(0,3,14, 16,13,16),      //0,3,14 -> 2,10,16 left hand face
            box(10,16,10, 14,20,14)};   //10,16,10 -> 4,4,4 fluid holder

    private static VoxelShape[] southShape = new VoxelShape[]{
            box(0,0,0,16,3,16),         //0,0,0 -? 16,3,16 Bottom
            box(2,3,0,12,13,3),         //2,3,0 -> 10, 10, 3  Back
            box(12,3,0,16,13,16),       //12,3,0 -> 4, 10, 16 Right hand face
            //box(4,3,13,14,13,16),   //4,3,13 -> 10,10,3
            box(0,13,0,16,16,16),       //0,13,0 -> 16,3,16 top
            box(0,3,0, 2,13,16),        //0,3,0 -> 2,10,16 left hand face
            box(2,16,10, 6,20,14)};     //2,16,10 -> 4,4,4 fluid holder

    private static VoxelShape[] westShape = new VoxelShape[]{
            box(0,0,0,16,3,16),         //0,0,0 -? 16,3,16 Bottom
            box(13,3,2,16,13,12),       //13,3,2 -> 3, 10, 10  Back
            box(0,3,12,16,13,16),       //0,3,12 -> 16, 10, 4 Right hand face
            //box(4,3,13,14,13,16),   //4,3,13 -> 10,10,3
            box(0,13,0,16,16,16),       //0,13,0 -> 16,3,16 top
            box(0,3,0, 16,13,2),        //0,3,0 -> 16,10,2 left hand face
            box(2,16,2, 6,20,6)};       //2,16,2 -> 4,4,4 fluid holder


    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {

        VoxelShape combinedShape = Shapes.empty();

        if (pState.getValue(FACING) == Direction.NORTH) {
            for (int i = 0; i < northShape.length; i++) {
                combinedShape = Shapes.joinUnoptimized(combinedShape, northShape[i], BooleanOp.OR);
            }
        } else if (pState.getValue(FACING) == Direction.EAST){
            for (int i = 0; i < eastShape.length; i++) {
                combinedShape = Shapes.joinUnoptimized(combinedShape, eastShape[i], BooleanOp.OR);
            }
        } else if(pState.getValue(FACING) == Direction.SOUTH){
            for (int i = 0; i < southShape.length; i++) {
                combinedShape = Shapes.joinUnoptimized(combinedShape, southShape[i], BooleanOp.OR);
            }
        } else if(pState.getValue(FACING) == Direction.WEST){
            for (int i = 0; i < westShape.length; i++) {
                combinedShape = Shapes.joinUnoptimized(combinedShape, westShape[i], BooleanOp.OR);
            }
        } else {
            combinedShape = box(0,0,0,16,16,16);       //Default it to a box JIC
        }
        return combinedShape;
    }

    /* FACING */

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    /* BLOCK ENTITY */

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof AbstractMatterInfusionBlockEntity) {
                ((AbstractMatterInfusionBlockEntity) blockEntity).drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }
}

