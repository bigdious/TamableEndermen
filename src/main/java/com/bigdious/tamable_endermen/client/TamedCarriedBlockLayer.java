package com.bigdious.tamable_endermen.client;

import com.bigdious.tamable_endermen.entity.TamedEnderman;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TamedCarriedBlockLayer extends RenderLayer<TamedEnderman, EndermanModel<TamedEnderman>> {
	private final BlockRenderDispatcher blockRenderer;

	public TamedCarriedBlockLayer(RenderLayerParent<TamedEnderman, EndermanModel<TamedEnderman>> renderer, BlockRenderDispatcher blockRenderer) {
		super(renderer);
		this.blockRenderer = blockRenderer;
	}

	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, TamedEnderman livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		BlockState blockstate = livingEntity.getCarriedBlock();
		if (blockstate != null) {
			poseStack.pushPose();
			poseStack.translate(0.0F, 0.6875F, -0.75F);
			poseStack.mulPose(Axis.XP.rotationDegrees(20.0F));
			poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
			poseStack.translate(0.25F, 0.1875F, 0.25F);
			float f = 0.5F;
			poseStack.scale(-0.5F, -0.5F, 0.5F);
			poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
			this.blockRenderer.renderSingleBlock(blockstate, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
			poseStack.popPose();
		}

	}
}