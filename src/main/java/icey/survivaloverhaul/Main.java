package icey.survivaloverhaul;

import icey.survivaloverhaul.api.item.ItemCanteen;
import icey.survivaloverhaul.api.temperature.DynamicModifierBase;
import icey.survivaloverhaul.api.temperature.ModifierBase;
import icey.survivaloverhaul.api.temperature.TemperatureEnum;
import icey.survivaloverhaul.api.temperature.TemperatureUtil;
import icey.survivaloverhaul.api.thirst.ThirstUtil;
import icey.survivaloverhaul.client.TooltipHandler;
import icey.survivaloverhaul.client.hud.DebugGUI;
import icey.survivaloverhaul.client.hud.ThirstGUI;
import icey.survivaloverhaul.common.capability.temperature.TemperatureCapability;
import icey.survivaloverhaul.common.capability.temperature.TemperatureStorage;
import icey.survivaloverhaul.common.capability.thirst.ThirstCapability;
import icey.survivaloverhaul.common.capability.thirst.ThirstHandler;
import icey.survivaloverhaul.common.capability.thirst.ThirstStorage;
import icey.survivaloverhaul.common.capability.wetness.WetnessCapability;
import icey.survivaloverhaul.common.capability.wetness.WetnessStorage;
import icey.survivaloverhaul.common.compat.sereneseasons.SereneSeasonsModifier;
import icey.survivaloverhaul.common.network.Network;
import icey.survivaloverhaul.common.registry.*;
import icey.survivaloverhaul.common.util.WorldUtil;
import icey.survivaloverhaul.config.Config;
import icey.survivaloverhaul.config.json.JsonConfigRegistration;
import icey.survivaloverhaul.internal.TemperatureUtilInternal;
import icey.survivaloverhaul.internal.ThirstUtilInternal;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mod(Main.MOD_ID)
@Mod.EventBusSubscriber(modid = Main.MOD_ID)
public class Main {
    public static final String MOD_ID = "survivaloverhaul";
    public static final Logger LOGGER = LogManager.getLogger();

    /**
     * Serene Seasons and Better Weather both add their own seasons system,
     * so we'll probably want to integrate those with the temperature/climbing
     * system, i.e. making it so that winter is colder, summer is hotter,
     * and perhaps you're more prone to slipping while climbing in the winter.
     */
    public static boolean betterWeatherLoaded = false;
    public static boolean sereneSeasonsLoaded = false;
    /**
     * Since my mod and Survive both do very similar things, it might be
     * a good idea to let the user know that should probably only choose
     * one or the other unless they know what they're doing.
     * <p>
     * Also it should only show this type of warning once so that we don't
     * annoy the player if they decide to go through with it.
     */
    public static boolean surviveLoaded = false;
    public static boolean curiosLoaded = false;
    public static Path configPath = FMLPaths.CONFIGDIR.get();
    public static Path modConfigPath = Paths.get(configPath.toAbsolutePath().toString(), "survivaloverhaul");
    public static Path modConfigJsons = Paths.get(modConfigPath.toString(), "json");
    public static Path ssConfigPath = Paths.get(configPath.toAbsolutePath().toString(), "sereneseasons");
    public static ForgeRegistry<ModifierBase> MODIFIERS;
    public static ForgeRegistry<DynamicModifierBase> DYNAMIC_MODIFIERS;

    public Main() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        modBus.addListener(this::onClientSetup);
        modBus.addListener(this::onCommonSetup);
        modBus.addListener(this::onModConfigEvent);
        modBus.addListener(this::buildRegistries);

        BlockRegistry.BLOCKS.register(modBus);
        CraftingRegistry.RECIPES.register(modBus);

        ItemRegistry.ITEMS.register(modBus);
        EffectRegistry.EFFECTS.register(modBus);
        EffectRegistry.POTIONS.register(modBus);
        EnchantRegistry.ENCHANTS.register(modBus);


        TemperatureModifierRegistry.MODIFIERS.register(modBus);
        TemperatureModifierRegistry.DYNAMIC_MODIFIERS.register(modBus);

        forgeBus.addListener(this::serverStarted);
        forgeBus.addListener(this::reloadListener);

        MinecraftForge.EVENT_BUS.register(this);

        Config.register();

        Config.Baked.bakeClient();
        Config.Baked.bakeCommon();

        ThirstUtil.internal = new ThirstUtilInternal();
        TemperatureUtil.internal = new TemperatureUtilInternal();
        modCompat();
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(ThirstCapability.class, new ThirstStorage(), ThirstCapability::new);
        CapabilityManager.INSTANCE.register(TemperatureCapability.class, new TemperatureStorage(), TemperatureCapability::new);
        CapabilityManager.INSTANCE.register(WetnessCapability.class, new WetnessStorage(), WetnessCapability::new);


        MinecraftForge.EVENT_BUS.register(new ThirstHandler());
        MinecraftForge.EVENT_BUS.register(new EventHandler());

        Network.register();
        //event.enqueueWork(EffectRegistry::registerPotionRecipes);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        RenderTypeLookup.setRenderLayer(BlockRegistry.COOLING_COIL.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.HEATING_COIL.get(), RenderType.getCutout());

        MinecraftForge.EVENT_BUS.register(new ThirstGUI());
        MinecraftForge.EVENT_BUS.register(new DebugGUI());

        MinecraftForge.EVENT_BUS.register(new TooltipHandler());

        event.enqueueWork(() -> {
            DistExecutor.safeRunWhenOn(Dist.CLIENT, Main::clientModelSetup);

            ItemModelsProperties.registerProperty(ItemRegistry.CANTEEN.get(), new ResourceLocation(MOD_ID, "empty"), new IItemPropertyGetter() {
                @OnlyIn(Dist.CLIENT)
                @Override
                public float call(@Nonnull ItemStack stack, ClientWorld clientWorld, LivingEntity entity) {
                    Item item = stack.getItem();

                    if (item instanceof ItemCanteen) {
                        return ((ItemCanteen) item).isCanteenEmpty(stack) ? 1 : 0;
                    }
                    return 1;
                }
            });
        });
    }
    private static DistExecutor.SafeRunnable clientModelSetup() {
        return new DistExecutor.SafeRunnable() {
            private static final long serialVersionUID = 1L;

            @Override
            public void run() {
                ItemModelsProperties.registerProperty(ItemRegistry.THERMOMETER.get(), new ResourceLocation("temperature"), new IItemPropertyGetter() {
                            @OnlyIn(Dist.CLIENT)
                            @Override
                            public float call(ItemStack stack, ClientWorld clientWorld, LivingEntity entity) {
                                World world = clientWorld;
                                Entity holder = entity != null ? entity : stack.getItemFrame();

                                if (world == null && holder != null) {
                                    world = holder.world;
                                }

                                if (world == null) {
                                    return 0.5f;
                                } else {
                                    try {
                                        double d;

                                        int temperature = WorldUtil.calculateClientWorldEntityTemperature(world, holder);
                                        d = (float) temperature / (float) TemperatureEnum.HEAT_STROKE.getUpperBound();

                                        return MathHelper.positiveModulo((float) d, 1.0333333f);
                                    }
                                    catch (NullPointerException e) {
                                        return 0.5f;
                                    }

                                }
                            }
                        }
                );
            }
        };
    }

    //TODO MOVE
    private void serverStarted(final FMLServerStartedEvent event) {
        if (sereneSeasonsLoaded)
            SereneSeasonsModifier.prepareBiomeIdentities();
    }

    //TODO MOVE
    private void modCompat() {
        sereneSeasonsLoaded = ModList.get().isLoaded("sereneseasons");
        curiosLoaded = ModList.get().isLoaded("curios");
        surviveLoaded = ModList.get().isLoaded("survive");

        if (sereneSeasonsLoaded)
            LOGGER.debug("Serene Seasons is loaded, enabling compatability");

        if (curiosLoaded)
            LOGGER.debug("Curios is loaded, enabling compatability");

        if (surviveLoaded)
            LOGGER.debug("Survive is loaded, I hope you know what you're doing");
    }

    private void reloadListener(final AddReloadListenerEvent event) {
        event.addListener(new ReloadListener<Void>() {
                              @Nonnull
                              @ParametersAreNonnullByDefault
                              @Override
                              protected Void prepare(IResourceManager manager, IProfiler profiler) {
                                  JsonConfigRegistration.clearContainers();
                                  return null;
                              }

                              @ParametersAreNonnullByDefault
                              @Override
                              protected void apply(Void objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
                                  Config.Baked.bakeCommon();
                                  JsonConfigRegistration.init(modConfigJsons.toFile());
                              }

                          }
        );
    }

    //TODO MOVE
    private void onModConfigEvent(ModConfig.ModConfigEvent event) {
        final ModConfig config = event.getConfig();

        // Since client config is not shared, we want it to update instantly whenever it's saved
        if (config.getSpec() == Config.CLIENT_SPEC)
            Config.Baked.bakeClient();
    }

    // Create registries for modifiers and dynamic modifiers
    private void buildRegistries(RegistryEvent.NewRegistry event) {
        RegistryBuilder<ModifierBase> modifierBuilder = new RegistryBuilder<>();
        modifierBuilder.setName(new ResourceLocation(Main.MOD_ID, "modifiers"));
        modifierBuilder.setType(ModifierBase.class);
        MODIFIERS = (ForgeRegistry<ModifierBase>) modifierBuilder.create();

        RegistryBuilder<DynamicModifierBase> dynamicModifierBuilder = new RegistryBuilder<>();
        dynamicModifierBuilder.setName(new ResourceLocation(Main.MOD_ID, "dynamic_modifiers"));
        dynamicModifierBuilder.setType(DynamicModifierBase.class);
        DYNAMIC_MODIFIERS = (ForgeRegistry<DynamicModifierBase>) dynamicModifierBuilder.create();
    }
}
