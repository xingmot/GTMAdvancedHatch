package com.xingmot.gtmadvancedhatch.api.gui;

/**
 * 可设置容量的虚拟槽位
 */
public interface IPhantomAmountWidget<T> {

    long getAmount();

    void setAmount(long capacity);

    /**
     * @return 返回虚拟槽位的物品
     */
    T getPhantomStack();
}
