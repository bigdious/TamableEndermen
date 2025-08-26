package com.bigdious.tamable_endermen.client;

import com.bigdious.tamable_endermen.TamableEndermen;
import com.bigdious.tamable_endermen.entity.TamedEnderman;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EndermanCollarLayer extends RenderLayer<TamedEnderman, EndermanModel<TamedEnderman>> {
	private static final ResourceLocation ENDERMAN_COLLAR_LOCATION = TamableEndermen.prefix("textures/entity/tamed_enderman_collar.png");

	public EndermanCollarLayer(RenderLayerParent<TamedEnderman, EndermanModel<TamedEnderman>> renderer) {
		super(renderer);
	}

	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, TamedEnderman livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		if (!livingEntity.isInvisible()) {
			int i = livingEntity.getCollarColor().getTextureDiffuseColor();
			VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(ENDERMAN_COLLAR_LOCATION));
			(this.getParentModel()).renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, i);
		}

	}
}
