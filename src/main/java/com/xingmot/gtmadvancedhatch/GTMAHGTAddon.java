package com.xingmot.gtmadvancedhatch;

import com.xingmot.gtmadvancedhatch.common.AHRegistration;
import com.xingmot.gtmadvancedhatch.common.data.AHRecipes;
import com.xingmot.gtmadvancedhatch.integration.gtlcore.ExGTLRecipes;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraftforge.fml.ModList;

import java.util.function.Consumer;

@GTAddon
public class GTMAHGTAddon implements IGTAddon {

    @Override
    public GTRegistrate getRegistrate() {
        return AHRegistration.registrate;
    }

    @Override
    public void initializeAddon() {}

    @Override
    public String addonModId() {
        return GTMAdvancedHatch.MODID;
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        AHRecipes.initRecipes(provider);
        if (ModList.get().isLoaded("gtlcore")) {
            ExGTLRecipes.initRecipes(provider);
        }
    }
}
