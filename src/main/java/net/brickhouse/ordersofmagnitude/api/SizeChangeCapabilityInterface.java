package net.brickhouse.ordersofmagnitude.api;

import net.brickhouse.ordersofmagnitude.sizechange.SizeChangeCapability;
import net.brickhouse.ordersofmagnitude.sizechange.SizeChangeCapabilityImplementation;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class SizeChangeCapabilityInterface {

    public interface SizeChangeCapabilityFunctions extends INBTSerializable<CompoundTag> {

        double getTargetScale();

        void setTargetScale(double myValue);

        double getCurrentScale();

        void setCurrentScale(double myValue);

        double getSelectedScale();

        void setSelectedScale(double myValue);

        boolean getIsScaled();
        void setIsScaled(boolean myValue);

        //String getSpeedAttributeUUID();

        void sync(@Nonnull LivingEntity livingEntity);

        void ChangeSize(@Nonnull LivingEntity livingEntity, double newTarget);

        void CopyFrom(SizeChangeCapabilityInterface.SizeChangeCapabilityFunctions OldValue);

        //AttributeModifier getSpeedChange(double newSpeed);

        //void ModifyAttributes(LivingEntity livingEntity, double newScale);

       //void ResetAttributes(LivingEntity livingEntity);
    }

    public static class SizeChangeCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag>{

        private final SizeChangeCapabilityInterface.SizeChangeCapabilityFunctions backend;
        private final LazyOptional<SizeChangeCapabilityInterface.SizeChangeCapabilityFunctions> optionalData;

        private SizeChangeCapabilityInterface.SizeChangeCapabilityFunctions capability = null;

        public SizeChangeCapabilityProvider(LivingEntity livingEntity) {
            this.backend = new SizeChangeCapabilityImplementation(livingEntity);
            this.optionalData = LazyOptional.of(()-> backend);
        }

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @org.jetbrains.annotations.Nullable Direction side) {
            return SizeChangeCapability.INSTANCE.orEmpty(cap, this.optionalData);
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.backend.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {this.backend.deserializeNBT(nbt);}
    }
}
