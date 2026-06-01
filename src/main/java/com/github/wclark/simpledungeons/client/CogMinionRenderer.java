package com.github.wclark.simpledungeons.client;

import com.github.wclark.simpledungeons.CogMinionEntity;
import com.github.wclark.simpledungeons.SimpleDungeons;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CogMinionRenderer extends MobRenderer<CogMinionEntity, CogMinionModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SimpleDungeons.MODID, "textures/entity/cog_minion_texture.png");

    public CogMinionRenderer(EntityRendererProvider.Context context) {
        super(context, new CogMinionModel(context.bakeLayer(CogMinionModel.LAYER_LOCATION)), 0.45F);
    }

    @Override
    protected void scale(CogMinionEntity cogMinion, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(1.17F, 1.17F, 1.17F);
    }

    @Override
    public ResourceLocation getTextureLocation(CogMinionEntity cogMinion) {
        return TEXTURE;
    }
}
