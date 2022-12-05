package net.brickhouse.ordersofmagnitude.sizechange;

import net.brickhouse.ordersofmagnitude.api.SizeChangeCapabilityInterface;
import net.brickhouse.ordersofmagnitude.config.OMServerConfig;
import net.brickhouse.ordersofmagnitude.networking.ModMessages;
import net.brickhouse.ordersofmagnitude.networking.packet.ClientboundChangeSizePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SizeChangeCapabilityImplementation implements SizeChangeCapabilityInterface.SizeChangeCapabilityFunctions {

    private LivingEntity livingEntity;

    private double currentScale=1.0;
    private double selectedScale=1.0;
    private double targetScale=selectedScale;
    private double defaultScale=1.0;
    private boolean isScaled = false;

/*
    private AttributeModifier SpeedChange;
    private AttributeModifier JumpChange;
    private String SpeedAttributeUUID = "0e9f68aa-87e2-415a-8f6c-b0ea8db2d343";
    private String JumpAttributeUUID = "a34c44be-8bf3-4a25-9395-d0a7adacf59c";
*/
    private EntityDimensions entityDimensions;

    /*public String getSpeedAttributeUUID(){
        return SpeedAttributeUUID;
    }*/

    public SizeChangeCapabilityImplementation(@Nullable LivingEntity livingEntity){
        this.livingEntity = livingEntity;
    }

    @Override
    public double getTargetScale() {
        return this.targetScale;
    }

    @Override
    public void setTargetScale(double myValue) {
        this.targetScale = myValue;
    }

    @Override
    public double getCurrentScale() {
        return this.currentScale;
    }

    @Override
    public void setCurrentScale(double myValue) {
        this.currentScale = myValue;
    }

    @Override
    public double getSelectedScale() { return this.selectedScale; }

    @Override
    public void setSelectedScale(double myValue) { this.selectedScale = myValue; }

    @Override
    public boolean getIsScaled() {
        return this.isScaled;
    }

    @Override
    public void setIsScaled(boolean myValue) {
        this.isScaled = myValue;
    }

    @Override
    public void sync(@NotNull LivingEntity livingEntity) {
        ModMessages.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity), new ClientboundChangeSizePacket(livingEntity.getId(), serializeNBT()));
    }

    @Override
    public void ChangeSize(@NotNull LivingEntity livingEntity, double newTarget) {
        //System.out.print("ChangeSize() targetScale: " + targetScale + " getScale: " + currentScale + "\n");
        if(newTarget >= OMServerConfig.MINIMUM_SIZE.get() && newTarget <= OMServerConfig.MAXIMUM_SIZE.get()) {
            if (targetScale != newTarget) {  //entity is wanting to go to a new scale
                setTargetScale(newTarget);
                setIsScaled(true);
                sync(livingEntity);
                livingEntity.refreshDimensions();  //order of operations.  Sync first, then client refresh.  This will ensure bounding boxes are built correctly

            } else {
                setIsScaled(false);
                setTargetScale(defaultScale);
                setCurrentScale(defaultScale);
                livingEntity.refreshDimensions();
                sync(livingEntity);
            }
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putDouble("targetScale", this.targetScale);
        nbt.putDouble("currentScale", this.currentScale);
        nbt.putDouble("selectedScale", this.selectedScale);
        nbt.putBoolean("isScaled", this.isScaled);
        if(entityDimensions != null) {
            nbt.putFloat("width", this.entityDimensions.width);
            nbt.putFloat("height", this.entityDimensions.height);
            nbt.putBoolean("fixed", this.entityDimensions.fixed);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.targetScale = nbt.getDouble("targetScale");
        if(this.targetScale == 0){this.targetScale=0.5;}        //will delete soon.  For some reason new syn on loads is zeroing this out
        this.currentScale = nbt.getDouble("currentScale");
        this.selectedScale = nbt.getDouble("selectedScale");
        this.isScaled = nbt.getBoolean("isScaled");
        this.entityDimensions = new EntityDimensions(nbt.getFloat("width"), nbt.getFloat("height"), nbt.getBoolean("fixed"));
    }

    @Override
    public void CopyFrom(SizeChangeCapabilityInterface.SizeChangeCapabilityFunctions OldValue)
    {
        deserializeNBT(OldValue.serializeNBT());
    }

    private void printStoredValues()
    {

    }
/*
    @Override
    public AttributeModifier getSpeedChange(double newSpeed){
        SpeedChange = new AttributeModifier(UUID.fromString(SpeedAttributeUUID), "ChangeSizeSpeed", newSpeed, AttributeModifier.Operation.MULTIPLY_TOTAL);
        return SpeedChange;
    }*/
 /*
    @Override
    public void ModifyAttributes(LivingEntity livingEntity, double newScale){

        //Check and remove previous attribute since this will be present if scaling from another scale
        //if(!livingEntity.getLevel().isClientSide()) {
            if (livingEntity.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(UUID.fromString(SpeedAttributeUUID)) != null) {
                ResetAttributes(livingEntity);
            }
            double oldBase = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED).getValue();
            double speedMult = (newScale - 1.0);
            if (newScale > 1.0) {
                speedMult *= 0.5;
            } else {
                speedMult *= 0.97;      //I'll keep toying with this factor  0.925 feels alright.  Not oppressively slow but prevents the player from speeding around too quickly.  Need to fix animations next
            }
            livingEntity.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(getSpeedChange(speedMult));
            double newBase = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED).getValue();
            System.out.print("ModifyAttributes oldBase: " + oldBase + " newBase: " + newBase + "\n");
            //livingEntity.getAttribute(Attributes.JUMP_STRENGTH).addPermanentModifier(getJumpChange(newScale));
        //}
    }
*/
 /*   @Override
    public void ResetAttributes(LivingEntity livingEntity){
        //if(!livingEntity.getLevel().isClientSide()) {
            livingEntity.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(getSpeedChange(1.0));
            //livingEntity.getAttribute(Attributes.JUMP_STRENGTH).removeModifier(getJumpChange(1.0));
        //}
    }
*/


}
