package com.github.wclark.simpledungeons.client;

import com.github.wclark.simpledungeons.CogMinionEntity;
import com.github.wclark.simpledungeons.SimpleDungeons;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CogMinionModel extends EntityModel<CogMinionEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(SimpleDungeons.MODID, "cog_minion"),
            "main");

    private static final int BRONZE = 0xFFB97719;
    private static final int BRONZE_LIGHT = 0xFFD59628;
    private static final int BRONZE_DARK = 0xFF7E4A10;
    private static final int COPPER = 0xFF9E5A20;
    private static final int COPPER_DARK = 0xFF5A301B;
    private static final int DARK_METAL = 0xFF242321;
    private static final int IRON = 0xFF9A9A92;
    private static final int IRON_DARK = 0xFF63635E;
    private static final int GLASS = 0xFFBDEDEA;
    private static final int GLASS_SHADOW = 0xFF74C9C9;

    private final ModelPart treadBase;
    private final ModelPart treadRim;
    private final ModelPart treadWheels;
    private final ModelPart body;
    private final ModelPart bodyTrim;
    private final ModelPart chestCore;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart headFrame;
    private final ModelPart headCap;
    private final ModelPart headEye;
    private final ModelPart sideEyeLeft;
    private final ModelPart sideEyeRight;
    private final ModelPart antennaLeft;
    private final ModelPart antennaRight;
    private final ModelPart rightShoulder;
    private final ModelPart leftShoulder;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightHand;
    private final ModelPart leftHand;
    private final ModelPart cog;
    private final ModelPart miniCog;
    private final ModelPart wrenchHandle;
    private final ModelPart wrenchHead;
    private final ModelPart rearPlate;

    public CogMinionModel(ModelPart root) {
        this.treadBase = root.getChild("tread_base");
        this.treadRim = root.getChild("tread_rim");
        this.treadWheels = root.getChild("tread_wheels");
        this.body = root.getChild("body");
        this.bodyTrim = root.getChild("body_trim");
        this.chestCore = root.getChild("chest_core");
        this.neck = root.getChild("neck");
        this.head = root.getChild("head");
        this.headFrame = root.getChild("head_frame");
        this.headCap = root.getChild("head_cap");
        this.headEye = root.getChild("head_eye");
        this.sideEyeLeft = root.getChild("side_eye_left");
        this.sideEyeRight = root.getChild("side_eye_right");
        this.antennaLeft = root.getChild("antenna_left");
        this.antennaRight = root.getChild("antenna_right");
        this.rightShoulder = root.getChild("right_shoulder");
        this.leftShoulder = root.getChild("left_shoulder");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightHand = root.getChild("right_hand");
        this.leftHand = root.getChild("left_hand");
        this.cog = root.getChild("cog");
        this.miniCog = root.getChild("mini_cog");
        this.wrenchHandle = root.getChild("wrench_handle");
        this.wrenchHead = root.getChild("wrench_head");
        this.rearPlate = root.getChild("rear_plate");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        var root = mesh.getRoot();

        root.addOrReplaceChild("tread_base", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, 20.4F, -3.2F, 8.0F, 3.2F, 6.4F), PartPose.ZERO);
        root.addOrReplaceChild("tread_rim", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.5F, 19.9F, -3.5F, 9.0F, 1.0F, 7.0F)
                .texOffs(0, 0).addBox(-4.5F, 23.2F, -3.5F, 9.0F, 0.8F, 7.0F), PartPose.ZERO);
        root.addOrReplaceChild("tread_wheels", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.6F, 21.0F, -3.8F, 1.8F, 1.8F, 0.8F)
                .texOffs(0, 0).addBox(-0.9F, 21.0F, -3.8F, 1.8F, 1.8F, 0.8F)
                .texOffs(0, 0).addBox(1.8F, 21.0F, -3.8F, 1.8F, 1.8F, 0.8F)
                .texOffs(0, 0).addBox(-3.6F, 21.0F, 3.0F, 1.8F, 1.8F, 0.8F)
                .texOffs(0, 0).addBox(-0.9F, 21.0F, 3.0F, 1.8F, 1.8F, 0.8F)
                .texOffs(0, 0).addBox(1.8F, 21.0F, 3.0F, 1.8F, 1.8F, 0.8F), PartPose.ZERO);

        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.5F, 10.7F, -3.1F, 9.0F, 9.4F, 6.2F), PartPose.ZERO);
        root.addOrReplaceChild("body_trim", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5.0F, 10.3F, -3.45F, 10.0F, 1.4F, 6.9F)
                .texOffs(0, 0).addBox(-4.9F, 18.7F, -3.45F, 9.8F, 1.2F, 6.9F)
                .texOffs(0, 0).addBox(-4.95F, 12.2F, -3.55F, 1.1F, 5.8F, 7.1F)
                .texOffs(0, 0).addBox(3.85F, 12.2F, -3.55F, 1.1F, 5.8F, 7.1F), PartPose.ZERO);
        root.addOrReplaceChild("chest_core", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.6F, 12.6F, -3.75F, 3.2F, 4.8F, 0.8F)
                .texOffs(0, 0).addBox(-0.9F, 13.2F, -3.95F, 1.8F, 3.6F, 0.6F), PartPose.ZERO);
        root.addOrReplaceChild("neck", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.0F, 8.7F, -1.8F, 4.0F, 2.4F, 3.6F), PartPose.ZERO);

        root.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.8F, 1.3F, -4.4F, 9.6F, 8.2F, 8.8F), PartPose.ZERO);
        root.addOrReplaceChild("head_frame", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5.3F, 1.0F, -4.75F, 10.6F, 1.4F, 9.5F)
                .texOffs(0, 0).addBox(-5.3F, 8.2F, -4.75F, 10.6F, 1.4F, 9.5F)
                .texOffs(0, 0).addBox(-5.4F, 2.2F, -4.85F, 1.3F, 6.2F, 9.7F)
                .texOffs(0, 0).addBox(4.1F, 2.2F, -4.85F, 1.3F, 6.2F, 9.7F), PartPose.ZERO);
        root.addOrReplaceChild("head_cap", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.2F, 0.0F, -3.2F, 6.4F, 1.4F, 6.4F), PartPose.ZERO);
        root.addOrReplaceChild("head_eye", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.7F, 4.0F, -5.05F, 3.4F, 3.4F, 0.6F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.78F));
        root.addOrReplaceChild("side_eye_left", CubeListBuilder.create()
                .texOffs(0, 0).addBox(4.85F, 4.2F, -1.3F, 0.6F, 2.0F, 2.6F), PartPose.ZERO);
        root.addOrReplaceChild("side_eye_right", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5.45F, 4.2F, -1.3F, 0.6F, 2.0F, 2.6F), PartPose.ZERO);
        root.addOrReplaceChild("antenna_left", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.8F, -3.6F, -1.2F, 1.0F, 3.8F, 1.0F), PartPose.ZERO);
        root.addOrReplaceChild("antenna_right", CubeListBuilder.create()
                .texOffs(0, 0).addBox(0.8F, -4.2F, -1.2F, 1.0F, 4.4F, 1.0F), PartPose.ZERO);

        root.addOrReplaceChild("right_shoulder", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-7.3F, 11.2F, -2.4F, 3.3F, 3.3F, 4.8F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.20F));
        root.addOrReplaceChild("left_shoulder", CubeListBuilder.create()
                .texOffs(0, 0).addBox(4.0F, 11.2F, -2.4F, 3.3F, 3.3F, 4.8F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.20F));
        root.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-7.5F, 14.0F, -1.8F, 2.4F, 6.4F, 3.6F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.18F));
        root.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(0, 0).addBox(5.1F, 14.0F, -1.8F, 2.4F, 6.4F, 3.6F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.18F));
        root.addOrReplaceChild("right_hand", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-8.3F, 19.2F, -1.3F, 3.8F, 1.4F, 2.6F)
                .texOffs(0, 0).addBox(-8.9F, 20.0F, -1.2F, 1.3F, 2.2F, 1.1F)
                .texOffs(0, 0).addBox(-5.2F, 20.0F, 0.1F, 1.3F, 2.2F, 1.1F), PartPose.ZERO);
        root.addOrReplaceChild("left_hand", CubeListBuilder.create()
                .texOffs(0, 0).addBox(4.5F, 19.2F, -1.3F, 3.8F, 1.4F, 2.6F)
                .texOffs(0, 0).addBox(7.6F, 20.0F, -1.2F, 1.3F, 2.2F, 1.1F)
                .texOffs(0, 0).addBox(3.9F, 20.0F, 0.1F, 1.3F, 2.2F, 1.1F), PartPose.ZERO);

        root.addOrReplaceChild("cog", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-9.0F, 10.7F, 2.5F, 3.4F, 3.4F, 1.1F)
                .texOffs(0, 0).addBox(-9.6F, 11.6F, 2.6F, 4.6F, 1.6F, 0.9F)
                .texOffs(0, 0).addBox(-8.4F, 10.1F, 2.6F, 1.6F, 4.6F, 0.9F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.78F));
        root.addOrReplaceChild("mini_cog", CubeListBuilder.create()
                .texOffs(0, 0).addBox(6.1F, 5.7F, 2.8F, 2.6F, 2.6F, 1.0F)
                .texOffs(0, 0).addBox(5.7F, 6.35F, 2.9F, 3.4F, 1.3F, 0.8F)
                .texOffs(0, 0).addBox(6.75F, 5.35F, 2.9F, 1.3F, 3.4F, 0.8F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.60F));
        root.addOrReplaceChild("wrench_handle", CubeListBuilder.create()
                .texOffs(0, 0).addBox(6.6F, 16.7F, -3.6F, 1.0F, 6.0F, 1.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.25F, 0.0F, -0.35F));
        root.addOrReplaceChild("wrench_head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(5.9F, 15.8F, -3.9F, 2.4F, 1.2F, 1.2F)
                .texOffs(0, 0).addBox(5.6F, 15.0F, -3.9F, 0.9F, 1.6F, 1.2F)
                .texOffs(0, 0).addBox(7.6F, 15.0F, -3.9F, 0.9F, 1.6F, 1.2F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.25F, 0.0F, -0.35F));
        root.addOrReplaceChild("rear_plate", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.2F, 12.0F, 3.0F, 6.4F, 5.4F, 1.2F)
                .texOffs(0, 0).addBox(-2.5F, 12.8F, 4.05F, 5.0F, 3.8F, 0.7F), PartPose.ZERO);

        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void setupAnim(CogMinionEntity cogMinion, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float yRot = netHeadYaw * Mth.DEG_TO_RAD;
        float xRot = headPitch * Mth.DEG_TO_RAD;
        head.yRot = yRot * 0.5F;
        headFrame.yRot = head.yRot;
        headCap.yRot = head.yRot;
        headEye.yRot = head.yRot;
        sideEyeLeft.yRot = head.yRot;
        sideEyeRight.yRot = head.yRot;
        antennaLeft.yRot = head.yRot;
        antennaRight.yRot = head.yRot;
        head.xRot = xRot * 0.35F;
        headFrame.xRot = head.xRot;
        headCap.xRot = head.xRot;
        headEye.xRot = head.xRot;
        sideEyeLeft.xRot = head.xRot;
        sideEyeRight.xRot = head.xRot;
        antennaLeft.xRot = head.xRot;
        antennaRight.xRot = head.xRot;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        render(treadBase, poseStack, buffer, packedLight, packedOverlay, DARK_METAL);
        render(treadRim, poseStack, buffer, packedLight, packedOverlay, COPPER_DARK);
        render(treadWheels, poseStack, buffer, packedLight, packedOverlay, IRON_DARK);
        render(body, poseStack, buffer, packedLight, packedOverlay, BRONZE);
        render(bodyTrim, poseStack, buffer, packedLight, packedOverlay, BRONZE_LIGHT);
        render(chestCore, poseStack, buffer, packedLight, packedOverlay, GLASS_SHADOW);
        render(neck, poseStack, buffer, packedLight, packedOverlay, COPPER_DARK);
        render(head, poseStack, buffer, packedLight, packedOverlay, BRONZE);
        render(headFrame, poseStack, buffer, packedLight, packedOverlay, BRONZE_LIGHT);
        render(headCap, poseStack, buffer, packedLight, packedOverlay, BRONZE_DARK);
        render(headEye, poseStack, buffer, packedLight, packedOverlay, GLASS);
        render(sideEyeLeft, poseStack, buffer, packedLight, packedOverlay, GLASS_SHADOW);
        render(sideEyeRight, poseStack, buffer, packedLight, packedOverlay, GLASS_SHADOW);
        render(antennaLeft, poseStack, buffer, packedLight, packedOverlay, IRON_DARK);
        render(antennaRight, poseStack, buffer, packedLight, packedOverlay, IRON);
        render(rightShoulder, poseStack, buffer, packedLight, packedOverlay, BRONZE_LIGHT);
        render(leftShoulder, poseStack, buffer, packedLight, packedOverlay, BRONZE_LIGHT);
        render(rightArm, poseStack, buffer, packedLight, packedOverlay, COPPER);
        render(leftArm, poseStack, buffer, packedLight, packedOverlay, COPPER);
        render(rightHand, poseStack, buffer, packedLight, packedOverlay, BRONZE_DARK);
        render(leftHand, poseStack, buffer, packedLight, packedOverlay, BRONZE_DARK);
        render(cog, poseStack, buffer, packedLight, packedOverlay, IRON_DARK);
        render(miniCog, poseStack, buffer, packedLight, packedOverlay, IRON);
        render(wrenchHandle, poseStack, buffer, packedLight, packedOverlay, IRON_DARK);
        render(wrenchHead, poseStack, buffer, packedLight, packedOverlay, IRON);
        render(rearPlate, poseStack, buffer, packedLight, packedOverlay, BRONZE_DARK);
    }

    private static void render(ModelPart part, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        part.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
