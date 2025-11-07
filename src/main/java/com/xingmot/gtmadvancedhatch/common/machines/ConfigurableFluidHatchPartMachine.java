package com.xingmot.gtmadvancedhatch.common.machines;

import com.xingmot.gtmadvancedhatch.api.ConfigNotifiableFluidTank;
import com.xingmot.gtmadvancedhatch.api.gui.HugeTankWidget;
import com.xingmot.gtmadvancedhatch.api.gui.PhantomFluidCapacityWidget;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.ConfiguratorPanel;
import com.gregtechceu.gtceu.api.gui.widget.ScrollablePhantomFluidWidget;
import com.gregtechceu.gtceu.api.gui.widget.ToggleButtonWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.fancyconfigurator.CircuitFancyConfigurator;
import com.gregtechceu.gtceu.api.machine.feature.IInteractedMachine;
import com.gregtechceu.gtceu.api.machine.feature.IMachineLife;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.integration.ae2.slot.ExportOnlyAEFluidList;

import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.side.fluid.FluidTransferHelper;
import com.lowdragmc.lowdraglib.syncdata.ISubscription;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Objects;
import java.util.function.BooleanSupplier;

import javax.annotation.ParametersAreNonnullByDefault;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ConfigurableFluidHatchPartMachine extends TieredIOPartMachine implements IMachineLife, IInteractedMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(ConfigurableFluidHatchPartMachine.class, TieredIOPartMachine.MANAGED_FIELD_HOLDER);
    @Persisted
    public final NotifiableFluidTank tank;
    @Getter
    @Persisted
    protected final NotifiableItemStackHandler circuitInventory;
    private final int slots;
    protected ExportOnlyAEFluidList fluidHandler;
    protected @Nullable TickableSubscription autoIOSubs;
    protected @Nullable ISubscription tankSubs;

    public ConfigurableFluidHatchPartMachine(IMachineBlockEntity holder, int tier, IO io, long initialCapacity, int slots, Object... args) {
        super(holder, tier, io);
        this.slots = slots;
        this.tank = this.createTank(initialCapacity, slots, args);
        this.fluidHandler = new ExportOnlyAEFluidList(this, slots);
        this.circuitInventory = this.createCircuitItemHandler(io);
    }

    public static long getTankCapacity(long initialCapacity, int tier) {
        return initialCapacity * (1L << Math.min(9, tier));
    }

    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    protected NotifiableFluidTank createTank(long initialCapacity, int slots, Object... args) {
        return new ConfigNotifiableFluidTank(this, slots, getTankCapacity(initialCapacity, this.getTier()), this.io);
    }

    protected NotifiableItemStackHandler createCircuitItemHandler(Object... args) {
        if (args.length > 0) {
            Object var3 = args[0];
            if (var3 instanceof IO io) {
                if (io == IO.IN) {
                    return (new NotifiableItemStackHandler(this, 1, IO.IN, IO.NONE)).setFilter(IntCircuitBehaviour::isIntegratedCircuit);
                }
            }
        }

        return new NotifiableItemStackHandler(this, 0, IO.NONE);
    }

    public void onMachineRemoved() {
        if (!ConfigHolder.INSTANCE.machines.ghostCircuit) {
            this.clearInventory(this.circuitInventory.storage);
        }
    }

    public void onLoad() {
        super.onLoad();
        Level var2 = this.getLevel();
        if (var2 instanceof ServerLevel serverLevel) {
            serverLevel.getServer()
                    .tell(new TickTask(0, this::updateTankSubscription));
        }

        this.tankSubs = this.tank.addChangedListener(this::updateTankSubscription);
    }

    public void onUnload() {
        super.onUnload();
        if (this.tankSubs != null) {
            this.tankSubs.unsubscribe();
            this.tankSubs = null;
        }
    }

    public InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack is = player.getItemInHand(hand);
        if (is.isEmpty()) {
            return InteractionResult.PASS;
        } else if (is.is(GTItems.TOOL_DATA_STICK.asItem())) {
            // TODO复制配置
            return InteractionResult.SUCCESS;
        } else if (is.is(Items.STICK)) {
            if (this.tank instanceof ConfigNotifiableFluidTank fluidtank)
                fluidtank.resetOneBasicInfo(0, this.tank.getTankCapacity(0) + 1000);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    public void onNeighborChanged(Block block, BlockPos fromPos, boolean isMoving) {
        super.onNeighborChanged(block, fromPos, isMoving);
        this.updateTankSubscription();
    }

    public void onRotated(Direction oldFacing, Direction newFacing) {
        super.onRotated(oldFacing, newFacing);
        this.updateTankSubscription();
    }

    protected void updateTankSubscription() {
        if (this.isWorkingEnabled() && (this.io == IO.OUT && !this.tank.isEmpty() || this.io == IO.IN) && FluidTransferHelper.getFluidTransfer(this.getLevel(), this.getPos()
                .relative(this.getFrontFacing()),
                this.getFrontFacing()
                        .getOpposite()) !=
                null) {
            this.autoIOSubs = this.subscribeServerTick(this.autoIOSubs, this::autoIO);
        } else if (this.autoIOSubs != null) {
            this.autoIOSubs.unsubscribe();
            this.autoIOSubs = null;
        }
    }

    protected void autoIO() {
        if (this.getOffsetTimer() % 5L == 0L) {
            if (this.isWorkingEnabled()) {
                if (this.io == IO.OUT) {
                    this.tank.exportToNearby(this.getFrontFacing());
                } else if (this.io == IO.IN) {
                    this.tank.importFromNearby(this.getFrontFacing());
                }
            }

            this.updateTankSubscription();
        }
    }

    public void setWorkingEnabled(boolean workingEnabled) {
        super.setWorkingEnabled(workingEnabled);
        this.updateTankSubscription();
    }

    public void attachConfigurators(ConfiguratorPanel configuratorPanel) {
        super.attachConfigurators(configuratorPanel);
        if (this.io == IO.IN) {
            configuratorPanel.attachConfigurators(new CircuitFancyConfigurator(this.circuitInventory.storage));
        }
    }

    public Widget createUIWidget() {
        return this.slots == 1 ? this.createSingleSlotGUI() : this.createMultiSlotGUI();
    }

    protected Widget createSingleSlotGUI() {
        WidgetGroup group = new WidgetGroup(0, 0, 89, 63);
        group.addWidget(new ImageWidget(4, 4, 81, 55, GuiTextures.DISPLAY));
        TankWidget tankWidget;
        if (this.io == IO.OUT) {
            group.addWidget(tankWidget = (new ScrollablePhantomFluidWidget(this.tank.getLockedFluid(), 0, 67, 40, 18, 18, () -> this.tank.getLockedFluid()
                    .getFluid(), (f) -> {
                        if (this.tank.getFluidInTank(0)
                                .isEmpty()) {
                            if (f != null && !f.isEmpty()) {
                                this.tank.setLocked(true, f.copy());
                            } else {
                                this.tank.setLocked(false);
                            }

                        }
                    })).setShowAmount(true)
                    .setDrawHoverTips(true)
                    .setBackground(GuiTextures.FLUID_SLOT));
            ResourceTexture texture = GuiTextures.BUTTON_LOCK;
            NotifiableFluidTank fluidTank = this.tank;
            Objects.requireNonNull(fluidTank);
            BooleanSupplier var3 = fluidTank::isLocked;
            NotifiableFluidTank notifiableFluidTank = this.tank;
            Objects.requireNonNull(notifiableFluidTank);
            group.addWidget((new ToggleButtonWidget(7, 40, 18, 18, texture, var3, notifiableFluidTank::setLocked)).setTooltipText("gtceu.gui.fluid_lock.tooltip")
                    .setShouldUseBaseBackground())
                    .addWidget((new TankWidget(this.tank.getStorages()[0], 67, 22, 18, 18, true, this.io.support(IO.IN))).setShowAmount(true)
                            .setDrawHoverTips(true)
                            .setBackground(GuiTextures.FLUID_SLOT));
        } else {
            group.addWidget(tankWidget = (new TankWidget(this.tank.getStorages()[0], 67, 22, 18, 18, true, this.io.support(IO.IN))).setShowAmount(true)
                    .setDrawHoverTips(true)
                    .setBackground(GuiTextures.FLUID_SLOT));
        }

        group.addWidget(new LabelWidget(8, 8, "gtceu.gui.fluid_amount"))
                .addWidget(new LabelWidget(8, 18, () -> this.getFluidAmountText(tankWidget)))
                .addWidget(new LabelWidget(8, 28, () -> this.getFluidNameText(tankWidget)
                        .getString()));
        group.setBackground(GuiTextures.BACKGROUND_INVERSE);
        return group;
    }

    private Component getFluidNameText(TankWidget tankWidget) {
        Component translation;
        if (!this.tank.getFluidInTank(tankWidget.getTank())
                .isEmpty()) {
            translation = this.tank.getFluidInTank(tankWidget.getTank())
                    .getDisplayName();
        } else {
            translation = this.tank.getLockedFluid()
                    .getFluid()
                    .getDisplayName();
        }

        return translation;
    }

    private String getFluidAmountText(TankWidget tankWidget) {
        String fluidAmount = "";
        if (!this.tank.getFluidInTank(tankWidget.getTank())
                .isEmpty()) {
            fluidAmount = this.getFormattedFluidAmount(this.tank.getFluidInTank(tankWidget.getTank()));
        } else if (!this.tank.getLockedFluid()
                .getFluid()
                .isEmpty()) {
                    fluidAmount = "0";
                }

        return fluidAmount;
    }

    public String getFormattedFluidAmount(FluidStack fluidStack) {
        return String.format("%,d", fluidStack.isEmpty() ? 0L : fluidStack.getAmount());
    }

    protected Widget createMultiSlotGUI() {
        int rowSize = (int) Math.sqrt(this.slots);
        int colSize = rowSize;

        WidgetGroup group = new WidgetGroup(0, 0, 18 * rowSize + 16, 18 * colSize * 2 + 16);
        WidgetGroup container = new WidgetGroup(4, 4, 18 * rowSize + 8, 18 * colSize * 2 + 8);
        int index = 0;
        if (this.tank instanceof ConfigNotifiableFluidTank ctank) {
            for (int y = 0; y < colSize; ++y) {
                for (int x = 0; x < rowSize; ++x) {
                    container.addWidget(new HugeTankWidget(ctank.getStorages()[index], 4 + x * 18, 4 + y * 36 + 18, true, this.io.support(IO.IN)).setBackground(GuiTextures.FLUID_SLOT));
                    int finalIndex = index;
                    container.addWidget((new PhantomFluidCapacityWidget(ctank, ctank.getLockedFluids()[index], index++, 4 + x * 18, 4 + y * 36, 18, 18,
                            () -> ctank.getLockedFluids()[finalIndex].getFluid(), (f) -> {
                                if (ctank.getFluidInTank(finalIndex).isEmpty()) {
                                    if (f != null && !f.isEmpty()) {
                                        ctank.setLocked(true, finalIndex, f.copy());
                                    } else {
                                        ctank.setLocked(false, finalIndex);
                                    }
                                }
                            })).setShowAmount(true)
                            .setDrawHoverTips(true)
                            .setBackground(GuiTextures.FLUID_SLOT));
                }
            }
        }

        container.setBackground(GuiTextures.BACKGROUND_INVERSE);
        group.addWidget(container);
        return group;
    }
}
