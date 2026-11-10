package com.benjimirooo.arbiter;

import com.xtracr.realcamera.renderer.MultiVertexCatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.common.Mod;

@Mod(Arbiter.MOD_ID)
public class Arbiter {
    public static final String MOD_ID = "arbiter";

    /**
     * True only while Real Camera's classic mode is rendering the camera entity
     * through LevelRenderer#renderEntity. Set by MixinLevelRenderer.
     */
    private static boolean classicCameraRender;

    public Arbiter() {
    }

    public static void setClassicCameraRender(boolean value) {
        classicCameraRender = value;
    }

    /**
     * Returns true while Real Camera is rendering the camera entity's model in
     * first person. Real Camera captures the entity into a {@link MultiVertexCatcher}
     * in its normal mode; classic mode renders through the regular level renderer,
     * which is tracked by {@link #classicCameraRender}.
     */
    public static boolean shouldHideHeadGear(LivingEntity entity, MultiBufferSource bufferSource) {
        if (entity != Minecraft.getInstance().getCameraEntity()) return false;
        return bufferSource instanceof MultiVertexCatcher || classicCameraRender;
    }
}
