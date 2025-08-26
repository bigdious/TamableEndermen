package main.java.com.bigdious.dn.data;

import com.bigdious.tamable_endermen.DN;
import com.bigdious.tamable_endermen.data.helper.DNLangProvider;
import com.bigdious.tamable_endermen.init.DNBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.DyeColor;
import org.apache.commons.lang3.text.WordUtils;


public class DNLangGenerator extends DNLangProvider {

	public DNLangGenerator(PackOutput output) {
		super(output);
	}
	@Override
	protected void addTranslations() {
		this.addBlock(DNBlocks.DISPLAY_NOTCH, "Display Notch");
		this.addBlock(DNBlocks.INVISIBLE_DISPLAY_NOTCH, "Invisible Display Notch");
		for (DyeColor color : DyeColor.values()) {
			if (color != DyeColor.BLACK) {
				this.add("block.dn." + color.getName() + "_display_notch", WordUtils.capitalize(color.getName().replace('_', ' ')) + " Display Notch");
			}
		}
		this.add("dn.configuration.title", "Display Notches Config");
		this.add("dn.configuration.section.obtrophies.common.toml", "Common Settings");
		this.add("dn.configuration.section.obtrophies.common.toml.title", "Common Settings");

		this.add("config.dn.spinning_source.signal", "Redstone Signal");
		this.add("config.dn.spinning_source.torch_item", "Redstone Torch Right-click");

		//this is not in order on purpose
		this.add("tooltip.dn.hidden", "[Hold Shift for Usages]");
		this.add("tooltip.dn.start", "Once an Item is inserted, you can manipulate it:");
		this.add("tooltip.dn.pickaxe", " - Rotate the item with a Pickaxe");
		this.add("tooltip.dn.axe", " - Flip the item between horizontal or vertical with an Axe");
		this.add("tooltip.dn.glow_ink_sac", " - Make the item glow with a Glow Inc Sac");
		this.add("tooltip.dn.dye", " - Color the notch with Dyes");
		this.add("tooltip.dn.phantom_membrane", " - Make the notch non-solid with a Phantom Membrane");
		this.add("tooltip.dn.tripwire_hook", " - Lock the notch with a Tripwire Hook");
		this.add("tooltip.dn.glass", " - Make the notch invisible with a Glass Block");
		this.add("tooltip.dn.signal", " - Make the item continuously rotate by powering it with a Redstone Signal");
		this.add("tooltip.dn.shovel", " - Change the item's elevation using a Shovel");
		this.add("tooltip.dn.redstone_torch", " - Make the item continuously rotate by using a Redstone Torch");
	}
}
