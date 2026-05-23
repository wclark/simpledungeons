package com.github.wclark.simpledungeons.client;

import com.github.wclark.simpledungeons.NecromancerEntity;
import com.github.wclark.simpledungeons.SimpleDungeons;

import net.minecraft.client.model.HumanoidModel;
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
public class NecromancerModel extends HumanoidModel<NecromancerEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(SimpleDungeons.MODID, "necromancer"),
            "main");

    public NecromancerModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F)
                        .texOffs(40, 0).addBox(-5.0F, -10.0F, -5.0F, 10.0F, 2.0F, 10.0F)
                        .texOffs(84, 0).addBox(-3.0F, -11.0F, -3.0F, 6.0F, 1.0F, 6.0F)
                        .texOffs(112, 0).addBox(-1.0F, -10.4F, -5.55F, 2.0F, 2.0F, 1.0F)
                        .texOffs(124, 0).addBox(-5.55F, -9.8F, -1.0F, 1.0F, 2.0F, 2.0F)
                        .texOffs(124, 0).mirror().addBox(4.55F, -9.8F, -1.0F, 1.0F, 2.0F, 2.0F)
                        .mirror(false)
                        .texOffs(136, 0).addBox(-4.35F, -4.5F, -4.6F, 1.0F, 6.0F, 1.0F)
                        .texOffs(136, 0).mirror().addBox(3.35F, -4.5F, -4.6F, 1.0F, 6.0F, 1.0F),
                PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition body = root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 8.0F, 4.0F)
                        .texOffs(32, 32).addBox(-5.0F, 8.0F, -2.5F, 10.0F, 14.0F, 5.0F, new CubeDeformation(0.05F))
                        .texOffs(70, 32).addBox(-2.0F, 8.0F, -3.05F, 4.0F, 14.0F, 1.0F)
                        .texOffs(88, 32).addBox(-5.0F, 0.0F, 2.35F, 10.0F, 20.0F, 1.0F)
                        .texOffs(112, 64).addBox(-4.5F, 6.5F, -2.75F, 9.0F, 2.0F, 5.0F)
                        .texOffs(116, 32).addBox(-8.0F, -1.0F, -3.0F, 4.0F, 3.0F, 6.0F)
                        .texOffs(116, 32).mirror().addBox(4.0F, -1.0F, -3.0F, 4.0F, 3.0F, 6.0F),
                PartPose.ZERO);

        PartDefinition rightArm = root.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create()
                        .texOffs(0, 64).addBox(-1.5F, -2.0F, -1.5F, 3.0F, 12.0F, 3.0F)
                        .texOffs(32, 64).addBox(-2.25F, -2.25F, -2.0F, 5.0F, 7.0F, 4.0F),
                PartPose.offset(-5.0F, 2.0F, 0.0F));
        PartDefinition leftArm = root.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create()
                        .texOffs(16, 64).mirror().addBox(-1.5F, -2.0F, -1.5F, 3.0F, 12.0F, 3.0F)
                        .texOffs(54, 64).mirror().addBox(-2.75F, -2.25F, -2.0F, 5.0F, 7.0F, 4.0F),
                PartPose.offset(5.0F, 2.0F, 0.0F));
        root.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create().texOffs(80, 64).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F),
                PartPose.offset(-1.9F, 12.0F, 0.0F));
        root.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create().texOffs(96, 64).mirror().addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F),
                PartPose.offset(1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(NecromancerEntity necromancer, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(necromancer, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.rightArm.xRot = -Mth.HALF_PI;
        this.rightArm.yRot = -0.12F;
        this.rightArm.zRot = 0.0F;

        if (necromancer.isSummoning()) {
            this.rightArm.z = 0.0F;
            this.rightArm.x = -5.0F;
            this.leftArm.z = 0.0F;
            this.leftArm.x = 5.0F;
            this.rightArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
            this.leftArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
            this.rightArm.yRot = 0.0F;
            this.leftArm.yRot = 0.0F;
            this.rightArm.zRot = (float) (Math.PI * 3.0D / 4.0D);
            this.leftArm.zRot = (float) (-Math.PI * 3.0D / 4.0D);
            return;
        }

        if (necromancer.isAggressive()) {
            this.rightArm.xRot = -Mth.HALF_PI + Mth.sin(ageInTicks * 0.45F) * 0.03F;
            this.leftArm.xRot = -1.15F;
            this.leftArm.yRot = 0.3F;
            this.leftArm.zRot = -0.12F;
        }
    }
}
