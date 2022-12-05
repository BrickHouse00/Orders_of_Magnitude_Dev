package net.brickhouse.ordersofmagnitude.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.brickhouse.ordersofmagnitude.OrdersOfMagnitude;
import net.brickhouse.ordersofmagnitude.fluid.ModFluids;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class MatterMixerRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final ItemStack itemOutput;

    //private final FluidStack fluidOutput;
    private final NonNullList<Ingredient> recipeItems;

    public MatterMixerRecipe(ResourceLocation pId, ItemStack pItemOutput, NonNullList<Ingredient> pRecipeItems){
        this.id = pId;
        this.itemOutput = pItemOutput;
        //this.fluidOutput = pFluidOutput;
        this.recipeItems = pRecipeItems;

    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        ItemStack[] test = recipeItems.get(0).getItems();
        System.out.println("isClientSide: " + pLevel.isClientSide() + " recipeItems.get(0): " + test + " pContainer " + pContainer);
        if(pLevel.isClientSide()){
            return false;
        }
        return recipeItems.get(0).test(pContainer.getItem(0)) && recipeItems.get(1).test(pContainer.getItem(1));
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer) {
        return itemOutput;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return itemOutput.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<MatterMixerRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "matter_mixing";
    }

    public static class Serializer implements RecipeSerializer<MatterMixerRecipe>{

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(OrdersOfMagnitude.MOD_ID,"matter_mixing");

        @Override
        public MatterMixerRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            //FluidStack fluidStackOut = FluidStack.EMPTY;
            ItemStack itemStackOut = ItemStack.EMPTY;
            /*if(pSerializedRecipe.has("fluid")){
                Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(GsonHelper.getAsString(pSerializedRecipe, "fluid")));
                int amount = GsonHelper.getAsInt(pSerializedRecipe, "amount");
                fluidStackOut = new FluidStack(fluid, amount);
            }*/
            //if(pSerializedRecipe.has("output")) {
                itemStackOut = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));
            //}

            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(1, Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            System.out.println("fromJson inputs: " + inputs);
            return new MatterMixerRecipe(pRecipeId, itemStackOut, inputs);
        }

        @Nullable
        @Override
        public MatterMixerRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            System.out.println("fromNetwork pBuffer: " + pBuffer);
            NonNullList<Ingredient> inputs = NonNullList.withSize(pBuffer.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(pBuffer));
            }

            ItemStack output = pBuffer.readItem();
            return new MatterMixerRecipe(pRecipeId, output, inputs);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, MatterMixerRecipe pRecipe) {
            pBuffer.writeInt(pRecipe.getIngredients().size());
            for (Ingredient ing : pRecipe.getIngredients()) {
                ing.toNetwork(pBuffer);
            }
            pBuffer.writeItemStack(pRecipe.getResultItem(), false);
        }

        @Override
        public RecipeSerializer<?> setRegistryName(ResourceLocation name) {
            return INSTANCE;
        }

        @Nullable
        @Override
        public ResourceLocation getRegistryName() {
            return ID;
        }

        @Override
        public Class<RecipeSerializer<?>> getRegistryType() {
            return Serializer.castClass(RecipeSerializer.class);
        }

        @SuppressWarnings("unchecked") // Need this wrapper, because generics
        private static <G> Class<G> castClass(Class<?> cls) {
            return (Class<G>)cls;
        }
    }
}
