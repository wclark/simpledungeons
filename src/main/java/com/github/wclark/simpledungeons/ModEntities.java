package com.github.wclark.simpledungeons;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, SimpleDungeons.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<NecromancerEntity>> NECROMANCER = ENTITY_TYPES.register(
            "necromancer",
            () -> EntityType.Builder.of(NecromancerEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .eyeHeight(1.74F)
                    .passengerAttachments(2.0125F)
                    .ridingOffset(-0.7F)
                    .clientTrackingRange(8)
                    .build(SimpleDungeons.MODID + ":necromancer"));

    public static final DeferredHolder<EntityType<?>, EntityType<BlueOrbEntity>> BLUE_ORB = ENTITY_TYPES.register(
            "blue_orb",
            () -> EntityType.Builder.<BlueOrbEntity>of(BlueOrbEntity::new, MobCategory.MISC)
                    .sized(0.35F, 0.35F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(SimpleDungeons.MODID + ":blue_orb"));

    private ModEntities() {
    }

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
