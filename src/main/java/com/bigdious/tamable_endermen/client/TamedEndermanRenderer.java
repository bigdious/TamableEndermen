package com.bigdious.tamable_endermen.client;

import com.bigdious.tamable_endermen.TamableEndermen;
import com.bigdious.tamable_endermen.entity.TamedEnderman;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EnderEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class TamedEndermanRenderer extends MobRenderer<TamedEnderman, EndermanModel<TamedEnderman>> {

	protected static final ResourceLocation TEXTURE = TamableEndermen.prefix("textures/entity/tamed_enderman.png");
	private final RandomSource random = RandomSource.create();

	public TamedEndermanRenderer(EntityRendererProvider.Context context) {
		super(context, new EndermanModel<>(context.bakeLayer(TEModelLayers.TAMED_ENDERMAN)),  0.8F);
		this.addLayer(new EndermanCollarLayer(this));
		this.addLayer(new EnderEyesLayer<>(this));
		this.addLayer(new TamedCarriedBlockLayer(this, context.getBlockRenderDispatcher()));
	}

	public void render(TamedEnderman entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		BlockState blockstate = entity.getCarriedBlock();
		EndermanModel<TamedEnderman> endermanmodel = this.getModel();
		endermanmodel.carrying = blockstate != null;
		endermanmodel.creepy = entity.isCreepy();
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}

	public Vec3 getRenderOffset(TamedEnderman entity, float partialTicks) {
		if (entity.isCreepy()) {
			double d0 = 0.02 * (double)entity.getScale();
			return new Vec3(this.random.nextGaussian() * d0, (double)0.0F, this.random.nextGaussian() * d0);
		} else {
			return super.getRenderOffset(entity, partialTicks);
		}
	}

	@Override
	public ResourceLocation getTextureLocation(TamedEnderman tamedEnderman) {
		return TEXTURE;
	}
}
