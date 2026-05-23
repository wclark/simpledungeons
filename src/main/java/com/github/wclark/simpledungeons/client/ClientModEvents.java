package com.github.wclark.simpledungeons.client;

import com.github.wclark.simpledungeons.ModEntities;
import com.github.wclark.simpledungeons.ModItems;
import com.github.wclark.simpledungeons.SimpleDungeons;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = SimpleDungeons.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(
                ModItems.SUMMONERS_STAFF.get(),
                ResourceLocation.fromNamespaceAndPath(SimpleDungeons.MODID, "held"),
                (stack, level, entity, seed) -> entity != null && (entity.getMainHandItem() == stack || entity.getOffhandItem() == stack) ? 1.0F : 0.0F));
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(NecromancerModel.LAYER_LOCATION, NecromancerModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.NECROMANCER.get(), NecromancerRenderer::new);
        event.registerEntityRenderer(ModEntities.BLUE_ORB.get(), context -> new ThrownItemRenderer<>(context, 1.1F, true));
    }
}
