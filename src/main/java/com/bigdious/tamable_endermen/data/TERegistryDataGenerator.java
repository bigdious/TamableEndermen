package com.bigdious.tamable_endermen.data;

import com.bigdious.tamable_endermen.TamableEndermen;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class TERegistryDataGenerator extends DatapackBuiltinEntriesProvider {
	private static final RegistrySetBuilder REGISTRIES = new RegistrySetBuilder();

	public TERegistryDataGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, REGISTRIES, Set.of("minecraft", TamableEndermen.MODID));
	}
}
