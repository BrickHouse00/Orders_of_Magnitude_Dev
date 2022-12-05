package net.brickhouse.ordersofmagnitude.block.blockEntity.matterinfusion;

import net.brickhouse.ordersofmagnitude.block.blockEntity.ModBlockEntities;
import net.brickhouse.ordersofmagnitude.block.blockEntity.matterinfusion.AbstractMatterInfusionBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AdvancedMatterInfusionBlockEntity extends AbstractMatterInfusionBlockEntity {

    public AdvancedMatterInfusionBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.ADVANCED_MATTER_INFUSION_BLOCK_ENTITY.get(), pPos, pBlockState, 2);
    }

}
