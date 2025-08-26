package com.bigdious.tamable_endermen.init;

import com.bigdious.tamable_endermen.TamableEndermen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class TETags {
	public static class Items {
		public static final TagKey<Item> ENDERMAN_FOOD = create("enderman_food");
		private static TagKey<Item> create(String name) {
			return ItemTags.create(ResourceLocation.fromNamespaceAndPath(TamableEndermen.MODID, name));
		}
	}
}
