package main.java.com.bigdious.dn.data;

import com.bigdious.tamable_endermen.DN;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DNRegistryDataGenerator extends DatapackBuiltinEntriesProvider {
	private static final RegistrySetBuilder REGISTRIES = new RegistrySetBuilder();

	public DNRegistryDataGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, REGISTRIES, Set.of("minecraft", DN.MODID));
	}
}
