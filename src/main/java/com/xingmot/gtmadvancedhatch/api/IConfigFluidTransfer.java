package com.xingmot.gtmadvancedhatch.api;

import com.lowdragmc.lowdraglib.side.fluid.IFluidTransfer;

public interface IConfigFluidTransfer extends IFluidTransfer {

    void newTankCapacity(long capacity);

    void newTankCapacity(int tank, long capacity);
}
