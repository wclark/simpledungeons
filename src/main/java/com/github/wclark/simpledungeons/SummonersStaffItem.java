package com.github.wclark.simpledungeons;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SummonersStaffItem extends Item {
    private static final int SUMMON_COST = 5;
    private static final int SUMMON_COOLDOWN_TICKS = 200;

    public SummonersStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }

        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.fail(stack);
        }

        if (player.getCooldowns().isOnCooldown(this)) {
            failSummon(serverLevel, player);
            return InteractionResultHolder.fail(stack);
        }

        SummonMaterial material = chooseMaterial(player);
        if (material == null) {
            failSummon(serverLevel, player);
            return InteractionResultHolder.fail(stack);
        }

        if (!player.getAbilities().instabuild && !consumeItems(player, material.item(), SUMMON_COST)) {
            failSummon(serverLevel, player);
            return InteractionResultHolder.fail(stack);
        }

        BlockPos spawnPos = summonPos(serverPlayer);
        if (!UndeadSummons.spawnPlayerPet(serverLevel, spawnPos, material.skeleton(), serverPlayer)) {
            failSummon(serverLevel, player);
            return InteractionResultHolder.fail(stack);
        }

        player.getCooldowns().addCooldown(this, SUMMON_COOLDOWN_TICKS);
        serverLevel.playSound(null, player.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 0.65F, 1.35F);
        return InteractionResultHolder.consume(stack);
    }

    private static SummonMaterial chooseMaterial(Player player) {
        if (countItems(player, Items.ROTTEN_FLESH) >= SUMMON_COST) {
            return new SummonMaterial(Items.ROTTEN_FLESH, false);
        }

        if (countItems(player, Items.BONE) >= SUMMON_COST) {
            return new SummonMaterial(Items.BONE, true);
        }

        return null;
    }

    private static BlockPos summonPos(ServerPlayer player) {
        Vec3 look = player.getLookAngle();
        if (look.horizontalDistanceSqr() < 0.01D) {
            return player.blockPosition().relative(player.getDirection(), 2);
        }

        return BlockPos.containing(player.getX() + look.x * 2.0D, player.getY(), player.getZ() + look.z * 2.0D);
    }

    private static int countItems(Player player, Item item) {
        Inventory inventory = player.getInventory();
        int count = 0;
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.is(item)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static boolean consumeItems(Player player, Item item, int count) {
        if (countItems(player, item) < count) {
            return false;
        }

        Inventory inventory = player.getInventory();
        int remaining = count;
        for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.is(item)) {
                continue;
            }

            int removed = Math.min(remaining, stack.getCount());
            stack.shrink(removed);
            if (stack.isEmpty()) {
                inventory.setItem(slot, ItemStack.EMPTY);
            }
            remaining -= removed;
        }

        inventory.setChanged();
        return true;
    }

    private static void failSummon(ServerLevel level, Player player) {
        level.playSound(null, player.blockPosition(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.7F, 0.7F);
        level.sendParticles(
                DustParticleOptions.REDSTONE,
                player.getX(),
                player.getY() + 1.0D,
                player.getZ(),
                12,
                0.35D,
                0.35D,
                0.35D,
                0.02D);
    }

    private record SummonMaterial(Item item, boolean skeleton) {
    }
}
