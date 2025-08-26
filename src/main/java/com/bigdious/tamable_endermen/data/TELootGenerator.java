package main.java.com.bigdious.dn.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DNLootGenerator extends LootTableProvider {

	public DNLootGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
		super(output, Set.of(), List.of(
				new SubProviderEntry(DNBlockLootTables::new, LootContextParamSets.BLOCK)

		), provider);
	}
}
