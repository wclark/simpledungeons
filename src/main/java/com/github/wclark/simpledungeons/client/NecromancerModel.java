package com.github.wclark.simpledungeons.client;

import com.github.wclark.simpledungeons.NecromancerEntity;
import com.github.wclark.simpledungeons.SimpleDungeons;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NecromancerModel extends SkeletonModel<NecromancerEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(SimpleDungeons.MODID, "necromancer"),
            "main");

    public NecromancerModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition root = mesh.getRoot();
        createDefaultSkeletonMesh(root);

        PartDefinition head = root.getChild("head");
        head.addOrReplaceChild(
                "front_crown",
                CubeListBuilder.create().texOffs(0, 32).addBox(-4.5F, -8.8F, -4.8F, 9.0F, 2.0F, 1.0F),
                PartPose.ZERO);
        head.addOrReplaceChild(
                "left_crown",
                CubeListBuilder.create().texOffs(0, 35).addBox(-4.8F, -8.8F, -4.5F, 1.0F, 2.0F, 9.0F),
                PartPose.ZERO);
        head.addOrReplaceChild(
                "right_crown",
                CubeListBuilder.create().texOffs(0, 35).mirror().addBox(3.8F, -8.8F, -4.5F, 1.0F, 2.0F, 9.0F),
                PartPose.ZERO);
        head.addOrReplaceChild(
                "crown_top",
                CubeListBuilder.create().texOffs(24, 32).addBox(-3.0F, -9.4F, -3.0F, 6.0F, 1.0F, 6.0F),
                PartPose.ZERO);
        head.addOrReplaceChild(
                "front_gem",
                CubeListBuilder.create().texOffs(42, 32).addBox(-1.0F, -9.1F, -5.05F, 2.0F, 2.0F, 1.0F),
                PartPose.ZERO);

        PartDefinition body = root.getChild("body");
        body.addOrReplaceChild(
                "robe",
                CubeListBuilder.create().texOffs(0, 46).addBox(-4.5F, 0.0F, -2.6F, 9.0F, 20.0F, 5.0F, new CubeDeformation(0.04F)),
                PartPose.ZERO);
        body.addOrReplaceChild(
                "cape",
                CubeListBuilder.create().texOffs(28, 46).addBox(-5.0F, -0.1F, 2.35F, 10.0F, 20.0F, 1.0F),
                PartPose.ZERO);
        body.addOrReplaceChild(
                "right_shoulder",
                CubeListBuilder.create().texOffs(48, 32).addBox(-8.6F, -1.0F, -3.0F, 5.5F, 3.0F, 5.5F),
                PartPose.ZERO);
        body.addOrReplaceChild(
                "left_shoulder",
                CubeListBuilder.create().texOffs(48, 41).mirror().addBox(3.1F, -1.0F, -3.0F, 5.5F, 3.0F, 5.5F),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(NecromancerEntity necromancer, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(necromancer, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.rightArm.xRot = -Mth.HALF_PI;
        this.rightArm.yRot = -0.12F;
        this.rightArm.zRot = 0.0F;

        if (necromancer.isAggressive()) {
            this.rightArm.xRot = -Mth.HALF_PI + Mth.sin(ageInTicks * 0.45F) * 0.03F;
            this.leftArm.xRot = -1.25F;
            this.leftArm.yRot = 0.35F;
            this.leftArm.zRot = -0.15F;
        }
    }
}
