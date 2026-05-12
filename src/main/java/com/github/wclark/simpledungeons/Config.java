package com.github.wclark.simpledungeons;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLE_SKY_ISLAND_DUNGEON = BUILDER
            .comment("Whether Simple Dungeons should generate the sky island dungeon.")
            .define("enableSkyIslandDungeon", true);

    public static final ModConfigSpec.BooleanValue ENABLE_SURFACE_CRYPT = BUILDER
            .comment("Whether Simple Dungeons should generate the surface crypt dungeon.")
            .define("enableSurfaceCrypt", true);

    public static final ModConfigSpec.BooleanValue ENABLE_CAVE_DUNGEON = BUILDER
            .comment("Whether Simple Dungeons should generate the cave-bound underground dungeon.")
            .define("enableCaveDungeon", true);

    public static final ModConfigSpec.IntValue SKY_ISLAND_AVERAGE_SPACING = BUILDER
            .comment("Approximate average chunk spacing for sky island dungeons.")
            .defineInRange("skyIslandAverageSpacing", 48, 16, 256);

    public static final ModConfigSpec.IntValue SURFACE_CRYPT_AVERAGE_SPACING = BUILDER
            .comment("Approximate average chunk spacing for surface crypt dungeons.")
            .defineInRange("surfaceCryptAverageSpacing", 36, 16, 256);

    public static final ModConfigSpec.IntValue CAVE_DUNGEON_AVERAGE_SPACING = BUILDER
            .comment("Approximate average chunk spacing for cave-bound underground dungeons.")
            .defineInRange("caveDungeonAverageSpacing", 40, 16, 256);

    static final ModConfigSpec SPEC = BUILDER.build();
}
