package com.bigdious.tamable_endermen.client;

import com.bigdious.tamable_endermen.TamableEndermen;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class TEModelLayers {
	public static final ModelLayerLocation TAMED_ENDERMAN = register("tamed_enderman");

	private static ModelLayerLocation register(String name) {
		return register(name, "main");
	}

	private static ModelLayerLocation register(String name, String type) {
		return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(TamableEndermen.MODID, name), type);
	}
}
