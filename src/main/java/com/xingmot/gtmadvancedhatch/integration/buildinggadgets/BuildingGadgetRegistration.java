package com.xingmot.gtmadvancedhatch.integration.buildinggadgets;

import com.xingmot.gtmadvancedhatch.common.AHRegistration;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;

import com.direwolf20.buildinggadgets2.setup.Registration;

public class BuildingGadgetRegistration extends Registration {

    static {
        AHRegistration.registrate.creativeModeTab(() -> null);
    }

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
    }

    public static final RegistryObject<Item> CopyPaste_Gadget_GT = ITEMS.register("gadget_copy_paste_gt", GadgetCopyPasteGT::new);
}
