package supercoder79.ecotones.world.biome.climatic;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Blocks;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.decorator.*;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placer.SimpleBlockPlacer;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import supercoder79.ecotones.api.BiomeRegistries;
import supercoder79.ecotones.api.Climate;
import supercoder79.ecotones.api.SimpleTreeDecorationData;
import supercoder79.ecotones.api.TreeType;
import supercoder79.ecotones.blocks.EcotonesBlocks;
import supercoder79.ecotones.util.state.DeferredBlockStateProvider;
import supercoder79.ecotones.util.compat.FloralisiaCompat;
import supercoder79.ecotones.world.biome.BiomeHelper;
import supercoder79.ecotones.world.biome.EcotonesBiomeBuilder;
import supercoder79.ecotones.world.decorator.EcotonesDecorators;
import supercoder79.ecotones.world.decorator.ShrubDecoratorConfig;
import supercoder79.ecotones.world.features.EcotonesFeatures;
import supercoder79.ecotones.world.features.config.*;
import supercoder79.ecotones.world.structure.EcotonesConfiguredStructures;

public class SpruceMarshBiome extends EcotonesBiomeBuilder {
    public static Biome INSTANCE;
    public static Biome THICKET;
    public static Biome HILLY;
    public static Biome MOUNTAINOUS;

    public static void init() {
        INSTANCE = Registry.register(BuiltinRegistries.BIOME, new Identifier("ecotones", "spruce_marsh"), new SpruceMarshBiome(0.1F, 0.025F, false).build());
        THICKET = Registry.register(BuiltinRegistries.BIOME, new Identifier("ecotones", "spruce_marsh_thicket"), new SpruceMarshBiome(0.1F, 0.025F, true).build());
        HILLY = Registry.register(BuiltinRegistries.BIOME, new Identifier("ecotones", "spruce_marsh_hilly"), new SpruceMarshBiome(0.25F, 0.225F, false).build());
        MOUNTAINOUS = Registry.register(BuiltinRegistries.BIOME, new Identifier("ecotones", "spruce_marsh_mountainous"), new SpruceMarshBiome(0.4F, 0.625F, false).build());
        BiomeRegistries.registerBiomeVariantChance(INSTANCE, 3);
        BiomeRegistries.registerBiomeVariants(INSTANCE, INSTANCE, THICKET);
        BiomeRegistries.registerNoRiverBiome(INSTANCE);
        BiomeRegistries.registerMountains(INSTANCE, HILLY, MOUNTAINOUS);

        Climate.WARM_HUMID.add(INSTANCE, 0.3);
        Climate.WARM_MILD.add(INSTANCE, 0.2);

        BiomeRegistries.registerNoBeachBiomes(INSTANCE, THICKET, HILLY, MOUNTAINOUS);
        BiomeRegistries.registerNoRiverBiomes(INSTANCE, THICKET, HILLY, MOUNTAINOUS);

//        TerraformSlimeSpawnBiomes.addSlimeSpawnBiomes(BuiltinRegistries.BIOME.getKey(INSTANCE).get(),
//                BuiltinRegistries.BIOME.getKey(THICKET).get(),
//                BuiltinRegistries.BIOME.getKey(HILLY).get(),
//                BuiltinRegistries.BIOME.getKey(MOUNTAINOUS).get());
    }

    public SpruceMarshBiome(float depth, float scale, boolean thicket) {
        this.surfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_CONFIG);

        this.depth(depth);
        this.scale(scale);
        this.temperature(0.9F);
        this.downfall(0.7F);

        this.hilliness(2.0);
        this.volatility(1.02);

        this.precipitation(Biome.Precipitation.RAIN);

        this.grassColor(0xd1c35c);
        this.foliageColor(0xBFA243);

        this.waterColor(0x5c5214);
        this.waterFogColor(0x5c5214);
        this.category(Biome.Category.SWAMP);

        this.addStructureFeature(ConfiguredStructureFeatures.STRONGHOLD);
        this.addStructureFeature(EcotonesConfiguredStructures.CAMPFIRE_SPRUCE);
        this.addStructureFeature(EcotonesConfiguredStructures.COTTAGE);

        DefaultBiomeFeatures.addLandCarvers(this.getGenerationSettings());
        DefaultBiomeFeatures.addPlainsTallGrass(this.getGenerationSettings());
        DefaultBiomeFeatures.addPlainsFeatures(this.getGenerationSettings());
        DefaultBiomeFeatures.addDefaultUndergroundStructures(this.getGenerationSettings());
        DefaultBiomeFeatures.addDungeons(this.getGenerationSettings());
        DefaultBiomeFeatures.addMineables(this.getGenerationSettings());
        DefaultBiomeFeatures.addDefaultOres(this.getGenerationSettings());
        DefaultBiomeFeatures.addAmethystGeodes(this.getGenerationSettings());
        DefaultBiomeFeatures.addDefaultDisks(this.getGenerationSettings());
        DefaultBiomeFeatures.addDefaultMushrooms(this.getGenerationSettings());
        DefaultBiomeFeatures.addSprings(this.getGenerationSettings());
        DefaultBiomeFeatures.addFrozenTopLayer(this.getGenerationSettings());

        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION,
                Feature.RANDOM_PATCH.configure(FeatureConfigHolder.SURFACE_ROCKS)
                        .decorate(EcotonesDecorators.ROCKINESS.configure(DecoratorConfig.DEFAULT)));

        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION,
                Feature.RANDOM_PATCH.configure(FeatureConfigHolder.TALL_GRASS_CONFIG)
                        .decorate(Decorator.SPREAD_32_ABOVE.configure(NopeDecoratorConfig.INSTANCE))
                        .decorate(Decorator.HEIGHTMAP.configure(new HeightmapDecoratorConfig(Heightmap.Type.MOTION_BLOCKING)))
                        .spreadHorizontally()
                        .decorate(Decorator.COUNT_NOISE.configure(new CountNoiseDecoratorConfig(-0.8D, 2, 4))));

        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION,
                EcotonesFeatures.SHRUB.configure(new SimpleTreeFeatureConfig(Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LEAVES.getDefaultState()))
                        .decorate(EcotonesDecorators.SHRUB_PLACEMENT_DECORATOR.configure(new ShrubDecoratorConfig(thicket ? 0.8 : 0.3))));

        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION,
                EcotonesFeatures.BRANCHING_OAK.configure(TreeType.RARE_LARGE_OAK)
                        .decorate(EcotonesDecorators.TREE_DECORATOR.configure(TreeType.RARE_LARGE_OAK.decorationData)));

        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION,
                EcotonesFeatures.WIDE_SHRUB.configure(new SimpleTreeFeatureConfig(Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LEAVES.getDefaultState()))
                        .decorate(EcotonesDecorators.SHRUB_PLACEMENT_DECORATOR.configure(new ShrubDecoratorConfig(thicket ? 1.1 : 0.95))));

        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION,
                Feature.RANDOM_PATCH.configure(FeatureConfigHolder.CLOVER)
                        .decorate(Decorator.SPREAD_32_ABOVE.configure(NopeDecoratorConfig.INSTANCE))
                        .spreadHorizontally()
                        .repeat(1));

        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION,
                Feature.RANDOM_PATCH.configure(FeatureConfigHolder.MOSS)
                        .decorate(Decorator.SPREAD_32_ABOVE.configure(NopeDecoratorConfig.INSTANCE))
                        .spreadHorizontally()
                        .repeat(2));

        this.addFeature(GenerationStep.Feature.LOCAL_MODIFICATIONS,
                EcotonesFeatures.PLACE_WATER.configure(FeatureConfigHolder.GRASS_WATER_PATCH)
                        .decorate(Decorator.HEIGHTMAP.configure(new HeightmapDecoratorConfig(Heightmap.Type.MOTION_BLOCKING)))
                        .spreadHorizontally()
                        .repeat(3));

        this.addFeature(GenerationStep.Feature.LOCAL_MODIFICATIONS,
                EcotonesFeatures.PLACE_WATER.configure(FeatureConfigHolder.PEAT_PATCH)
                        .decorate(Decorator.HEIGHTMAP.configure(new HeightmapDecoratorConfig(Heightmap.Type.MOTION_BLOCKING)))
                        .spreadHorizontally()
                        .applyChance(2));

        this.addFeature(GenerationStep.Feature.RAW_GENERATION,
                EcotonesFeatures.GROUND_PATCH.configure(new PatchFeatureConfig(EcotonesBlocks.PEAT_BLOCK.getDefaultState(), Blocks.GRASS_BLOCK, UniformIntProvider.create(1, 4)))
                        .spreadHorizontally()
                        .applyChance(3));

        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION,
                Feature.TREE.configure(FeatureConfigHolder.SPRUCE_TREE_CONFIG)
                        .decorate(EcotonesDecorators.SIMPLE_TREE_DECORATOR.configure(new SimpleTreeDecorationData(thicket ? 3.5 : 1.3))));

        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION,
                Feature.RANDOM_PATCH.configure(new RandomPatchFeatureConfig.Builder(
                        new SimpleBlockStateProvider(Blocks.SWEET_BERRY_BUSH.getDefaultState().with(SweetBerryBushBlock.AGE, 3)),
                        SimpleBlockPlacer.INSTANCE).tries(64)
                        .whitelist(ImmutableSet.of(Blocks.GRASS_BLOCK)).cannotProject().build())
                        .repeat(3).spreadHorizontally().decorate(Decorator.HEIGHTMAP_SPREAD_DOUBLE.configure(new HeightmapDecoratorConfig(Heightmap.Type.MOTION_BLOCKING))));

        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION,
                EcotonesFeatures.DEAD_TREE.configure(new SimpleTreeFeatureConfig(Blocks.OAK_LOG.getDefaultState(), Blocks.AIR.getDefaultState()))
                        .decorate(EcotonesDecorators.REVERSE_QUALITY_TREE_DECORATOR.configure(new SimpleTreeDecorationData(thicket ? 0.3 : 0.2))));

        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION,
                Feature.RANDOM_PATCH.configure(new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(Blocks.LILY_PAD.getDefaultState()),
                        SimpleBlockPlacer.INSTANCE).tries(10).build())
                        .decorate(Decorator.SPREAD_32_ABOVE.configure(NopeDecoratorConfig.INSTANCE))
                        .decorate(Decorator.HEIGHTMAP.configure(new HeightmapDecoratorConfig(Heightmap.Type.MOTION_BLOCKING)))
                        .spreadHorizontally()
                        .repeat(8));

        this.addFeature(GenerationStep.Feature.LAKES,
                EcotonesFeatures.PODZOL.configure(FeatureConfig.DEFAULT)
                        .spreadHorizontally()
                        .applyChance(2)
                        .repeat(3));

        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION,
                EcotonesFeatures.CATTAIL.configure(new CattailFeatureConfig(UniformIntProvider.create(64, 96), true, UniformIntProvider.create(10, 14)))
                        .decorate(Decorator.HEIGHTMAP.configure(new HeightmapDecoratorConfig(Heightmap.Type.MOTION_BLOCKING)))
                        .spreadHorizontally()
                        .applyChance(2)
                        .repeat(3));

        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION,
                EcotonesFeatures.CATTAIL.configure(new CattailFeatureConfig(UniformIntProvider.create(8, 20), true, UniformIntProvider.create(3, 6)))
                        .decorate(Decorator.HEIGHTMAP.configure(new HeightmapDecoratorConfig(Heightmap.Type.MOTION_BLOCKING)))
                        .spreadHorizontally()
                        .repeat(3));

        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION,
                EcotonesFeatures.CATTAIL.configure(new CattailFeatureConfig(UniformIntProvider.create(8, 16), false, UniformIntProvider.create(3, 5)))
                        .decorate(Decorator.HEIGHTMAP.configure(new HeightmapDecoratorConfig(Heightmap.Type.MOTION_BLOCKING)))
                        .spreadHorizontally()
                        .applyChance(3));

        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION,
                EcotonesFeatures.CATTAIL.configure(new CattailFeatureConfig(UniformIntProvider.create(16, 64), false, UniformIntProvider.create(8, 14)))
                        .decorate(Decorator.HEIGHTMAP.configure(new HeightmapDecoratorConfig(Heightmap.Type.MOTION_BLOCKING)))
                        .spreadHorizontally()
                        .applyChance(32));

        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION,
                EcotonesFeatures.DUCKWEED.configure(new DuckweedFeatureConfig(UniformIntProvider.create(64, 96), UniformIntProvider.create(10, 14)))
                        .decorate(Decorator.HEIGHTMAP.configure(new HeightmapDecoratorConfig(Heightmap.Type.MOTION_BLOCKING)))
                        .spreadHorizontally()
                        .applyChance(4)
                        .repeat(2));

        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION,
                EcotonesFeatures.DUCKWEED.configure(new DuckweedFeatureConfig(UniformIntProvider.create(8, 16), UniformIntProvider.create(4, 6)))
                        .decorate(Decorator.HEIGHTMAP.configure(new HeightmapDecoratorConfig(Heightmap.Type.MOTION_BLOCKING)))
                        .spreadHorizontally()
                        .applyChance(3));

        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION,
                Feature.RANDOM_PATCH.configure(FeatureConfigHolder.MUSHROOMS)
                        .decorate(Decorator.HEIGHTMAP.configure(new HeightmapDecoratorConfig(Heightmap.Type.MOTION_BLOCKING)))
                        .spreadHorizontally()
                        .repeat(2));

        //todo refactor
        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION,
                Feature.TREE.configure(FeatureConfigHolder.SMALL_PINE_CONFIG)
                        .decorate(EcotonesDecorators.SIMPLE_TREE_DECORATOR.configure(new SimpleTreeDecorationData(0.45))));

        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.FLOWER.configure(FeatureConfigHolder.WIDE_FERNS)
                .repeat(6)
                .spreadHorizontally()
                .decorate(Decorator.SPREAD_32_ABOVE.configure(NopeDecoratorConfig.INSTANCE)));

        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION,
                EcotonesFeatures.SMALL_SPRUCE.configure(new SimpleTreeFeatureConfig(Blocks.SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState()))
                        .decorate(EcotonesDecorators.SIMPLE_TREE_DECORATOR.configure(new SimpleTreeDecorationData(0.2))));

        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION,
                EcotonesFeatures.BLUEBERRY_BUSH.configure(FeatureConfig.DEFAULT)
                        .decorate(EcotonesDecorators.BLUEBERRY_BUSH.configure(new ShrubDecoratorConfig(0.1))));

        this.addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION,
                EcotonesFeatures.DUCK_NEST.configure(DefaultFeatureConfig.INSTANCE)
                        .decorate(EcotonesDecorators.DUCK_NEST.configure(new ShrubDecoratorConfig(0.2))));

        if (FloralisiaCompat.isEnabled()) {
            this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION,
                    Feature.RANDOM_PATCH.configure(new RandomPatchFeatureConfig.Builder(new DeferredBlockStateProvider(FloralisiaCompat.cymbidium()),
                            SimpleBlockPlacer.INSTANCE).tries(24).build())
                            .decorate(Decorator.SPREAD_32_ABOVE.configure(NopeDecoratorConfig.INSTANCE))
                            .decorate(Decorator.HEIGHTMAP.configure(new HeightmapDecoratorConfig(Heightmap.Type.MOTION_BLOCKING)))
                            .spreadHorizontally()
                            .repeat(2));
        }

        DefaultBiomeFeatures.addForestFlowers(this.getGenerationSettings());

        BiomeHelper.addDefaultFeatures(this);
        BiomeHelper.addDefaultSpawns(this.getSpawnSettings());
        BiomeHelper.addTemperateSpawns(this.getSpawnSettings());
    }
}
