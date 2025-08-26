package com.bigdious.tamable_endermen.data;

import com.bigdious.tamable_endermen.init.TEEntities;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class TEEntityLootTables extends EntityLootSubProvider {
	protected TEEntityLootTables(HolderLookup.Provider provider) {
		super(FeatureFlags.REGISTRY.allFlags(), provider);
	}

	@Override
	public void generate() {
		add(TEEntities.TAMED_ENDERMAN.get(),
			LootTable.lootTable()
				.withPool(LootPool.lootPool()
					.setRolls(ConstantValue.exactly(1.0F))
					.add(LootItem.lootTableItem(Items.ENDER_PEARL))
					.apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))));
	}

	@Override
	public Stream<EntityType<?>> getKnownEntityTypes() {
		return TEEntities.ENTITIES.getEntries().stream().map(Supplier::get);
	}
}
