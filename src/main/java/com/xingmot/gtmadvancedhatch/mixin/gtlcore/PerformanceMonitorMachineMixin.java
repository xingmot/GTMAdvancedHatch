package com.xingmot.gtmadvancedhatch.mixin.gtlcore;

import com.xingmot.gtmadvancedhatch.util.copy.MessageUtil;

import org.gtlcore.gtlcore.api.machine.PerformanceMonitorMachine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;

import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.gtlcore.gtlcore.api.machine.PerformanceMonitorMachine.*;

import com.glodblock.github.extendedae.client.render.EAEHighlightHandler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(PerformanceMonitorMachine.class)
public class PerformanceMonitorMachineMixin extends MetaMachine implements IFancyUIMachine {

    @Unique
    private List<Component> gtmadvancedhatch$textListCache;
    @Unique
    private static Player gtmadvancedhatch$player;

    public PerformanceMonitorMachineMixin(IMachineBlockEntity holder) {
        super(holder);
    }

    @Inject(remap = false, method = "handleDisplayClick", at = @At("HEAD"), cancellable = true)
    private static void handleDisplayClickMixin(String componentData, ClickData clickData, CallbackInfo ci) {
        if (clickData.isRemote) {
            String[] parts = componentData.split(", ");
            BlockPos pos = new BlockPos(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
            EAEHighlightHandler.highlight(pos, getDimensionKey(new ResourceLocation(parts[3])), System.currentTimeMillis() + 15000L);
            if (gtmadvancedhatch$player != null) {
                Component message = MessageUtil.createEnhancedHighlightMessage(gtmadvancedhatch$player, pos, gtmadvancedhatch$player.level()
                        .dimension(), Component.translatable(parts[4]), "gtmthings.machine.highlight");
                gtmadvancedhatch$player.displayClientMessage(message, false);
            }
        }
        ci.cancel();
    }

    @Inject(remap = false, method = "addDisplayText", at = @At("HEAD"), cancellable = true)
    private void addDisplayTextMixin(@NotNull List<Component> textList, CallbackInfo ci) {
        if (isRemote()) return;
        observe = true;
        if (gtmadvancedhatch$textListCache == null || getOffsetTimer() % 40 == 0) {
            gtmadvancedhatch$textListCache = new ArrayList<>();
            Map<MetaMachine, Integer> sortedMap = new TreeMap<>((mm1, mm2) -> PERFORMANCE_MAP.get(mm2)
                    .compareTo(PERFORMANCE_MAP.get(mm1)));
            sortedMap.putAll(PERFORMANCE_MAP);
            PERFORMANCE_MAP.clear();
            for (Map.Entry<MetaMachine, Integer> entry : sortedMap.entrySet()) {
                MetaMachine machine = entry.getKey();
                String pos = machine.getPos()
                        .toShortString();
                String machineName = machine.getBlockState()
                        .getBlock()
                        .getDescriptionId();
                Level level = machine.getLevel();
                if (level == null) continue;
                gtmadvancedhatch$textListCache.add(Component.translatable(machineName)
                        .append(" ")
                        .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("recipe.condition.dimension.tooltip", level.dimension()
                                .location())
                                .append(" [")
                                .append(pos)
                                .append("] "))))
                        .append(Component.translatable("tooltip.jade.delay", entry.getValue())
                                .append(" μs"))
                        .append(ComponentPanelWidget.withButton(Component.literal(" [ ] "), pos + ", " + level.dimension()
                                .location() + ", " + machineName)));
            }
        }
        textList.addAll(gtmadvancedhatch$textListCache);
        ci.cancel();
    }

    @Override
    public boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        gtmadvancedhatch$player = player;
        return true;
    }
}
