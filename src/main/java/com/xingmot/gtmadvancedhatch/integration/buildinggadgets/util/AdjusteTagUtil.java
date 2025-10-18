package com.xingmot.gtmadvancedhatch.integration.buildinggadgets.util;

import net.minecraft.nbt.CompoundTag;

import java.util.List;

public class AdjusteTagUtil {
    public static CompoundTag getEmptyStorageTag(CompoundTag tag){
        emptyTagContent(tag,"storage",List.of("circuitInventory"));
        return emptyTagContent(tag,"Items",List.of("circuitInventory"));
    }

    public static CompoundTag emptyEnergy(CompoundTag tag,String name){
        return  tag;
    }
    public static CompoundTag emptyTagContent(CompoundTag tag,String name,List<String> except){
        if (tag.contains(name)&& tag.getTagType(name)==CompoundTag.TAG_COMPOUND) {
            tag.put(name, new CompoundTag());
        }else if(!tag.getAllKeys().isEmpty()){
            for (String key : tag.getAllKeys()) {
                if(tag.getTagType(key)== CompoundTag.TAG_COMPOUND && !except.contains(key))
                    emptyTagContent(tag.getCompound(key),name,except);
            }
        }
        return  tag;
    }
}
