package com.github.wclark.simpledungeons;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class RestlessGraveSoilBlock extends Block {
    private static final int TRIGGER_RADIUS = 6;
    private static final int SCAN_VERTICAL_RADIUS = 2;
    private static final int MAX_AWAKENED_PER_SCAN = 2;

    public RestlessGraveSoilBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static void awakenNearby(ServerPlayer player) {
        if (player.tickCount % 20 != 0 || !Config.ENABLE_SURFACE_CRYPT.getAsBoolean()) {
            return;
        }

        ServerLevel level = player.serverLevel();
        BlockPos playerPos = player.blockPosition();
        int awakened = 0;

        for (BlockPos pos : BlockPos.betweenClosed(
                playerPos.offset(-TRIGGER_RADIUS, -SCAN_VERTICAL_RADIUS, -TRIGGER_RADIUS),
                playerPos.offset(TRIGGER_RADIUS, SCAN_VERTICAL_RADIUS, TRIGGER_RADIUS))) {
            if (pos.distSqr(playerPos) > TRIGGER_RADIUS * TRIGGER_RADIUS) {
                continue;
            }

            if (level.getBlockState(pos).is(SimpleDungeons.RESTLESS_GRAVE_SOIL.get())) {
                awaken(level, pos.immutable());
                awakened++;
                if (awakened >= MAX_AWAKENED_PER_SCAN) {
                    return;
                }
            }
        }
    }

    private static void awaken(ServerLevel level, BlockPos pos) {
        RandomSource random = level.random;
        level.setBlock(pos, Blocks.COARSE_DIRT.defaultBlockState(), 3);
        level.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, Blocks.COARSE_DIRT.defaultBlockState()),
                pos.getX() + 0.5,
                pos.getY() + 1.0,
                pos.getZ() + 0.5,
                36,
                0.35,
                0.25,
                0.35,
                0.08);
        level.playSound(null, pos, SoundEvents.WARDEN_DIG, SoundSource.HOSTILE, 0.9F, 1.35F + random.nextFloat() * 0.2F);

        Mob mob = random.nextBoolean() ? createZombie(level) : createSkeleton(level);
        if (mob == null) {
            return;
        }

        mob.moveTo(pos.getX() + 0.5, pos.getY() + 1.05, pos.getZ() + 0.5, random.nextFloat() * 360.0F, 0.0F);
        mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.TRIGGERED, null);
        mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 45, 5));
        mob.setDeltaMovement(0.0, 0.18, 0.0);
        mob.setPersistenceRequired();
        level.addFreshEntity(mob);
        equipGraveMob(level, mob, random);
    }

    private static Zombie createZombie(ServerLevel level) {
        return net.minecraft.world.entity.EntityType.ZOMBIE.create(level);
    }

    private static Skeleton createSkeleton(ServerLevel level) {
        return net.minecraft.world.entity.EntityType.SKELETON.create(level);
    }

    private static void equipGraveMob(ServerLevel level, Mob mob, RandomSource random) {
        boolean skeleton = mob instanceof Skeleton;
        ItemStack weapon = skeleton ? new ItemStack(Items.BOW) : new ItemStack(Items.IRON_SWORD);

        if (skeleton) {
            weaklyEnchantBow(level, weapon, random);
        }

        damageHalf(weapon, random);
        mob.setItemSlot(EquipmentSlot.MAINHAND, weapon);
        mob.setDropChance(EquipmentSlot.MAINHAND, 0.12F);

        equipArmor(mob, EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET), random);
        equipArmor(mob, EquipmentSlot.CHEST, new ItemStack(random.nextBoolean() ? Items.CHAINMAIL_CHESTPLATE : Items.IRON_CHESTPLATE), random);
        equipArmor(mob, EquipmentSlot.LEGS, new ItemStack(random.nextBoolean() ? Items.CHAINMAIL_LEGGINGS : Items.IRON_LEGGINGS), random);
        equipArmor(mob, EquipmentSlot.FEET, new ItemStack(random.nextBoolean() ? Items.CHAINMAIL_BOOTS : Items.IRON_BOOTS), random);
    }

    private static void weaklyEnchantBow(ServerLevel level, ItemStack bow, RandomSource random) {
        var enchantments = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        bow.enchant(enchantments.getHolderOrThrow(Enchantments.POWER), 1);
        if (random.nextFloat() < 0.25F) {
            bow.enchant(enchantments.getHolderOrThrow(Enchantments.PUNCH), 1);
        }
    }

    private static void equipArmor(Mob mob, EquipmentSlot slot, ItemStack stack, RandomSource random) {
        damageHalf(stack, random);
        mob.setItemSlot(slot, stack);
        mob.setDropChance(slot, 0.08F);
    }

    private static void damageHalf(ItemStack stack, RandomSource random) {
        if (!stack.isDamageableItem()) {
            return;
        }

        int maxDamage = stack.getMaxDamage();
        int variance = Math.max(1, maxDamage / 8);
        int damage = maxDamage / 2 - variance / 2 + random.nextInt(variance + 1);
        damage = Math.max(1, Math.min(maxDamage - 1, damage));
        stack.setDamageValue(damage);
    }
}
