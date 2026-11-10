package com.benjimirooo.arbiter.mixin;

import com.benjimirooo.arbiter.Arbiter;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hides head-slot items that are not armor pieces (skulls, carved pumpkins,
 * player heads) while Real Camera renders the first-person model.
 */
@Mixin(CustomHeadLayer.class)
public abstract class MixinCustomHeadLayer<T extends LivingEntity, M extends EntityModel<T>> {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void arbiter$hideHeadItemInRealCamera(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (Arbiter.shouldHideHeadGear(entity, bufferSource)) {
            ci.cancel();
        }
    }
}
