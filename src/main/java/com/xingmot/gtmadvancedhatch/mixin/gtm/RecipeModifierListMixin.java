package com.xingmot.gtmadvancedhatch.mixin.gtm;

import com.xingmot.gtmadvancedhatch.api.IBatchable;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.logic.OCParams;
import com.gregtechceu.gtceu.api.recipe.logic.OCResult;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifierList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeModifierList.class)
public abstract class RecipeModifierListMixin {

    @Inject(remap = false, method = "apply", at = @At("RETURN"), cancellable = true)
    private void applyMixin(MetaMachine machine, GTRecipe recipe, OCParams params, OCResult result, CallbackInfoReturnable<GTRecipe> cir) {
        GTRecipe gtRecipe = cir.getReturnValue();
        if (gtRecipe != null && gtRecipe.duration <= 20) {
            if (machine instanceof MultiblockControllerMachine mmachine) {
                boolean hasNetHatch = false;
                for (IMultiPart part : mmachine.getParts()) {
                    if (part instanceof IBatchable ibatchable && ibatchable.isBatchEnable()) {
                        hasNetHatch = true;
                        break;
                    }
                }
                if (!hasNetHatch) return;
                // GTMAdvancedHatch.LOGGER.info("gtRecipe.duration: {}", gtRecipe.duration);
                // cir.setReturnValue(gtRecipe.copy(ContentModifier.multiplier(Math.ceil(20.0 / gtRecipe.duration))));
                GTRecipe gtRecipe1 = ParallelLogic.applyParallel(machine, gtRecipe, (int) Math.ceil(20.0 / gtRecipe.duration), true).getFirst();
                RecipeHelper.setInputEUt(gtRecipe1, RecipeHelper.getInputEUt(gtRecipe));
                RecipeHelper.setOutputEUt(gtRecipe1, RecipeHelper.getOutputEUt(gtRecipe));
                cir.setReturnValue(gtRecipe1);
            }
        }
    }
}
