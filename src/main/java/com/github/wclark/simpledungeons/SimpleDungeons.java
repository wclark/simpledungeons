package com.github.wclark.simpledungeons;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(SimpleDungeons.MODID)
public class SimpleDungeons {
    public static final String MODID = "simpledungeons";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SimpleDungeons(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info(
                "Simple Dungeons setup: sky island={}, surface crypt={}, cave dungeon={}",
                Config.ENABLE_SKY_ISLAND_DUNGEON.getAsBoolean(),
                Config.ENABLE_SURFACE_CRYPT.getAsBoolean(),
                Config.ENABLE_CAVE_DUNGEON.getAsBoolean());
    }
}
