package com.xingmot.gtmadvancedhatch.mixin.gtm;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.utils.GTUtil;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 修复数组溢出问题
@Mixin(GTUtil.class)
public class GTUtilMixin {

    @Inject(remap = false, method = "getFloorTierByVoltage", at = @At("HEAD"), cancellable = true)
    private static void getFloorTierByVoltage(long voltage, CallbackInfoReturnable<Byte> cir) {
        cir.setReturnValue((byte) Math.max(GTValues.ULV, GTUtil.nearestLesserOrEqual(GTValues.V, voltage)));
    }
}
