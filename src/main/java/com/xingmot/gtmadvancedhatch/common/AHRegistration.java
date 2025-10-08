package com.xingmot.gtmadvancedhatch.common;

import com.xingmot.gtmadvancedhatch.GTMAdvancedHatch;

import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

public class AHRegistration {

    public static GTRegistrate registrate = GTRegistrate.create(GTMAdvancedHatch.MODID);

    static {
        registrate.defaultCreativeTab((ResourceKey<CreativeModeTab>) null);
    }

    private AHRegistration() {}
}
