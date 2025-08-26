package main.java.com.bigdious.dn.init;

import com.bigdious.tamable_endermen.DN;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class DNTags {
	public static class Items {
		public static final TagKey<Item> ITEM_FRAMES = create("item_frames");
		private static TagKey<Item> create(String name) {
			return ItemTags.create(ResourceLocation.fromNamespaceAndPath(DN.MODID, name));
		}
	}
}
