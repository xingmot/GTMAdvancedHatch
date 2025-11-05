package com.xingmot.gtmadvancedhatch.integration.buildinggadgets;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import com.direwolf20.buildinggadgets2.setup.Registration;

public class BuildingGadgetRegistration extends Registration {

    public static void init() {}

    public static final RegistryObject<Item> CopyPaste_Gadget_GT = ITEMS.register("gadget_copy_paste_gt", GadgetCopyPasteGT::new);
}
