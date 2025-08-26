package com.bigdious.tamable_endermen;

import com.bigdious.tamable_endermen.client.TEClientEvents;
import com.bigdious.tamable_endermen.data.*;
import com.bigdious.tamable_endermen.entity.TamedEnderman;
import com.bigdious.tamable_endermen.event.TEEvents;
import com.bigdious.tamable_endermen.init.TEEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

@Mod(TamableEndermen.MODID)
public class TamableEndermen {
	public static final String MODID = "tamable_endermen";

	public static final Logger LOGGER = LogManager.getLogger();

	public TamableEndermen(IEventBus bus, Dist dist) {
		TEEntities.ENTITIES.register(bus);
		TEEntities.SPAWN_EGGS.register(bus);
		ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> ConfigurationScreen::new);
		bus.addListener(this::registerPacket);
		bus.addListener(this::gatherData);
		TEEvents.initEvents(bus);
		if (dist.isClient()) {
			TEClientEvents.initEvents(bus);
		}
	}

	private void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		DatapackBuiltinEntriesProvider datapackProvider = new TERegistryDataGenerator(packOutput, event.getLookupProvider());
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

		boolean isClient = event.includeClient();
		generator.addProvider(isClient, new TELangGenerator(packOutput));

		boolean isServer = event.includeServer();

		TERegistryDataGenerator registryDataGenerator = new TERegistryDataGenerator(packOutput, datapackProvider.getRegistryProvider());
		var lookupProvider = registryDataGenerator.getRegistryProvider();
		var blocktags = new BlockTagGenerator(packOutput, lookupProvider, existingFileHelper);
		generator.addProvider(isServer, new TELootGenerator(packOutput, lookupProvider));
		generator.addProvider(isServer, blocktags);
		generator.addProvider(isServer, new ItemTagGenerator(packOutput, lookupProvider, blocktags.contentsGetter(), existingFileHelper));
	}

	public void registerPacket(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar(MODID).versioned("1.0.1");
	}

	public static ResourceLocation prefix(String name) {
		return ResourceLocation.fromNamespaceAndPath(MODID, name.toLowerCase(Locale.ROOT));
	}


}
