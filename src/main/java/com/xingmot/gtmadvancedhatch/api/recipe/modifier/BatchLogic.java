package com.xingmot.gtmadvancedhatch.api.recipe.modifier;

import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;

import net.minecraft.MethodsReturnNonnullByDefault;

import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BatchLogic {

    public static Pair<GTRecipe, Integer> applyBatch(MetaMachine machine, GTRecipe recipe, int batchLimit) {
        if (machine instanceof IRecipeLogicMachine rlm) {
            return doBatchRecipes(recipe, rlm, batchLimit);
        } else {
            return Pair.of(recipe, 1);
        }
    }

    public static int getMaxRecipeMultiplier(GTRecipe recipe, IRecipeCapabilityHolder holder, int batchAmount) {
        IntSet multipliers = new IntOpenHashSet();

        for (RecipeCapability<?> cap : recipe.inputs.keySet()) {
            if (cap.doMatchInRecipe()) {
                multipliers.add(cap.getMaxParallelRatio(holder, recipe, batchAmount));
            }
        }

        if (multipliers.intStream().allMatch((value) -> value == Integer.MAX_VALUE)) {
            return 0;
        } else {
            return multipliers.intStream().min().orElse(0);
        }
    }

    public static int limitByOutputMerging(GTRecipe recipe, IRecipeCapabilityHolder holder, int batchAmount, Predicate<RecipeCapability<?>> canVoid) {
        Object2IntMap<RecipeCapability<?>> modifiedBatchAmounts = new Object2IntOpenHashMap();
        boolean canVoidAll = true;

        for (RecipeCapability<?> cap : recipe.outputs.keySet()) {
            modifiedBatchAmounts.put(cap, Integer.MAX_VALUE);
            if (!canVoid.test(cap)) {
                canVoidAll = false;
            }
        }

        if (canVoidAll) {
            return batchAmount;
        } else {
            for (RecipeCapability<?> cap : recipe.outputs.keySet()) {
                if (cap.doMatchInRecipe() && !recipe.getOutputContents(cap).isEmpty()) {
                    boolean voiding = canVoid.test(cap);
                    if (voiding) {
                        modifiedBatchAmounts.put(cap, batchAmount);
                    } else {
                        modifiedBatchAmounts.put(cap, cap.limitParallel(recipe, holder, batchAmount));
                    }

                    if (modifiedBatchAmounts.getInt(cap) == 0 && !voiding) {
                        return 0;
                    }
                }
            }

            return modifiedBatchAmounts.values().intStream().min().orElse(0);
        }
    }

    public static Pair<GTRecipe, Integer> doBatchRecipes(GTRecipe currentRecipe, IRecipeLogicMachine machine, int batchAmount) {
        int multiplierByInputs = getMaxRecipeMultiplier(currentRecipe, machine, batchAmount);
        if (multiplierByInputs == 0) {
            return Pair.of(currentRecipe, 1);
        } else {
            Objects.requireNonNull(machine);
            int limitByOutput = limitByOutputMerging(currentRecipe, machine, multiplierByInputs, machine::canVoidRecipeOutputs);
            if (limitByOutput > 0) {
                GTRecipe multiRecipe = currentRecipe.copy(ContentModifier.multiplier(limitByOutput), true);
                multiRecipe.parallels = limitByOutput;
                RecipeHelper.setOutputEUt(multiRecipe, RecipeHelper.getOutputEUt(currentRecipe));
                RecipeHelper.setInputEUt(multiRecipe, RecipeHelper.getInputEUt(currentRecipe));
                return Pair.of(multiRecipe, limitByOutput);
            } else {
                return Pair.of(currentRecipe, limitByOutput);
            }
        }
    }
}
