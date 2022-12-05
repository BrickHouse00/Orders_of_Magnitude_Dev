package net.brickhouse.ordersofmagnitude.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class OMFluidTank extends FluidTank {

    protected boolean canAcceptExternal = true;

    public OMFluidTank(int capacity) {
        super(capacity);
    }

    public void setCanAcceptExternal(boolean pValue){
        this.canAcceptExternal = pValue;
    }

    public boolean getCanAcceptExternal(){
        return canAcceptExternal;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        //System.out.println("fill canAcceptExternal: " + canAcceptExternal);
        if (!canAcceptExternal) {
            return 0;
        }

        return super.fill(resource, action);
    }

    public int fillInternal(FluidStack resource, FluidAction action){

        if (resource.isEmpty() || !isFluidValid(resource))
        {
            return 0;
        }
        if (action.simulate())
        {
            if (fluid.isEmpty())
            {
                return Math.min(capacity, resource.getAmount());
            }
            if (!fluid.isFluidEqual(resource))
            {
                return 0;
            }
            return Math.min(capacity - fluid.getAmount(), resource.getAmount());
        }
        if (fluid.isEmpty())
        {
            fluid = new FluidStack(resource, Math.min(capacity, resource.getAmount()));
            onContentsChanged();
            return fluid.getAmount();
        }
        if (!fluid.isFluidEqual(resource))
        {
            return 0;
        }
        int filled = capacity - fluid.getAmount();

        if (resource.getAmount() < filled)
        {
            fluid.grow(resource.getAmount());
            filled = resource.getAmount();
        }
        else
        {
            fluid.setAmount(capacity);
        }
        if (filled > 0)
            onContentsChanged();
        return filled;
    }
}
