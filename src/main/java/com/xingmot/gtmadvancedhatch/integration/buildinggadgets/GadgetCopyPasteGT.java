package com.xingmot.gtmadvancedhatch.integration.buildinggadgets;

import com.direwolf20.buildinggadgets2.api.gadgets.GadgetTarget;
import com.direwolf20.buildinggadgets2.common.items.GadgetCopyPaste;
import com.direwolf20.buildinggadgets2.common.worlddata.BG2Data;
import com.direwolf20.buildinggadgets2.util.GadgetNBT;
import com.direwolf20.buildinggadgets2.util.VectorHelper;
import com.direwolf20.buildinggadgets2.util.context.ItemActionContext;
import com.direwolf20.buildinggadgets2.util.datatypes.StatePos;
import com.direwolf20.buildinggadgets2.util.datatypes.TagPos;
import com.direwolf20.buildinggadgets2.util.modes.BaseMode;
import com.direwolf20.buildinggadgets2.util.modes.Copy;
import com.direwolf20.buildinggadgets2.util.modes.Paste;
import com.xingmot.gtmadvancedhatch.GTMAdvancedHatch;
import com.xingmot.gtmadvancedhatch.integration.buildinggadgets.util.AdjusteTagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class GadgetCopyPasteGT extends GadgetCopyPaste {
    @Override
    public int getEnergyMax() {
        return BuildingGadgetConfig.INSTANCE.buildingGadgetMaxPower;
    }

    InteractionResultHolder<ItemStack> onAction(ItemActionContext context) {
        var gadget = context.stack();

        var mode = GadgetNBT.getMode(gadget);
        if (mode.getId().getPath().equals("copy")) {
            GadgetNBT.setCopyStartPos(gadget, context.pos());
            /* 关键在于此处：复制方块时需要把GT配置，电网绑定等信息拿到 */
            buildAndStore(context, gadget);
        } else if (mode.getId().getPath().equals("paste")) {
            UUID uuid = GadgetNBT.getUUID(gadget);
            BG2Data bg2Data = BG2Data.get(Objects.requireNonNull(context.player().level().getServer()).overworld());
            ArrayList<StatePos> buildList = bg2Data.getCopyPasteList(uuid, false); //Don't remove the data just yet
            ArrayList<TagPos> tagList = bg2Data.peekTEMap(uuid);
            BuildingUtilsGT.buildWithTileData(context.level(), context.player(), buildList, getHitPos(context).above().offset(GadgetNBT.getRelativePaste(gadget)), tagList, gadget,true,false);
            return InteractionResultHolder.success(gadget);
        } else {
            return InteractionResultHolder.pass(gadget);
        }

        return InteractionResultHolder.success(gadget);
    }

    InteractionResultHolder<ItemStack> onShiftAction(ItemActionContext context) {
        ItemStack gadget = context.stack();
        BaseMode mode = GadgetNBT.getMode(gadget);
        if (mode.getId().getPath().equals("copy")) {
            GadgetNBT.setCopyEndPos(gadget, context.pos());
            this.buildAndStore(context, gadget);
        } else if (!mode.equals(new Paste())) {
            return InteractionResultHolder.pass(gadget);
        }

        return InteractionResultHolder.success(gadget);
    }

    /** 复制方法：这里需要根据不同方块进行定制 */
    public void buildAndStore(ItemActionContext context, ItemStack gadget) {
        ArrayList<StatePos> buildList = (new Copy()).collect(context.hitResult().getDirection(), context.player(), context.pos(), Blocks.AIR.defaultBlockState());
        UUID uuid = GadgetNBT.getUUID(gadget);
        GadgetNBT.setCopyUUID(gadget);
        BG2Data bg2Data = BG2Data.get(((MinecraftServer)Objects.requireNonNull(context.player().level().getServer())).overworld());
        bg2Data.addToCopyPaste(uuid, buildList);
        ArrayList<TagPos> teData = new ArrayList<>();
        Level level = context.level();
        for (StatePos statePos : buildList) {
            BlockPos blockPos = statePos.pos.offset(GadgetNBT.getCopyStartPos(gadget));
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity != null) {
                CompoundTag blockTag = blockEntity.saveWithFullMetadata();
                blockTag = AdjusteTagUtil.getEmptyStorageTag(blockTag);
                GTMAdvancedHatch.LOGGER.info("blockTag: {}", blockTag);
                TagPos tagPos = new TagPos(blockTag, blockPos.subtract(GadgetNBT.getCopyStartPos(gadget)));
                teData.add(tagPos);
            }
        }
        bg2Data.addToTEMap(uuid,teData);
        context.player().displayClientMessage(Component.translatable("buildinggadgets2.messages.copyblocks", new Object[]{buildList.size()}), true);
    }

    public GadgetTarget gadgetTarget() {
        return GadgetTarget.COPYPASTE;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack gadget = player.getItemInHand(hand);

        if (level.isClientSide()) //No client
            return InteractionResultHolder.success(gadget);

        BlockHitResult lookingAt = VectorHelper.getLookingAt(player, gadget);
        if (level.getBlockState(lookingAt.getBlockPos()).isAir() && GadgetNBT.getAnchorPos(gadget).equals(GadgetNBT.nullPos))
            return InteractionResultHolder.success(gadget);
        ItemActionContext context = new ItemActionContext(lookingAt.getBlockPos(), lookingAt, player, level, hand, gadget);

        if (player.isShiftKeyDown()) {
            if (GadgetNBT.getSetting(gadget, "bind")) {
                if (bindToInventory(level, player, gadget, lookingAt)) {
                    GadgetNBT.toggleSetting(gadget, "bind"); //Turn off bind
                    return InteractionResultHolder.success(gadget);
                } else {
                    return InteractionResultHolder.fail(gadget);
                }
            }
            return this.onShiftAction(context);
        }

        return this.onAction(context);
    }
}
