package com.bigdious.tamable_endermen.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class TELootGenerator extends LootTableProvider {

	public TELootGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
		super(output, Set.of(), List.of(
			new LootTableProvider.SubProviderEntry(TEEntityLootTables::new, LootContextParamSets.ENTITY)

		), provider);
	}
}
