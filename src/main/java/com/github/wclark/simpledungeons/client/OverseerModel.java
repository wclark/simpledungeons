package com.github.wclark.simpledungeons.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import com.github.wclark.simpledungeons.OverseerEntity;
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
public class OverseerModel extends EntityModel<OverseerEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(SimpleDungeons.MODID, "overseer"),
            "main");
    private static final float DIAGONAL_PANEL_ROTATION = Mth.PI / 4.0F;

    private static final int GOLD = 0xFFD09308;
    private static final int GOLD_LIGHT = 0xFFE3B51D;
    private static final int GOLD_DARK = 0xFF8E5709;
    private static final int BRONZE = 0xFF74410F;
    private static final int BROWN = 0xFF4D3324;
    private static final int WHEEL = 0xFF33251D;
    private static final int WHEEL_SHADOW = 0xFF211913;
    private static final int CYAN = 0xFFC1F3F0;
    private static final int CYAN_SHADOW = 0xFF79D7DA;
    private static final int IRON = 0xFF9A9A95;
    private static final int IRON_DARK = 0xFF626461;
    private static final int SHADOW = 0xFF2D241D;

    private final ModelPart head;
    private final ModelPart headCap;
    private final ModelPart faceSocket;
    private final ModelPart frontLens;
    private final ModelPart frontLensShadow;
    private final ModelPart sideLensLeft;
    private final ModelPart sideLensRight;
    private final ModelPart pipeLeft;
    private final ModelPart pipeRight;
    private final ModelPart neck;
    private final ModelPart body;
    private final ModelPart chestFrame;
    private final ModelPart chestGlass;
    private final ModelPart chestGlassShadow;
    private final ModelPart sideTankLeft;
    private final ModelPart sideTankRight;
    private final ModelPart sideGlassLeft;
    private final ModelPart sideGlassRight;
    private final ModelPart backBlade;
    private final ModelPart rightShoulder;
    private final ModelPart leftShoulder;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightForearm;
    private final ModelPart leftForearm;
    private final ModelPart rightClawBase;
    private final ModelPart leftClawBase;
    private final ModelPart rightClawUpper;
    private final ModelPart rightClawLower;
    private final ModelPart leftClawUpper;
    private final ModelPart leftClawLower;
    private final ModelPart waist;
    private final ModelPart hoverPost;
    private final ModelPart wheelCore;
    private final ModelPart wheelRim;
    private final ModelPart wheelHub;
    private final ModelPart sideStrutLeft;
    private final ModelPart sideStrutRight;

    public OverseerModel(ModelPart root) {
        this.head = root.getChild("head");
        this.headCap = root.getChild("head_cap");
        this.faceSocket = root.getChild("face_socket");
        this.frontLens = root.getChild("front_lens");
        this.frontLensShadow = root.getChild("front_lens_shadow");
        this.sideLensLeft = root.getChild("side_lens_left");
        this.sideLensRight = root.getChild("side_lens_right");
        this.pipeLeft = root.getChild("pipe_left");
        this.pipeRight = root.getChild("pipe_right");
        this.neck = root.getChild("neck");
        this.body = root.getChild("body");
        this.chestFrame = root.getChild("chest_frame");
        this.chestGlass = root.getChild("chest_glass");
        this.chestGlassShadow = root.getChild("chest_glass_shadow");
        this.sideTankLeft = root.getChild("side_tank_left");
        this.sideTankRight = root.getChild("side_tank_right");
        this.sideGlassLeft = root.getChild("side_glass_left");
        this.sideGlassRight = root.getChild("side_glass_right");
        this.backBlade = root.getChild("back_blade");
        this.rightShoulder = root.getChild("right_shoulder");
        this.leftShoulder = root.getChild("left_shoulder");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightForearm = root.getChild("right_forearm");
        this.leftForearm = root.getChild("left_forearm");
        this.rightClawBase = root.getChild("right_claw_base");
        this.leftClawBase = root.getChild("left_claw_base");
        this.rightClawUpper = root.getChild("right_claw_upper");
        this.rightClawLower = root.getChild("right_claw_lower");
        this.leftClawUpper = root.getChild("left_claw_upper");
        this.leftClawLower = root.getChild("left_claw_lower");
        this.waist = root.getChild("waist");
        this.hoverPost = root.getChild("hover_post");
        this.wheelCore = root.getChild("wheel_core");
        this.wheelRim = root.getChild("wheel_rim");
        this.wheelHub = root.getChild("wheel_hub");
        this.sideStrutLeft = root.getChild("side_strut_left");
        this.sideStrutRight = root.getChild("side_strut_right");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5.0F, -10.0F, -5.0F, 10.0F, 9.0F, 10.0F)
                .texOffs(0, 0).addBox(-6.0F, -8.8F, -4.0F, 1.4F, 6.8F, 8.0F)
                .texOffs(0, 0).addBox(4.6F, -8.8F, -4.0F, 1.4F, 6.8F, 8.0F), PartPose.ZERO);
        root.addOrReplaceChild("head_cap", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5.4F, -10.5F, -5.4F, 10.8F, 2.0F, 10.8F, new CubeDeformation(0.05F))
                .texOffs(0, 0).addBox(-5.4F, -2.3F, -5.4F, 10.8F, 1.6F, 10.8F, new CubeDeformation(0.05F)), PartPose.ZERO);
        root.addOrReplaceChild("face_socket", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.2F, -7.4F, -5.5F, 6.4F, 4.9F, 1.0F), PartPose.ZERO);
        root.addOrReplaceChild("front_lens", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.85F, -1.85F, -0.45F, 3.7F, 3.7F, 0.9F),
                PartPose.offsetAndRotation(0.0F, -5.1F, -5.85F, 0.0F, 0.0F, DIAGONAL_PANEL_ROTATION));
        root.addOrReplaceChild("front_lens_shadow", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.15F, -1.15F, -0.5F, 2.3F, 2.3F, 0.5F),
                PartPose.offsetAndRotation(0.0F, -5.1F, -5.95F, 0.0F, 0.0F, DIAGONAL_PANEL_ROTATION));
        root.addOrReplaceChild("side_lens_left", CubeListBuilder.create()
                .texOffs(0, 0).addBox(5.15F, -6.4F, -1.2F, 0.8F, 2.4F, 2.4F), PartPose.ZERO);
        root.addOrReplaceChild("side_lens_right", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5.95F, -6.4F, -1.2F, 0.8F, 2.4F, 2.4F), PartPose.ZERO);
        root.addOrReplaceChild("pipe_left", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.0F, -16.0F, -0.8F, 1.8F, 6.0F, 1.8F), PartPose.offset(-2.0F, 0.0F, 1.8F));
        root.addOrReplaceChild("pipe_right", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-0.8F, -17.6F, -0.8F, 1.8F, 7.6F, 1.8F), PartPose.offset(2.0F, 0.0F, 1.6F));

        root.addOrReplaceChild("neck", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.2F, -1.8F, -2.8F, 6.4F, 4.0F, 5.6F)
                .texOffs(0, 0).addBox(-5.0F, -0.1F, -3.6F, 10.0F, 2.0F, 7.2F), PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5.2F, 1.2F, -3.4F, 10.4F, 11.5F, 6.8F)
                .texOffs(0, 0).addBox(-6.2F, 2.6F, -2.5F, 1.6F, 8.5F, 5.0F)
                .texOffs(0, 0).addBox(4.6F, 2.6F, -2.5F, 1.6F, 8.5F, 5.0F), PartPose.ZERO);
        root.addOrReplaceChild("chest_frame", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.5F, 2.5F, -4.0F, 7.0F, 8.0F, 1.0F)
                .texOffs(0, 0).addBox(-4.4F, 1.8F, -3.8F, 8.8F, 1.4F, 1.0F)
                .texOffs(0, 0).addBox(-4.4F, 9.8F, -3.8F, 8.8F, 1.4F, 1.0F), PartPose.ZERO);
        root.addOrReplaceChild("chest_glass", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.0F, 3.4F, -4.5F, 4.0F, 5.8F, 0.9F), PartPose.ZERO);
        root.addOrReplaceChild("chest_glass_shadow", CubeListBuilder.create()
                .texOffs(0, 0).addBox(0.2F, 3.7F, -4.6F, 1.5F, 5.1F, 0.6F), PartPose.ZERO);
        root.addOrReplaceChild("side_tank_left", CubeListBuilder.create()
                .texOffs(0, 0).addBox(4.9F, 3.0F, -0.2F, 3.0F, 8.6F, 4.2F), PartPose.ZERO);
        root.addOrReplaceChild("side_tank_right", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-7.9F, 3.0F, -0.2F, 3.0F, 8.6F, 4.2F), PartPose.ZERO);
        root.addOrReplaceChild("side_glass_left", CubeListBuilder.create()
                .texOffs(0, 0).addBox(7.45F, 4.4F, 0.8F, 0.7F, 5.6F, 1.8F), PartPose.ZERO);
        root.addOrReplaceChild("side_glass_right", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-8.15F, 4.4F, 0.8F, 0.7F, 5.6F, 1.8F), PartPose.ZERO);
        root.addOrReplaceChild("back_blade", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-8.0F, 0.6F, 3.2F, 16.0F, 2.0F, 3.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.12F, 0.0F, 0.0F));

        root.addOrReplaceChild("right_shoulder", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.4F, -2.0F, -2.8F, 5.2F, 4.8F, 5.6F), PartPose.offset(-6.1F, 4.0F, 0.0F));
        root.addOrReplaceChild("left_shoulder", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-0.8F, -2.0F, -2.8F, 5.2F, 4.8F, 5.6F), PartPose.offset(6.1F, 4.0F, 0.0F));
        root.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.4F, 1.4F, -1.9F, 3.5F, 6.5F, 3.8F), PartPose.offset(-6.1F, 4.0F, 0.0F));
        root.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-0.1F, 1.4F, -1.9F, 3.5F, 6.5F, 3.8F), PartPose.offset(6.1F, 4.0F, 0.0F));
        root.addOrReplaceChild("right_forearm", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.7F, 7.4F, -2.2F, 4.0F, 5.6F, 4.4F), PartPose.offset(-6.1F, 4.0F, 0.0F));
        root.addOrReplaceChild("left_forearm", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-0.3F, 7.4F, -2.2F, 4.0F, 5.6F, 4.4F), PartPose.offset(6.1F, 4.0F, 0.0F));
        root.addOrReplaceChild("right_claw_base", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.35F, 12.0F, -1.5F, 3.1F, 2.2F, 3.0F), PartPose.offset(-6.1F, 4.0F, 0.0F));
        root.addOrReplaceChild("left_claw_base", CubeListBuilder.create()
                .texOffs(0, 0).addBox(0.25F, 12.0F, -1.5F, 3.1F, 2.2F, 3.0F), PartPose.offset(6.1F, 4.0F, 0.0F));
        root.addOrReplaceChild("right_claw_upper", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.6F, 12.9F, -1.0F, 3.5F, 1.1F, 1.5F), PartPose.offsetAndRotation(-6.1F, 4.0F, 0.0F, 0.0F, 0.0F, -0.18F));
        root.addOrReplaceChild("right_claw_lower", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.2F, 14.1F, -1.0F, 3.2F, 1.1F, 1.5F), PartPose.offsetAndRotation(-6.1F, 4.0F, 0.0F, 0.0F, 0.0F, 0.22F));
        root.addOrReplaceChild("left_claw_upper", CubeListBuilder.create()
                .texOffs(0, 0).addBox(1.1F, 12.9F, -1.0F, 3.5F, 1.1F, 1.5F), PartPose.offsetAndRotation(6.1F, 4.0F, 0.0F, 0.0F, 0.0F, 0.18F));
        root.addOrReplaceChild("left_claw_lower", CubeListBuilder.create()
                .texOffs(0, 0).addBox(1.0F, 14.1F, -1.0F, 3.2F, 1.1F, 1.5F), PartPose.offsetAndRotation(6.1F, 4.0F, 0.0F, 0.0F, 0.0F, -0.22F));

        root.addOrReplaceChild("waist", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.4F, 11.2F, -2.8F, 8.8F, 3.0F, 5.6F), PartPose.ZERO);
        root.addOrReplaceChild("hover_post", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.1F, 13.8F, -1.1F, 2.2F, 8.0F, 2.2F), PartPose.ZERO);
        root.addOrReplaceChild("wheel_core", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.1F, 17.2F, -3.2F, 4.2F, 7.0F, 6.4F), PartPose.ZERO);
        root.addOrReplaceChild("wheel_rim", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.8F, 18.1F, -3.4F, 5.6F, 1.2F, 6.8F)
                .texOffs(0, 0).addBox(-2.8F, 22.1F, -3.4F, 5.6F, 1.2F, 6.8F)
                .texOffs(0, 0).addBox(-2.8F, 18.1F, -3.5F, 1.2F, 5.2F, 7.0F)
                .texOffs(0, 0).addBox(1.6F, 18.1F, -3.5F, 1.2F, 5.2F, 7.0F), PartPose.ZERO);
        root.addOrReplaceChild("wheel_hub", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.0F, 19.5F, -0.9F, 6.0F, 2.4F, 1.8F), PartPose.ZERO);
        root.addOrReplaceChild("side_strut_left", CubeListBuilder.create()
                .texOffs(0, 0).addBox(4.4F, 13.0F, -1.0F, 1.4F, 8.8F, 2.0F), PartPose.ZERO);
        root.addOrReplaceChild("side_strut_right", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5.8F, 13.0F, -1.0F, 1.4F, 8.8F, 2.0F), PartPose.ZERO);

        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void setupAnim(OverseerEntity overseer, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float yRot = netHeadYaw * Mth.DEG_TO_RAD;
        float xRot = headPitch * Mth.DEG_TO_RAD;
        applyHeadPose(head, yRot, xRot);
        applyHeadPose(headCap, yRot, xRot);
        applyHeadPose(faceSocket, yRot, xRot);
        applyHeadPose(frontLens, yRot, xRot);
        applyHeadPose(frontLensShadow, yRot, xRot);
        applyHeadPose(sideLensLeft, yRot, xRot);
        applyHeadPose(sideLensRight, yRot, xRot);
        applyHeadPose(pipeLeft, yRot, xRot);
        applyHeadPose(pipeRight, yRot, xRot);
    }

    private static void applyHeadPose(ModelPart part, float yRot, float xRot) {
        part.yRot = yRot;
        part.xRot = xRot;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        render(neck, poseStack, buffer, packedLight, packedOverlay, BROWN);
        render(body, poseStack, buffer, packedLight, packedOverlay, GOLD);
        render(chestFrame, poseStack, buffer, packedLight, packedOverlay, GOLD_LIGHT);
        render(chestGlass, poseStack, buffer, packedLight, packedOverlay, CYAN);
        render(chestGlassShadow, poseStack, buffer, packedLight, packedOverlay, CYAN_SHADOW);
        render(sideTankLeft, poseStack, buffer, packedLight, packedOverlay, GOLD_DARK);
        render(sideTankRight, poseStack, buffer, packedLight, packedOverlay, GOLD_DARK);
        render(sideGlassLeft, poseStack, buffer, packedLight, packedOverlay, CYAN);
        render(sideGlassRight, poseStack, buffer, packedLight, packedOverlay, CYAN);
        render(backBlade, poseStack, buffer, packedLight, packedOverlay, IRON);
        render(head, poseStack, buffer, packedLight, packedOverlay, GOLD);
        render(headCap, poseStack, buffer, packedLight, packedOverlay, GOLD_LIGHT);
        render(faceSocket, poseStack, buffer, packedLight, packedOverlay, SHADOW);
        render(frontLensShadow, poseStack, buffer, packedLight, packedOverlay, CYAN_SHADOW);
        render(frontLens, poseStack, buffer, packedLight, packedOverlay, CYAN);
        render(sideLensLeft, poseStack, buffer, packedLight, packedOverlay, CYAN);
        render(sideLensRight, poseStack, buffer, packedLight, packedOverlay, CYAN);
        render(pipeLeft, poseStack, buffer, packedLight, packedOverlay, IRON_DARK);
        render(pipeRight, poseStack, buffer, packedLight, packedOverlay, IRON);
        render(rightShoulder, poseStack, buffer, packedLight, packedOverlay, GOLD_LIGHT);
        render(leftShoulder, poseStack, buffer, packedLight, packedOverlay, GOLD_LIGHT);
        render(rightArm, poseStack, buffer, packedLight, packedOverlay, BRONZE);
        render(leftArm, poseStack, buffer, packedLight, packedOverlay, BRONZE);
        render(rightForearm, poseStack, buffer, packedLight, packedOverlay, GOLD_DARK);
        render(leftForearm, poseStack, buffer, packedLight, packedOverlay, GOLD_DARK);
        render(rightClawBase, poseStack, buffer, packedLight, packedOverlay, GOLD);
        render(leftClawBase, poseStack, buffer, packedLight, packedOverlay, GOLD);
        render(rightClawUpper, poseStack, buffer, packedLight, packedOverlay, GOLD_LIGHT);
        render(rightClawLower, poseStack, buffer, packedLight, packedOverlay, GOLD_DARK);
        render(leftClawUpper, poseStack, buffer, packedLight, packedOverlay, GOLD_LIGHT);
        render(leftClawLower, poseStack, buffer, packedLight, packedOverlay, GOLD_DARK);
        render(waist, poseStack, buffer, packedLight, packedOverlay, GOLD_DARK);
        render(hoverPost, poseStack, buffer, packedLight, packedOverlay, IRON_DARK);
        render(wheelCore, poseStack, buffer, packedLight, packedOverlay, WHEEL);
        render(wheelRim, poseStack, buffer, packedLight, packedOverlay, BROWN);
        render(wheelHub, poseStack, buffer, packedLight, packedOverlay, WHEEL_SHADOW);
        render(sideStrutLeft, poseStack, buffer, packedLight, packedOverlay, IRON);
        render(sideStrutRight, poseStack, buffer, packedLight, packedOverlay, IRON);
    }

    private static void render(ModelPart part, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        part.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
