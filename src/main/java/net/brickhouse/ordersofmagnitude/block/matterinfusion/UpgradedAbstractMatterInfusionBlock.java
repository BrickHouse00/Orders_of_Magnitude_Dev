package net.brickhouse.ordersofmagnitude.block.matterinfusion;

import net.brickhouse.ordersofmagnitude.block.blockEntity.ModBlockEntities;
import net.brickhouse.ordersofmagnitude.block.blockEntity.matterinfusion.UpgradedMatterInfusionBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class UpgradedAbstractMatterInfusionBlock extends AbstractMatterInfusionBlock {
    private int tier;
    public UpgradedAbstractMatterInfusionBlock(Properties pProperties, int pTier) {
        super(pProperties, pTier);
        this.tier = pTier;
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new UpgradedMatterInfusionBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createMatterInfuserTicker(pLevel, pBlockEntityType, ModBlockEntities.UPGRADED_MATTER_INFUSION_BLOCK_ENTITY.get());
    }


}
