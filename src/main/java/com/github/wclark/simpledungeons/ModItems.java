package com.github.wclark.simpledungeons;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SimpleDungeons.MODID);

    public static final DeferredItem<SummonersStaffItem> SUMMONERS_STAFF = ITEMS.register(
            "summoners_staff",
            () -> new SummonersStaffItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .attributes(AxeItem.createAttributes(Tiers.STONE, 7.0F, -3.2F))));

    private ModItems() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
