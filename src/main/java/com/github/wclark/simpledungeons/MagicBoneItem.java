package com.github.wclark.simpledungeons;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class MagicBoneItem extends Item {
    public MagicBoneItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
