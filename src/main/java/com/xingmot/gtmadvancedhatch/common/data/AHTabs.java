package com.xingmot.gtmadvancedhatch.common.data;

import com.xingmot.gtmadvancedhatch.GTMAdvancedHatch;
import com.xingmot.gtmadvancedhatch.common.AHRegistration;

import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs;

import net.minecraft.world.item.*;

import com.tterrag.registrate.util.entry.RegistryEntry;

public class AHTabs {

    public static final RegistryEntry<CreativeModeTab> BASE_TAB = AHRegistration.registrate
            .defaultCreativeTab("base_tab", builder -> builder
                    .icon(() -> AHItems.TOOL_NET_DATA_STICK.asStack())
                    .displayItems(new GTCreativeModeTabs.RegistrateDisplayItemsGenerator("base_tab", AHRegistration.registrate))
                    .title(AHRegistration.registrate.addLang("itemGroup", GTMAdvancedHatch.id("base_tab"), GTMAdvancedHatch.NAME))
                    .build())
            .register();

    public static void init() {}
}
