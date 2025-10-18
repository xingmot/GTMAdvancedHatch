package com.xingmot.gtmadvancedhatch.integration.buildinggadgets;

import com.xingmot.gtmadvancedhatch.GTMAdvancedHatch;

import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

import static com.xingmot.gtmadvancedhatch.integration.buildinggadgets.BuildingGadgetRegistration.CopyPaste_Gadget_GT;

import com.direwolf20.buildinggadgets2.setup.Registration;

public class BuildingGadgetRecipes {

    public static void initRecipes(Consumer<FinishedRecipe> provider) {
        VanillaRecipeHelper.addShapelessRecipe(provider, GTMAdvancedHatch.id("gadget_copy_paste_gt"),
                new ItemStack(CopyPaste_Gadget_GT.get()),
                new ItemStack(Registration.CopyPaste_Gadget.get()));
    }
}
