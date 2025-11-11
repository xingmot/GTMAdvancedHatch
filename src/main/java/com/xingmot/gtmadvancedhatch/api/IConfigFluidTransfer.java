package com.xingmot.gtmadvancedhatch.api;

import com.lowdragmc.lowdraglib.side.fluid.IFluidTransfer;

/**
 * 可配置流体槽
 */
public interface IConfigFluidTransfer extends IFluidTransfer, IMultiCapacity {

    void newTankCapacity(long capacity);

    void newTankCapacity(int tank, long capacity);

    /**
     * 是否会截断流体
     * 
     * @param index    格子索引
     * @param capacity 容量
     */
    boolean isTruncateFluid(int index, long capacity);

    /**
     * 因为capacity并非FluidStorage持久化数据，因此需要手动实现持久化读取
     */
    void initTank();
}
