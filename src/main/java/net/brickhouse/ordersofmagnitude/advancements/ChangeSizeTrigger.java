package net.brickhouse.ordersofmagnitude.advancements;

import com.google.gson.JsonObject;
import net.brickhouse.ordersofmagnitude.OrdersOfMagnitude;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ChangeSizeTrigger extends SimpleCriterionTrigger<ChangeSizeTrigger.TriggerInstance> {

    static final ResourceLocation ID = new ResourceLocation(OrdersOfMagnitude.MOD_ID,"change_size");

    protected TriggerInstance createInstance(JsonObject pJson, EntityPredicate.Composite pPlayer, DeserializationContext pContext) {
        MinMaxBounds.Doubles minMaxBounds$doubles = MinMaxBounds.Doubles.fromJson(pJson.get("scale"));
        return new ChangeSizeTrigger.TriggerInstance(pPlayer, minMaxBounds$doubles);
    }

    public ResourceLocation getId() {
        return ID;
    }

    public void trigger(ServerPlayer pPlayer, double pScale){
        this.trigger(pPlayer, triggerInstance->{
            return triggerInstance.matches(pScale);
        });
    }

    public class TriggerInstance extends AbstractCriterionTriggerInstance {
        //private final ItemPredicate item;
        private final MinMaxBounds.Doubles scale;

        public TriggerInstance(EntityPredicate.Composite pPlayer, MinMaxBounds.Doubles pScale){
            super(ChangeSizeTrigger.ID, pPlayer);
            this.scale = pScale;
        }

        public ChangeSizeTrigger.TriggerInstance changedscale(MinMaxBounds.Doubles pScale){
            return new ChangeSizeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, pScale);
        }
        public boolean matches(double pScale) {
            return this.scale.matches(pScale);
        }

        public JsonObject serializeToJson(SerializationContext pConditions) {
            JsonObject jsonobject = super.serializeToJson(pConditions);
            jsonobject.add("scale", this.scale.serializeToJson());
            return jsonobject;
        }
    }
}
