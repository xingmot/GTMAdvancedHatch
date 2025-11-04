package com.xingmot.gtmadvancedhatch.api.adaptivenet;

import com.gregtechceu.gtceu.api.gui.fancy.IFancyConfigurator;

import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.util.Strings;

public class SetFrequencyFancyConfigurator implements IFancyConfigurator {

    IFrequency frequency_holder;

    public SetFrequencyFancyConfigurator(IFrequency iFrequency) {
        this.frequency_holder = iFrequency;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gtmadvancedhatch.gui.set_frequency.title").setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW));
    }

    @Override
    public IGuiTexture getIcon() {
        return new GuiTextureGroup(new ResourceTexture("gtceu:textures/gui/icon/distribution_mode/round_robin_global.png"));
    }

    @Override
    public Widget createConfigurator() {
        var group = new WidgetGroup(0, 0, 158, 50);
        group.addWidget(new LabelWidget(9, 8, "gtmadvancedhatch.gui.set_frequency.title2"));
        group.addWidget(new TextFieldWidget(6, 20, 146, 16, () -> String.valueOf(frequency_holder.getFrequency()), value -> {
            if (!Strings.isBlank(value)) frequency_holder.setFrequency(Long.parseLong(value));
        }).setNumbersOnly(Long.MIN_VALUE, Long.MAX_VALUE));
        return group;
    }

    @Override
    public List<Component> getTooltips() {
        var list = new ArrayList<>(IFancyConfigurator.super.getTooltips());
        String s_range = "[-9.2E18, 9.2E18]";
        list.add(Component.translatable("gtmadvancedhatch.gui.set_frequency.tooltip.0"));
        list.add(Component.translatable("gtmadvancedhatch.gui.set_frequency.tooltip.1", Component.literal(s_range).withStyle(ChatFormatting.GOLD)));
        list.add(Component.translatable("gtmadvancedhatch.gui.set_frequency.tooltip.2"));
        return list;
    }
}
