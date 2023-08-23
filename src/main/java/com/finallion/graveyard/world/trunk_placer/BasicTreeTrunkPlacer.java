package com.finallion.graveyard.world.trunk_placer;

import com.finallion.graveyard.init.TGTrunkPlacer;
import com.finallion.graveyard.util.NBTParser;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class BasicTreeTrunkPlacer extends TrunkPlacer {
    private static final List<Integer> trunkStates = List.of(-1, 0, -3, 0, 0, -2, 0, 0, 0, 1, 0, -5, 2, 0, 0, 4, 0, -2, 4, 0, 1, 5, 0, -5, 0, 1, -3, 1, 1, -4, 1, 1, -3, 1, 1, -2, 1, 1, -1, 2, 1, -4, 2, 1, -3, 2, 1, -2, 3, 1, -3, 3, 1, -2, 3, 1, 0, 4, 1, -4, 1, 2, -4, 1, 2, -2, 2, 2, -3, 2, 2, -2, 3, 2, -3, 1, 3, -3, 2, 3, -3, 2, 3, -2, 0, 4, -3, 1, 4, -3, 1, 4, -2, 0, 5, -3, 0, 5, -2, 1, 5, -2, -1, 6, -3, 0, 6, -3, 0, 6, -2, 0, 6, -1, 3, 6, 4, -2, 7, -2, -1, 7, -4, -1, 7, -3, -1, 7, -2, 0, 7, -2, 1, 7, -4, 1, 7, 0, 2, 7, 1, 2, 7, 2, 3, 7, 0, 3, 7, 4, -5, 8, -2, -3, 8, -2, -1, 8, -2, 1, 8, -4, 2, 8, 3, 4, 8, -1, 5, 8, -1, -5, 9, -2, -4, 9, -2, -3, 9, -1, -3, 9, 0, -1, 9, -2, -1, 9, -1, 2, 9, -4, 3, 9, 3, 4, 9, 3, -4, 10, 1, -1, 10, -3, -1, 10, -1, 0, 10, -1, -2, 11, -6, -2, 11, -1, -1, 11, -5, -1, 11, -4, -1, 11, 0, -1, 11, 1, 0, 11, -6, 1, 11, -1, 2, 11, -1, -4, 12, -1, -3, 12, -1, -1, 12, 2, -1, 12, 3, 3, 12, -1, -6, 13, -1, -5, 13, -1, 4, 13, -1, 5, 13, -1, 0, 14, -2);
    private static final List<Integer> leavesStates = List.of(-1, 1, -4, 7, 0, 1, -1, 1, 1, 1, -5, 1, 2, 1, -5, 1, 2, 1, -1, 1, 3, 1, -1, 1, 4, 1, -3, 1, 4, 1, -1, 2, 4, 1, 0, 1, 2, 2, -4, 1, 3, 2, -2, 1, 3, 2, -1, 2, 1, 3, -4, 1, 3, 3, -2, 1, 4, 3, 5, 5, 1, 4, 2, 4, 1, 4, 5, 5, 2, 4, -5, 5, 3, 4, 1, 4, 4, 4, 5, 4, 5, 4, 6, 6, 6, 4, 3, 6, 6, 4, 5, 6, 7, 4, -1, 6, -7, 5, -1, 6, -1, 5, 3, 6, 0, 5, -7, 6, 0, 5, 3, 5, 0, 5, 4, 4, 0, 5, 5, 5, 1, 5, 2, 3, 1, 5, 3, 4, 1, 5, 4, 3, 1, 5, 5, 4, 2, 5, -5, 4, 2, 5, -1, 4, 2, 5, 4, 2, 2, 5, 5, 3, 3, 5, -4, 5, 3, 5, 1, 3, 3, 5, 4, 1, 3, 5, 6, 3, 4, 5, 3, 3, 4, 5, 5, 3, 4, 5, 6, 4, 5, 5, -3, 5, 5, 5, 3, 4, 5, 5, 4, 3, 5, 5, 5, 4, 5, 5, 6, 5, 6, 5, -1, 4, 6, 5, 3, 5, 6, 5, 4, 4, 6, 5, 5, 5, 7, 5, -1, 5, -7, 6, -3, 5, -7, 6, -1, 5, -6, 6, -1, 4, -6, 6, 1, 6, -5, 6, 2, 6, -3, 6, -6, 5, -3, 6, 2, 5, -2, 6, 1, 5, -2, 6, 2, 5, -2, 6, 3, 6, -1, 6, -7, 4, -1, 6, -6, 3, -1, 6, 2, 4, -1, 6, 3, 5, -1, 6, 4, 4, 0, 6, -7, 5, 0, 6, -6, 4, 0, 6, -4, 1, 0, 6, 3, 4, 0, 6, 4, 3, 0, 6, 5, 4, 1, 6, -5, 2, 1, 6, 2, 2, 1, 6, 3, 3, 1, 6, 4, 2, 1, 6, 5, 3, 2, 6, -5, 3, 2, 6, -2, 4, 2, 6, -1, 3, 2, 6, 1, 1, 2, 6, 2, 1, 2, 6, 4, 1, 2, 6, 5, 2, 3, 6, -4, 4, 3, 6, 1, 2, 3, 6, 2, 2, 3, 6, 3, 1, 3, 6, 5, 1, 3, 6, 6, 2, 4, 6, -6, 6, 4, 6, 1, 3, 4, 6, 3, 2, 4, 6, 4, 1, 4, 6, 5, 2, 4, 6, 6, 3, 4, 6, 7, 4, 5, 6, -4, 5, 5, 6, -3, 4, 5, 6, 2, 4, 5, 6, 3, 3, 5, 6, 4, 2, 5, 6, 5, 3, 5, 6, 6, 4, 6, 6, -2, 4, 6, 6, -1, 3, 6, 6, 0, 4, 6, 6, 2, 5, 6, 6, 3, 4, 6, 6, 4, 3, 6, 6, 5, 4, 6, 6, 6, 5, 7, 6, -1, 4, 7, 6, 3, 5, 7, 6, 5, 5, 8, 6, 3, 6, -8, 7, -1, 5, -7, 7, -3, 4, -7, 7, -1, 4, -7, 7, 0, 5, -6, 7, -1, 3, -6, 7, 1, 5, -5, 7, 1, 4, -5, 7, 2, 5, -3, 7, -6, 4, -3, 7, 2, 4, -2, 7, -7, 4, -2, 7, -6, 3, -2, 7, 1, 4, -2, 7, 2, 4, -2, 7, 3, 5, -1, 7, -8, 4, -1, 7, -7, 3, -1, 7, -6, 2, -1, 7, -5, 1, -1, 7, 2, 3, -1, 7, 3, 4, -1, 7, 4, 5, 0, 7, -7, 4, 0, 7, -6, 3, 0, 7, -5, 2, 0, 7, -4, 1, 0, 7, 0, 1, 0, 7, 1, 2, 0, 7, 2, 2, 0, 7, 3, 3, 0, 7, 4, 4, 1, 7, -5, 1, 1, 7, -1, 1, 1, 7, 1, 1, 1, 7, 2, 1, 1, 7, 3, 2, 1, 7, 4, 3, 1, 7, 5, 3, 2, 7, -5, 2, 2, 7, -2, 3, 2, 7, -1, 2, 2, 7, 0, 1, 2, 7, 3, 1, 2, 7, 5, 2, 2, 7, 6, 3, 3, 7, -6, 4, 3, 7, -5, 3, 3, 7, -4, 3, 3, 7, -2, 3, 3, 7, 1, 1, 3, 7, 2, 1, 3, 7, 3, 1, 3, 7, 5, 1, 3, 7, 6, 2, 3, 7, 7, 3, 4, 7, -6, 5, 4, 7, -5, 4, 4, 7, -3, 3, 4, 7, -2, 2, 4, 7, 1, 2, 4, 7, 2, 2, 4, 7, 3, 2, 4, 7, 4, 1, 4, 7, 5, 2, 4, 7, 6, 3, 4, 7, 7, 4, 5, 7, -3, 3, 5, 7, -2, 2, 5, 7, 2, 3, 5, 7, 3, 3, 5, 7, 4, 2, 5, 7, 5, 3, 5, 7, 6, 4, 5, 7, 7, 5, 6, 7, -3, 4, 6, 7, -2, 3, 6, 7, -1, 2, 6, 7, 0, 3, 6, 7, 2, 4, 6, 7, 3, 4, 6, 7, 4, 3, 6, 7, 5, 4, 6, 7, 6, 5, 7, 7, -2, 4, 7, 7, -1, 3, 7, 7, 0, 4, 7, 7, 1, 5, 7, 7, 2, 5, 7, 7, 3, 5, 7, 7, 4, 4, 8, 7, -1, 4, 8, 7, 2, 6, 8, 7, 3, 6, -8, 8, 0, 5, -7, 8, -3, 3, -7, 8, -2, 2, -7, 8, -1, 3, -7, 8, 0, 4, -6, 8, -3, 2, -6, 8, -2, 1, -6, 8, -1, 2, -6, 8, 0, 3, -6, 8, 1, 4, -6, 8, 2, 5, -5, 8, -4, 2, -5, 8, -3, 1, -5, 8, -1, 1, -5, 8, 0, 2, -5, 8, 1, 3, -5, 8, 2, 4, -4, 8, -6, 5, -4, 8, -4, 3, -4, 8, -3, 2, -4, 8, -1, 2, -4, 8, 0, 3, -4, 8, 2, 3, -3, 8, -6, 4, -3, 8, -5, 4, -3, 8, -3, 1, -3, 8, -1, 1, -3, 8, 1, 2, -3, 8, 2, 3, -3, 8, 3, 4, -2, 8, -7, 4, -2, 8, -6, 3, -2, 8, -5, 3, -2, 8, -4, 2, -2, 8, -3, 2, -2, 8, -1, 2, -2, 8, 1, 3, -2, 8, 2, 4, -2, 8, 3, 4, -2, 8, 4, 5, -1, 8, -8, 5, -1, 8, -7, 4, -1, 8, -6, 3, -1, 8, -5, 2, -1, 8, -4, 1, -1, 8, 0, 2, -1, 8, 2, 4, -1, 8, 3, 3, -1, 8, 4, 4, 0, 8, -8, 5, 0, 8, -7, 4, 0, 8, -6, 3, 0, 8, -5, 2, 0, 8, -4, 1, 0, 8, 0, 2, 0, 8, 1, 3, 0, 8, 2, 3, 0, 8, 3, 2, 0, 8, 5, 4, 1, 8, -6, 2, 1, 8, -5, 1, 1, 8, -2, 3, 1, 8, -1, 2, 1, 8, 0, 1, 1, 8, 1, 2, 1, 8, 2, 2, 1, 8, 3, 1, 1, 8, 4, 2, 1, 8, 5, 3, 2, 8, -7, 6, 2, 8, -5, 2, 2, 8, -4, 1, 2, 8, -2, 3, 2, 8, -1, 3, 2, 8, 0, 2, 2, 8, 1, 1, 2, 8, 2, 1, 2, 8, 4, 1, 2, 8, 5, 2, 2, 8, 6, 3, 3, 8, -6, 5, 3, 8, -4, 2, 3, 8, -2, 2, 3, 8, 0, 1, 3, 8, 1, 2, 3, 8, 2, 2, 3, 8, 3, 1, 3, 8, 4, 1, 3, 8, 5, 2, 3, 8, 6, 3, 3, 8, 7, 4, 4, 8, -5, 4, 4, 8, -4, 3, 4, 8, -3, 2, 4, 8, -2, 1, 4, 8, 0, 1, 4, 8, 1, 2, 4, 8, 2, 2, 4, 8, 3, 1, 4, 8, 4, 2, 4, 8, 5, 3, 4, 8, 6, 4, 4, 8, 7, 5, 5, 8, -5, 4, 5, 8, -4, 3, 5, 8, -3, 2, 5, 8, -2, 1, 5, 8, 1, 3, 5, 8, 2, 3, 5, 8, 3, 2, 5, 8, 4, 3, 5, 8, 5, 4, 5, 8, 6, 5, 5, 8, 7, 6, 6, 8, -4, 4, 6, 8, -3, 3, 6, 8, -2, 2, 6, 8, -1, 1, 6, 8, 0, 2, 6, 8, 1, 3, 6, 8, 2, 4, 6, 8, 3, 3, 6, 8, 4, 4, 6, 8, 5, 5, 7, 8, -2, 3, 7, 8, -1, 2, 7, 8, 0, 3, 7, 8, 1, 4, 7, 8, 2, 5, 7, 8, 3, 4, 7, 8, 4, 5, 7, 8, 5, 6, 8, 8, -2, 4, 8, 8, -1, 3, 8, 8, 0, 4, 8, 8, 1, 5, 8, 8, 2, 6, 8, 8, 3, 5, -8, 9, -2, 6, -8, 9, -1, 5, -8, 9, 0, 6, -7, 9, -3, 3, -7, 9, -1, 4, -7, 9, 0, 5, -7, 9, 1, 4, -6, 9, -3, 2, -6, 9, -2, 1, -6, 9, 0, 4, -6, 9, 1, 3, -6, 9, 2, 4, -5, 9, -5, 6, -5, 9, -3, 1, -5, 9, -1, 1, -5, 9, 1, 2, -5, 9, 2, 3, -5, 9, 3, 4, -4, 9, -6, 4, -4, 9, -5, 5, -4, 9, -3, 1, -4, 9, -1, 1, -4, 9, 2, 2, -3, 9, -7, 4, -3, 9, -6, 3, -3, 9, -2, 1, -3, 9, 1, 1, -3, 9, 3, 4, -2, 9, -7, 3, -2, 9, -6, 2, -2, 9, -5, 3, -2, 9, -4, 3, -2, 9, -3, 2, -2, 9, -2, 1, -2, 9, -1, 1, -2, 9, 1, 2, -2, 9, 2, 3, -2, 9, 3, 4, -2, 9, 4, 5, -1, 9, -8, 5, -1, 9, -7, 4, -1, 9, -6, 3, -1, 9, -5, 2, -1, 9, -4, 2, -1, 9, -3, 1, -1, 9, 0, 1, -1, 9, 1, 2, -1, 9, 2, 3, -1, 9, 3, 3, -1, 9, 4, 4, -1, 9, 5, 5, 0, 9, -8, 4, 0, 9, -7, 3, 0, 9, -6, 2, 0, 9, -5, 3, 0, 9, -4, 2, 0, 9, -2, 1, 0, 9, 0, 2, 0, 9, 1, 3, 0, 9, 2, 4, 0, 9, 3, 3, 0, 9, 5, 5, 1, 9, -7, 4, 1, 9, -5, 2, 1, 9, -3, 3, 1, 9, -2, 2, 1, 9, 0, 2, 1, 9, 2, 3, 1, 9, 3, 2, 1, 9, 4, 3, 1, 9, 5, 4, 1, 9, 6, 5, 2, 9, -7, 5, 2, 9, -5, 1, 2, 9, -2, 3, 2, 9, -1, 2, 2, 9, 0, 3, 2, 9, 1, 2, 2, 9, 3, 1, 2, 9, 4, 2, 2, 9, 5, 3, 2, 9, 6, 4, 3, 9, -7, 6, 3, 9, -6, 5, 3, 9, -4, 1, 3, 9, -3, 2, 3, 9, -2, 3, 3, 9, 0, 2, 3, 9, 4, 1, 3, 9, 5, 2, 3, 9, 6, 3, 3, 9, 7, 4, 4, 9, -6, 4, 4, 9, -5, 3, 4, 9, -4, 2, 4, 9, -3, 3, 4, 9, -2, 2, 4, 9, -1, 1, 4, 9, 0, 2, 4, 9, 1, 2, 4, 9, 2, 1, 4, 9, 4, 1, 4, 9, 5, 2, 4, 9, 6, 3, 4, 9, 7, 4, 5, 9, -5, 4, 5, 9, -4, 3, 5, 9, -3, 3, 5, 9, -2, 2, 5, 9, -1, 1, 5, 9, 0, 2, 5, 9, 1, 3, 5, 9, 2, 2, 5, 9, 3, 1, 5, 9, 4, 2, 5, 9, 5, 3, 5, 9, 6, 4, 6, 9, -4, 4, 6, 9, -3, 4, 6, 9, -2, 3, 6, 9, -1, 2, 6, 9, 0, 3, 6, 9, 1, 4, 6, 9, 2, 3, 6, 9, 3, 2, 6, 9, 4, 3, 6, 9, 5, 4, 6, 9, 6, 5, 7, 9, -4, 5, 7, 9, -3, 5, 7, 9, -2, 4, 7, 9, -1, 3, 7, 9, 0, 4, 7, 9, 1, 5, 7, 9, 2, 4, 7, 9, 3, 3, 7, 9, 4, 4, 8, 9, -2, 5, 8, 9, -1, 4, 8, 9, 0, 5, 8, 9, 1, 6, 8, 9, 2, 5, -8, 10, -2, 6, -8, 10, -1, 5, -8, 10, 0, 5, -7, 10, -4, 5, -7, 10, -3, 4, -7, 10, -2, 5, -7, 10, -1, 4, -7, 10, 0, 4, -7, 10, 1, 3, -6, 10, -6, 5, -6, 10, -5, 6, -6, 10, -4, 6, -6, 10, 0, 3, -6, 10, 1, 2, -6, 10, 2, 3, -5, 10, -6, 4, -5, 10, -5, 5, -5, 10, -1, 2, -5, 10, 0, 2, -5, 10, 1, 1, -5, 10, 2, 2, -5, 10, 3, 3, -4, 10, -7, 4, -4, 10, -6, 3, -4, 10, -4, 4, -4, 10, 0, 1, -4, 10, 2, 1, -4, 10, 3, 2, -3, 10, -7, 3, -3, 10, -6, 2, -3, 10, -5, 3, -3, 10, -4, 3, -3, 10, -1, 1, -3, 10, 1, 1, -3, 10, 2, 2, -3, 10, 3, 3, -3, 10, 4, 4, -2, 10, -8, 3, -2, 10, -7, 2, -2, 10, -6, 1, -2, 10, -4, 2, -2, 10, -3, 1, -2, 10, -2, 2, -2, 10, -1, 1, -2, 10, 0, 2, -2, 10, 1, 2, -2, 10, 2, 3, -2, 10, 3, 3, -2, 10, 4, 4, -1, 10, -8, 4, -1, 10, -7, 3, -1, 10, -6, 2, -1, 10, -5, 1, -1, 10, -4, 1, -1, 10, 0, 1, -1, 10, 1, 1, -1, 10, 3, 2, -1, 10, 4, 3, -1, 10, 5, 4, 0, 10, -8, 3, 0, 10, -7, 2, 0, 10, -6, 1, 0, 10, -5, 2, 0, 10, -4, 2, 0, 10, 0, 1, 0, 10, 1, 2, 0, 10, 2, 3, 0, 10, 3, 3, 0, 10, 4, 4, 0, 10, 5, 5, 1, 10, -7, 3, 1, 10, -6, 2, 1, 10, -5, 3, 1, 10, -4, 2, 1, 10, -3, 3, 1, 10, 2, 4, 1, 10, 3, 3, 1, 10, 4, 4, 1, 10, 5, 5, 2, 10, -7, 4, 2, 10, -6, 3, 2, 10, -5, 2, 2, 10, -4, 1, 2, 10, -2, 2, 2, 10, -1, 1, 2, 10, 2, 3, 2, 10, 3, 2, 2, 10, 4, 3, 2, 10, 5, 4, 2, 10, 6, 5, 3, 10, -7, 5, 3, 10, -6, 4, 3, 10, -4, 2, 3, 10, -3, 3, 3, 10, -2, 3, 3, 10, -1, 2, 3, 10, 0, 3, 3, 10, 1, 3, 3, 10, 2, 2, 3, 10, 3, 1, 3, 10, 4, 2, 3, 10, 5, 3, 3, 10, 6, 4, 4, 10, -6, 5, 4, 10, -5, 4, 4, 10, -4, 3, 4, 10, -3, 4, 4, 10, -2, 3, 4, 10, -1, 2, 4, 10, 1, 3, 4, 10, 2, 2, 4, 10, 3, 1, 4, 10, 4, 2, 4, 10, 5, 3, 4, 10, 6, 4, 5, 10, -6, 6, 5, 10, -5, 5, 5, 10, -4, 4, 5, 10, -3, 4, 5, 10, -2, 3, 5, 10, -1, 2, 5, 10, 0, 3, 5, 10, 1, 4, 5, 10, 2, 3, 5, 10, 3, 2, 5, 10, 4, 3, 5, 10, 5, 4, 5, 10, 6, 5, 6, 10, -5, 6, 6, 10, -4, 5, 6, 10, -3, 5, 6, 10, -2, 4, 6, 10, -1, 3, 6, 10, 0, 4, 6, 10, 1, 5, 6, 10, 2, 4, 6, 10, 3, 3, 6, 10, 4, 4, 6, 10, 5, 5, 7, 10, -4, 6, 7, 10, -3, 6, 7, 10, -2, 5, 7, 10, -1, 4, 7, 10, 0, 5, 7, 10, 1, 6, 7, 10, 2, 5, 8, 10, -1, 5, 8, 10, 0, 6, -8, 11, -3, 6, -8, 11, -2, 5, -8, 11, -1, 4, -8, 11, 0, 5, -7, 11, -4, 6, -7, 11, -3, 5, -7, 11, -2, 4, -7, 11, -1, 3, -7, 11, 0, 4, -7, 11, 1, 4, -7, 11, 2, 5, -6, 11, -5, 5, -6, 11, -4, 5, -6, 11, -2, 3, -6, 11, 0, 3, -6, 11, 1, 3, -6, 11, 2, 4, -6, 11, 3, 5, -5, 11, -6, 5, -5, 11, -5, 4, -5, 11, -4, 4, -5, 11, -3, 4, -5, 11, 0, 3, -5, 11, 1, 2, -5, 11, 2, 3, -5, 11, 3, 4, -4, 11, -7, 3, -4, 11, -5, 3, -4, 11, -4, 3, -4, 11, -1, 1, -4, 11, 0, 2, -4, 11, 1, 1, -4, 11, 2, 2, -4, 11, 3, 3, -3, 11, -7, 2, -3, 11, -6, 1, -3, 11, -5, 2, -3, 11, -4, 2, -3, 11, -3, 3, -3, 11, -1, 1, -3, 11, 0, 2, -3, 11, 1, 2, -3, 11, 2, 3, -3, 11, 3, 3, -3, 11, 4, 4, -2, 11, -8, 2, -2, 11, -7, 1, -2, 11, -5, 1, -2, 11, -4, 1, -2, 11, -3, 2, -2, 11, -2, 1, -2, 11, 0, 1, -2, 11, 1, 1, -2, 11, 2, 2, -2, 11, 3, 2, -2, 11, 4, 3, -2, 11, 5, 4, -1, 11, -8, 3, -1, 11, -7, 2, -1, 11, -6, 1, -1, 11, -3, 1, -1, 11, -2, 2, -1, 11, 2, 1, -1, 11, 3, 1, -1, 11, 4, 2, -1, 11, 5, 3, 0, 11, -8, 2, 0, 11, -7, 1, 0, 11, -5, 1, 0, 11, -4, 1, 0, 11, -3, 2, 0, 11, -2, 3, 0, 11, 0, 1, 0, 11, 1, 1, 0, 11, 2, 2, 0, 11, 3, 2, 0, 11, 4, 3, 0, 11, 5, 4, 1, 11, -8, 3, 1, 11, -7, 2, 1, 11, -6, 1, 1, 11, -5, 2, 1, 11, -4, 2, 1, 11, -3, 3, 1, 11, 0, 1, 1, 11, 1, 2, 1, 11, 3, 3, 1, 11, 4, 4, 1, 11, 5, 5, 2, 11, -7, 3, 2, 11, -6, 2, 2, 11, -5, 3, 2, 11, -4, 2, 2, 11, -3, 2, 2, 11, -2, 1, 2, 11, 2, 4, 2, 11, 3, 3, 2, 11, 4, 4, 2, 11, 5, 5, 3, 11, -6, 3, 3, 11, -5, 4, 3, 11, -4, 3, 3, 11, -3, 3, 3, 11, -2, 2, 3, 11, -1, 1, 3, 11, 0, 2, 3, 11, 1, 3, 3, 11, 2, 3, 3, 11, 3, 2, 3, 11, 4, 3, 3, 11, 5, 4, 4, 11, -6, 4, 4, 11, -5, 5, 4, 11, -4, 4, 4, 11, -3, 4, 4, 11, -2, 3, 4, 11, -1, 2, 4, 11, 0, 3, 4, 11, 1, 4, 4, 11, 3, 2, 4, 11, 4, 3, 4, 11, 5, 4, 4, 11, 6, 5, 5, 11, -5, 6, 5, 11, -3, 4, 5, 11, -2, 3, 5, 11, -1, 2, 5, 11, 0, 3, 5, 11, 1, 4, 5, 11, 2, 4, 5, 11, 3, 3, 5, 11, 4, 4, 5, 11, 5, 5, 6, 11, -4, 6, 6, 11, -3, 5, 6, 11, -2, 4, 6, 11, -1, 3, 6, 11, 0, 4, 6, 11, 1, 5, 6, 11, 2, 5, 7, 11, -3, 6, 7, 11, -2, 5, 7, 11, -1, 4, 7, 11, 0, 5, 7, 11, 1, 6, -8, 12, -3, 5, -8, 12, -2, 4, -8, 12, -1, 3, -7, 12, -4, 5, -7, 12, -3, 4, -7, 12, -2, 3, -7, 12, -1, 2, -7, 12, 0, 3, -7, 12, 1, 4, -7, 12, 2, 5, -6, 12, -5, 5, -6, 12, -4, 4, -6, 12, -3, 3, -6, 12, -2, 2, -6, 12, -1, 1, -6, 12, 0, 2, -6, 12, 1, 3, -6, 12, 2, 4, -6, 12, 3, 5, -5, 12, -5, 5, -5, 12, -4, 4, -5, 12, -3, 3, -5, 12, -2, 2, -5, 12, -1, 1, -5, 12, 0, 2, -5, 12, 1, 3, -5, 12, 2, 4, -5, 12, 3, 4, -5, 12, 4, 5, -4, 12, -6, 3, -4, 12, -5, 4, -4, 12, -4, 3, -4, 12, -3, 2, -4, 12, -2, 1, -4, 12, 0, 1, -4, 12, 1, 2, -4, 12, 2, 3, -4, 12, 3, 3, -4, 12, 4, 4, -3, 12, -6, 2, -3, 12, -5, 3, -3, 12, -4, 3, -3, 12, -3, 2, -3, 12, -2, 1, -3, 12, 0, 1, -3, 12, 1, 2, -3, 12, 2, 2, -3, 12, 3, 2, -3, 12, 4, 3, -2, 12, -7, 2, -2, 12, -6, 1, -2, 12, -5, 2, -2, 12, -3, 3, -2, 12, -2, 2, -2, 12, -1, 1, -2, 12, 0, 2, -2, 12, 1, 2, -2, 12, 2, 1, -2, 12, 3, 1, -2, 12, 4, 2, -1, 12, -8, 4, -1, 12, -7, 3, -1, 12, -6, 2, -1, 12, -4, 1, -1, 12, -3, 2, -1, 12, -2, 3, -1, 12, -1, 2, -1, 12, 0, 1, -1, 12, 4, 1, -1, 12, 5, 2, 0, 12, -8, 3, 0, 12, -7, 2, 0, 12, -6, 1, 0, 12, -4, 2, 0, 12, -3, 3, 0, 12, -1, 2, 0, 12, 0, 2, 0, 12, 2, 1, 0, 12, 3, 1, 0, 12, 4, 2, 0, 12, 5, 3, 1, 12, -7, 3, 1, 12, -6, 2, 1, 12, -5, 3, 1, 12, -4, 3, 1, 12, -2, 2, 1, 12, -1, 1, 1, 12, 0, 2, 1, 12, 1, 3, 1, 12, 3, 2, 1, 12, 4, 3, 1, 12, 5, 4, 2, 12, -7, 4, 2, 12, -6, 3, 2, 12, -5, 4, 2, 12, -4, 3, 2, 12, -2, 2, 2, 12, -1, 1, 2, 12, 0, 2, 2, 12, 1, 3, 2, 12, 2, 4, 2, 12, 4, 4, 3, 12, -6, 4, 3, 12, -5, 4, 3, 12, -4, 3, 3, 12, -3, 2, 3, 12, -2, 1, 3, 12, 0, 1, 3, 12, 1, 2, 3, 12, 3, 3, 3, 12, 4, 4, 4, 12, -5, 5, 4, 12, -4, 4, 4, 12, -3, 3, 4, 12, -2, 2, 4, 12, -1, 1, 4, 12, 0, 2, 4, 12, 1, 3, 4, 12, 2, 4, 4, 12, 3, 3, 4, 12, 4, 4, 5, 12, -4, 4, 5, 12, -3, 3, 5, 12, -2, 2, 5, 12, -1, 1, 5, 12, 0, 2, 5, 12, 1, 3, 5, 12, 2, 4, 5, 12, 3, 4, 6, 12, -3, 4, 6, 12, -2, 3, 6, 12, -1, 2, 6, 12, 0, 3, 6, 12, 1, 4, 7, 12, -1, 3, -8, 13, -2, 3, -8, 13, -1, 2, -7, 13, -3, 3, -7, 13, -2, 2, -7, 13, -1, 1, -7, 13, 0, 2, -6, 13, -3, 2, -6, 13, -2, 1, -6, 13, 0, 1, -6, 13, 1, 2, -6, 13, 2, 3, -5, 13, -4, 3, -5, 13, -3, 2, -5, 13, -2, 1, -5, 13, 0, 1, -5, 13, 1, 2, -5, 13, 2, 3, -5, 13, 3, 4, -4, 13, -5, 5, -4, 13, -4, 4, -4, 13, -3, 3, -4, 13, -2, 2, -4, 13, -1, 1, -4, 13, 1, 3, -4, 13, 2, 4, -4, 13, 3, 4, -3, 13, -5, 4, -3, 13, -4, 4, -3, 13, -3, 3, -3, 13, -2, 2, -3, 13, -1, 1, -3, 13, 0, 2, -3, 13, 1, 3, -3, 13, 2, 3, -3, 13, 3, 3, -3, 13, 4, 4, -2, 13, -6, 2, -2, 13, -5, 3, -2, 13, -4, 3, -2, 13, -3, 4, -2, 13, -2, 3, -2, 13, -1, 2, -2, 13, 0, 3, -2, 13, 1, 3, -2, 13, 2, 2, -2, 13, 3, 2, -2, 13, 4, 3, -2, 13, 5, 4, -1, 13, -7, 4, -1, 13, -6, 3, -1, 13, -5, 3, -1, 13, -4, 2, -1, 13, -3, 3, -1, 13, -2, 2, -1, 13, -1, 3, -1, 13, 0, 2, -1, 13, 1, 2, -1, 13, 2, 1, -1, 13, 3, 1, -1, 13, 4, 2, -1, 13, 5, 3, 0, 13, -7, 3, 0, 13, -6, 2, 0, 13, -5, 3, 0, 13, -4, 3, 0, 13, -3, 2, 0, 13, -2, 1, 0, 13, 0, 3, 0, 13, 1, 3, 0, 13, 2, 2, 0, 13, 3, 2, 0, 13, 4, 3, 1, 13, -7, 4, 1, 13, -6, 3, 1, 13, -5, 4, 1, 13, -4, 4, 1, 13, -3, 3, 1, 13, -2, 2, 1, 13, -1, 2, 1, 13, 0, 3, 1, 13, 1, 4, 1, 13, 2, 3, 1, 13, 3, 3, 1, 13, 4, 4, 2, 13, -6, 4, 2, 13, -5, 5, 2, 13, -4, 4, 2, 13, -3, 4, 2, 13, -2, 3, 2, 13, 0, 3, 2, 13, 1, 4, 2, 13, 2, 4, 2, 13, 3, 4, 2, 13, 4, 5, 3, 13, -6, 5, 3, 13, -5, 5, 3, 13, -4, 4, 3, 13, -3, 3, 3, 13, -2, 2, 3, 13, -1, 1, 3, 13, 0, 2, 3, 13, 1, 3, 3, 13, 2, 4, 3, 13, 3, 4, 3, 13, 4, 5, 4, 13, -5, 4, 4, 13, -4, 3, 4, 13, -3, 2, 4, 13, -2, 1, 4, 13, 0, 1, 4, 13, 1, 2, 4, 13, 2, 3, 4, 13, 3, 4, 5, 13, -4, 3, 5, 13, -3, 2, 5, 13, -2, 1, 5, 13, 0, 1, 5, 13, 1, 2, 6, 13, -2, 2, 6, 13, -1, 1, 6, 13, 0, 2, -7, 14, -2, 3, -7, 14, -1, 2, -6, 14, -2, 2, -6, 14, -1, 1, -6, 14, 0, 2, -5, 14, -3, 3, -5, 14, -2, 2, -5, 14, -1, 1, -5, 14, 0, 2, -5, 14, 1, 3, -4, 14, -4, 5, -4, 14, -3, 4, -4, 14, -2, 3, -4, 14, -1, 2, -4, 14, 0, 3, -4, 14, 1, 4, -4, 14, 2, 5, -3, 14, -4, 5, -3, 14, -3, 4, -3, 14, -2, 3, -3, 14, 0, 3, -3, 14, 1, 4, -3, 14, 2, 4, -3, 14, 3, 4, -2, 14, -5, 4, -2, 14, -4, 4, -2, 14, -3, 3, -2, 14, -2, 2, -2, 14, -1, 3, -2, 14, 0, 4, -2, 14, 2, 3, -2, 14, 3, 3, -1, 14, -6, 4, -1, 14, -5, 4, -1, 14, -4, 3, -1, 14, -3, 2, -1, 14, -2, 1, -1, 14, -1, 2, -1, 14, 0, 3, -1, 14, 1, 3, -1, 14, 2, 2, -1, 14, 3, 2, -1, 14, 4, 3, 0, 14, -6, 3, 0, 14, -5, 3, 0, 14, -4, 2, 0, 14, -3, 1, 0, 14, -1, 1, 0, 14, 0, 2, 0, 14, 1, 3, 0, 14, 2, 3, 0, 14, 3, 3, 0, 14, 4, 4, 1, 14, -5, 4, 1, 14, -4, 3, 1, 14, -3, 2, 1, 14, -2, 1, 1, 14, -1, 2, 1, 14, 0, 3, 1, 14, 1, 4, 1, 14, 2, 4, 1, 14, 3, 4, 2, 14, -5, 5, 2, 14, -4, 4, 2, 14, -3, 3, 2, 14, -2, 2, 2, 14, -1, 3, 2, 14, 0, 4, 2, 14, 1, 5, 2, 14, 2, 5, 2, 14, 3, 5, 3, 14, -5, 6, 3, 14, -4, 5, 3, 14, -3, 4, 3, 14, -2, 3, 3, 14, -1, 2, 3, 14, 0, 3, 3, 14, 1, 4, 3, 14, 2, 5, 4, 14, -4, 4, 4, 14, -3, 3, 4, 14, -2, 2, 4, 14, -1, 1, 4, 14, 0, 2, 4, 14, 1, 3, 5, 14, -3, 3, 5, 14, -2, 2, 5, 14, -1, 1, 5, 14, 0, 2, -6, 15, -2, 3, -6, 15, -1, 2, -5, 15, -2, 3, -5, 15, -1, 2, -5, 15, 0, 3, -4, 15, -3, 5, -4, 15, -2, 4, -4, 15, -1, 3, -4, 15, 0, 4, -4, 15, 1, 5, -3, 15, -3, 5, -3, 15, -2, 4, -3, 15, -1, 4, -3, 15, 0, 4, -3, 15, 1, 5, -3, 15, 2, 5, -2, 15, -4, 5, -2, 15, -3, 4, -2, 15, -2, 3, -2, 15, -1, 4, -2, 15, 0, 5, -2, 15, 1, 5, -2, 15, 2, 4, -2, 15, 3, 4, -1, 15, -4, 4, -1, 15, -3, 3, -1, 15, -1, 3, -1, 15, 0, 4, -1, 15, 1, 4, -1, 15, 2, 3, -1, 15, 3, 3, 0, 15, -5, 4, 0, 15, -4, 3, 0, 15, -3, 2, 0, 15, -2, 1, 0, 15, -1, 2, 0, 15, 0, 3, 0, 15, 1, 4, 0, 15, 2, 4, 0, 15, 3, 4, 1, 15, -5, 5, 1, 15, -4, 4, 1, 15, -3, 3, 1, 15, -2, 2, 1, 15, -1, 3, 1, 15, 0, 4, 1, 15, 1, 5, 1, 15, 2, 5, 2, 15, -3, 4, 2, 15, -2, 3, 2, 15, -1, 4, 2, 15, 0, 5, 2, 15, 1, 6, 2, 15, 2, 6, 3, 15, -1, 3, 3, 15, 0, 4, -5, 16, -1, 3, -4, 16, -2, 5, -4, 16, -1, 4, -4, 16, 0, 5, -3, 16, -2, 5, -3, 16, -1, 5, -3, 16, 0, 5, -2, 16, -3, 5, -2, 16, -2, 4, -2, 16, -1, 5, -2, 16, 0, 6, -2, 16, 1, 6, -1, 16, -4, 5, -1, 16, -3, 4, -1, 16, -2, 3, -1, 16, -1, 4, -1, 16, 0, 5, -1, 16, 1, 5, -1, 16, 2, 4, 0, 16, -4, 4, 0, 16, -3, 3, 0, 16, -2, 2, 0, 16, -1, 3, 0, 16, 0, 4, 0, 16, 1, 5, 1, 16, -2, 3, 1, 16, -1, 4, 1, 16, 0, 5, 1, 16, 1, 6, 2, 16, -1, 5, 2, 16, 0, 6);

    public static final Codec<BasicTreeTrunkPlacer> CODEC = RecordCodecBuilder.create(instance ->
            fillTrunkPlacerFields(instance).apply(instance, BasicTreeTrunkPlacer::new));

    public BasicTreeTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
        super(baseHeight, firstRandomHeight, secondRandomHeight);
    }

    @Override
    protected TrunkPlacerType<?> getType() {
        return TGTrunkPlacer.BASIC_TREE_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.TreeNode> generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config) {
        int startX = startPos.getX();
        int startY = startPos.getY();
        int startZ = startPos.getZ();

        for (int i = 0; i < trunkStates.size() - NBTParser.TRUNK_PIECE_SIZE; i += NBTParser.TRUNK_PIECE_SIZE) {
            BlockPos newPos = new BlockPos(startX + trunkStates.get(i), startY + trunkStates.get(i + 1), startZ + trunkStates.get(i + 2));
            getAndSetState(world, replacer, random, newPos, config);
        }

        for (int i = 0; i < leavesStates.size() - NBTParser.LEAF_PIECE_SIZE; i += NBTParser.LEAF_PIECE_SIZE) {
            BlockPos newPos = new BlockPos(startX + leavesStates.get(i), startY + leavesStates.get(i + 1), startZ + leavesStates.get(i + 2));
            getAndSetLeafState(world, replacer, newPos, Blocks.BIRCH_LEAVES.getDefaultState().with(Properties.DISTANCE_1_7, leavesStates.get(i + 3)));
        }

        return new ArrayList<>();
    }


    private boolean getAndSetLeafState(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, BlockPos pos, BlockState state) {
        if (this.canReplace(world, pos)) {
            replacer.accept(pos, state);
            return true;
        } else {
            return false;
        }
    }
}
