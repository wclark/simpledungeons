package com.github.wclark.simpledungeons.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import com.github.wclark.simpledungeons.FactoryRobotEntity;
import com.github.wclark.simpledungeons.SimpleDungeons;

import net.minecraft.client.model.EntityModel;
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
public class FactoryRobotModel extends EntityModel<FactoryRobotEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(SimpleDungeons.MODID, "factory_robot"),
            "main");
    private static final int COPPER = 0xFFB76A3D;
    private static final int DARK_COPPER = 0xFF6F3A24;
    private static final int IRON = 0xFFD0D5D7;
    private static final int DARK_METAL = 0xFF25242A;
    private static final int REDSTONE = 0xFFE31E24;

    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart rightAxeHandle;
    private final ModelPart leftAxeHandle;
    private final ModelPart rightAxeBlade;
    private final ModelPart leftAxeBlade;
    private final ModelPart eyes;

    public FactoryRobotModel(ModelPart root) {
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        this.rightAxeHandle = root.getChild("right_axe_handle");
        this.leftAxeHandle = root.getChild("left_axe_handle");
        this.rightAxeBlade = root.getChild("right_axe_blade");
        this.leftAxeBlade = root.getChild("left_axe_blade");
        this.eyes = root.getChild("eyes");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.5F, -8.5F, -4.5F, 9.0F, 8.0F, 9.0F)
                        .texOffs(0, 0).addBox(-5.5F, -10.0F, -5.5F, 11.0F, 2.0F, 11.0F, new CubeDeformation(0.05F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild(
                "eyes",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3.0F, -5.5F, -4.9F, 2.0F, 1.5F, 0.5F)
                        .texOffs(0, 0).addBox(1.0F, -5.5F, -4.9F, 2.0F, 1.5F, 0.5F),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-5.0F, 0.0F, -3.0F, 10.0F, 12.0F, 6.0F)
                        .texOffs(0, 0).addBox(-6.0F, 2.0F, -3.5F, 12.0F, 3.0F, 7.0F, new CubeDeformation(0.05F)),
                PartPose.ZERO);
        root.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3.0F, -1.0F, -2.0F, 4.0F, 13.0F, 4.0F)
                        .texOffs(0, 0).addBox(-4.0F, -2.0F, -2.75F, 5.0F, 4.0F, 5.5F),
                PartPose.offset(-6.0F, 2.0F, 0.0F));
        root.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-1.0F, -1.0F, -2.0F, 4.0F, 13.0F, 4.0F)
                        .texOffs(0, 0).addBox(-1.0F, -2.0F, -2.75F, 5.0F, 4.0F, 5.5F),
                PartPose.offset(6.0F, 2.0F, 0.0F));
        root.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(-2.5F, 12.0F, 0.0F));
        root.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(2.5F, 12.0F, 0.0F));
        root.addOrReplaceChild(
                "right_axe_handle",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -7.0F, -0.5F, 1.0F, 14.0F, 1.0F),
                PartPose.offsetAndRotation(-9.0F, 10.0F, -1.0F, 0.0F, 0.0F, 0.28F));
        root.addOrReplaceChild(
                "left_axe_handle",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -7.0F, -0.5F, 1.0F, 14.0F, 1.0F),
                PartPose.offsetAndRotation(9.0F, 10.0F, -1.0F, 0.0F, 0.0F, -0.28F));
        root.addOrReplaceChild(
                "right_axe_blade",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3.0F, -4.0F, -0.75F, 4.0F, 5.0F, 1.5F)
                        .texOffs(0, 0).addBox(0.25F, -3.0F, -0.75F, 2.5F, 3.0F, 1.5F),
                PartPose.offsetAndRotation(-10.0F, 5.0F, -1.0F, 0.0F, 0.0F, 0.28F));
        root.addOrReplaceChild(
                "left_axe_blade",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-1.0F, -4.0F, -0.75F, 4.0F, 5.0F, 1.5F)
                        .texOffs(0, 0).addBox(-2.75F, -3.0F, -0.75F, 2.5F, 3.0F, 1.5F),
                PartPose.offsetAndRotation(10.0F, 5.0F, -1.0F, 0.0F, 0.0F, -0.28F));

        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void setupAnim(FactoryRobotEntity robot, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.head.xRot = headPitch * Mth.DEG_TO_RAD;
        this.eyes.yRot = this.head.yRot;
        this.eyes.xRot = this.head.xRot;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.body.render(poseStack, buffer, packedLight, packedOverlay, COPPER);
        this.head.render(poseStack, buffer, packedLight, packedOverlay, COPPER);
        this.eyes.render(poseStack, buffer, packedLight, packedOverlay, REDSTONE);
        this.rightArm.render(poseStack, buffer, packedLight, packedOverlay, DARK_COPPER);
        this.leftArm.render(poseStack, buffer, packedLight, packedOverlay, DARK_COPPER);
        this.rightLeg.render(poseStack, buffer, packedLight, packedOverlay, DARK_METAL);
        this.leftLeg.render(poseStack, buffer, packedLight, packedOverlay, DARK_METAL);
        this.rightAxeHandle.render(poseStack, buffer, packedLight, packedOverlay, DARK_METAL);
        this.leftAxeHandle.render(poseStack, buffer, packedLight, packedOverlay, DARK_METAL);
        this.rightAxeBlade.render(poseStack, buffer, packedLight, packedOverlay, IRON);
        this.leftAxeBlade.render(poseStack, buffer, packedLight, packedOverlay, IRON);
    }
}
