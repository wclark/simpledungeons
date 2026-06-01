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
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CogMinionModel extends EntityModel<CogMinionEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(SimpleDungeons.MODID, "cog_minion"),
            "main");

    private final ModelPart main;

    public CogMinionModel(ModelPart root) {
        this.main = root.getChild("main");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition main = root.addOrReplaceChild("main", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 1.5708F, 0F));

        main.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(36, 23).addBox(-2.72F, -5.44F, -2.72F, 6.12F, 5.44F, 5.44F), PartPose.offsetAndRotation(0F, -18.36F, 0F, 0F, 0F, 0F));

        PartDefinition body = main.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 31).addBox(-2.72F, -8.16F, -3.4F, 6.12F, 2.04F, 6.8F)
                .texOffs(58, 0).addBox(-2.04F, -10.2F, -2.72F, 4.76F, 2.04F, 5.44F)
                .texOffs(64, 63).addBox(-2.04F, -14.96F, -2.04F, 4.08F, 4.76F, 4.08F)
                .texOffs(50, 50).addBox(-2.04F, -18.36F, -2.72F, 4.08F, 3.4F, 5.44F)
                .texOffs(28, 39).addBox(-5.44F, -18.36F, -3.4F, 3.4F, 6.12F, 6.8F)
                .texOffs(70, 42).addBox(-5.44F, -12.24F, -2.72F, 3.4F, 0.68F, 5.44F)
                .texOffs(48, 39).addBox(-4.08F, -19.72F, -2.04F, 1.36F, 1.36F, 4.08F)
                .texOffs(0, 44).addBox(0.68F, -6.12F, -4.76F, 2.04F, 0.68F, 9.52F)
                .texOffs(38, 3).addBox(-2.72F, -6.12F, -4.76F, 2.04F, 0.68F, 9.52F)
                .texOffs(72, 23).addBox(-0.68F, -6.12F, -3.4F, 1.36F, 0.68F, 6.8F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));

        PartDefinition cog = body.addOrReplaceChild("cog", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-0.0068F, -3.23F, -0.68F, 2.04F, 6.63F, 1.36F)
                .texOffs(34, 44).addBox(-1.02F, -0.68F, -0.68F, 1.02F, 1.36F, 1.19F)
                .texOffs(62, 11).addBox(0F, -0.68F, -3.4F, 2.04F, 1.36F, 6.8F), PartPose.offsetAndRotation(-7.48F, -15.13F, 0F, 0F, 0F, 0F));
        cog.addOrReplaceChild("cog_cube_0", CubeListBuilder.create()
                .texOffs(38, 63).addBox(-7.4732F, -10.37F, -14.96F, 2.04F, 1.53F, 6.8F), PartPose.offsetAndRotation(7.48F, 15.13F, 0F, -0.8727F, 0F, 0F));
        cog.addOrReplaceChild("cog_cube_1", CubeListBuilder.create()
                .texOffs(0, 63).addBox(-7.48F, -10.2F, 7.99F, 2.04F, 1.36F, 6.8F), PartPose.offsetAndRotation(7.48F, 15.13F, 0F, 0.8727F, 0F, 0F));

        PartDefinition hand = main.addOrReplaceChild("hand", CubeListBuilder.create()
                .texOffs(60, 43).addBox(1.36F, -1.36F, -1.36F, 2.72F, 2.04F, 2.72F)
                .texOffs(16, 59).addBox(-0.85F, -1.87F, -1.7F, 2.72F, 2.89F, 3.4F)
                .texOffs(20, 73).addBox(4.08F, -2.04F, -2.04F, 3.4F, 3.4F, 4.08F)
                .texOffs(0, 17).addBox(7.48F, -2.04F, -2.04F, 2.04F, 3.4F, 2.04F)
                .texOffs(32, 21).addBox(7.48F, -2.04F, 0F, 2.04F, 2.04F, 2.04F), PartPose.offsetAndRotation(2.04F, -13.6F, 0F, 0F, 0F, 0F));

        PartDefinition wrench = hand.addOrReplaceChild("wrench", CubeListBuilder.create()
                .texOffs(0, 44).addBox(8.84F, -18.36F, -0.51F, 1.19F, 8.84F, 1.19F)
                .texOffs(10, 0).addBox(10.37F, -21.76F, -0.17F, 0.68F, 3.4F, 0.51F)
                .texOffs(0, 31).addBox(8.33F, -23.63F, -0.68F, 1.7F, 5.27F, 1.53F)
                .texOffs(32, 18).addBox(8.16F, -24.82F, -0.85F, 4.25F, 1.19F, 1.87F)
                .texOffs(20, 44).addBox(8.16F, -22.44F, -0.85F, 3.91F, 1.19F, 1.87F)
                .texOffs(40, 0).addBox(8.16F, -18.87F, -0.85F, 3.91F, 0.68F, 1.87F), PartPose.offsetAndRotation(-2.04F, 13.77F, 0F, 0F, 0F, 0F));

        PartDefinition miniCog = wrench.addOrReplaceChild("mini_cog", CubeListBuilder.create()
                .texOffs(32, 27).addBox(-1.7F, -0.51F, -0.507F, 3.91F, 0.68F, 1.19F), PartPose.offsetAndRotation(9.69F, -17.68F, 0F, 0F, 0F, 0F));
        miniCog.addOrReplaceChild("mini_cog_cube_0", CubeListBuilder.create()
                .texOffs(41, 21).addBox(-7.48F, 0.17F, -6.29F, 3.91F, 0.68F, 1.19F), PartPose.offsetAndRotation(-0.17F, -0.68F, 8.163F, 0F, -0.7854F, 0F));
        miniCog.addOrReplaceChild("mini_cog_cube_1", CubeListBuilder.create()
                .texOffs(0, 25).addBox(-6.8F, 0.17F, 5.95F, 3.91F, 0.68F, 1.19F), PartPose.offsetAndRotation(-0.17F, -0.68F, 8.163F, 0F, -2.5307F, 0F));
        miniCog.addOrReplaceChild("mini_cog_cube_2", CubeListBuilder.create()
                .texOffs(0, 27).addBox(-2.04F, -0.51F, 0.17F, 3.91F, 0.68F, 1.19F), PartPose.offsetAndRotation(-0.85F, 0F, 0.003F, 0F, 1.5708F, 0F));

        main.addOrReplaceChild("tread", CubeListBuilder.create()
                .texOffs(22, 58).addBox(-5.44F, 4.76F, -4.08F, 0.68F, 2.04F, 8.16F)
                .texOffs(4, 44).addBox(-2.72F, 2.04F, -4.76F, 2.04F, 4.76F, 0.68F)
                .texOffs(28, 31).addBox(0.68F, 2.04F, 4.08F, 2.04F, 4.76F, 0.68F)
                .texOffs(40, 3).addBox(-2.72F, 2.04F, 4.08F, 2.04F, 4.76F, 0.68F)
                .texOffs(0, 0).addBox(-4.76F, 4.08F, -4.08F, 9.52F, 3.4F, 8.16F)
                .texOffs(58, 27).addBox(4.76F, 4.76F, -4.08F, 0.68F, 2.04F, 8.16F)
                .texOffs(0, 17).addBox(-3.4F, 2.04F, -4.08F, 6.8F, 1.36F, 8.16F)
                .texOffs(20, 47).addBox(0.68F, 2.04F, -4.76F, 2.04F, 4.76F, 0.68F), PartPose.offsetAndRotation(0F, -7.48F, 0F, 0F, 0F, 0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(CogMinionEntity cogMinion, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        main.render(poseStack, buffer, packedLight, packedOverlay, color);
        poseStack.pushPose();
        main.translateAndRotate(poseStack);
        renderFacePanel(poseStack.last(), buffer, packedLight, packedOverlay, color);
        poseStack.popPose();
    }

    private static void renderFacePanel(PoseStack.Pose pose, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        float x0 = -2.72F;
        float x1 = 3.4F;
        float y0 = -23.8F;
        float y1 = -18.36F;
        float z = -2.78F;
        float u0 = 52.0F / 128.0F;
        float u1 = 61.0F / 128.0F;
        float v0 = 31.0F / 128.0F;
        float v1 = 39.0F / 128.0F;

        addVertex(pose, buffer, x1, y0, z, u0, v0, packedLight, packedOverlay, color, 0.0F, 0.0F, -1.0F);
        addVertex(pose, buffer, x0, y0, z, u1, v0, packedLight, packedOverlay, color, 0.0F, 0.0F, -1.0F);
        addVertex(pose, buffer, x0, y1, z, u1, v1, packedLight, packedOverlay, color, 0.0F, 0.0F, -1.0F);
        addVertex(pose, buffer, x1, y1, z, u0, v1, packedLight, packedOverlay, color, 0.0F, 0.0F, -1.0F);
    }

    private static void addVertex(
            PoseStack.Pose pose,
            VertexConsumer buffer,
            float x,
            float y,
            float z,
            float u,
            float v,
            int packedLight,
            int packedOverlay,
            int color,
            float normalX,
            float normalY,
            float normalZ) {
        buffer.addVertex(pose, x / 16.0F, y / 16.0F, z / 16.0F)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(packedOverlay)
                .setLight(packedLight)
                .setNormal(pose, normalX, normalY, normalZ);
    }
}
