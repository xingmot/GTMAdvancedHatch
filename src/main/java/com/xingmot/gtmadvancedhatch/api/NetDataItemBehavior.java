package com.xingmot.gtmadvancedhatch.api;

import com.xingmot.gtmadvancedhatch.common.data.TagConstants;

import com.gregtechceu.gtceu.api.item.component.IAddInformation;
import com.gregtechceu.gtceu.api.item.component.IInteractionItem;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

public class NetDataItemBehavior implements IInteractionItem, IAddInformation {

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(TagConstants.ADAPTIVE_NET_UUID)) {
            UUID uuid = tag.getUUID(TagConstants.ADAPTIVE_NET_UUID);
            String name = tag.getString(TagConstants.ADAPTIVE_NET_NAME);
            tooltipComponents.add(Component.literal("UUID: ").withStyle(ChatFormatting.GRAY).append(Component.literal(uuid.toString()).withStyle(ChatFormatting.YELLOW)));
            if (name.equals("everyone"))
                tooltipComponents.add(Component.translatable("gtmadvancedhatch.machine.adaptivee.player").withStyle(ChatFormatting.GRAY).append(Component.translatable("gtmadvancedhatch.gui.binduuid.everyone").withStyle(ChatFormatting.YELLOW)));
            else
                tooltipComponents.add(Component.translatable("gtmadvancedhatch.machine.adaptivee.player").withStyle(ChatFormatting.GRAY).append(Component.literal(name).withStyle(ChatFormatting.YELLOW)));
        }
        if (tag.contains(TagConstants.ADAPTIVE_NET_FREQUENCY)) {
            long frequency = tag.getLong("adaptive_net_frequency");
            tooltipComponents.add(Component.translatable("gtmadvancedhatch.machine.adaptivee.frequency").withStyle(ChatFormatting.GRAY).append(Component.literal("" + frequency).withStyle(ChatFormatting.AQUA)));
        }
    }

    // 复制数据
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() == null) return InteractionResult.PASS;
        ItemStack left = context.getPlayer().getOffhandItem();
        ItemStack right = context.getItemInHand();
        if (left.is(right.getItem()) && left.hasTag()) {
            right.setTag(left.getTag());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
