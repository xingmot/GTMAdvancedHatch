package com.xingmot.gtmadvancedhatch.api.gui;

import com.gregtechceu.gtceu.integration.ae2.gui.widget.slot.AEConfigSlotWidget;

import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.side.fluid.FluidStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PhantomFluidCapacityWidgetGroup extends WidgetGroup {

    protected CapacityAmountSetWidget<FluidStack> capacitySetWidget;

    @OnlyIn(Dist.CLIENT)
    public void enableAmountClient(int slotIndex) {
        this.capacitySetWidget.setSlotIndexClient(slotIndex);
        this.capacitySetWidget.setVisible(true);
        this.capacitySetWidget.getAmountText().setVisible(true);
    }

    @OnlyIn(Dist.CLIENT)
    public void disableAmountClient() {
        this.capacitySetWidget.setSlotIndexClient(-1);
        this.capacitySetWidget.setVisible(false);
        this.capacitySetWidget.getAmountText().setVisible(false);
    }

    public void enableAmount(int slotIndex) {
        this.capacitySetWidget.setSlotIndex(slotIndex);
        this.capacitySetWidget.setVisible(true);
        this.capacitySetWidget.getAmountText().setVisible(true);
    }

    public void disableAmount() {
        this.capacitySetWidget.setSlotIndex(-1);
        this.capacitySetWidget.setVisible(false);
        this.capacitySetWidget.getAmountText().setVisible(false);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.capacitySetWidget.isVisible()) {
            if (this.capacitySetWidget.getAmountText().mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        for (Widget w : this.widgets) {
            if (w instanceof AEConfigSlotWidget slot) {
                slot.setSelect(false);
            }
        }
        this.disableAmountClient();
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
