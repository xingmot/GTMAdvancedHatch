package com.xingmot.gtmadvancedhatch.api;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;

import com.lowdragmc.lowdraglib.misc.FluidStorage;
import com.lowdragmc.lowdraglib.side.fluid.FluidHelper;
import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import java.util.function.Predicate;

import lombok.Getter;

/**
 * 容量可变，可单独设置过滤的流体存储
 */
public class ConfigNotifiableFluidTank extends NotifiableFluidTank implements IConfigFluidTransfer {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(ConfigNotifiableFluidTank.class, NotifiableRecipeHandlerTrait.MANAGED_FIELD_HOLDER);
    @Getter
    @Persisted
    @DescSynced
    protected FluidStorage[] lockedFluids;
    @Persisted
    protected boolean isLockedEmptySlot;

    public ConfigNotifiableFluidTank(MetaMachine machine, int slots, long capacity, IO io, IO capabilityIO) {
        super(machine, slots, capacity, io, capabilityIO);
        this.lockedFluids = new FluidStorage[slots];
        for (int i = 0; i < slots; i++)
            lockedFluids[i] = new FluidStorage(capacity);
    }

    public ConfigNotifiableFluidTank(MetaMachine machine, int slots, long capacity, IO io) {
        this(machine, slots, capacity, io, io);
    }

    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void newTankCapacity(long capacity) {
        resetBasicInfo(capacity);
    }

    @Override
    public void newTankCapacity(int tank, long capacity) {
        resetOneBasicInfo(tank, capacity);
    }

    /**
     * 设置全部格子的流体容量
     *
     * @param capacity 容量
     */
    public void resetBasicInfo(long capacity) {
        for (int i = 0; i < this.getSize(); i++) {
            resetOneBasicInfo(i, capacity);
        }
    }

    /**
     * 单独设置某一格的流体容量
     *
     * @param index    格子索引
     * @param capacity 容量，最小为1桶，最大为long.max
     */
    public void resetOneBasicInfo(int index, long capacity) {
        capacity = Math.max(capacity, 0);
        // 容量小于流体量时进行截断
        if (isTruncateFluid(index, capacity))
            this.getFluidInTank(index).setAmount(capacity);
        this.getStorages()[index].setCapacity(capacity);
        lockedFluids[index].setCapacity(capacity);
    }

    @Override
    public boolean isTruncateFluid(int index, long capacity) {
        return !this.getFluidInTank(index).isEmpty() && capacity < this.getFluidInTank(index).getAmount();
    }

    public boolean test(FluidIngredient ingredient) {
        if (ingredient.isEmpty()) return false;
        boolean result = false;
        for (int i = 0; i < this.getSize(); i++) {
            result = !isLocked(i) || ingredient.test(this.getFluidInTank(i));
            if (result)
                break;
        }
        return result;
    }

    public int getPriority() {
        return this.isLocked() && !this.lockedFluid.getFluid()
                .isEmpty() ? 1073741823 - this.getTanks() : super.getPriority();
    }

    @Override
    public FluidStorage getLockedFluid() {
        return this.lockedFluids[0];
    }

    @Override
    public boolean isLocked() {
        return this.isLocked(0);
    }

    @Override
    public void setLocked(boolean locked) {
        this.setLocked(locked, 0);
    }

    @Override
    public void setLocked(boolean locked, FluidStack fluidStack) {
        this.setLocked(locked, 0, fluidStack);
    }

    /** 这个方法的index不会超出大小 */
    public boolean isLocked(int index) {
        return !isLockedEmptySlot && !this.lockedFluids[index].getFluid()
                .isEmpty();
    }

    public void setLocked(boolean locked, int tank) {
        if (tank < this.getSize() && this.isLocked(tank) != locked) {
            FluidStack fluidStack = this.getStorages()[tank].getFluid();
            if (locked && !fluidStack.isEmpty()) {
                this.lockedFluids[tank].setFluid(fluidStack.copy());
                this.lockedFluids[tank].getFluid()
                        .setAmount(1L);
                this.onContentsChanged();
                this.setFilter(tank, (stack) -> stack.isFluidEqual(this.lockedFluid.getFluid()));
            } else {
                this.lockedFluids[tank].setFluid(FluidStack.empty());
                this.setFilter(tank, (stack) -> true);
                this.onContentsChanged();
            }

        }
    }

    public void setLocked(boolean locked, int tank, FluidStack fluidStack) {
        if (tank < this.getSize() && this.isLocked(tank) != locked) {
            if (locked && !fluidStack.isEmpty()) {
                this.lockedFluids[tank].setFluid(fluidStack.copy());
                this.lockedFluids[tank].getFluid()
                        .setAmount(FluidHelper.getBucket());
                this.onContentsChanged();
                this.setFilter(tank, (stack) -> stack.isFluidEqual(this.lockedFluid.getFluid()));
            } else {
                this.lockedFluids[tank].setFluid(FluidStack.empty());
                this.setFilter(tank, (stack) -> true);
                this.onContentsChanged();
            }

        }
    }

    public NotifiableFluidTank setFilter(int tank, Predicate<FluidStack> filter) {
        this.getStorages()[tank].setValidator(filter);
        return this;
    }
}
