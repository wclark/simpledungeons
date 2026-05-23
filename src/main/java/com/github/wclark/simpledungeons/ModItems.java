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
    public static final DeferredItem<MagicBoneItem> MAGIC_BONE = ITEMS.register(
            "magic_bone",
            () -> new MagicBoneItem(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<MagicBonemealItem> MAGIC_BONEMEAL = ITEMS.register(
            "magic_bonemeal",
            () -> new MagicBonemealItem(new Item.Properties().durability(3).rarity(Rarity.UNCOMMON)));

    private ModItems() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
