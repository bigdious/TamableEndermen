package com.bigdious.tamable_endermen.client;

import com.bigdious.tamable_endermen.init.TEEntities;
import net.minecraft.client.model.EndermanModel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class TEClientEvents {

	public static void initEvents(IEventBus bus) {
		bus.addListener(TEClientEvents::registerEntityLayers);
		bus.addListener(TEClientEvents::registerEntityRenderers);
	}

	private static void registerEntityLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(TEModelLayers.TAMED_ENDERMAN, EndermanModel::createBodyLayer);
	}
	private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(TEEntities.TAMED_ENDERMAN.get(), TamedEndermanRenderer::new);
	}
}
