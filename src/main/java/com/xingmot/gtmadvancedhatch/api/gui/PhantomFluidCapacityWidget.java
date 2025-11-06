package com.xingmot.gtmadvancedhatch.api.gui;

import com.xingmot.gtmadvancedhatch.api.IConfigFluidTransfer;
import com.xingmot.gtmadvancedhatch.util.AHFormattingUtil;
import com.xingmot.gtmadvancedhatch.util.AHUtil;

import com.gregtechceu.gtceu.api.gui.widget.ScrollablePhantomFluidWidget;
import com.gregtechceu.gtceu.utils.GTUtil;

import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.side.fluid.IFluidTransfer;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO 翻页

/**
 * 可设置数量的虚拟流体槽
 */
public class PhantomFluidCapacityWidget extends ScrollablePhantomFluidWidget implements IPhantomAmountWidget<FluidStack> {

    @Getter
    private IConfigFluidTransfer icFluidTank;

    public PhantomFluidCapacityWidget(@Nullable IConfigFluidTransfer icFluidTank, @Nullable IFluidTransfer fluidTank, int tank, int x, int y, int width, int height, Supplier<FluidStack> phantomFluidGetter, Consumer<FluidStack> phantomFluidSetter) {
        super(fluidTank, tank, x, y, width, height, phantomFluidGetter, phantomFluidSetter);
        this.icFluidTank = icFluidTank;
        this.showAmount = false;
    }

    @Override
    public long getAmount() {
        return this.lastTankCapacity;
    }

    @Override
    public void setAmount(long capacity) {
        this.lastTankCapacity = capacity;
    }

    @Override
    public FluidStack getPhantomStack() {
        return this.lastPhantomStack;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean mouseWheelMove(double mouseX, double mouseY, double wheelDelta) {
        if (!this.isMouseOverElement(mouseX, mouseY)) {
            return false;
        } else {
            long newCapacity = this.getnewCapacity(wheelDelta > (double) 0.0F ? 1 : -1);
            this.writeClientAction(65537, (buf) -> buf.writeLong(newCapacity));
            return true;
        }
    }

    private long getnewCapacity(int wheel) {
        if (GTUtil.isCtrlDown()) {
            long multi = 2;
            if (GTUtil.isShiftDown()) {
                multi *= 10;
            }
            if (GTUtil.isAltDown()) {
                multi *= 1000;
            }
            return wheel > 0 ? AHUtil.multiplyWithBounds(this.getAmount(), multi) : AHUtil.divWithBounds(this.getAmount(), multi);
        }
        long add = wheel;
        if (GTUtil.isShiftDown()) {
            add *= 10;
            if (GTUtil.isAltDown()) {
                add *= 10;
            }
        }

        if (!GTUtil.isAltDown()) {
            add *= 1000;
        }
        return AHUtil.addWithBounds(this.getAmount(), add);
    }

    @Override
    public void handleClientAction(int id, FriendlyByteBuf buffer) {
        switch (id) {
            case 65537 -> this.handleScrollAction(buffer.readLong());
            default -> super.handleClientAction(id, buffer);
        }

        this.detectAndSendChanges();
    }

    private void handleScrollAction(long newAmount) {
        if (this.getFluidTank() != null)
            icFluidTank.newTankCapacity(this.tank, Math.max(newAmount, 0));
    }

    /** 写的石山代码，不知道在写什么 */
    @Override
    public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (isClientSideWidget && fluidTank != null) {
            FluidStack fluidStack = fluidTank.getFluidInTank(tank);
            long capacity = fluidTank.getTankCapacity(tank);
            if (capacity != lastTankCapacity) {
                this.lastTankCapacity = capacity;
            }
            if (!fluidStack.isFluidEqual(lastFluidInTank)) {
                this.lastFluidInTank = fluidStack.copy();
            } else if (fluidStack.getAmount() != lastFluidInTank.getAmount()) {
                this.lastFluidInTank.setAmount(fluidStack.getAmount());
            }
        }
        Position pos = getPosition();
        Size size = getSize();
        RenderSystem.disableBlend();
        if (!lastFluidInTank.isEmpty()) {
            double progress = lastFluidInTank.getAmount() * 1.0 / Math.max(Math.max(lastFluidInTank.getAmount(), lastTankCapacity), 1);
            float drawnU = (float) fillDirection.getDrawnU(progress);
            float drawnV = (float) fillDirection.getDrawnV(progress);
            float drawnWidth = (float) fillDirection.getDrawnWidth(progress);
            float drawnHeight = (float) fillDirection.getDrawnHeight(progress);
            int width = size.width - 2;
            int height = size.height - 2;
            int x = pos.x + 1;
            int y = pos.y + 1;
            DrawerHelper.drawFluidForGui(graphics, lastFluidInTank, lastFluidInTank.getAmount(),
                    (int) (x + drawnU * width), (int) (y + drawnV * height), ((int) (width * drawnWidth)), ((int) (height * drawnHeight)));
        } else {
            var isHovered = isMouseOverElement(mouseX, mouseY);
            if (backgroundTexture != null && (!isHovered || drawBackgroundWhenHover)) {
                backgroundTexture.draw(graphics, mouseX, mouseY, pos.x, pos.y, size.width, size.height);
            }
            if (hoverTexture != null && isHovered && isActive()) {
                hoverTexture.draw(graphics, mouseX, mouseY, pos.x, pos.y, size.width, size.height);
            }
        }
        drawOverlay(graphics, mouseX, mouseY, partialTicks);
        if (showAmount) {
            graphics.pose().pushPose();
            graphics.pose().scale(0.5F, 0.5F, 1);
            // String s = "/" + TextFormattingUtil.formatLongToCompactStringBuckets(lastTankCapacity, 3) + "B";
            String s = "/" + AHFormattingUtil.formatLongBuckets(lastTankCapacity) + "B";
            Font fontRenderer = Minecraft.getInstance().font;
            graphics.drawString(fontRenderer, s,
                    (int) ((pos.x + (size.width / 3f)) * 2 - fontRenderer.width(s) + 21),
                    (int) ((pos.y + (size.height / 3f) + 6) * 2), 0xFFFFFF, true);
            graphics.pose().popPose();
        }

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }
}
