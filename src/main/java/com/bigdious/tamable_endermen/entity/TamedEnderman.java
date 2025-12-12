package com.bigdious.tamable_endermen.entity;

import com.bigdious.tamable_endermen.init.TEEntities;
import com.bigdious.tamable_endermen.init.TETags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Unit;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class TamedEnderman extends EnderMan implements OwnableEntity {

	@Nullable
	private UUID OwnerUUID;
	public boolean orderedToSit;
	private static final EntityDataAccessor<Integer> DATA_COLLAR_COLOR;
	protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID;
	protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID;

	public TamedEnderman(EntityType<? extends EnderMan> entityType, Level level) {
		super(entityType, level);
	}

	public static AttributeSupplier.Builder attributes() {
		return Monster.createMonsterAttributes()
			.add(Attributes.MAX_HEALTH, 40.0F)
			.add(Attributes.MOVEMENT_SPEED, 0.3F)
			.add(Attributes.ATTACK_DAMAGE, 7.0F)
			.add(Attributes.FOLLOW_RANGE, 64.0F)
			.add(Attributes.STEP_HEIGHT, 1.0F);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(2, new MonsterFollowOwnerGoal(this, 1.0D, 5.0F, 2.0F));
		this.goalSelector.addGoal(1, new TamedEndermanFreezeWhenLookedAt(this));
		this.goalSelector.addGoal(2, new MonsterSitWhenOrderedToGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0F, true));
		this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, (double)1.0F, 0.0F));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(10, new EndermanLeaveBlockGoal(this));
		this.goalSelector.addGoal(11, new EndermanTakeBlockGoal(this));
		this.targetSelector.addGoal(1, new TamedEndermanLookForPlayerGoal(this, this::isAngryAt));
		this.targetSelector.addGoal(1, new MonsterOwnerHurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new MonsterOwnerHurtTargetGoal(this));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Endermite.class, true, false));
		this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));
	}

	@Override
	public void tick() {
		if (this.firstTick) {
			succesParticles(this.level(), this);
		}
		super.tick();
	}

	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_FLAGS_ID, (byte)0);
		builder.define(DATA_OWNERUUID_ID, Optional.empty());
		builder.define(DATA_COLLAR_COLOR, DyeColor.RED.getId());
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		BlockState blockstate = this.getCarriedBlock();
		if (blockstate != null) {
			compound.put("carriedBlockState", NbtUtils.writeBlockState(blockstate));
		}
		if (this.OwnerUUID != null) {
			compound.putUUID("OwnerUUID", this.OwnerUUID);
		}
		compound.putByte("CollarColor", (byte)this.getCollarColor().getId());
		compound.putBoolean("Sitting", this.orderedToSit);
		this.addPersistentAngerSaveData(compound);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		BlockState blockstate = null;
		if (compound.contains("carriedBlockState", 10)) {
			blockstate = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), compound.getCompound("carriedBlockState"));
			if (blockstate.isAir()) {
				blockstate = null;
			}
		}
		if (compound.contains("CollearColor", 99)) {
			this.setCollarColor(DyeColor.byId(compound.getInt("CollarColor")));
		}
		if (compound.contains("OwnerUUID")) {
			this.OwnerUUID = compound.getUUID("OwnerUUID");
		}
		this.orderedToSit = compound.getBoolean("Sitting");
		this.setInSittingPose(this.orderedToSit);
		this.setCarriedBlock(blockstate);
		this.readPersistentAngerSaveData(this.level(), compound);
	}



	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		Item item = itemstack.getItem();
		if (!this.level().isClientSide || itemstack.is(TETags.Items.ENDERMAN_FOOD) ) {
			if (itemstack.is(TETags.Items.ENDERMAN_FOOD)  && this.getHealth() < this.getMaxHealth()) {
				this.heal(3.0F);
				itemstack.consume(1, player);
				this.gameEvent(GameEvent.EAT);
				return InteractionResult.sidedSuccess(this.level().isClientSide());
			} else {
				if (item instanceof DyeItem) {
					DyeItem dyeitem = (DyeItem) item;
					if (this.isOwnedBy(player)) {
						DyeColor dyecolor = dyeitem.getDyeColor();
						if (dyecolor != this.getCollarColor()) {
							this.setCollarColor(dyecolor);
							itemstack.consume(1, player);
							return InteractionResult.SUCCESS;
						}

						return super.mobInteract(player, hand);
					}
				}
			}
		}
		boolean flag = this.isOwnedBy(player) || itemstack.is(TETags.Items.ENDERMAN_FOOD) ;
		return flag ? InteractionResult.CONSUME : InteractionResult.PASS;
	}

	public DyeColor getCollarColor() {
		return DyeColor.byId(this.entityData.get(DATA_COLLAR_COLOR));
	}

	private void setCollarColor(DyeColor collarColor) {
		this.entityData.set(DATA_COLLAR_COLOR, collarColor.getId());
	}

	boolean isNotOwnerLookingAtMe(Player player) {
		ItemStack itemstack = player.getInventory().armor.get(3);
		if (CommonHooks.shouldSuppressEnderManAnger(this, player, itemstack) || isOwnedBy(player)) {
			return false;
		} else {
			Vec3 vec3 = player.getViewVector(1.0F).normalize();
			Vec3 vec31 = new Vec3(this.getX() - player.getX(), this.getEyeY() - player.getEyeY(), this.getZ() - player.getZ());
			double d0 = vec31.length();
			vec31 = vec31.normalize();
			double d1 = vec3.dot(vec31);
			return d1 > (double)1.0F - 0.025 / d0 ? player.hasLineOfSight(this) : false;
		}
	}

	@Override
	public void die(DamageSource cause) {
		Component deathMessage = this.getCombatTracker().getDeathMessage();
		super.die(cause);
		if (this.dead && !this.level().isClientSide && this.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && this.getOwner() instanceof ServerPlayer) {
			this.getOwner().sendSystemMessage(deathMessage);
		}

	}

	@Override
	public boolean removeWhenFarAway(double dist) {
		return false;
	}

	@Override
	public @Nullable UUID getOwnerUUID() {
		return this.OwnerUUID;
	}
	public boolean canBeLeashed() {
		return true;
	}

	public boolean isOwnedBy(LivingEntity entity) {
		return entity == this.getOwner();
	}

	public UUID setOwnerUUID(UUID uuid) {
		return this.OwnerUUID = uuid;
	}

	public boolean isOrderedToSit() {
		return this.orderedToSit;
	}

	public void setInSittingPose(boolean sitting) {
		byte b0 = this.entityData.get(DATA_FLAGS_ID);
		if (sitting) {
			this.entityData.set(DATA_FLAGS_ID, (byte)(b0 | 1));
		} else {
			this.entityData.set(DATA_FLAGS_ID, (byte)(b0 & -2));
		}

	}

	public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
		return true;
	}

	public final boolean unableToMoveToOwner() {
		return this.isPassenger() || this.mayBeLeashed() || this.getOwner() != null && this.getOwner().isSpectator();
	}
	public boolean shouldTryTeleportToOwner() {
		LivingEntity livingentity = this.getOwner();
		return livingentity != null && this.distanceToSqr(this.getOwner()) >= (double)144.0F;
	}
	public void tryToTeleportToOwner() {
		LivingEntity livingentity = this.getOwner();
		if (livingentity != null) {
			this.teleportToAroundBlockPos(livingentity.blockPosition());
		}

	}
	private void teleportToAroundBlockPos(BlockPos pos) {
		for(int i = 0; i < 10; ++i) {
			int j = this.random.nextIntBetweenInclusive(-3, 3);
			int k = this.random.nextIntBetweenInclusive(-3, 3);
			if (Math.abs(j) >= 2 || Math.abs(k) >= 2) {
				int l = this.random.nextIntBetweenInclusive(-1, 1);
				if (this.maybeTeleportTo(pos.getX() + j, pos.getY() + l, pos.getZ() + k)) {
					return;
				}
			}
		}

	}
	@Override
	protected boolean teleport() {
		if (!this.level().isClientSide() && !this.isLeashed() && this.isAlive()) {
			double d0 = this.getX() + (this.random.nextDouble() - (double)0.5F) * (double)64.0F;
			double d1 = this.getY() + (double)(this.random.nextInt(64) - 32);
			double d2 = this.getZ() + (this.random.nextDouble() - (double)0.5F) * (double)64.0F;
			return this.teleport(d0, d1, d2);
		} else {
			return false;
		}
	}

	private boolean maybeTeleportTo(int x, int y, int z) {
		if (!this.canTeleportTo(new BlockPos(x, y, z))) {
			return false;
		} else {
			this.moveTo((double)x + (double)0.5F, (double)y, (double)z + (double)0.5F, this.getYRot(), this.getXRot());
			this.navigation.stop();
			return true;
		}
	}
	private boolean canTeleportTo(BlockPos pos) {
		PathType pathtype = WalkNodeEvaluator.getPathTypeStatic(this, pos);
		if (pathtype != PathType.WALKABLE) {
			return false;
		} else {
			BlockState blockstate = this.level().getBlockState(pos.below());
			if (!this.canFlyToOwner() && blockstate.getBlock() instanceof LeavesBlock) {
				return false;
			} else {
				BlockPos blockpos = pos.subtract(this.blockPosition());
				return this.level().noCollision(this, this.getBoundingBox().move(blockpos));
			}
		}
	}
	protected boolean canFlyToOwner() {
		return false;
	}



	public static class TamedEndermanLookForPlayerGoal extends NearestAttackableTargetGoal<Player> {
		private final TamedEnderman enderman;
		@javax.annotation.Nullable
		private Player pendingTarget;
		private int aggroTime;
		private int teleportTime;
		private final TargetingConditions startAggroTargetConditions;
		private final TargetingConditions continueAggroTargetConditions = TargetingConditions.forCombat().ignoreLineOfSight();
		private final Predicate<LivingEntity> isAngerInducing;

		public TamedEndermanLookForPlayerGoal(TamedEnderman enderman, @javax.annotation.Nullable Predicate<LivingEntity> selectionPredicate) {
			super(enderman, Player.class, 10, false, false, selectionPredicate);
			this.enderman = enderman;
			this.isAngerInducing = (p_325811_) -> (enderman.isNotOwnerLookingAtMe((Player)p_325811_) || enderman.isAngryAt(p_325811_)) && !enderman.hasIndirectPassenger(p_325811_);
			this.startAggroTargetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(this.isAngerInducing);
		}

		public boolean canUse() {
			this.pendingTarget = this.enderman.level().getNearestPlayer(this.startAggroTargetConditions, this.enderman);
			return this.pendingTarget != null;
		}

		public void start() {
			this.aggroTime = this.adjustedTickDelay(5);
			this.teleportTime = 0;
			this.enderman.setBeingStaredAt();
		}

		public void stop() {
			this.pendingTarget = null;
			super.stop();
		}

		public boolean canContinueToUse() {
			if (this.pendingTarget != null) {
				if (!this.isAngerInducing.test(this.pendingTarget)) {
					return false;
				} else {
					this.enderman.lookAt(this.pendingTarget, 10.0F, 10.0F);
					return true;
				}
			} else {
				if (this.target != null) {
					if (this.enderman.hasIndirectPassenger(this.target)) {
						return false;
					}

					if (this.continueAggroTargetConditions.test(this.enderman, this.target)) {
						return true;
					}
				}

				return super.canContinueToUse();
			}
		}

		public void tick() {
			if (this.enderman.getTarget() == null) {
				super.setTarget((LivingEntity)null);
			}

			if (this.pendingTarget != null) {
				if (--this.aggroTime <= 0) {
					this.target = this.pendingTarget;
					this.pendingTarget = null;
					super.start();
				}
			} else {
				if (this.target != null && !this.enderman.isPassenger()) {
					if (this.enderman.isNotOwnerLookingAtMe((Player)this.target)) {
						if (this.target.distanceToSqr(this.enderman) < (double)16.0F) {
							this.enderman.teleport();
						}

						this.teleportTime = 0;
					} else if (this.target.distanceToSqr(this.enderman) > (double)256.0F && this.teleportTime++ >= this.adjustedTickDelay(30) && this.enderman.teleportTowards(this.target)) {
						this.teleportTime = 0;
					}
				}

				super.tick();
			}

		}
	}

	boolean teleportTowards(Entity target) {
		Vec3 vec3 = new Vec3(this.getX() - target.getX(), this.getY((double)0.5F) - target.getEyeY(), this.getZ() - target.getZ());
		vec3 = vec3.normalize();
		double d0 = (double)16.0F;
		double d1 = this.getX() + (this.random.nextDouble() - (double)0.5F) * (double)8.0F - vec3.x * (double)16.0F;
		double d2 = this.getY() + (double)(this.random.nextInt(16) - 8) - vec3.y * (double)16.0F;
		double d3 = this.getZ() + (this.random.nextDouble() - (double)0.5F) * (double)8.0F - vec3.z * (double)16.0F;
		return this.teleport(d1, d2, d3);
	}

	private boolean teleport(double x, double y, double z) {
		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(x, y, z);

		while(blockpos$mutableblockpos.getY() > this.level().getMinBuildHeight() && !this.level().getBlockState(blockpos$mutableblockpos).blocksMotion()) {
			blockpos$mutableblockpos.move(Direction.DOWN);
		}

		BlockState blockstate = this.level().getBlockState(blockpos$mutableblockpos);
		boolean flag = blockstate.blocksMotion();
		boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);
		if (flag && !flag1) {
			EntityTeleportEvent.EnderEntity event = EventHooks.onEnderTeleport(this, x, y, z);
			if (event.isCanceled()) {
				return false;
			} else {
				Vec3 vec3 = this.position();
				boolean flag2 = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
				if (flag2) {
					this.level().gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(this));
					if (!this.isSilent()) {
						this.level().playSound(null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
						this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
					}
				}

				return flag2;
			}
		} else {
			return false;
		}
	}

	public static class TamedEndermanFreezeWhenLookedAt extends Goal {
		private final TamedEnderman enderman;
		@javax.annotation.Nullable
		private LivingEntity target;

		public TamedEndermanFreezeWhenLookedAt(TamedEnderman enderman) {
			this.enderman = enderman;
			this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
		}

		public boolean canUse() {
			this.target = this.enderman.getTarget();
			if (!(this.target instanceof Player)) {
				return false;
			} else {
				double d0 = this.target.distanceToSqr(this.enderman);
				return d0 > (double)256.0F ? false : this.enderman.isNotOwnerLookingAtMe((Player)this.target);
			}
		}

		public void start() {
			this.enderman.getNavigation().stop();
		}

		public void tick() {
			this.enderman.getLookControl().setLookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
		}
	}

	public static void tryTaming(Level level, Entity Target, LivingEntity Abuser, ItemStack food) {
		TamedEnderman tamedEnderman = TEEntities.TAMED_ENDERMAN.get().create(level);
		tamedEnderman.moveTo(Target.getX(), Target.getY(), Target.getZ());
		tamedEnderman.setOwnerUUID(Abuser.getUUID());
		level.addFreshEntity(tamedEnderman);
		if (tamedEnderman.isAddedToLevel()) {
			Target.remove(Entity.RemovalReason.DISCARDED);
		}
		food.shrink(1);
	}


	public static void succesParticles(Level level, Entity entity){
		for (int i = 0; i < 7; ++i) {
			double d0 = level.getRandom().nextGaussian() * 0.02;
			double d1 = level.getRandom().nextGaussian() * 0.02;
			double d2 = level.getRandom().nextGaussian() * 0.02;
			level.addParticle(ParticleTypes.HEART, entity.getRandomX(1.0F), entity.getRandomY() + (double) 0.5F, entity.getRandomZ(1.0F), d0, d1, d2);
		}
	}

	public static void failureParticles(Level level, Entity entity){
		for (int i = 0; i < 7; ++i) {
			double d0 = level.getRandom().nextGaussian() * 0.02;
			double d1 = level.getRandom().nextGaussian() * 0.02;
			double d2 = level.getRandom().nextGaussian() * 0.02;
			level.addParticle(ParticleTypes.SMOKE, entity.getRandomX(1.0F), entity.getRandomY() + (double) 0.5F, entity.getRandomZ(1.0F), d0, d1, d2);
		}
	}

	@Override
	protected boolean shouldDespawnInPeaceful() {
		return false;
	}

	static {
		DATA_FLAGS_ID = SynchedEntityData.defineId(TamedEnderman.class, EntityDataSerializers.BYTE);
		DATA_OWNERUUID_ID = SynchedEntityData.defineId(TamedEnderman.class, EntityDataSerializers.OPTIONAL_UUID);
		DATA_COLLAR_COLOR = SynchedEntityData.defineId(TamedEnderman.class, EntityDataSerializers.INT);
	}

}
