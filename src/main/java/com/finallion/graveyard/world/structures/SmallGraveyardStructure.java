package com.finallion.graveyard.world.structures;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.config.StructureConfigEntry;
import com.finallion.graveyard.init.TGConfiguredStructureFeatures;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

import java.util.Arrays;
import java.util.Collections;

public class SmallGraveyardStructure extends AbstractGraveyardStructure {

    public SmallGraveyardStructure(Codec<StructurePoolFeatureConfig> codec) {
        super(codec, new StructureConfigEntry(20, 18, 240451934,
                        Arrays.asList("#" + Biome.Category.PLAINS.getName()),
                        Collections.emptyList(), Arrays.asList("#minecraft", "#terralith"), false),
                15, 240451934, SmallGraveyardGenerator.STARTING_POOL, "small_graveyard");
    }

    @Override
    public ConfiguredStructureFeature<?, ?> getStructureFeature() {
        return TGConfiguredStructureFeatures.SMALL_GRAVEYARD_STRUCTURE_CONFIG.value();
    }

    public static class SmallGraveyardGenerator {
        public static final RegistryEntry<StructurePool> STARTING_POOL;

        public SmallGraveyardGenerator() {
        }

        public static void init() {
        }

        static {
            STARTING_POOL = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "small_graveyard"), new Identifier("empty"), ImmutableList.of(
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_graveyard/small_graveyard_01"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_graveyard/small_graveyard_02"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_graveyard/small_graveyard_03"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_graveyard/small_graveyard_04"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_graveyard/small_graveyard_05"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_graveyard/small_graveyard_06"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_graveyard/small_graveyard_07"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_graveyard/small_graveyard_08"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_graveyard/small_graveyard_09"), 1)),
                    StructurePool.Projection.RIGID));
        }
    }

}
