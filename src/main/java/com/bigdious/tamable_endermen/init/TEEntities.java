package com.bigdious.tamable_endermen.init;

import com.bigdious.tamable_endermen.TamableEndermen;
import com.bigdious.tamable_endermen.entity.TamedEnderman;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TEEntities {
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, TamableEndermen.MODID);
	public static final DeferredRegister<Item> SPAWN_EGGS = DeferredRegister.create(Registries.ITEM, TamableEndermen.MODID);

	public static final DeferredHolder<EntityType<?>, EntityType<TamedEnderman>> TAMED_ENDERMAN = register(ResourceLocation.fromNamespaceAndPath(TamableEndermen.MODID, "tamed_enderman"), EntityType.Builder.of(TamedEnderman::new, MobCategory.MONSTER).sized(0.8F, 3.0F), 0x000000, 0x8b0000);

	private static <T extends Mob> DeferredHolder<EntityType<?>, EntityType<T>> register(ResourceLocation id, EntityType.Builder<T> builder, int primary, int secondary) {
		DeferredHolder<EntityType<?>, EntityType<T>> ret = ENTITIES.register(id.getPath(), () -> builder.build(id.toString()));
		SPAWN_EGGS.register(id.getPath() + "_spawn_egg", () -> new DeferredSpawnEggItem(ret, primary, secondary, new Item.Properties()));
		return ret;
	}
}
