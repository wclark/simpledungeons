package com.github.wclark.simpledungeons;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(SimpleDungeons.MODID)
public class SimpleDungeons {
    public static final String MODID = "simpledungeons";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, MODID);

    public static final DeferredBlock<RestlessGraveSoilBlock> RESTLESS_GRAVE_SOIL = BLOCKS.register(
            "restless_grave_soil",
            () -> new RestlessGraveSoilBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COARSE_DIRT).sound(SoundType.GRAVEL)));
    public static final DeferredItem<BlockItem> RESTLESS_GRAVE_SOIL_ITEM = ITEMS.registerSimpleBlockItem(
            "restless_grave_soil",
            RESTLESS_GRAVE_SOIL,
            new Item.Properties());
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> GRAVEYARD_FEATURE = FEATURES.register(
            "graveyard",
            () -> new GraveyardFeature(NoneFeatureConfiguration.CODEC));

    public SimpleDungeons(IEventBus modEventBus, ModContainer modContainer) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        FEATURES.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info(
                "Simple Dungeons setup: sky island={}, surface crypt={}, cave dungeon={}",
                Config.ENABLE_SKY_ISLAND_DUNGEON.getAsBoolean(),
                Config.ENABLE_SURFACE_CRYPT.getAsBoolean(),
                Config.ENABLE_CAVE_DUNGEON.getAsBoolean());
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        boolean placed = GraveyardFeature.ensureSpawnGraveyard(event.getServer().overworld());
        LOGGER.info(
                "Simple Dungeons spawn graveyard {}.",
                placed ? "was placed near the world spawn" : "already exists near the world spawn");
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer player) {
            RestlessGraveSoilBlock.awakenNearby(player);
        }
    }
}
