package com.xingmot.gtmadvancedhatch.api.adaptivenet;

import com.xingmot.gtmadvancedhatch.common.data.TagConstants;

import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

import javax.annotation.Nonnull;

import static com.xingmot.gtmadvancedhatch.api.adaptivenet.AdaptiveConstants.NET_TYPE_EMPTY;

import joptsimple.internal.Strings;

public record NetKey(@Nonnull String type, long freq, UUID owner) {

    @Override
    public String toString() {
        return (Strings.isNullOrEmpty(type) ? NET_TYPE_EMPTY : type) + "#" + freq + "@" + owner;
    }

    public CompoundTag toTag() {
        final CompoundTag tag = new CompoundTag();
        tag.putString("type", type);
        tag.putLong(TagConstants.ADAPTIVE_NET_FREQUENCY, freq);
        tag.putUUID(TagConstants.ADAPTIVE_NET_UUID, owner);
        return tag;
    }
}
