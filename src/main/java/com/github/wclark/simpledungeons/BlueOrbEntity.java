package com.github.wclark.simpledungeons;

import javax.annotation.Nullable;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BlueOrbEntity extends ThrowableItemProjectile {
    private static final int LIFETIME_TICKS = 120;
    private static final double KNOCKBACK_STRENGTH = 0.3D;

    public BlueOrbEntity(EntityType<? extends BlueOrbEntity> type, Level level) {
        super(type, level);
    }

    public BlueOrbEntity(Level level, LivingEntity owner) {
        super(ModEntities.BLUE_ORB.get(), owner, level);
        this.setItem(new ItemStack(Blocks.LIGHT_BLUE_CONCRETE));
    }

    @Override
    protected Item getDefaultItem() {
        return Blocks.LIGHT_BLUE_CONCRETE.asItem();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.0D;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.tickCount > LIFETIME_TICKS) {
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        Entity target = hitResult.getEntity();
        Entity owner = this.getOwner();
        if (target == owner) {
            return;
        }

        if (target.hurt(this.damageSources().source(ModDamageTypes.BLUE_ORB, this, owner), 1.0F)) {
            knockbackTarget(target, owner);
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }

    @Override
    public void handleEntityEvent(byte eventId) {
        if (eventId == 3) {
            ParticleOptions particle = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Blocks.LIGHT_BLUE_CONCRETE));
            for (int i = 0; i < 10; i++) {
                this.level().addParticle(particle, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
            return;
        }

        super.handleEntityEvent(eventId);
    }

    private static void knockbackTarget(Entity target, @Nullable Entity owner) {
        Vec3 source = owner == null ? target.position().subtract(target.getDeltaMovement()) : owner.position();
        Vec3 away = target.position().subtract(source);
        double horizontalDistance = away.horizontalDistance();
        if (horizontalDistance > 0.0D) {
            target.push(away.x / horizontalDistance * KNOCKBACK_STRENGTH, 0.05D, away.z / horizontalDistance * KNOCKBACK_STRENGTH);
        }
    }
}
