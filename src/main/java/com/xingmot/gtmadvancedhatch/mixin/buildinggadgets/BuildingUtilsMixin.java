package com.xingmot.gtmadvancedhatch.mixin.buildinggadgets;

import com.xingmot.gtmadvancedhatch.config.AHConfig;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import com.direwolf20.buildinggadgets2.util.BuildingUtils;
import com.direwolf20.buildinggadgets2.util.DimBlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BuildingUtils.class, remap = false)
public class BuildingUtilsMixin {

    @Inject(remap = false, method = "removeStacksFromInventory", at = @At("HEAD"), cancellable = true)
    private static void removeStacksFromInventoryMixin(Player player, List<ItemStack> itemStacks, boolean simulate, DimBlockPos boundInventory, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (itemStacks.isEmpty() && AHConfig.INSTANCE.buildingGadgetBuildAE2) cir.setReturnValue(true);
    }
}
