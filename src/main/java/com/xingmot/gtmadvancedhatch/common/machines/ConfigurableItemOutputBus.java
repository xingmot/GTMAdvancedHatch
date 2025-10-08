package com.xingmot.gtmadvancedhatch.common.machines;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;

import com.lowdragmc.lowdraglib.gui.widget.PhantomSlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.jei.IngredientIO;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;

import lombok.Getter;

public class ConfigurableItemOutputBus extends LockItemOutputBus {

    @Persisted
    @Getter
    protected final NotifiableItemStackHandler filterInventory;

    public ConfigurableItemOutputBus(IMachineBlockEntity holder, int tier, Object... args) {
        super(holder, tier, args);
        this.filterInventory = createFilterInventory();
    }

    private NotifiableItemStackHandler createFilterInventory() {
        int sizeRoot = 1 + Math.min(9, getTier());
        return new NotifiableItemStackHandler(this, sizeRoot, IO.NONE);
    }

    @Override
    protected int getInventorySize() {
        int sizeRoot = 1 + Math.min(9, getTier());
        return sizeRoot * sizeRoot;
    }

    @Override
    public Widget createUIWidget() {
        int rowSize = (int) Math.sqrt(getInventorySize());
        int colSize = rowSize;
        if (getInventorySize() == 8) {
            rowSize = 4;
            colSize = 2;
        }
        var group = new WidgetGroup(0, 0, 18 * rowSize + 16, 18 * colSize + 16);
        var container = new WidgetGroup(4, 4, 18 * rowSize + 8, 18 * colSize + 8);
        int index = 0;
        for (int y = 0; y < colSize; y++) {
            // 添加一列标记列
            for (int x = 0; x <= rowSize; x++) {
                if (x == 0) {
                    PhantomSlotWidget phantomSlotWidget = new PhantomSlotWidget(getFilterInventory().storage, index++,
                            4, 4 + y * 18);
                    phantomSlotWidget
                            .setBackgroundTexture(GuiTextures.SLOT);
                    phantomSlotWidget.setMaxStackSize(64 * Math.max(1, Math.min(8, getTier() - 4)));
                    container.addWidget(phantomSlotWidget);
                } else {
                    container.addWidget(new SlotWidget(getInventory().storage, index++,
                            4 + x * 18, 4 + y * 18, true,
                            true // TODO 暂时改成可以随意放物品
                    // io.support(IO.IN)
                    ).setBackgroundTexture(GuiTextures.SLOT)
                            .setIngredientIO(this.io == IO.IN ? IngredientIO.INPUT : IngredientIO.OUTPUT));
                }
            }
        }
        container.setBackground(GuiTextures.BACKGROUND_INVERSE);
        group.addWidget(container);
        return group;
    }
}
