package com.xingmot.gtmadvancedhatch.api;

import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.side.item.IItemTransfer;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

// 超堆叠物品格组件
public class LockStackSlotWidget extends SlotWidget {

    protected IGuiTexture occupiedTexture;

    public LockStackSlotWidget() {}

    public LockStackSlotWidget(
                               Container inventory,
                               int slotIndex,
                               int xPosition,
                               int yPosition,
                               boolean canTakeItems,
                               boolean canPutItems) {
        super(inventory, slotIndex, xPosition, yPosition, canTakeItems, canPutItems);
    }

    public LockStackSlotWidget(
                               IItemTransfer itemHandler,
                               int slotIndex,
                               int xPosition,
                               int yPosition,
                               boolean canTakeItems,
                               boolean canPutItems) {
        super(itemHandler, slotIndex, xPosition, yPosition, canTakeItems, canPutItems);
    }

    public LockStackSlotWidget(
                               IItemTransfer itemHandler, int slotIndex, int xPosition, int yPosition) {
        super(itemHandler, slotIndex, xPosition, yPosition);
    }

    public LockStackSlotWidget(Container inventory, int slotIndex, int xPosition, int yPosition) {
        super(inventory, slotIndex, xPosition, yPosition);
    }

    // =============================
    // === GUI ==
    // =============================
    public LockStackSlotWidget setOccupiedTexture(IGuiTexture... occupiedTexture) {
        this.occupiedTexture = occupiedTexture.length > 1 ? new GuiTextureGroup(occupiedTexture) : occupiedTexture[0];
        return this;
    }

    @Override
    protected Slot createSlot(IItemTransfer itemHandler, int index) {
        return new MyWidgetSlotItemTransfer(itemHandler, index, 0, 0);
    }

    public class MyWidgetSlotItemTransfer extends SlotWidget.WidgetSlotItemTransfer {

        public MyWidgetSlotItemTransfer(IItemTransfer itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public int getMaxStackSize(@Nonnull ItemStack stack) {
            return this.getItemHandler().getSlotLimit(this.getSlotIndex()) * stack.getMaxStackSize() / 64;
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void updateScreen() {
        super.updateScreen();
        if (occupiedTexture != null) {
            occupiedTexture.updateTick();
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void drawBackgroundTexture(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        Position pos = getPosition();
        Size size = getSize();
        if (getHandler() != null && getHandler().hasItem()) {
            if (occupiedTexture != null) {
                occupiedTexture.draw(graphics, mouseX, mouseY, pos.x, pos.y, size.width, size.height);
            }
        } else {
            if (backgroundTexture != null) {
                backgroundTexture.draw(graphics, mouseX, mouseY, pos.x, pos.y, size.width, size.height);
            }
        }

        if (hoverTexture != null && isMouseOverElement(mouseX, mouseY)) {
            hoverTexture.draw(graphics, mouseX, mouseY, pos.x, pos.y, size.width, size.height);
        }
    }
}
