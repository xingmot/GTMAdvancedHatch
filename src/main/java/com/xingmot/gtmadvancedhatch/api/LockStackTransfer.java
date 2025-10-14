package com.xingmot.gtmadvancedhatch.api;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

import javax.annotation.Nonnull;

import com.hepdd.gtmthings.api.misc.UnlimitedItemStackTransfer;
import lombok.Setter;

public class LockStackTransfer extends UnlimitedItemStackTransfer {

    @Setter
    private Function<ItemStack, Boolean> filter;

    public LockStackTransfer() {
        this(1);
    }

    public LockStackTransfer(int size) {
        super(size);
    }

    public LockStackTransfer(NonNullList<ItemStack> stacks) {
        super(stacks);
    }

    public LockStackTransfer(ItemStack stack) {
        super(stack);
    }

    // @Nonnull
    // public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate, boolean notifyChanges) {
    // if (stack.isEmpty()) {
    // return ItemStack.EMPTY;
    // } else if (!this.isItemValid(slot, stack)) {
    // return stack;
    // } else {
    // this.validateSlotIndex(slot);
    // ItemStack existing = (ItemStack)this.stacks.get(slot);
    // int limit = this.getStackLimit(slot, stack);
    // if (!existing.isEmpty()) {
    // if (!ItemTransferHelper.canItemStacksStack(stack, existing)) {
    // return stack;
    // }
    //
    // limit -= existing.getCount();
    // }
    //
    // if (limit <= 0) {
    // return stack;
    // } else {
    // boolean reachedLimit = stack.getCount() > limit;
    // if (!simulate) {
    // if (existing.isEmpty()) {
    // this.stacks.set(slot, reachedLimit ? ItemTransferHelper.copyStackWithSize(stack, limit) : stack);
    // } else {
    // existing.grow(reachedLimit ? limit : stack.getCount());
    // }
    //
    // if (notifyChanges) {
    // this.onContentsChanged(slot);
    // }
    // }
    //
    // return reachedLimit ? ItemTransferHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    // }
    // }
    // }
    //
    // @Nonnull
    // public ItemStack extractItem(int slot, int amount, boolean simulate, boolean notifyChanges) {
    // if (amount == 0) {
    // return ItemStack.EMPTY;
    // } else {
    // this.validateSlotIndex(slot);
    // ItemStack existing = (ItemStack)this.stacks.get(slot);
    // if (existing.isEmpty()) {
    // return ItemStack.EMPTY;
    // } else {
    // int toExtract = Math.min(amount, existing.getMaxStackSize());
    // if (existing.getCount() <= toExtract) {
    // if (!simulate) {
    // this.stacks.set(slot, ItemStack.EMPTY);
    // if (notifyChanges) {
    // this.onContentsChanged(slot);
    // }
    //
    // return existing;
    // } else {
    // return existing.copy();
    // }
    // } else {
    // if (!simulate) {
    // this.stacks.set(slot, ItemTransferHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
    // if (notifyChanges) {
    // this.onContentsChanged(slot);
    // }
    // }
    //
    // return ItemTransferHelper.copyStackWithSize(existing, toExtract);
    // }
    // }
    // }
    // }

    public int getSlotLimit(int slot) {
        return 64;
    }

    protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
        return Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());
    }

    @Override
    public LockStackTransfer copy() {
        NonNullList<ItemStack> copiedStack = NonNullList.withSize(stacks.size(), ItemStack.EMPTY);
        for (int i = 0; i < stacks.size(); i++) {
            copiedStack.set(i, stacks.get(i).copy());
        }
        var copied = new LockStackTransfer(copiedStack);
        copied.setFilter(filter);
        return copied;
    }
}
