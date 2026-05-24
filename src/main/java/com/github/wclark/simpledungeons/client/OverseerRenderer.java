package com.github.wclark.simpledungeons.client;

import com.github.wclark.simpledungeons.OverseerEntity;
import com.github.wclark.simpledungeons.SimpleDungeons;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OverseerRenderer extends MobRenderer<OverseerEntity, OverseerModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SimpleDungeons.MODID, "textures/entity/overseer.png");

    public OverseerRenderer(EntityRendererProvider.Context context) {
        super(context, new OverseerModel(context.bakeLayer(OverseerModel.LAYER_LOCATION)), 0.55F);
    }

    @Override
    public ResourceLocation getTextureLocation(OverseerEntity overseer) {
        return TEXTURE;
    }
}
