package net.brickhouse.ordersofmagnitude.recipe;

import net.brickhouse.ordersofmagnitude.OrdersOfMagnitude;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, OrdersOfMagnitude.MOD_ID);

    public static final RegistryObject<RecipeSerializer<MatterMixerRecipe>> MATTER_MIXING_SERIALIZER =
            SERIALIZERS.register("matter_mixer", () -> MatterMixerRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
