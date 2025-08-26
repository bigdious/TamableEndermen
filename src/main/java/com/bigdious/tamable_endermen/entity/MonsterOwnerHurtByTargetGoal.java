package com.bigdious.tamable_endermen.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class MonsterOwnerHurtByTargetGoal extends TargetGoal {
	private final TamedEnderman tameAnimal;
	private LivingEntity ownerLastHurtBy;
	private int timestamp;

	public MonsterOwnerHurtByTargetGoal(TamedEnderman tameAnimal) {
		super(tameAnimal, false);
		this.tameAnimal = tameAnimal;
		this.setFlags(EnumSet.of(Flag.TARGET));
	}

	public boolean canUse() {
		if (!this.tameAnimal.isOrderedToSit()) {
			LivingEntity livingentity = this.tameAnimal.getOwner();
			if (livingentity == null) {
				return false;
			} else {
				this.ownerLastHurtBy = livingentity.getLastHurtByMob();
				int i = livingentity.getLastHurtByMobTimestamp();
				return i != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT) && this.tameAnimal.wantsToAttack(this.ownerLastHurtBy, livingentity);
			}
		} else {
			return false;
		}
	}

	public void start() {
		this.mob.setTarget(this.ownerLastHurtBy);
		LivingEntity livingentity = this.tameAnimal.getOwner();
		if (livingentity != null) {
			this.timestamp = livingentity.getLastHurtByMobTimestamp();
		}

		super.start();
	}
}
