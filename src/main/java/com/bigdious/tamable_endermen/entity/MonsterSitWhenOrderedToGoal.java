package com.bigdious.tamable_endermen.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class MonsterSitWhenOrderedToGoal extends Goal{
	public final TamedEnderman mob;

	public MonsterSitWhenOrderedToGoal(TamedEnderman mob) {
		this.mob = mob;
		this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
	}

	public boolean canContinueToUse() {
		return this.mob.isOrderedToSit();
	}

	public boolean canUse() {
		if (this.mob.isInWaterOrBubble()) {
			return false;
		} else if (!this.mob.onGround()) {
			return false;
		} else {
			LivingEntity livingentity = this.mob.getOwner();
			if (livingentity == null) {
				return true;
			} else {
				return (!(this.mob.distanceToSqr(livingentity) < (double) 144.0F) || livingentity.getLastHurtByMob() == null) && this.mob.isOrderedToSit();
			}
		}
	}

	public void start() {
		this.mob.getNavigation().stop();
	}

}
