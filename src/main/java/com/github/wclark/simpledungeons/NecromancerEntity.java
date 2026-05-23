package com.github.wclark.simpledungeons;

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class NecromancerEntity extends Monster implements RangedAttackMob {
    private static final int ATTACK_INTERVAL_TICKS = 20;
    private static final float ATTACK_RANGE_BLOCKS = 24.0F;
    private static final float ORB_SPEED = 0.25F;

    public NecromancerEntity(EntityType<? extends NecromancerEntity> type, Level level) {
        super(type, level);
        this.xpReward = 20;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, ATTACK_INTERVAL_TICKS, ATTACK_RANGE_BLOCKS));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        LivingEntity target = this.getTarget();
        this.setAggressive(target != null && target.isAlive());
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        this.equipStaff();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, spawnType, spawnData);
        this.equipStaff();
        return result;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocityScale) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        this.swing(InteractionHand.MAIN_HAND);
        BlueOrbEntity orb = new BlueOrbEntity(serverLevel, this);
        double x = target.getX() - this.getX();
        double y = target.getEyeY() - orb.getY();
        double z = target.getZ() - this.getZ();
        double horizontalDistance = Math.sqrt(x * x + z * z);
        orb.shoot(x, y + horizontalDistance * 0.08D, z, ORB_SPEED, 0.25F);
        serverLevel.addFreshEntity(orb);
        serverLevel.playSound(null, this.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.HOSTILE, 0.8F, 1.2F);
    }

    private void equipStaff() {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.SUMMONERS_STAFF.get()));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }
}
