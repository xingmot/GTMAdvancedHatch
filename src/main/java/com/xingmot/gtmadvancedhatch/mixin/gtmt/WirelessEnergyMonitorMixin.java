package com.xingmot.gtmadvancedhatch.mixin.gtmt;

import com.xingmot.gtmadvancedhatch.api.util.SortedEntriesStorage;
import com.xingmot.gtmadvancedhatch.integration.gtmt.newmonitor.EnergyStat;
import com.xingmot.gtmadvancedhatch.util.NumberUtils;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.utils.GTUtil;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.*;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static com.xingmot.gtmadvancedhatch.integration.gtmt.newmonitor.FormatUtil.formatWithConstantWidth;
import static com.xingmot.gtmadvancedhatch.util.NumberUtils.formatBigDecimalNumberOrSic;
import static com.xingmot.gtmadvancedhatch.util.NumberUtils.formatBigIntegerNumberOrSic;

import com.hepdd.gtmthings.api.misc.WirelessEnergyManager;
import com.hepdd.gtmthings.common.block.machine.electric.WirelessEnergyMonitor;
import com.hepdd.gtmthings.utils.TeamUtil;
import com.mojang.datafixers.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Slf4j
@Mixin(WirelessEnergyMonitor.class)
public abstract class WirelessEnergyMonitorMixin extends MetaMachine implements IFancyUIMachine {

    @Shadow(remap = false)
    private UUID userid;
    @Shadow(remap = false)
    private boolean all = false;

    public WirelessEnergyMonitorMixin(IMachineBlockEntity holder) {
        super(holder);
    }

    @Shadow(remap = false)
    protected abstract void handleDisplayClick(String s, ClickData clickData);

    @Shadow(remap = false)
    protected abstract void addDisplayText(@NotNull List<Component> textList);

    @Shadow(remap = false)
    private static Component getTimeToFillDrainText(BigInteger timeToFillSeconds) {
        return null;
    }

    @Shadow(remap = false)
    protected abstract List<Map.Entry<Pair<UUID, MetaMachine>, Long>> getSortedEntries();

    @Inject(remap = false, method = "createUIWidget", at = @At("HEAD"), cancellable = true)
    private void createUIWidgetMixin(CallbackInfoReturnable<Widget> cir) {
        WidgetGroup group = new WidgetGroup(0, 0, 220 + 8 + 8, 117 + 8);
        group.addWidget((new DraggableScrollableWidgetGroup(4, 4, 220 + 8, 117)).setBackground(GuiTextures.DISPLAY)
                .addWidget(new LabelWidget(4, 5, this.self().getBlockState().getBlock().getDescriptionId()))
                .addWidget((new ComponentPanelWidget(4, 17, this::addDisplayText))
                        .setMaxWidthLimit(220)
                        .clickHandler(this::handleDisplayClick)));
        group.setBackground(new IGuiTexture[] { GuiTextures.BACKGROUND_INVERSE });
        cir.setReturnValue(group);
        cir.cancel();
    }

    @Inject(remap = false, method = "addDisplayText", at = @At("HEAD"), cancellable = true)
    private void addDisplayTextMixin(@NotNull List<Component> textList, CallbackInfo ci) {
        BigInteger energyTotal = WirelessEnergyManager.getUserEU(this.userid);
        textList.add(Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.0", TeamUtil.GetName(this.holder.level(), this.userid)).withStyle(ChatFormatting.AQUA));
        textList.add(formatWithConstantWidth("gtmthings.machine.wireless_energy_monitor.tooltip.1", 200, Component.literal(formatBigIntegerNumberOrSic(energyTotal))).withStyle(ChatFormatting.GOLD));

        var stat = EnergyStat.createOrgetEnergyStat(this.userid);
        textList.add(Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.net_power"));

        BigDecimal avgMinute = stat.getMinuteAvg();
        textList.add(formatWithConstantWidth("gtmthings.machine.wireless_energy_monitor.tooltip.last_minute", 200, Component.literal(formatBigDecimalNumberOrSic(avgMinute))).withStyle(ChatFormatting.AQUA));
        BigDecimal avgHour = stat.getHourAvg();
        textList.add(formatWithConstantWidth("gtmthings.machine.wireless_energy_monitor.tooltip.last_hour", 200, Component.literal(formatBigDecimalNumberOrSic(avgHour))).withStyle(ChatFormatting.YELLOW));
        BigDecimal avgDay = stat.getDayAvg();
        textList.add(formatWithConstantWidth("gtmthings.machine.wireless_energy_monitor.tooltip.last_day", 200, Component.literal(formatBigDecimalNumberOrSic(avgDay))).withStyle(ChatFormatting.GREEN));
        // average useage
        BigDecimal avgEnergy = stat.getAvgEnergy();
        textList.add(formatWithConstantWidth("gtmthings.machine.wireless_energy_monitor.tooltip.now", 200, Component.literal(formatBigDecimalNumberOrSic(avgEnergy))).withStyle(ChatFormatting.LIGHT_PURPLE));

        textList.add(Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.statistics").append(ComponentPanelWidget.withButton(this.all ? Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.all") : Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.team"), "all")));

        int compare = avgEnergy.compareTo(BigDecimal.valueOf(0));
        if (compare > 0) {
            textList.add(Component.translatable("gtceu.multiblock.power_substation.time_to_fill",
                    Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.time_to_fill")).withStyle(ChatFormatting.GRAY));
        } else if (compare < 0) {
            textList.add(Component.translatable("gtceu.multiblock.power_substation.time_to_drain",
                    getTimeToFillDrainText(energyTotal.divide(avgEnergy.abs().toBigInteger().multiply(BigInteger.valueOf(20))))).withStyle(ChatFormatting.GRAY));
        }

        for (Map.Entry<Pair<UUID, MetaMachine>, BigDecimal> m : SortedEntriesStorage.getSortedEntries(getOffsetTimer())) {
            UUID uuid = (UUID) ((Pair<?, ?>) m.getKey()).getFirst();
            if (this.all || TeamUtil.getTeamUUID(uuid) == TeamUtil.getTeamUUID(this.userid)) {
                MetaMachine machine = (MetaMachine) ((Pair<?, ?>) m.getKey()).getSecond();
                BigDecimal eut = m.getValue();
                String pos = machine.getPos().toShortString();
                boolean isOverLong = eut.abs().compareTo(BigDecimal.valueOf(Long.MAX_VALUE)) > 0;
                MutableComponent component = Component.translatable(machine.getBlockState().getBlock().getDescriptionId()).withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("recipe.condition.dimension.tooltip", new Object[] { Objects.requireNonNull(machine.getLevel()).dimension().location() }).append(" [").append(pos).append("] ").append(Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.0", TeamUtil.GetName(this.holder.level(), uuid)))))).append(" ");
                Component overMax = Component.literal("MAX+N").withStyle(ChatFormatting.RED);
                if (eut.compareTo(BigDecimal.ZERO) > 0) {
                    component.append(Component.translatable(NumberUtils.formatBigDecimalNumberOrSic(eut)).withStyle(ChatFormatting.GREEN)).append(" EU/t (");
                    if (isOverLong) {
                        component.append(overMax).append(")");
                    } else {
                        component.append(GTValues.VNF[GTUtil.getFloorTierByVoltage(Long.parseLong(String.valueOf(eut)))]).append(")");
                    }
                } else {
                    component.append(Component.translatable(NumberUtils.formatBigDecimalNumberOrSic(eut)).withStyle(ChatFormatting.RED)).append(" EU/t (");
                    if (isOverLong) {
                        component.append(overMax).append(")");
                    } else {
                        component.append(GTValues.VNF[GTUtil.getFloorTierByVoltage(-Long.parseLong(String.valueOf(eut)))]).append(")");
                    }

                }
                textList.add(component);
            }
        }
        ci.cancel();
    }

    // private List<Map.Entry<Pair<UUID, MetaMachine>, BigInteger>> getSortedEntries() {
    // if (sortedEntries == null || getOffsetTimer() % 20 == 0) {
    // sortedEntries = MachineData.entrySet()
    // .stream()
    // .sorted(Map.Entry.comparingByValue())
    // .toList();
    // MachineData.clear();
    // }
    // return sortedEntries;
    // }
}
