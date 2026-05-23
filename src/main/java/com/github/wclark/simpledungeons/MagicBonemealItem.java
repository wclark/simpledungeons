package com.github.wclark.simpledungeons;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class MagicBonemealItem extends Item {
    public MagicBonemealItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof CropBlock crop) || crop.isMaxAge(state)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            level.setBlock(pos, crop.getStateForAge(crop.getMaxAge()), 2);
            Player player = context.getPlayer();
            if (player != null) {
                player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
                context.getItemInHand().hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
            } else if (level instanceof ServerLevel serverLevel) {
                context.getItemInHand().hurtAndBreak(1, serverLevel, null, item -> {
                });
            }

            level.levelEvent(1505, pos, 15);
            BoneMealItem.addGrowthParticles(level, pos, 15);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
