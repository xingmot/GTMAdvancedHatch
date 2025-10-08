package com.xingmot.gtmadvancedhatch.common.data;

import com.gregtechceu.gtceu.api.item.ComponentItem;

import java.util.List;

import com.hepdd.gtmthings.data.CustomItems;
import com.tterrag.registrate.util.entry.ItemEntry;

public final class ConstantsMachines {

    private ConstantsMachines() {}

    // 我自己用的开关，true可以一键关闭GTL联动，方便测试
    public static boolean isDebugNoOthers = false;

    // ULV:1*1 LV:2*3 MV:3*5 HV:4*7 EV:5*9 Luv:6*10 UV:7*10...
    public static int getLockItemOutputBusSlotRow(int tier) {
        return 1 + Math.min(9, 2 * tier);
    } // 行宽row 即列数

    public static int getLockItemOutputBusSlotCol(int tier) {
        return 1 + Math.min(9, tier);
    } // 列宽col 即行数

    public static int getLockItemOutputBusSlot(int tier) {
        return getLockItemOutputBusSlotRow(tier) * getLockItemOutputBusSlotCol(tier);
    }

    // region GTMThings
    static List<ItemEntry<ComponentItem>> WIRELESS_ENERGY_RECEIVE_COVER = List.of(
            CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_LV,
            CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_MV,
            CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_HV,
            CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_EV,
            CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_IV,
            CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_LUV,
            CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_ZPM,
            CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_UV,
            CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_UHV,
            CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_UEV,
            CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_UIV,
            CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_UXV,
            CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_OPV);

    static List<ItemEntry<ComponentItem>> WIRELESS_ENERGY_RECEIVE_COVER_4A = List.of(
            CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_LV_4A,
            CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_MV_4A,
            CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_HV_4A,
            CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_EV_4A,
            CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_IV_4A,
            CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_LUV_4A,
            CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_ZPM_4A,
            CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_UV_4A,
            CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_UHV_4A,
            CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_UEV_4A,
            CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_UIV_4A,
            CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_UXV_4A,
            CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_OPV_4A);
    // endregion
}
