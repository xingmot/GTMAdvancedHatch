package com.xingmot.gtmadvancedhatch.common.data;

import com.xingmot.gtmadvancedhatch.api.NetDataItemBehavior;
import com.xingmot.gtmadvancedhatch.common.AHRegistration;

import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.common.data.GTCompassSections;

import static com.gregtechceu.gtceu.common.data.GTItems.attach;
import static com.gregtechceu.gtceu.common.data.GTItems.compassNode;

import com.tterrag.registrate.util.entry.ItemEntry;

public class AHItems {

    static {
        AHRegistration.registrate.creativeModeTab(() -> AHTabs.BASE_TAB);
    }

    public static ItemEntry<ComponentItem> TOOL_NET_DATA_STICK = AHRegistration.registrate.item("net_data_stick", ComponentItem::create)
            .lang("Net Data Stick")
            .onRegister(attach(new NetDataItemBehavior()))
            .onRegister(compassNode(GTCompassSections.COMPONENTS))
            .register();

    public static void init() {}
}
