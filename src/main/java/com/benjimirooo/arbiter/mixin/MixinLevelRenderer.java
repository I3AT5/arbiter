package com.benjimirooo.arbiter.mixin;

import com.benjimirooo.arbiter.Arbiter;
import com.mojang.blaze3d.vertex.PoseStack;
import com.xtracr.realcamera.RealCameraCore;
import com.xtracr.realcamera.config.ConfigFile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Real Camera's classic mode renders the camera entity through
 * {@link LevelRenderer#renderEntity} with the regular buffer source instead of
 * its vertex catcher, so the armor layer mixins cannot detect it. Track that
 * call with a flag instead.
 */
@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer {
    @Inject(method = "renderEntity", at = @At("HEAD"))
    private void arbiter$onRenderEntityHead(Entity entity, double camX, double camY, double camZ, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, CallbackInfo ci) {
        if (arbiter$isClassicCameraRender(entity)) {
            Arbiter.setClassicCameraRender(true);
        }
    }

    @Inject(method = "renderEntity", at = @At("RETURN"))
    private void arbiter$onRenderEntityReturn(Entity entity, double camX, double camY, double camZ, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, CallbackInfo ci) {
        if (arbiter$isClassicCameraRender(entity)) {
            Arbiter.setClassicCameraRender(false);
        }
    }

    private static boolean arbiter$isClassicCameraRender(Entity entity) {
        return entity == Minecraft.getInstance().getCameraEntity()
                && RealCameraCore.isRendering()
                && ConfigFile.config().isClassic;
    }
}
