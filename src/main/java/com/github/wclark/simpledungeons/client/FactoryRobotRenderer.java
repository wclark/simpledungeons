package com.github.wclark.simpledungeons.client;

import com.github.wclark.simpledungeons.FactoryRobotEntity;
import com.github.wclark.simpledungeons.SimpleDungeons;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FactoryRobotRenderer extends MobRenderer<FactoryRobotEntity, FactoryRobotModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SimpleDungeons.MODID, "textures/entity/factory_robot.png");

    public FactoryRobotRenderer(EntityRendererProvider.Context context) {
        super(context, new FactoryRobotModel(context.bakeLayer(FactoryRobotModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(FactoryRobotEntity robot) {
        return TEXTURE;
    }
}
