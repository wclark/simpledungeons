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

    private static final int COPPER = 0xFFB66B3B;
    private static final int COPPER_LIGHT = 0xFFD18A4C;
    private static final int COPPER_DARK = 0xFF7A3D24;
    private static final int COPPER_SHADOW = 0xFF552B1B;
    private static final int COPPER_RIVET = 0xFFC98242;
    private static final int IRON = 0xFFD4D8D8;
    private static final int IRON_SHADOW = 0xFF858D90;
    private static final int DARK_METAL = 0xFF1D2024;
    private static final int REDSTONE = 0xFFE21A22;
    private static final int REDSTONE_DARK = 0xFF871016;

    private final ModelPart head;
    private final ModelPart headCap;
    private final ModelPart headShadow;
    private final ModelPart headRivets;
    private final ModelPart brow;
    private final ModelPart eyes;
    private final ModelPart mouth;
    private final ModelPart neck;
    private final ModelPart collar;
    private final ModelPart body;
    private final ModelPart chestPanel;
    private final ModelPart chestCore;
    private final ModelPart chestRivets;
    private final ModelPart bodyShadow;
    private final ModelPart belt;
    private final ModelPart rightShoulder;
    private final ModelPart leftShoulder;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightForearm;
    private final ModelPart leftForearm;
    private final ModelPart rightArmTrim;
    private final ModelPart leftArmTrim;
    private final ModelPart rightHand;
    private final ModelPart leftHand;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart rightKnee;
    private final ModelPart leftKnee;
    private final ModelPart rightLegTrim;
    private final ModelPart leftLegTrim;
    private final ModelPart rightFoot;
    private final ModelPart leftFoot;
    private final ModelPart rightAxeHandle;
    private final ModelPart leftAxeHandle;
    private final ModelPart rightAxeHead;
    private final ModelPart leftAxeHead;
    private final ModelPart rightAxeEdge;
    private final ModelPart leftAxeEdge;

    public FactoryRobotModel(ModelPart root) {
        this.head = root.getChild("head");
        this.headCap = root.getChild("head_cap");
        this.headShadow = root.getChild("head_shadow");
        this.headRivets = root.getChild("head_rivets");
        this.brow = root.getChild("brow");
        this.eyes = root.getChild("eyes");
        this.mouth = root.getChild("mouth");
        this.neck = root.getChild("neck");
        this.collar = root.getChild("collar");
        this.body = root.getChild("body");
        this.chestPanel = root.getChild("chest_panel");
        this.chestCore = root.getChild("chest_core");
        this.chestRivets = root.getChild("chest_rivets");
        this.bodyShadow = root.getChild("body_shadow");
        this.belt = root.getChild("belt");
        this.rightShoulder = root.getChild("right_shoulder");
        this.leftShoulder = root.getChild("left_shoulder");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightForearm = root.getChild("right_forearm");
        this.leftForearm = root.getChild("left_forearm");
        this.rightArmTrim = root.getChild("right_arm_trim");
        this.leftArmTrim = root.getChild("left_arm_trim");
        this.rightHand = root.getChild("right_hand");
        this.leftHand = root.getChild("left_hand");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        this.rightKnee = root.getChild("right_knee");
        this.leftKnee = root.getChild("left_knee");
        this.rightLegTrim = root.getChild("right_leg_trim");
        this.leftLegTrim = root.getChild("left_leg_trim");
        this.rightFoot = root.getChild("right_foot");
        this.leftFoot = root.getChild("left_foot");
        this.rightAxeHandle = root.getChild("right_axe_handle");
        this.leftAxeHandle = root.getChild("left_axe_handle");
        this.rightAxeHead = root.getChild("right_axe_head");
        this.leftAxeHead = root.getChild("left_axe_head");
        this.rightAxeEdge = root.getChild("right_axe_edge");
        this.leftAxeEdge = root.getChild("left_axe_edge");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.5F, -8.0F, -4.5F, 9.0F, 8.5F, 9.0F), PartPose.ZERO);
        root.addOrReplaceChild("head_cap", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5.5F, -10.0F, -5.5F, 11.0F, 2.0F, 11.0F, new CubeDeformation(0.05F))
                .texOffs(0, 0).addBox(-3.0F, -11.0F, -3.0F, 6.0F, 1.0F, 6.0F), PartPose.ZERO);
        root.addOrReplaceChild("head_shadow", CubeListBuilder.create()
                .texOffs(0, 0).addBox(3.55F, -7.25F, -4.65F, 1.2F, 7.2F, 9.3F)
                .texOffs(0, 0).addBox(-4.75F, -0.6F, -4.65F, 9.5F, 1.2F, 9.3F), PartPose.ZERO);
        root.addOrReplaceChild("head_rivets", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.1F, -7.2F, -5.1F, 0.9F, 0.9F, 0.7F)
                .texOffs(0, 0).addBox(3.2F, -7.2F, -5.1F, 0.9F, 0.9F, 0.7F)
                .texOffs(0, 0).addBox(-4.1F, -1.8F, -5.1F, 0.9F, 0.9F, 0.7F)
                .texOffs(0, 0).addBox(3.2F, -1.8F, -5.1F, 0.9F, 0.9F, 0.7F), PartPose.ZERO);
        root.addOrReplaceChild("brow", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.8F, -6.2F, -4.95F, 7.6F, 1.2F, 0.8F), PartPose.ZERO);
        root.addOrReplaceChild("eyes", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.1F, -4.8F, -5.05F, 2.2F, 1.4F, 0.8F)
                .texOffs(0, 0).addBox(0.9F, -4.8F, -5.05F, 2.2F, 1.4F, 0.8F), PartPose.ZERO);
        root.addOrReplaceChild("mouth", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.4F, -2.1F, -4.95F, 4.8F, 0.7F, 0.7F), PartPose.ZERO);

        root.addOrReplaceChild("neck", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.0F, -0.4F, -2.0F, 4.0F, 3.0F, 4.0F), PartPose.ZERO);
        root.addOrReplaceChild("collar", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5.2F, 0.1F, -3.4F, 10.4F, 2.2F, 6.8F, new CubeDeformation(0.05F)), PartPose.ZERO);

        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5.0F, 1.6F, -3.0F, 10.0F, 10.9F, 6.0F), PartPose.ZERO);
        root.addOrReplaceChild("chest_panel", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.8F, 3.0F, -3.45F, 7.6F, 5.2F, 0.9F)
                .texOffs(0, 0).addBox(-5.9F, 2.1F, -3.55F, 2.0F, 3.8F, 1.0F)
                .texOffs(0, 0).addBox(3.9F, 2.1F, -3.55F, 2.0F, 3.8F, 1.0F), PartPose.ZERO);
        root.addOrReplaceChild("chest_core", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.2F, 4.4F, -3.95F, 2.4F, 2.4F, 0.8F), PartPose.ZERO);
        root.addOrReplaceChild("chest_rivets", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.4F, 3.0F, -4.05F, 0.8F, 0.8F, 0.7F)
                .texOffs(0, 0).addBox(3.6F, 3.0F, -4.05F, 0.8F, 0.8F, 0.7F)
                .texOffs(0, 0).addBox(-4.4F, 7.9F, -4.05F, 0.8F, 0.8F, 0.7F)
                .texOffs(0, 0).addBox(3.6F, 7.9F, -4.05F, 0.8F, 0.8F, 0.7F), PartPose.ZERO);
        root.addOrReplaceChild("body_shadow", CubeListBuilder.create()
                .texOffs(0, 0).addBox(4.2F, 2.2F, -2.6F, 1.2F, 9.6F, 5.2F)
                .texOffs(0, 0).addBox(-5.4F, 11.1F, -3.2F, 10.8F, 1.4F, 6.4F), PartPose.ZERO);
        root.addOrReplaceChild("belt", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5.8F, 9.7F, -3.6F, 11.6F, 2.4F, 7.2F, new CubeDeformation(0.03F)), PartPose.ZERO);

        root.addOrReplaceChild("right_shoulder", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.2F, -2.0F, -3.0F, 5.2F, 4.3F, 6.0F), PartPose.offset(-6.0F, 3.2F, 0.0F));
        root.addOrReplaceChild("left_shoulder", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.0F, -2.0F, -3.0F, 5.2F, 4.3F, 6.0F), PartPose.offset(6.0F, 3.2F, 0.0F));
        root.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.8F, 1.3F, -1.6F, 3.4F, 6.4F, 3.2F), PartPose.offset(-6.0F, 3.2F, 0.0F));
        root.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-0.6F, 1.3F, -1.6F, 3.4F, 6.4F, 3.2F), PartPose.offset(6.0F, 3.2F, 0.0F));
        root.addOrReplaceChild("right_forearm", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.1F, 7.1F, -1.9F, 3.8F, 6.0F, 3.8F), PartPose.offset(-6.0F, 3.2F, 0.0F));
        root.addOrReplaceChild("left_forearm", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-0.7F, 7.1F, -1.9F, 3.8F, 6.0F, 3.8F), PartPose.offset(6.0F, 3.2F, 0.0F));
        root.addOrReplaceChild("right_arm_trim", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.35F, 7.0F, -2.05F, 4.2F, 1.2F, 4.1F)
                .texOffs(0, 0).addBox(-3.25F, 11.2F, -2.1F, 4.0F, 1.0F, 4.2F), PartPose.offset(-6.0F, 3.2F, 0.0F));
        root.addOrReplaceChild("left_arm_trim", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-0.85F, 7.0F, -2.05F, 4.2F, 1.2F, 4.1F)
                .texOffs(0, 0).addBox(-0.75F, 11.2F, -2.1F, 4.0F, 1.0F, 4.2F), PartPose.offset(6.0F, 3.2F, 0.0F));
        root.addOrReplaceChild("right_hand", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.0F, 12.5F, -2.0F, 3.6F, 2.2F, 4.0F), PartPose.offset(-6.0F, 3.2F, 0.0F));
        root.addOrReplaceChild("left_hand", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-0.6F, 12.5F, -2.0F, 3.6F, 2.2F, 4.0F), PartPose.offset(6.0F, 3.2F, 0.0F));

        root.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.7F, 0.0F, -1.6F, 3.4F, 9.5F, 3.2F), PartPose.offset(-2.4F, 12.4F, 0.0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.7F, 0.0F, -1.6F, 3.4F, 9.5F, 3.2F), PartPose.offset(2.4F, 12.4F, 0.0F));
        root.addOrReplaceChild("right_knee", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.1F, 4.8F, -2.0F, 4.2F, 2.2F, 4.0F), PartPose.offset(-2.4F, 12.4F, 0.0F));
        root.addOrReplaceChild("left_knee", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.1F, 4.8F, -2.0F, 4.2F, 2.2F, 4.0F), PartPose.offset(2.4F, 12.4F, 0.0F));
        root.addOrReplaceChild("right_leg_trim", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.0F, 2.2F, -1.9F, 4.0F, 1.0F, 3.8F), PartPose.offset(-2.4F, 12.4F, 0.0F));
        root.addOrReplaceChild("left_leg_trim", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.0F, 2.2F, -1.9F, 4.0F, 1.0F, 3.8F), PartPose.offset(2.4F, 12.4F, 0.0F));
        root.addOrReplaceChild("right_foot", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.3F, 9.0F, -3.0F, 4.6F, 2.6F, 5.0F), PartPose.offset(-2.4F, 12.4F, 0.0F));
        root.addOrReplaceChild("left_foot", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.3F, 9.0F, -3.0F, 4.6F, 2.6F, 5.0F), PartPose.offset(2.4F, 12.4F, 0.0F));

        root.addOrReplaceChild("right_axe_handle", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-0.45F, -2.0F, -0.45F, 0.9F, 7.6F, 0.9F), PartPose.offsetAndRotation(-8.9F, 17.0F, -2.0F, 0.0F, 0.0F, 0.10F));
        root.addOrReplaceChild("left_axe_handle", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-0.45F, -2.0F, -0.45F, 0.9F, 7.6F, 0.9F), PartPose.offsetAndRotation(8.9F, 17.0F, -2.0F, 0.0F, 0.0F, -0.10F));
        root.addOrReplaceChild("right_axe_head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.7F, -1.7F, -0.35F, 2.8F, 3.8F, 0.7F)
                .texOffs(0, 0).addBox(-0.1F, -0.8F, -0.35F, 1.7F, 2.0F, 0.7F), PartPose.offsetAndRotation(-9.4F, 16.7F, -2.0F, 0.0F, 0.0F, 0.10F));
        root.addOrReplaceChild("left_axe_head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-0.1F, -1.7F, -0.35F, 2.8F, 3.8F, 0.7F)
                .texOffs(0, 0).addBox(-1.6F, -0.8F, -0.35F, 1.7F, 2.0F, 0.7F), PartPose.offsetAndRotation(9.4F, 16.7F, -2.0F, 0.0F, 0.0F, -0.10F));
        root.addOrReplaceChild("right_axe_edge", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.1F, -0.95F, -0.4F, 0.7F, 2.4F, 0.8F), PartPose.offsetAndRotation(-9.4F, 16.7F, -2.0F, 0.0F, 0.0F, 0.10F));
        root.addOrReplaceChild("left_axe_edge", CubeListBuilder.create()
                .texOffs(0, 0).addBox(2.4F, -0.95F, -0.4F, 0.7F, 2.4F, 0.8F), PartPose.offsetAndRotation(9.4F, 16.7F, -2.0F, 0.0F, 0.0F, -0.10F));

        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void setupAnim(FactoryRobotEntity robot, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float yRot = netHeadYaw * Mth.DEG_TO_RAD;
        float xRot = headPitch * Mth.DEG_TO_RAD;
        applyHeadPose(head, yRot, xRot);
        applyHeadPose(headCap, yRot, xRot);
        applyHeadPose(headShadow, yRot, xRot);
        applyHeadPose(headRivets, yRot, xRot);
        applyHeadPose(brow, yRot, xRot);
        applyHeadPose(eyes, yRot, xRot);
        applyHeadPose(mouth, yRot, xRot);
    }

    private static void applyHeadPose(ModelPart part, float yRot, float xRot) {
        part.yRot = yRot;
        part.xRot = xRot;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        render(neck, poseStack, buffer, packedLight, packedOverlay, DARK_METAL);
        render(collar, poseStack, buffer, packedLight, packedOverlay, COPPER_DARK);
        render(body, poseStack, buffer, packedLight, packedOverlay, COPPER);
        render(bodyShadow, poseStack, buffer, packedLight, packedOverlay, COPPER_SHADOW);
        render(chestPanel, poseStack, buffer, packedLight, packedOverlay, COPPER_LIGHT);
        render(chestCore, poseStack, buffer, packedLight, packedOverlay, REDSTONE_DARK);
        render(chestRivets, poseStack, buffer, packedLight, packedOverlay, COPPER_RIVET);
        render(belt, poseStack, buffer, packedLight, packedOverlay, DARK_METAL);
        render(head, poseStack, buffer, packedLight, packedOverlay, COPPER);
        render(headShadow, poseStack, buffer, packedLight, packedOverlay, COPPER_DARK);
        render(headCap, poseStack, buffer, packedLight, packedOverlay, COPPER_LIGHT);
        render(headRivets, poseStack, buffer, packedLight, packedOverlay, COPPER_RIVET);
        render(brow, poseStack, buffer, packedLight, packedOverlay, DARK_METAL);
        render(eyes, poseStack, buffer, packedLight, packedOverlay, REDSTONE);
        render(mouth, poseStack, buffer, packedLight, packedOverlay, DARK_METAL);
        render(rightShoulder, poseStack, buffer, packedLight, packedOverlay, COPPER_DARK);
        render(leftShoulder, poseStack, buffer, packedLight, packedOverlay, COPPER_DARK);
        render(rightArm, poseStack, buffer, packedLight, packedOverlay, COPPER);
        render(leftArm, poseStack, buffer, packedLight, packedOverlay, COPPER);
        render(rightForearm, poseStack, buffer, packedLight, packedOverlay, COPPER_SHADOW);
        render(leftForearm, poseStack, buffer, packedLight, packedOverlay, COPPER_SHADOW);
        render(rightArmTrim, poseStack, buffer, packedLight, packedOverlay, COPPER_RIVET);
        render(leftArmTrim, poseStack, buffer, packedLight, packedOverlay, COPPER_RIVET);
        render(rightHand, poseStack, buffer, packedLight, packedOverlay, DARK_METAL);
        render(leftHand, poseStack, buffer, packedLight, packedOverlay, DARK_METAL);
        render(rightLeg, poseStack, buffer, packedLight, packedOverlay, DARK_METAL);
        render(leftLeg, poseStack, buffer, packedLight, packedOverlay, DARK_METAL);
        render(rightKnee, poseStack, buffer, packedLight, packedOverlay, COPPER_DARK);
        render(leftKnee, poseStack, buffer, packedLight, packedOverlay, COPPER_DARK);
        render(rightLegTrim, poseStack, buffer, packedLight, packedOverlay, IRON_SHADOW);
        render(leftLegTrim, poseStack, buffer, packedLight, packedOverlay, IRON_SHADOW);
        render(rightFoot, poseStack, buffer, packedLight, packedOverlay, IRON_SHADOW);
        render(leftFoot, poseStack, buffer, packedLight, packedOverlay, IRON_SHADOW);
        render(rightAxeHandle, poseStack, buffer, packedLight, packedOverlay, DARK_METAL);
        render(leftAxeHandle, poseStack, buffer, packedLight, packedOverlay, DARK_METAL);
        render(rightAxeHead, poseStack, buffer, packedLight, packedOverlay, IRON_SHADOW);
        render(leftAxeHead, poseStack, buffer, packedLight, packedOverlay, IRON_SHADOW);
        render(rightAxeEdge, poseStack, buffer, packedLight, packedOverlay, IRON);
        render(leftAxeEdge, poseStack, buffer, packedLight, packedOverlay, IRON);
    }

    private static void render(ModelPart part, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        part.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
