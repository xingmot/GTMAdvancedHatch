package com.xingmot.gtmadvancedhatch.api.adaptivenet;

import com.xingmot.gtmadvancedhatch.common.data.MachinesConstants;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyConfigurator;
import com.gregtechceu.gtceu.data.lang.LangHandler;

import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hepdd.gtmthings.api.capability.IBindable;
import com.hepdd.gtmthings.utils.TeamUtil;

/**
 * 指纹录入侧页（绑定uuid）
 * - 其获取的uuid已经是ftbteam的队伍uuid
 */
public class BindUUIDFancyConfigurator implements IFancyConfigurator {

    IBindable bindable;

    public BindUUIDFancyConfigurator(IBindable bindable) {
        this.bindable = bindable;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gtmadvancedhatch.gui.binduuid.title");
    }

    @Override
    public IGuiTexture getIcon() {
        return new GuiTextureGroup(GuiTextures.BUTTON_LOCK);
    }

    @Override
    public Widget createConfigurator() {
        var group = new WidgetGroup(0, 0, 60, 50);
        group.addWidget(new LabelWidget(9, 8, "gtmadvancedhatch.gui.binduuid.title2"));
        group.addWidget(new ButtonWidget(group.getSize().width / 2 - 9, 20, 18, 18, GuiTextures.MAINTENANCE_BUTTON,
                data -> bindUUID(group.getGui().entityPlayer)).setHoverTooltips("gtmadvancedhatch.gui.binduuid.button.tooltip"));
        return group;
    }

    private void bindUUID(Player player) {
        if (bindable.getUUID().equals(MachinesConstants.UUID_ZERO)) {
            bindable.setUUID(TeamUtil.getTeamUUID(player.getUUID()));
        } else {
            bindable.setUUID(MachinesConstants.UUID_ZERO);
        }
    }

    @Override
    public List<Component> getTooltips() {
        var list = new ArrayList<>(IFancyConfigurator.super.getTooltips());
        list.addAll(Arrays.stream(
                LangHandler.getMultiLang("gtmadvancedhatch.gui.binduuid.tooltip").toArray(new MutableComponent[0]))
                .toList());
        return list;
    }
}
