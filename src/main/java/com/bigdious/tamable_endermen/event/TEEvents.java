package com.bigdious.tamable_endermen.event;

import com.bigdious.tamable_endermen.TamableEndermen;
import com.bigdious.tamable_endermen.entity.TamedEnderman;
import com.bigdious.tamable_endermen.init.TEEntities;
import com.bigdious.tamable_endermen.init.TETags;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class TEEvents {

	public static void initEvents(IEventBus bus) {
		bus.addListener(TEEvents::registerAttributes);
		NeoForge.EVENT_BUS.addListener(TEEvents::tameEnderman);
	}

	private static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(TEEntities.TAMED_ENDERMAN.get(), TamedEnderman.attributes().build());
	}

	public static void tameEnderman(PlayerInteractEvent.EntityInteract event){
		if (event.getItemStack().is(TETags.Items.ENDERMAN_FOOD) && event.getTarget().getType() == EntityType.ENDERMAN) {
			if (event.getEntity().getRandom().nextInt(3) == 1) {
				TamedEnderman.tryTaming(event.getEntity().level(), event.getTarget(), event.getEntity(), event.getItemStack());
			} else {
				TamedEnderman.failureParticles(event.getEntity().level(), event.getTarget());
			}
		}
	}
}
