package com.github.wclark.simpledungeons.client;

import com.github.wclark.simpledungeons.CogMinionEntity;
import com.github.wclark.simpledungeons.SimpleDungeons;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class CogMinionModel extends EntityModel<CogMinionEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(SimpleDungeons.MODID, "cog_minion"),
            "main");

    private static final float MODEL_SCALE = 0.68F;
    private static final float TEXTURE_WIDTH = 128.0F;
    private static final float TEXTURE_HEIGHT = 128.0F;
    private static final float DEG_TO_RAD = (float) Math.PI / 180.0F;

    private static final Bone[] BONES = {
            new Bone(-1, 0F, 0F, 0F, 0F, 90F, 0F), // main
            new Bone(0, 0F, 27F, 0F, 0F, 0F, 0F), // head
            new Bone(0, 0F, 0F, 0F, 0F, 0F, 0F), // body
            new Bone(2, -11F, 22.25F, 0F, 0F, 0F, 0F), // cog
            new Bone(0, 3F, 20F, 0F, 0F, 0F, 0F), // hand
            new Bone(4, 0F, -0.25F, 0F, 0F, 0F, 0F), // wrench
            new Bone(5, 14.25F, 25.75F, 0F, 0F, 0F, 0F), // mini_cog
            new Bone(0, 0F, 11F, 0F, 0F, 0F, 0F), // tread
    };

    private static final Cube[] CUBES = {
            new Cube(1, -4F, 27F, -4F, 9F, 8F, 8F, 0F, 27F, 0F, 0F, 0F, 0F, 36F, 23F),
            new Cube(2, -4F, 9F, -5F, 9F, 3F, 10F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 31F),
            new Cube(2, -3F, 12F, -4F, 7F, 3F, 8F, 0F, 0F, 0F, 0F, 0F, 0F, 58F, 0F),
            new Cube(2, -3F, 15F, -3F, 6F, 7F, 6F, 0F, 0F, 0F, 0F, 0F, 0F, 64F, 63F),
            new Cube(2, -3F, 22F, -4F, 6F, 5F, 8F, 0F, 0F, 0F, 0F, 0F, 0F, 50F, 50F),
            new Cube(2, -8F, 18F, -5F, 5F, 9F, 10F, 0F, 0F, 0F, 0F, 0F, 0F, 28F, 39F),
            new Cube(2, -8F, 17F, -4F, 5F, 1F, 8F, 0F, 0F, 0F, 0F, 0F, 0F, 70F, 42F),
            new Cube(2, -6F, 27F, -3F, 2F, 2F, 6F, 0F, 0F, 0F, 0F, 0F, 0F, 48F, 39F),
            new Cube(2, 1F, 8F, -7F, 3F, 1F, 14F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 44F),
            new Cube(2, -4F, 8F, -7F, 3F, 1F, 14F, 0F, 0F, 0F, 0F, 0F, 0F, 38F, 3F),
            new Cube(2, -1F, 8F, -5F, 2F, 1F, 10F, 0F, 0F, 0F, 0F, 0F, 0F, 72F, 23F),
            new Cube(3, -11.01F, 17.25F, -1F, 3F, 9.75F, 2F, -11F, 22.25F, 0F, 0F, 0F, 0F, 0F, 0F),
            new Cube(3, -12.5F, 21.25F, -1F, 1.5F, 2F, 1.75F, -11F, 22.25F, 0F, 0F, 0F, 0F, 34F, 44F),
            new Cube(3, -11F, 21.25F, -5F, 3F, 2F, 10F, -11F, 22.25F, 0F, 0F, 0F, 0F, 62F, 11F),
            new Cube(3, -10.99F, 13F, -22F, 3F, 2.25F, 10F, 0F, 0F, 0F, -50F, 0F, 0F, 38F, 63F),
            new Cube(3, -11F, 13F, 11.75F, 3F, 2F, 10F, 0F, 0F, 0F, 50F, 0F, 0F, 0F, 63F),
            new Cube(4, 5F, 19F, -2F, 4F, 3F, 4F, 3F, 20F, 0F, 0F, 0F, 0F, 60F, 43F),
            new Cube(4, 1.75F, 18.5F, -2.5F, 4F, 4.25F, 5F, 3F, 20F, 0F, 0F, 0F, 0F, 16F, 59F),
            new Cube(4, 9F, 18F, -3F, 5F, 5F, 6F, 3F, 20F, 0F, 0F, 0F, 0F, 20F, 73F),
            new Cube(4, 14F, 18F, -3F, 3F, 5F, 3F, 3F, 20F, 0F, 0F, 0F, 0F, 0F, 17F),
            new Cube(4, 14F, 20F, 0F, 3F, 3F, 3F, 3F, 20F, 0F, 0F, 0F, 0F, 32F, 21F),
            new Cube(5, 13F, 13.75F, -0.75F, 1.75F, 13F, 1.75F, 0F, -0.25F, 0F, 0F, 0F, 0F, 0F, 44F),
            new Cube(5, 15.25F, 26.75F, -0.25F, 1F, 5F, 0.75F, 0F, -0.25F, 0F, 0F, 0F, 0F, 10F, 0F),
            new Cube(5, 12.25F, 26.75F, -1F, 2.5F, 7.75F, 2.25F, 0F, -0.25F, 0F, 0F, 0F, 0F, 0F, 31F),
            new Cube(5, 12F, 34.5F, -1.25F, 6.25F, 1.75F, 2.75F, 0F, -0.25F, 0F, 0F, 0F, 0F, 32F, 18F),
            new Cube(5, 12F, 31F, -1.25F, 5.75F, 1.75F, 2.75F, 0F, -0.25F, 0F, 0F, 0F, 0F, 20F, 44F),
            new Cube(5, 12F, 26.5F, -1.25F, 5.75F, 1F, 2.75F, 0F, -0.25F, 0F, 0F, 0F, 0F, 40F, 0F),
            new Cube(6, 3.00004F, 25.5F, 2.75436F, 5.75F, 1F, 1.75F, 14.00004F, 26.75F, 12.00436F, 0F, -45F, 0F, 41F, 21F),
            new Cube(6, 4.00004F, 25.5F, 20.75436F, 5.75F, 1F, 1.75F, 14.00004F, 26.75F, 12.00436F, 0F, -145F, 0F, 0F, 25F),
            new Cube(6, 11.75004F, 25.5F, -0.74564F, 5.75F, 1F, 1.75F, 14.25F, 25.75F, 0F, 0F, 0F, 0F, 32F, 27F),
            new Cube(6, 10.00004F, 25.5F, 0.25436F, 5.75F, 1F, 1.75F, 13.00004F, 25.75F, 0.00436F, 0F, 90F, 0F, 0F, 27F),
            new Cube(7, -8F, 1F, -6F, 1F, 3F, 12F, 0F, 11F, 0F, 0F, 0F, 0F, 22F, 58F),
            new Cube(7, -4F, 1F, -7F, 3F, 7F, 1F, 0F, 11F, 0F, 0F, 0F, 0F, 4F, 44F),
            new Cube(7, 1F, 1F, 6F, 3F, 7F, 1F, 0F, 11F, 0F, 0F, 0F, 0F, 28F, 31F),
            new Cube(7, -4F, 1F, 6F, 3F, 7F, 1F, 0F, 11F, 0F, 0F, 0F, 0F, 40F, 3F),
            new Cube(7, -7F, 0F, -6F, 14F, 5F, 12F, 0F, 11F, 0F, 0F, 0F, 0F, 0F, 0F),
            new Cube(7, 7F, 1F, -6F, 1F, 3F, 12F, 0F, 11F, 0F, 0F, 0F, 0F, 58F, 27F),
            new Cube(7, -5F, 6F, -6F, 10F, 2F, 12F, 0F, 11F, 0F, 0F, 0F, 0F, 0F, 17F),
            new Cube(7, 1F, 1F, -7F, 3F, 7F, 1F, 0F, 11F, 0F, 0F, 0F, 0F, 20F, 47F),
    };

    public CogMinionModel() {
    }

    public CogMinionModel(ModelPart root) {
    }

    public static LayerDefinition createBodyLayer() {
        return LayerDefinition.create(new MeshDefinition(), 128, 128);
    }

    @Override
    public void setupAnim(CogMinionEntity cogMinion, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        for (int i = 0; i < BONES.length; i++) {
            if (BONES[i].parent() == -1) {
                renderBone(i, poseStack, buffer, packedLight, packedOverlay, color);
            }
        }
    }

    private static void renderBone(int index, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        Bone bone = BONES[index];
        Bone parent = bone.parent() >= 0 ? BONES[bone.parent()] : null;
        poseStack.pushPose();
        poseStack.translate(offsetX(bone, parent) / 16.0F, offsetY(bone, parent) / 16.0F, offsetZ(bone, parent) / 16.0F);
        poseStack.mulPose(new Quaternionf().rotationZYX(rad(bone.rz()), rad(bone.ry()), rad(bone.rx())));

        for (Cube cube : CUBES) {
            if (cube.bone() == index) {
                renderCube(bone, cube, poseStack, buffer, packedLight, packedOverlay, color);
            }
        }

        for (int child = 0; child < BONES.length; child++) {
            if (BONES[child].parent() == index) {
                renderBone(child, poseStack, buffer, packedLight, packedOverlay, color);
            }
        }

        poseStack.popPose();
    }

    private static void renderCube(Bone bone, Cube cube, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        poseStack.pushPose();
        poseStack.translate((modelX(cube.pivotX()) - modelX(bone.pivotX())) / 16.0F,
                (modelY(cube.pivotY()) - modelY(bone.pivotY())) / 16.0F,
                (modelZ(cube.pivotZ()) - modelZ(bone.pivotZ())) / 16.0F);
        poseStack.mulPose(new Quaternionf().rotationZYX(rad(cube.rz()), rad(cube.ry()), rad(cube.rx())));

        float x0 = (cube.originX() - cube.pivotX()) * MODEL_SCALE;
        float x1 = (cube.originX() + cube.sizeX() - cube.pivotX()) * MODEL_SCALE;
        float y0 = (cube.pivotY() - cube.originY() - cube.sizeY()) * MODEL_SCALE;
        float y1 = (cube.pivotY() - cube.originY()) * MODEL_SCALE;
        float z0 = (cube.originZ() - cube.pivotZ()) * MODEL_SCALE;
        float z1 = (cube.originZ() + cube.sizeZ() - cube.pivotZ()) * MODEL_SCALE;

        float w = uvSpan(cube.sizeX());
        float h = uvSpan(cube.sizeY());
        float d = uvSpan(cube.sizeZ());
        float u = cube.u();
        float v = cube.v();
        float u0 = u;
        float u1 = u + d;
        float u2 = u + d + w;
        float u22 = u + d + w + w;
        float u3 = u + d + w + d;
        float u4 = u + d + w + d + w;
        float v0 = v;
        float v1 = v + d;
        float v2 = v + d + h;

        PoseStack.Pose pose = poseStack.last();
        addFace(pose, buffer, packedLight, packedOverlay, color, Rect.bounds(u1, v0, u2, v1), 0.0F, -1.0F, 0.0F,
                x1, y0, z1, x0, y0, z1, x0, y0, z0, x1, y0, z0);
        addFace(pose, buffer, packedLight, packedOverlay, color, Rect.bounds(u2, v1, u22, v0), 0.0F, 1.0F, 0.0F,
                x1, y1, z0, x0, y1, z0, x0, y1, z1, x1, y1, z1);
        addFace(pose, buffer, packedLight, packedOverlay, color, Rect.bounds(u0, v1, u1, v2), -1.0F, 0.0F, 0.0F,
                x0, y0, z0, x0, y0, z1, x0, y1, z1, x0, y1, z0);
        addFace(pose, buffer, packedLight, packedOverlay, color, Rect.bounds(u1, v1, u2, v2), 0.0F, 0.0F, -1.0F,
                x1, y0, z0, x0, y0, z0, x0, y1, z0, x1, y1, z0);
        addFace(pose, buffer, packedLight, packedOverlay, color, Rect.bounds(u2, v1, u3, v2), 1.0F, 0.0F, 0.0F,
                x1, y0, z1, x1, y0, z0, x1, y1, z0, x1, y1, z1);
        addFace(pose, buffer, packedLight, packedOverlay, color, Rect.bounds(u3, v1, u4, v2), 0.0F, 0.0F, 1.0F,
                x0, y0, z1, x1, y0, z1, x1, y1, z1, x0, y1, z1);

        poseStack.popPose();
    }

    private static void addFace(
            PoseStack.Pose pose,
            VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            int color,
            Rect rect,
            float normalX,
            float normalY,
            float normalZ,
            float x0,
            float y0,
            float z0,
            float x1,
            float y1,
            float z1,
            float x2,
            float y2,
            float z2,
            float x3,
            float y3,
            float z3) {
        addVertex(pose, buffer, x0, y0, z0, rect.u1(), rect.v0(), packedLight, packedOverlay, color, normalX, normalY, normalZ);
        addVertex(pose, buffer, x1, y1, z1, rect.u0(), rect.v0(), packedLight, packedOverlay, color, normalX, normalY, normalZ);
        addVertex(pose, buffer, x2, y2, z2, rect.u0(), rect.v1(), packedLight, packedOverlay, color, normalX, normalY, normalZ);
        addVertex(pose, buffer, x3, y3, z3, rect.u1(), rect.v1(), packedLight, packedOverlay, color, normalX, normalY, normalZ);
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

    private static float offsetX(Bone bone, Bone parent) {
        return modelX(bone.pivotX()) - (parent == null ? 0.0F : modelX(parent.pivotX()));
    }

    private static float offsetY(Bone bone, Bone parent) {
        return modelY(bone.pivotY()) - (parent == null ? 0.0F : modelY(parent.pivotY()));
    }

    private static float offsetZ(Bone bone, Bone parent) {
        return modelZ(bone.pivotZ()) - (parent == null ? 0.0F : modelZ(parent.pivotZ()));
    }

    private static float modelX(float x) {
        return x * MODEL_SCALE;
    }

    private static float modelY(float y) {
        return 24.0F - y * MODEL_SCALE;
    }

    private static float modelZ(float z) {
        return z * MODEL_SCALE;
    }

    private static float rad(float degrees) {
        return degrees * DEG_TO_RAD;
    }

    private static float uvSpan(float size) {
        return Math.max(1.0F, (float) Math.floor(size));
    }

    private record Bone(int parent, float pivotX, float pivotY, float pivotZ, float rx, float ry, float rz) {
    }

    private record Cube(
            int bone,
            float originX,
            float originY,
            float originZ,
            float sizeX,
            float sizeY,
            float sizeZ,
            float pivotX,
            float pivotY,
            float pivotZ,
            float rx,
            float ry,
            float rz,
            float u,
            float v) {
    }

    private record Rect(float u0, float v0, float u1, float v1) {
        private static Rect bounds(float u0, float v0, float u1, float v1) {
            return new Rect(u0 / TEXTURE_WIDTH, v0 / TEXTURE_HEIGHT, u1 / TEXTURE_WIDTH, v1 / TEXTURE_HEIGHT);
        }
    }
}
