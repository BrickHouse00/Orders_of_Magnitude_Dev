package net.brickhouse.ordersofmagnitude.block.blockEntity.matterinfusion;

import net.brickhouse.ordersofmagnitude.block.blockEntity.ModBlockEntities;
import net.brickhouse.ordersofmagnitude.block.blockEntity.matterinfusion.AbstractMatterInfusionBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class UpgradedMatterInfusionBlockEntity extends AbstractMatterInfusionBlockEntity {

    public UpgradedMatterInfusionBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.UPGRADED_MATTER_INFUSION_BLOCK_ENTITY.get(), pPos, pBlockState, 1);
    }
}
