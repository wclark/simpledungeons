package com.github.wclark.simpledungeons.client;

import java.util.Set;

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
import net.minecraft.core.Direction;
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

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition rightArm = root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));
        PartDefinition leftArm = root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));
        PartDefinition rightLeg = root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
        PartDefinition leftLeg = root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));

        head.addOrReplaceChild(
                "sheet_head_front",
                face(0, 0, -4.0F, -13.0F, -4.6F, 8.0F, 17.0F, 1.0F, Direction.NORTH),
                PartPose.ZERO);
        head.addOrReplaceChild(
                "sheet_head_back",
                face(20, 0, -4.0F, -8.0F, 3.6F, 8.0F, 8.0F, 1.0F, Direction.SOUTH),
                PartPose.ZERO);
        head.addOrReplaceChild(
                "sheet_head_left",
                face(40, 0, -4.6F, -12.0F, -4.0F, 1.0F, 15.0F, 8.0F, Direction.WEST),
                PartPose.ZERO);
        head.addOrReplaceChild(
                "sheet_head_right",
                face(60, 0, 3.6F, -12.0F, -4.0F, 1.0F, 15.0F, 8.0F, Direction.EAST),
                PartPose.ZERO);

        body.addOrReplaceChild(
                "sheet_body_front",
                face(80, 0, -12.0F, -2.0F, -3.7F, 24.0F, 26.0F, 1.0F, Direction.NORTH),
                PartPose.ZERO);
        body.addOrReplaceChild(
                "sheet_body_back",
                face(132, 0, -13.0F, -2.0F, 3.7F, 26.0F, 26.0F, 1.0F, Direction.SOUTH),
                PartPose.ZERO);
        rightArm.addOrReplaceChild(
                "sheet_right_arm",
                face(208, 0, -4.0F, -2.0F, -2.7F, 8.0F, 16.0F, 1.0F, Direction.NORTH),
                PartPose.ZERO);
        leftArm.addOrReplaceChild(
                "sheet_left_arm",
                face(188, 0, -4.0F, -2.0F, -2.7F, 8.0F, 16.0F, 1.0F, Direction.NORTH),
                PartPose.ZERO);
        rightLeg.addOrReplaceChild(
                "sheet_right_leg",
                face(0, 29, -4.0F, 0.0F, -2.6F, 8.0F, 16.0F, 1.0F, Direction.NORTH),
                PartPose.ZERO);
        leftLeg.addOrReplaceChild(
                "sheet_left_leg",
                face(228, 0, -4.0F, 0.0F, -2.6F, 8.0F, 16.0F, 1.0F, Direction.NORTH),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(NecromancerEntity necromancer, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(necromancer, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.rightArm.xRot = -Mth.HALF_PI;
        this.rightArm.yRot = -0.12F;
        this.rightArm.zRot = 0.0F;

        if (necromancer.isAggressive()) {
            this.rightArm.xRot = -Mth.HALF_PI + Mth.sin(ageInTicks * 0.45F) * 0.03F;
            this.leftArm.xRot = -1.15F;
            this.leftArm.yRot = 0.3F;
            this.leftArm.zRot = -0.12F;
        }
    }

    private static CubeListBuilder face(int texU, int texV, float x, float y, float z, float width, float height, float depth, Direction direction) {
        return CubeListBuilder.create()
                .texOffs(texU, texV)
                .addBox(x, y, z, width, height, depth, Set.of(direction));
    }
}
