package net.brickhouse.ordersofmagnitude.sizechange;

import net.brickhouse.ordersofmagnitude.api.SizeChangeCapabilityInterface;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class SizeChangeCapability {
    public static final Capability<SizeChangeCapabilityInterface.SizeChangeCapabilityFunctions> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(SizeChangeCapabilityInterface.SizeChangeCapabilityFunctions.class);
    }

    public SizeChangeCapability() {
    }
}
