package main.java.com.bigdious.dn;

import com.bigdious.dn.client.DNClientEvents;
import com.bigdious.dn.config.ConfigSetup;
import com.bigdious.dn.data.*;
import com.bigdious.dn.init.DNBlockEntities;
import com.bigdious.dn.init.DNBlocks;
import com.bigdious.dn.init.DNItems;
import com.bigdious.dn.init.DNTab;
import com.bigdious.dn.network.SyncCommonConfigPacket;
import com.bigdious.dn.network.SyncDNConfigPacket;
import com.google.common.reflect.Reflection;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

@Mod(DN.MODID)
public class DN {
	public static final String MODID = "dn";

	public static final Logger LOGGER = LogManager.getLogger();

	public DN(IEventBus bus, Dist dist) {
		Reflection.initialize(ConfigSetup.class);
		ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> ConfigurationScreen::new);
		bus.addListener(ConfigSetup::loadConfigs);
		bus.addListener(ConfigSetup::reloadConfigs);
		NeoForge.EVENT_BUS.addListener(ConfigSetup::syncConfigOnLogin);
		bus.addListener(this::registerPacket);
		bus.addListener(DNTab::addToTabs);

		DNBlockEntities.BLOCK_ENTITIES.register(bus);
		DNBlocks.BLOCKS.register(bus);
		DNItems.ITEMS.register(bus);

		if (dist.isClient()) {
			DNClientEvents.initEvents(bus);
		}

		bus.addListener(this::gatherData);
	}

	private void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		DatapackBuiltinEntriesProvider datapackProvider = new DNRegistryDataGenerator(packOutput, event.getLookupProvider());
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

		boolean isClient = event.includeClient();
		generator.addProvider(isClient, new DNBlockModelGenerator(packOutput, existingFileHelper));
		generator.addProvider(isClient, new DNItemModelGenerator(packOutput, existingFileHelper));
		generator.addProvider(isClient, new DNLangGenerator(packOutput));

		boolean isServer = event.includeServer();

		DNRegistryDataGenerator registryDataGenerator = new DNRegistryDataGenerator(packOutput, datapackProvider.getRegistryProvider());
		var lookupProvider = registryDataGenerator.getRegistryProvider();
		var blocktags = new DNBlockTagGenerator(packOutput, lookupProvider, existingFileHelper);
		generator.addProvider(isServer, blocktags);
		generator.addProvider(isServer, new DNItemTagGenerator(packOutput, lookupProvider, blocktags.contentsGetter(), existingFileHelper));
		generator.addProvider(isServer, new DNLootGenerator(packOutput, lookupProvider));
		generator.addProvider(isServer, new DNCraftingGenerator(packOutput, lookupProvider));
	}

	public void registerPacket(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar(MODID).versioned("1.0.1");
		registrar.playToClient(SyncCommonConfigPacket.TYPE, SyncCommonConfigPacket.STREAM_CODEC, SyncCommonConfigPacket::handle);
	}
	public static ResourceLocation prefix(String name) {
		return ResourceLocation.fromNamespaceAndPath(MODID, name.toLowerCase(Locale.ROOT));
	}

}
