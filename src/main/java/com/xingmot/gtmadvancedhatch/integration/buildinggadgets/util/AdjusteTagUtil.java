package com.xingmot.gtmadvancedhatch.integration.buildinggadgets.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

import appeng.blockentity.AEBaseBlockEntity;

/**
 * 复制机tag加工工具类
 * （我们主要在default开头的方法中编写适配逻辑
 */
public class AdjusteTagUtil {

    /** 复制工具核心修改 tag 方法 */
    public static CompoundTag getEmptyStorageTag(CompoundTag tag) {
        emptyCustomTagContent(tag);
        emptyAHTagContent(tag);
        return defaultEmptyTagContent(tag);
    }

    public static boolean isModBlackList(BlockEntity blockEntity) {
        return defaultIsModBlackList(blockEntity) || customIsModBlackList(blockEntity);
    }

    /** 自定义清除数据 */ // TODO 给KubeJS留的接口，此处等待实现读取kubejs
    public static CompoundTag emptyCustomTagContent(CompoundTag tag) {
        return tag;
    }

    /**
     * 自定义复制方块的模组黑名单
     *
     * @return 如果是黑名单中的物品或方块则返回true（即不加载nbt）
     */ // TODO 给KubeJS留的接口，此处等待实现读取kubejs
    public static boolean customIsModBlackList(BlockEntity blockEntity) {
        return false;
    }

    /** 默认清除存储 */
    public static CompoundTag defaultEmptyTagContent(CompoundTag tag) {
        emptyTagContent(tag, "inv");
        return emptyTagContent(tag, "Items");
    }

    /** 默认复制方块的模组黑名单 */
    public static boolean defaultIsModBlackList(BlockEntity blockEntity) {
        if (blockEntity == null) return false;
        return blockEntity instanceof AEBaseBlockEntity;
    }

    /** gt清除存储 */
    public static CompoundTag gtEmptyTagContent(CompoundTag tag) {
        // 清空输入输出仓流体
        emptyTagContentOnly(tag, "storages", List.of("tank"));
        // 清空ME输入仓的流体(库存的虽然也清了，但是不影响
        emptyTagContentOnly(tag, "stock", List.of("tank"));
        // 清空物品存储，排除电路槽位
        return emptyTagContentExcept(tag, "storage", List.of("circuitInventory"));
    }

    /** 本模组机器的特殊处理 */
    public static CompoundTag emptyAHTagContent(CompoundTag tag) {
        if ("gtmadvancedhatch:adaptive_net_energy_terminal".equals(tag.getString("id"))) {
            tag.putLong("frequency", 0L);
            tag.putBoolean("isSlave", false);
        }
        return tag;
    }

    public static CompoundTag emptyTagContent(CompoundTag tag, String name) {
        return emptyTagContentExcept(tag, name, null);
    }

    /** 递归清除名称为name的tag存储。except为黑名单 */
    public static CompoundTag emptyTagContentExcept(CompoundTag tag, String name, List<String> except) {
        if (tag.contains(name) && tag.getTagType(name) == CompoundTag.TAG_COMPOUND) {
            tag.put(name, new CompoundTag());
        } else if (!tag.getAllKeys()
                .isEmpty()) {
                    for (String key : tag.getAllKeys()) {
                        if (tag.getTagType(key) == CompoundTag.TAG_COMPOUND && except != null && !except.contains(key))
                            emptyTagContentExcept(tag.getCompound(key), name, except);
                    }
                }
        return tag;
    }

    /** 递归清除名称为name的tag存储。only为白名单 */
    public static CompoundTag emptyTagContentOnly(CompoundTag tag, String name, List<String> only) {
        if (tag.contains(name) && tag.getTagType(name) == CompoundTag.TAG_COMPOUND) {
            tag.put(name, new CompoundTag());
        } else if (!tag.getAllKeys()
                .isEmpty()) {
                    for (String key : tag.getAllKeys()) {
                        if (tag.getTagType(key) == CompoundTag.TAG_COMPOUND && only != null && only.contains(key))
                            emptyTagContentOnly(tag.getCompound(key), name, only);
                    }
                }
        return tag;
    }
}
