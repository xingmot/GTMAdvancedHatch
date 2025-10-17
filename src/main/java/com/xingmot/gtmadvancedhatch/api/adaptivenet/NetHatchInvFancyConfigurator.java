package com.xingmot.gtmadvancedhatch.api.adaptivenet;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyConfigurator;
import com.gregtechceu.gtceu.data.lang.LangHandler;

import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.misc.ItemStackTransfer;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 给机器添加侧页，四个槽位分别用于放入电网能源仓、电网动力仓、电网激光靶仓、电网激光源仓
 */
public class NetHatchInvFancyConfigurator implements IFancyConfigurator {

    final ItemStackTransfer netEnergyOutputSlot;
    final ItemStackTransfer netEnergyInputSlot;
    final ItemStackTransfer netLaserOutputSlot;
    final ItemStackTransfer netLaserInputSlot;

    public NetHatchInvFancyConfigurator(ItemStackTransfer netEnergyOutputSlot, ItemStackTransfer netEnergyInputSlot,
                                        ItemStackTransfer netLaserOutputSlot, ItemStackTransfer netLaserInputSlot) {
        this.netEnergyOutputSlot = netEnergyOutputSlot;
        this.netEnergyInputSlot = netEnergyInputSlot;
        this.netLaserOutputSlot = netLaserOutputSlot;
        this.netLaserInputSlot = netLaserInputSlot;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gtmadvancedhatch.gui.adaptive.title");
    }

    @Override
    public IGuiTexture getIcon() {
         return new GuiTextureGroup(new ResourceTexture("gtceu:textures/gui/widget/energy_bar_overlay.png"));
    }

    @Override
    public Widget createConfigurator() {
        var group = new WidgetGroup(0, 0, 12 + 18 * 4, 50);
        group.addWidget(new LabelWidget(9, 8, "gtmadvancedhatch.gui.adaptive.title2"));
        group.addWidget(new SlotWidget(netEnergyOutputSlot, 0, 5, 20, true, true));
        group.addWidget(new SlotWidget(netEnergyInputSlot, 0, 5 + (18), 20, true, true));
        group.addWidget(new SlotWidget(netLaserOutputSlot, 0, 5 + (18 * 2), 20, true, true));
        group.addWidget(new SlotWidget(netLaserInputSlot, 0, 5 + (18 * 3), 20, true, true));
        return group;
    }

    @Override
    public List<Component> getTooltips() {
        var list = new ArrayList<>(IFancyConfigurator.super.getTooltips());
        list.addAll(Arrays.stream(
                LangHandler.getMultiLang("gtmadvancedhatch.gui.adaptive_slot.tooltip").toArray(new MutableComponent[0]))
                .toList());
        return list;
    }
}
