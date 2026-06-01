package com.github.wclark.simpledungeons.client;

import com.github.wclark.simpledungeons.CogMinionEntity;
import com.github.wclark.simpledungeons.SimpleDungeons;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CogMinionRenderer extends MobRenderer<CogMinionEntity, CogMinionModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SimpleDungeons.MODID, "textures/entity/factory_robot.png");

    public CogMinionRenderer(EntityRendererProvider.Context context) {
        super(context, new CogMinionModel(context.bakeLayer(CogMinionModel.LAYER_LOCATION)), 0.35F);
    }

    @Override
    public ResourceLocation getTextureLocation(CogMinionEntity cogMinion) {
        return TEXTURE;
    }
}
