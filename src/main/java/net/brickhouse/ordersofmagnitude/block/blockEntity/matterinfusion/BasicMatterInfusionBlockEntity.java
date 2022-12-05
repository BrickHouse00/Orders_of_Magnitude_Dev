package net.brickhouse.ordersofmagnitude.block.blockEntity.matterinfusion;

import net.brickhouse.ordersofmagnitude.block.blockEntity.ModBlockEntities;
import net.brickhouse.ordersofmagnitude.block.blockEntity.matterinfusion.AbstractMatterInfusionBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BasicMatterInfusionBlockEntity extends AbstractMatterInfusionBlockEntity {

    public int tier = 0;
    public BasicMatterInfusionBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.BASIC_MATTER_INFUSION_BLOCK_ENTITY.get(), pPos, pBlockState, 0);
    }
/*
    public BasicMatterInfusionBlockEntity(BlockPos pPos, BlockState pBlockState){
        this(pPos, pBlockState, 0);
    }
*/
    /*public static void tick(Level pLevel, BlockPos pPos, BlockState pState, AbstractMatterInfusionBlockEntity pBlockEntity){
        tickHandler(pLevel, pPos, pState, pBlockEntity);
    }*/
}
