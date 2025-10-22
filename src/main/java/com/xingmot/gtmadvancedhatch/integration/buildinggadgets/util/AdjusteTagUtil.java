package com.xingmot.gtmadvancedhatch.integration.buildinggadgets.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import appeng.me.helpers.IGridConnectedBlockEntity;

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

    /** 加载nbt时，如果此方法返回true则选择不载入nbt */
    public static boolean isModBlackListTag(BlockEntity blockEntity) {
        return defaultIsModBlackListTag(blockEntity) || customIsModBlackListTag(blockEntity);
    }

    /** 返回true表示该方块不能被复制 */
    public static boolean isModBlackListBlock(BlockState blockState) {
        return defaultIsModBlackListBlock(blockState) || customIsModBlackListBlock(blockState);
    }

    /** 自定义清除数据 */ // TODO 给KubeJS留的接口，此处等待实现读取kubejs
    public static CompoundTag emptyCustomTagContent(CompoundTag tag) {
        return tag;
    }

    /**
     * 自定义复制方块tag的模组黑名单
     *
     * @return 如果是黑名单中的物品或方块则返回true（即不加载nbt）
     */ // TODO 给KubeJS留的接口，此处等待实现读取kubejs
    public static boolean customIsModBlackListTag(BlockEntity blockEntity) {
        return false;
    }

    /**
     * 自定义复制方块的模组黑名单
     *
     * @return 如果是黑名单中的物品或方块则返回true（即不加载nbt）
     */ // TODO 给KubeJS留的接口，此处等待实现读取kubejs
    public static boolean customIsModBlackListBlock(BlockState block) {
        return false;
    }

    /** 默认清除存储 */
    public static CompoundTag defaultEmptyTagContent(CompoundTag tag) {
        emptyTagInv(tag, "inv");
        // 清除熔炉燃烧时间
        toZeroTag(tag, "BurnTime");
        toZeroTag(tag, "CookTime");
        toZeroTag(tag, "CookTimeTotal");
        return emptyTagInvExcept(tag, "Items", List.of("circuitInventory", "creativeStorage"));
    }

    /** 默认复制方块tag的模组黑名单 */
    public static boolean defaultIsModBlackListTag(BlockEntity blockEntity) {
        if (blockEntity == null) return false;
        return blockEntity instanceof IGridConnectedBlockEntity;
    }

    /** 默认复制方块的模组黑名单 */
    // 暂时还没有要直接禁止nbt载入的东西
    public static boolean defaultIsModBlackListBlock(BlockState block) {
        if (block == null) return false;
        return false;
    }

    /** gt清除存储 */
    public static CompoundTag gtEmptyTagContent(CompoundTag tag) {
        // 清空机器配方
        emptyTagInv(tag, "recipeLogic");
        emptyTagInv(tag, "cover");
        // 清空超级缸超级箱
        emptyTagFluidOnly(tag, "storages", List.of("cache"));
        emptyTagFluid(tag, "stored");
        emptyTagInvOnly(tag, "storage", List.of("cache"));
        // 清空输入输出仓流体
        emptyTagFluidOnly(tag, "storages", List.of("tank", "shareTank"));
        // 清空ME输入仓的流体(库存的虽然也清了，但是不影响
        emptyTagInvExcept(tag, "stock", List.of("circuitInventory"));
        // 清空ME输出总线、仓
        emptyTagInvExcept(tag, "internalBuffer", List.of("circuitInventory"));
        // 清空物品存储，排除电路槽位、超级箱缓存
        return emptyTagInvExcept(tag, "storage", List.of("circuitInventory", "cache"));
    }

    /** 本模组机器的特殊处理 */
    public static CompoundTag emptyAHTagContent(CompoundTag tag) {
        if ("gtmadvancedhatch:adaptive_net_energy_terminal".equals(tag.getString("id"))) {
            tag.putLong("frequency", 0L);
            tag.putBoolean("isSlave", false);
        }
        return tag;
    }

    public static CompoundTag emptyTagInv(CompoundTag tag, String name) {
        return emptyTagInvExcept(tag, name, null);
    }

    public static CompoundTag emptyTagFluid(CompoundTag tag, String name) {
        return emptyTagFluidExcept(tag, name, null);
    }

    /** 递归清除名称为name的tag存储。except为黑名单 */
    public static CompoundTag emptyTagInvExcept(CompoundTag tag, String name, List<String> except) {
        if (tag.contains(name) && tag.getTagType(name) == CompoundTag.TAG_COMPOUND) {
            tag.put(name, new CompoundTag());
        } else if (tag.contains(name) && tag.getTagType(name) == CompoundTag.TAG_LIST) {
            tag.getList(name, CompoundTag.TAG_COMPOUND).clear();
        } else if (!tag.getAllKeys().isEmpty()) {
            for (String key : tag.getAllKeys()) {
                if (except != null && except.contains(key)) continue;
                if (tag.getTagType(key) == CompoundTag.TAG_COMPOUND)
                    emptyTagInvExcept(tag.getCompound(key), name, except);
                else if (tag.getTagType(key) == CompoundTag.TAG_LIST) {
                    tag.getList(key, CompoundTag.TAG_COMPOUND).forEach(i -> emptyTagInvExcept((CompoundTag) i, name, except));
                }
            }
        }
        return tag;
    }

    /** 递归清除名称为name的tag存储。only为白名单 */
    public static CompoundTag emptyTagInvOnly(CompoundTag tag, String name, List<String> only) {
        if (only == null) return tag;
        for (String only_str : only) {
            if (tag.contains(only_str) && tag.getTagType(only_str) == CompoundTag.TAG_COMPOUND) {
                emptyTagInv(tag.getCompound(only_str), name);
            } else if (!tag.getAllKeys().isEmpty()) {
                for (String key : tag.getAllKeys()) {
                    if (tag.getTagType(key) == CompoundTag.TAG_COMPOUND)
                        emptyTagInvOnly(tag.getCompound(key), name, only);
                }
            }
        }
        return tag;
    }

    /** 递归清除名称为name的tag流体存储。except为黑名单 */
    public static CompoundTag emptyTagFluidExcept(CompoundTag tag, String name, List<String> except) {
        if (tag.contains(name) && tag.getTagType(name) == CompoundTag.TAG_LIST) {
            ListTag list = tag.getList(name, CompoundTag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag tagp = list.getCompound(i).getCompound("p");
                tagp.putLong("Amount", 0L);
                // list.set(i, tagp);
            }
        } else if (!tag.getAllKeys()
                .isEmpty()) {
                    for (String key : tag.getAllKeys()) {
                        if (except != null && except.contains(key)) continue;
                        if (tag.getTagType(key) == CompoundTag.TAG_COMPOUND)
                            emptyTagInvExcept(tag.getCompound(key), name, except);
                    }
                }
        return tag;
    }

    /** 递归清除名称为name的tag流体存储。only为白名单 */
    public static CompoundTag emptyTagFluidOnly(CompoundTag tag, String name, List<String> only) {
        if (only == null) return tag;
        for (String only_str : only) {
            if (tag.contains(only_str) && tag.getTagType(only_str) == CompoundTag.TAG_COMPOUND) {
                emptyTagFluid(tag.getCompound(only_str), name);
            } else if (!tag.getAllKeys().isEmpty()) {
                for (String key : tag.getAllKeys()) {
                    if (tag.getTagType(key) == CompoundTag.TAG_COMPOUND)
                        emptyTagInvOnly(tag.getCompound(key), name, only);
                }
            }
        }
        return tag;
    }

    /** 将name的值清0 */
    public static CompoundTag toZeroTag(CompoundTag tag, String name) {
        return toZeroTagExcept(tag, name, null);
    }

    public static CompoundTag toZeroTagExcept(CompoundTag tag, String name, List<String> except) {
        if (tag.contains(name)) {
            if (tag.getTagType(name) == CompoundTag.TAG_BYTE) tag.putByte(name, (byte) 0);
            if (tag.getTagType(name) == CompoundTag.TAG_SHORT) tag.putShort(name, (short) 0);
            if (tag.getTagType(name) == CompoundTag.TAG_INT) tag.putInt(name, 0);
            if (tag.getTagType(name) == CompoundTag.TAG_LONG) tag.putLong(name, 0L);
        } else if (!tag.getAllKeys().isEmpty()) {
            for (String key : tag.getAllKeys()) {
                if (except != null && except.contains(key)) continue;
                if (tag.getTagType(key) == CompoundTag.TAG_COMPOUND)
                    emptyTagInvExcept(tag.getCompound(key), name, except);
                else if (tag.getTagType(key) == CompoundTag.TAG_LIST) {
                    tag.getList(key, CompoundTag.TAG_COMPOUND).forEach(i -> emptyTagInvExcept((CompoundTag) i, name, except));
                }
            }
        }
        return tag;
    }
}
