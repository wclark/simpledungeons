package com.github.wclark.simpledungeons.client;

import com.mojang.blaze3d.vertex.PoseStack;

import com.github.wclark.simpledungeons.NecromancerEntity;
import com.github.wclark.simpledungeons.SimpleDungeons;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NecromancerRenderer extends HumanoidMobRenderer<NecromancerEntity, NecromancerModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SimpleDungeons.MODID, "textures/entity/necromancer.png");

    public NecromancerRenderer(EntityRendererProvider.Context context) {
        super(context, new NecromancerModel(context.bakeLayer(NecromancerModel.LAYER_LOCATION)), 0.45F);
    }

    @Override
    public ResourceLocation getTextureLocation(NecromancerEntity necromancer) {
        return TEXTURE;
    }

    @Override
    protected void scale(NecromancerEntity necromancer, PoseStack poseStack, float partialTick) {
        poseStack.scale(0.73F, 0.73F, 0.73F);
    }
}
