package com.github.wclark.simpledungeons;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@Mod(SimpleDungeons.MODID)
public class SimpleDungeons {
    public static final String MODID = "simpledungeons";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SimpleDungeons(IEventBus modEventBus, ModContainer modContainer) {
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreativeTabContents);
        modEventBus.addListener(this::registerEntityAttributes);
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new GraveyardUndeadSpawner());
        NeoForge.EVENT_BUS.register(new SurfaceCryptPlacementManager());
        NeoForge.EVENT_BUS.register(new UndeadSummons());
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info(
                "Simple Dungeons setup: sky island={}, surface crypt={}, cave dungeon={}",
                Config.ENABLE_SKY_ISLAND_DUNGEON.getAsBoolean(),
                Config.ENABLE_SURFACE_CRYPT.getAsBoolean(),
                Config.ENABLE_CAVE_DUNGEON.getAsBoolean());
    }

    private void addCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(new ItemStack(ModItems.SUMMONERS_STAFF.get()));
        }
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(new ItemStack(ModItems.MAGIC_BONE.get()));
            event.accept(new ItemStack(ModItems.MAGIC_BONEMEAL.get()));
        }
    }

    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.NECROMANCER.get(), NecromancerEntity.createAttributes().build());
        event.put(ModEntities.FACTORY_ROBOT.get(), FactoryRobotEntity.createAttributes().build());
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        boolean placed = SpawnSkyIslandStructure.ensureAtSpawn(event.getServer().overworld());
        LOGGER.info("Simple Dungeons spawn sky island {}.", placed ? "was placed above spawn" : "is already present or disabled");
    }
}
