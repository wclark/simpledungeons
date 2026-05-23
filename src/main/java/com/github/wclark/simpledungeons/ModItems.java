package com.github.wclark.simpledungeons;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SimpleDungeons.MODID);

    public static final DeferredItem<Item> SUMMONERS_STAFF = ITEMS.registerSimpleItem(
            "summoners_staff",
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    private ModItems() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
