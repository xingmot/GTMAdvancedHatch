package com.xingmot.gtmadvancedhatch.common.machines;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;

import net.minecraft.world.entity.player.Player;

import com.hepdd.gtmthings.common.block.machine.multiblock.part.WirelessEnergyHatchPartMachine;

// TODO 巨型无线能源仓
public class HugeWirelessEnergyHatchPartMachine extends WirelessEnergyHatchPartMachine {

    int energyCapacityMultiplier = 16;

    public HugeWirelessEnergyHatchPartMachine(IMachineBlockEntity holder, int tier, IO io, int amperage, Object... args) {
        super(holder, tier, io, amperage, args);
    }

    //////////////////////////////////////
    // ********** GUI ***********//
    //////////////////////////////////////
    @Override
    public ModularUI createUI(Player entityPlayer) {
        return new ModularUI(176, 136, this, entityPlayer)
                .background(GuiTextures.BACKGROUND)
                .widget(new LabelWidget(7, 74, "gtceu.creative.energy.amperage"))
                .widget(new ButtonWidget(7, 87, 20, 20,
                        new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON, new TextTexture("-")),
                        cd -> amperage = --amperage == -1 ? 0 : amperage))
                .widget(new TextFieldWidget(31, 89, 114, 16, () -> String.valueOf(amperage),
                        value -> amperage = Integer.parseInt(value)).setNumbersOnly(1, 67108864))
                .widget(new ButtonWidget(149, 87, 20, 20,
                        new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON, new TextTexture("+")),
                        cd -> {
                            if (amperage < Integer.MAX_VALUE) {
                                amperage++;
                            }
                        }));
    }
}
