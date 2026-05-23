package com.github.wclark.simpledungeons.client;

import com.github.wclark.simpledungeons.NecromancerEntity;

import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NecromancerModel extends SkeletonModel<NecromancerEntity> {
    public NecromancerModel(ModelPart root) {
        super(root);
    }

    @Override
    public void setupAnim(NecromancerEntity necromancer, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(necromancer, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (necromancer.isAggressive()) {
            float pulse = Mth.sin(ageInTicks * 0.3F) * 0.08F;
            this.rightArm.xRot = -2.7F + pulse;
            this.rightArm.yRot = -0.25F;
            this.rightArm.zRot = 0.18F;
            this.leftArm.xRot = -1.25F;
            this.leftArm.yRot = 0.35F;
            this.leftArm.zRot = -0.15F;
        }
    }
}
