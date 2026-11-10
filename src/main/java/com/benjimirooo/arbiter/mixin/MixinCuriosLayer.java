
package com.benjimirooo.arbiter.mixin;

import com.benjimirooo.arbiter.Arbiter;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Set;

@Pseudo
@Mixin(
        targets = "top.theillusivec4.curios.client.render.CuriosLayer",
        remap = false
)
public abstract class MixinCuriosLayer {

    private static final Set<String> HEAD_SLOTS = Set.of(
            "head",
            "hat",
            "helmet",
            "headwear",
            "face",
            "crown"
    );

    private record ContextMethods(Method identifier, Method entity) {}

    private static final ClassValue<ContextMethods> METHODS =
            new ClassValue<>() {
                @Override
                protected ContextMethods computeValue(Class<?> type) {
                    try {
                        Method identifier = type.getMethod("identifier");
                        Method entity = type.getMethod("entity");

                        try {
                            identifier.setAccessible(true);
                            entity.setAccessible(true);
                        } catch (RuntimeException ignored) {
                        }

                        return new ContextMethods(identifier, entity);
                    } catch (ReflectiveOperationException | LinkageError e) {
                        return new ContextMethods(null, null);
                    }
                }
            };

    private static boolean arbiter$isHeadSlot(Object context) {
        if (context == null) return false;

        try {
            ContextMethods methods = METHODS.get(context.getClass());

            if (methods.identifier() == null || methods.entity() == null) {
                return false;
            }

            Object identifier = methods.identifier().invoke(context);

            if (!(identifier instanceof String slot)) return false;

            slot = slot.toLowerCase(Locale.ROOT);

            // Also support namespaced slot identifiers.
            int separator = slot.lastIndexOf(':');
            if (separator >= 0) {
                slot = slot.substring(separator + 1);
            }

            return HEAD_SLOTS.contains(slot);

        } catch (ReflectiveOperationException | RuntimeException e) {
            return false;
        }
    }

    private static LivingEntity arbiter$getEntity(Object context) {
        if (context == null) return null;

        try {
            ContextMethods methods = METHODS.get(context.getClass());

            if (methods.entity() == null) return null;

            Object entity = methods.entity().invoke(context);

            return entity instanceof LivingEntity living
                    ? living
                    : null;

        } catch (ReflectiveOperationException | RuntimeException e) {
            return null;
        }
    }

    @WrapOperation(
            method = "lambda$render$0",
            at = @At(
                    value = "INVOKE",
                    target = "Ltop/theillusivec4/curios/api/client/ICurioRenderer;render",
                    remap = false
            ),
            remap = false,
            require = 0
    )
    private void arbiter$hideCuriosHeadAccessories(
            @Coerce Object renderer,
            @Coerce Object stack,
            @Coerce Object slotContext,
            @Coerce Object poseStack,
            @Coerce Object renderLayerParent,
            @Coerce Object bufferSource,
            int packedLight,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch,
            Operation<Void> original
    ) {
        LivingEntity entity = arbiter$getEntity(slotContext);

        if (
                entity != null
                        && arbiter$isHeadSlot(slotContext)
                        && bufferSource instanceof MultiBufferSource buffers
                        && Arbiter.shouldHideHeadGear(entity, buffers)
        ) {
            return;
        }

        original.call(
                renderer,
                stack,
                slotContext,
                poseStack,
                renderLayerParent,
                bufferSource,
                packedLight,
                limbSwing,
                limbSwingAmount,
                partialTick,
                ageInTicks,
                netHeadYaw,
                headPitch
        );
    }
}
