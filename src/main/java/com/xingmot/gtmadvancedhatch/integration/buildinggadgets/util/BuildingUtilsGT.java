package com.xingmot.gtmadvancedhatch.integration.buildinggadgets.util;

import com.xingmot.gtmadvancedhatch.config.AHConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import appeng.block.networking.CableBusBlock;
import com.direwolf20.buildinggadgets2.common.events.ServerBuildList;
import com.direwolf20.buildinggadgets2.common.events.ServerTickHandler;
import com.direwolf20.buildinggadgets2.common.items.GadgetBuilding;
import com.direwolf20.buildinggadgets2.common.worlddata.BG2Data;
import com.direwolf20.buildinggadgets2.util.*;
import com.direwolf20.buildinggadgets2.util.datatypes.StatePos;
import com.direwolf20.buildinggadgets2.util.datatypes.TagPos;
import org.jetbrains.annotations.NotNull;

public class BuildingUtilsGT extends BuildingUtils {

    public static @NotNull UUID build(Level level, Player player, ArrayList<StatePos> blockPosList, BlockPos lookingAt, ItemStack gadget, boolean needItems) {
        UUID buildUUID = UUID.randomUUID();
        FakeRenderingWorld fakeRenderingWorld = new FakeRenderingWorld(level, blockPosList, lookingAt);
        DimBlockPos boundPos = GadgetNBT.getBoundPos(gadget);
        int dir = boundPos == null ? -1 : GadgetNBT.getToolValue(gadget, "binddirection");
        Direction direction = dir == -1 ? null : Direction.values()[dir];

        for (StatePos pos : blockPosList) {
            if (!pos.state.isAir()) {
                BlockPos blockPos = pos.pos;
                if (level.mayInteract(player, blockPos.offset(lookingAt)) && level.getBlockState(blockPos.offset(lookingAt)).canBeReplaced() && (!(gadget.getItem() instanceof GadgetBuilding) || !needItems || pos.state.canSurvive(level, blockPos.offset(lookingAt)))) {
                    if (pos.state.getFluidState().isEmpty()) {
                        List<ItemStack> neededItems = GadgetUtils.getDropsForBlockState((ServerLevel) level, blockPos.offset(lookingAt), pos.state, player);
                        if (!player.isCreative() && needItems && !removeStacksFromInventory(player, neededItems, true, boundPos, direction)) {
                            if (!(pos.state.getBlock() instanceof CableBusBlock) || !AHConfig.INSTANCE.buildingGadgetBuildAE2)
                                continue;
                        }
                    } else {
                        FluidState fluidState = pos.state.getFluidState();
                        if (!fluidState.isEmpty() && fluidState.isSource()) {
                            Fluid fluid = fluidState.getType();
                            FluidStack fluidStack = new FluidStack(fluid, 1000);
                            if (!player.isCreative() && needItems && !removeFluidStacksFromInventory(player, fluidStack, true, boundPos, direction)) {
                                if (!(pos.state.getBlock() instanceof CableBusBlock) || !AHConfig.INSTANCE.buildingGadgetBuildAE2)
                                    continue;
                            }
                        }
                    }

                    if (!player.isCreative() && !hasEnoughEnergy(gadget)) {
                        player.displayClientMessage(Component.translatable("buildinggadgets2.messages.outofpower"), true);
                        break;
                    }

                    if (!player.isCreative()) {
                        useEnergy(gadget);
                    }

                    ServerTickHandler.addToMap(buildUUID, new StatePos(fakeRenderingWorld.getBlockStateWithoutReal(pos.pos), pos.pos), level, GadgetNBT.getRenderTypeByte(gadget), player, needItems, false, gadget, ServerBuildList.BuildType.BUILD, true, lookingAt);
                }
            }
        }

        return buildUUID;
    }

    public static @NotNull UUID exchange(Level level, Player player, ArrayList<StatePos> blockPosList, BlockPos lookingAt, ItemStack gadget, boolean needItems, boolean returnItems) {
        UUID buildUUID = UUID.randomUUID();
        FakeRenderingWorld fakeRenderingWorld = new FakeRenderingWorld(level, blockPosList, lookingAt);
        DimBlockPos boundPos = GadgetNBT.getBoundPos(gadget);
        int dir = boundPos == null ? -1 : GadgetNBT.getToolValue(gadget, "binddirection");
        Direction direction = dir == -1 ? null : Direction.values()[dir];

        for (StatePos pos : blockPosList) {
            BlockPos blockPos = pos.pos;
            if (level.mayInteract(player, blockPos.offset(lookingAt)) && !level.getBlockState(blockPos.offset(lookingAt)).equals(pos.state) && GadgetUtils.isValidBlockState(level.getBlockState(blockPos.offset(lookingAt)), level, blockPos) && (!(gadget.getItem() instanceof GadgetBuilding) || !needItems || pos.state.canSurvive(level, blockPos.offset(lookingAt)))) {
                if (pos.state.getFluidState().isEmpty()) {
                    List<ItemStack> neededItems = GadgetUtils.getDropsForBlockState((ServerLevel) level, blockPos.offset(lookingAt), pos.state, player);
                    if (!player.isCreative() && needItems && !pos.state.isAir() && !removeStacksFromInventory(player, neededItems, true, boundPos, direction)) {
                        continue;
                    }
                } else {
                    FluidState fluidState = pos.state.getFluidState();
                    if (!fluidState.isEmpty() && fluidState.isSource()) {
                        Fluid fluid = fluidState.getType();
                        FluidStack fluidStack = new FluidStack(fluid, 1000);
                        if (!player.isCreative() && needItems && !removeFluidStacksFromInventory(player, fluidStack, true, boundPos, direction)) {
                            continue;
                        }
                    }
                }

                if (!player.isCreative() && !hasEnoughEnergy(gadget)) {
                    player.displayClientMessage(Component.translatable("buildinggadgets2.messages.outofpower"), true);
                    break;
                }

                if (!player.isCreative()) {
                    useEnergy(gadget);
                }

                ServerTickHandler.addToMap(buildUUID, new StatePos(fakeRenderingWorld.getBlockStateWithoutReal(pos.pos), pos.pos), level, GadgetNBT.getRenderTypeByte(gadget), player, needItems, returnItems, gadget, ServerBuildList.BuildType.EXCHANGE, true, lookingAt);
            }
        }

        return buildUUID;
    }

    public static @NotNull ArrayList<StatePos> buildWithTileData(Level level, Player player, ArrayList<StatePos> blockPosList, BlockPos lookingAt, ArrayList<TagPos> teData, ItemStack gadget, boolean needItems, boolean returnItems) {
        ArrayList<StatePos> actuallyBuiltList = new ArrayList();
        if (teData == null) {
            return actuallyBuiltList;
        } else {
            boolean replace = GadgetNBT.getPasteReplace(gadget);
            UUID buildUUID;
            if (!replace) {
                buildUUID = build(level, player, blockPosList, lookingAt, gadget, needItems);
            } else {
                buildUUID = exchange(level, player, blockPosList, lookingAt, gadget, needItems, returnItems);
            }

            ServerTickHandler.addTEData(buildUUID, teData);
            BG2Data bg2Data = BG2Data.get(((MinecraftServer) Objects.requireNonNull(level.getServer())).overworld());
            if (bg2Data.containsUndoList(GadgetNBT.getUUID(gadget))) {
                GadgetUtils.addToUndoList(level, gadget, null, GadgetNBT.getUUID(gadget));
            }

            return actuallyBuiltList;
        }
    }
}
