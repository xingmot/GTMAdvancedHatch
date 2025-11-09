package com.xingmot.gtmadvancedhatch.mixin.gtmt;

import com.xingmot.gtmadvancedhatch.api.util.SortedEntriesStorage;
import com.xingmot.gtmadvancedhatch.api.util.VoltageLevelLookup;
import com.xingmot.gtmadvancedhatch.integration.gtmt.newmonitor.EnergyStat;
import com.xingmot.gtmadvancedhatch.integration.gtmt.newmonitor.FormatUtil;
import com.xingmot.gtmadvancedhatch.util.AHUtil;
import com.xingmot.gtmadvancedhatch.util.copy.MessageUtil;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.client.util.TooltipHelper;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.*;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static com.xingmot.gtmadvancedhatch.integration.gtmt.newmonitor.FormatUtil.formatBigDecimalNumberOrSicWithSign;

import com.glodblock.github.extendedae.client.render.EAEHighlightHandler;
import com.hepdd.gtmthings.api.misc.WirelessEnergyManager;
import com.hepdd.gtmthings.common.block.machine.electric.WirelessEnergyMonitor;
import com.hepdd.gtmthings.utils.TeamUtil;
import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WirelessEnergyMonitor.class)
public abstract class WirelessEnergyMonitorMixin extends MetaMachine implements IFancyUIMachine {

    @Shadow(remap = false)
    private UUID userid;
    @Shadow(remap = false)
    private boolean all = false;

    // 是否使用科学计数法
    @Unique
    private boolean gtmadvancedhatch$isScientificNotation = false;

    // 净功率显示模式
    @Unique
    private int gtmadvancedhatch$netPower = 0;

    // 当前Player
    @Unique
    private Player gtmadvancedhatch$player;

    public WirelessEnergyMonitorMixin(IMachineBlockEntity holder) {
        super(holder);
    }

    @Shadow(remap = false)
    protected abstract void addDisplayText(@NotNull List<Component> textList);

    @Shadow(remap = false)
    private static Component getTimeToFillDrainText(BigInteger timeToFillSeconds) {
        return null;
    }

    @Shadow(remap = false)
    protected abstract List<Map.Entry<Pair<UUID, MetaMachine>, Long>> getSortedEntries();

    @Shadow(remap = false)
    protected abstract void handleDisplayClick(String componentData, ClickData clickData);

    @Unique
    private ResourceKey<Level> gtmadvancedhatch$getDimensionKey(ResourceLocation resourceLocation) {
        return ResourceKey.create(Registries.DIMENSION, resourceLocation);
    }

    @Inject(remap = false, method = "handleDisplayClick", at = @At(value = "HEAD"), cancellable = true)
    private void handleDisplayClickMixin(String componentData, ClickData clickData, CallbackInfo ci) {
        if (!clickData.isRemote) {
            switch (componentData) {
                case "isScientificNotation" -> gtmadvancedhatch$isScientificNotation = !gtmadvancedhatch$isScientificNotation;
                case "netPower" -> {
                    gtmadvancedhatch$netPower += 1;
                    if (gtmadvancedhatch$netPower > 2) gtmadvancedhatch$netPower = 0;
                }
                case "all" -> all = !all;
                case "reset" -> WirelessEnergyManager.setUserEU(this.userid, BigInteger.ZERO);
                default -> {
                    // 如果extendedae加载，则显示高亮
                    String[] parts = componentData.split(", ");
                    BlockPos pos = new BlockPos(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                    if (LDLib.isModLoaded("expatternprovider"))
                        EAEHighlightHandler.highlight(pos, gtmadvancedhatch$getDimensionKey(new ResourceLocation(parts[3])), System.currentTimeMillis() + 15000);
                    // 扩展ae无线收发器同款定位
                    if (this.gtmadvancedhatch$player != null) {
                        Component message = MessageUtil.createEnhancedHighlightMessage(this.gtmadvancedhatch$player, pos, this.gtmadvancedhatch$player.level()
                                .dimension(), Component.translatable(parts[4]), "gtmthings.machine.highlight");
                        this.gtmadvancedhatch$player.displayClientMessage(message, false);
                    }
                }
            }
        }
        ci.cancel();
    }

    @Inject(remap = false, method = "createUIWidget", at = @At("HEAD"), cancellable = true)
    private void createUIWidgetMixin(CallbackInfoReturnable<Widget> cir) {
        WidgetGroup group = new WidgetGroup(0, 0, 220 + 8 + 8, 117 + 8);
        group.addWidget((new DraggableScrollableWidgetGroup(4, 4, 220 + 8, 117)).setBackground(GuiTextures.DISPLAY)
                .addWidget(new LabelWidget(4, 5, this.self()
                        .getBlockState()
                        .getBlock()
                        .getDescriptionId()))
                .addWidget((new ComponentPanelWidget(4, 17, this::addDisplayText) {

                    @Override
                    public void readUpdateInfo(int id, FriendlyByteBuf buffer) {
                        this.lastText.clear();
                        int count = buffer.readVarInt();
                        for (int i = 0; i < count; i++) {
                            this.lastText.add(buffer.readComponent());
                        }
                        this.lastText = FormatUtil.getLastText(this.lastText);
                        formatDisplayText();
                        updateComponentTextSize();
                    }
                }).setMaxWidthLimit(220)
                        .clickHandler(this::handleDisplayClick)));
        group.setBackground(GuiTextures.BACKGROUND_INVERSE);
        cir.setReturnValue(group);
        cir.cancel();
    }

    @Inject(method = "shouldOpenUI", remap = false, at = @At("HEAD"))
    private void shouldOpenUIMixin(Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<Boolean> cir) {
        this.gtmadvancedhatch$player = player;
    }

    @Inject(remap = false, method = "addDisplayText", at = @At("HEAD"), cancellable = true)
    private void addDisplayTextMixin(@NotNull List<Component> textList, CallbackInfo ci) {
        BigInteger energyTotal = WirelessEnergyManager.getUserEU(this.userid);
        String energyTotalString = formatBigDecimalNumberOrSicWithSign(new BigDecimal(energyTotal), gtmadvancedhatch$isScientificNotation);
        Component name = AHUtil.getTeamName(this.holder.level(), this.userid);
        textList.add(Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.0", name)
                .withStyle(ChatFormatting.AQUA));
        textList.add(Component.literal("formatWidth,gtmthings.machine.wireless_energy_monitor.tooltip.1,200,GOLD"));
        textList.add(Component.literal(energyTotalString.charAt(0) == '+' ? energyTotalString.substring(1) : energyTotalString));
        // textList.add(formatWithConstantWidth("gtmthings.machine.wireless_energy_monitor.tooltip.1", 200,
        // Component.literal(formatBigIntegerNumberOrSic(energyTotal))).withStyle(ChatFormatting.GOLD));

        var stat = EnergyStat.createOrgetEnergyStat(this.userid);
        textList.add(Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.net_power"));

        BigDecimal avgMinute = stat.getMinuteAvg();
        textList.add(Component.literal("formatWidth,gtmthings.machine.wireless_energy_monitor.tooltip.last_minute,180,AQUA"));
        textList.add(Component.literal(formatBigDecimalNumberOrSicWithSign(avgMinute, gtmadvancedhatch$isScientificNotation)));
        // textList.add(formatWithConstantWidth("gtmthings.machine.wireless_energy_monitor.tooltip.last_minute", 180,
        // Component.literal(formatBigDecimalNumberOrSicWithSign(avgMinute,
        // gtmadvancedhatch$isScientificNotation))).withStyle(ChatFormatting.AQUA));
        BigDecimal avgHour = stat.getHourAvg();
        textList.add(Component.literal("formatWidth,gtmthings.machine.wireless_energy_monitor.tooltip.last_hour,180,YELLOW"));
        textList.add(Component.literal(formatBigDecimalNumberOrSicWithSign(avgHour, gtmadvancedhatch$isScientificNotation)));
        // textList.add(formatWithConstantWidth("gtmthings.machine.wireless_energy_monitor.tooltip.last_hour", 180,
        // Component.literal(formatBigDecimalNumberOrSicWithSign(avgHour,
        // gtmadvancedhatch$isScientificNotation))).withStyle(ChatFormatting.YELLOW));
        BigDecimal avgDay = stat.getDayAvg();
        textList.add(Component.literal("formatWidth,gtmthings.machine.wireless_energy_monitor.tooltip.last_day,180,GREEN"));
        textList.add(Component.literal(formatBigDecimalNumberOrSicWithSign(avgDay, gtmadvancedhatch$isScientificNotation)));
        // textList.add(formatWithConstantWidth("gtmthings.machine.wireless_energy_monitor.tooltip.last_day", 180,
        // Component.literal(formatBigDecimalNumberOrSicWithSign(avgDay,
        // gtmadvancedhatch$isScientificNotation))).withStyle(ChatFormatting.GREEN));
        // average useage
        BigDecimal avgEnergy = stat.getAvgEnergy();
        textList.add(Component.literal("formatWidth,gtmthings.machine.wireless_energy_monitor.tooltip.now,180,LIGHT_PURPLE"));
        textList.add(Component.literal(formatBigDecimalNumberOrSicWithSign(avgEnergy, gtmadvancedhatch$isScientificNotation)));
        // textList.add(formatWithConstantWidth("gtmthings.machine.wireless_energy_monitor.tooltip.now", 180,
        // Component.literal(formatBigDecimalNumberOrSicWithSign(avgEnergy,
        // gtmadvancedhatch$isScientificNotation))).withStyle(ChatFormatting.LIGHT_PURPLE));

        textList.add(Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.statistics")
                .append(ComponentPanelWidget.withButton(this.all ?
                        Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.all") :
                        Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.team"), "all"))
                .append(Component.literal(" "))
                .append(Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.format")
                        .append(ComponentPanelWidget.withButton(this.gtmadvancedhatch$isScientificNotation ?
                                Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.scientific_notation") :
                                Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.unit"), "isScientificNotation", Objects.requireNonNull(ChatFormatting.AQUA.getColor()))))
                .append(Component.literal(" "))
                .append(Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.netPower")
                        .append(ComponentPanelWidget.withButton(this.gtmadvancedhatch$netPower == 0 ?
                                Component.translatable("gtmthings.machine.wireless_energy_monitor.net_power_all") :
                                this.gtmadvancedhatch$netPower == 1 ?
                                        Component.translatable("gtmthings.machine.wireless_energy_monitor.net_power_positive") :
                                        Component.translatable("gtmthings.machine.wireless_energy_monitor.net_power_negative"),
                                "netPower", Objects.requireNonNull(ChatFormatting.LIGHT_PURPLE.getColor())))));
        int compare = avgEnergy.compareTo(BigDecimal.valueOf(0));
        if (compare > 0) {
            textList.add(Component.translatable("gtceu.multiblock.power_substation.time_to_fill", Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.time_to_fill"))
                    .append(Component.literal(" "))
                    .append(ComponentPanelWidget.withButton(
                            Component.literal("清空电网"),
                            "reset", Objects.requireNonNull(ChatFormatting.DARK_RED.getColor())))
                    .withStyle(ChatFormatting.GRAY));
        } else if (compare < 0) {
            textList.add(Component.translatable("gtceu.multiblock.power_substation.time_to_drain", getTimeToFillDrainText(energyTotal.divide(avgEnergy.abs()
                    .toBigInteger()
                    .multiply(BigInteger.valueOf(20)))))
                    .withStyle(ChatFormatting.GRAY));
        }

        for (Map.Entry<Pair<UUID, MetaMachine>, BigDecimal> m : SortedEntriesStorage.getSortedEntries(getOffsetTimer())) {
            if (m.getKey() == null) {
                continue; // 跳过键为null的条目
            }
            UUID uuid = m.getKey()
                    .getFirst();
            if (this.all || TeamUtil.getTeamUUID(uuid) == TeamUtil.getTeamUUID(this.userid)) {
                MetaMachine machine = m.getKey()
                        .getSecond();
                BigDecimal eut = m.getValue();
                String pos = machine.getPos()
                        .toShortString();
                Level level = machine.getLevel();
                if (level == null) continue;
                String machineName = machine.getBlockState()
                        .getBlock()
                        .getDescriptionId();
                MutableComponent component = Component.translatable(machineName)
                        .withStyle(Style.EMPTY.withHoverEvent(
                                new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        Component.translatable(
                                                "recipe.condition.dimension.tooltip",
                                                new Object[] { Objects.requireNonNull(machine.getLevel())
                                                        .dimension().location() })
                                                .append(" [")
                                                .append(pos)
                                                .append("] ")
                                                .append(Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.0", TeamUtil.GetName(this.holder.level(), uuid))))))
                        .append(" ");
                if (eut.compareTo(BigDecimal.ZERO) > 0) {
                    component.append(Component.literal(formatBigDecimalNumberOrSicWithSign(eut, gtmadvancedhatch$isScientificNotation))
                            .withStyle(ChatFormatting.GREEN))
                            .append(" EU/t (");
                } else {
                    component.append(Component.literal(formatBigDecimalNumberOrSicWithSign(eut, gtmadvancedhatch$isScientificNotation))
                            .withStyle(ChatFormatting.RED))
                            .append(" EU/t (");
                }
                component.append(ComponentPanelWidget.withButton(Component.literal(VoltageLevelLookup.findVoltageLevel(eut.abs())),
                        pos + ", " + level.dimension()
                                .location() + ", " + machineName))
                        .append(")");
                if (gtmadvancedhatch$netPower == 1 && eut.compareTo(BigDecimal.ZERO) > 0)
                    textList.add(component);
                else if (gtmadvancedhatch$netPower == 2 && eut.compareTo(BigDecimal.ZERO) < 0)
                    textList.add(component);
                else if (gtmadvancedhatch$netPower == 0)
                    textList.add(component);
            }
        }
        ci.cancel();
    }

    // 添加FancyInformationTooltip内容
    @Override
    public void onAddFancyInformationTooltip(@NotNull List<Component> tooltips) {
        super.onAddFancyInformationTooltip(tooltips);
        tooltips.add(Component.literal("格式化词头对应量级")
                .withStyle(style -> style.withColor(TooltipHelper.RAINBOW_SLOW.getCurrent())));
        tooltips.add(Component.literal("T(太)----1E12")
                .withStyle(ChatFormatting.GOLD));
        tooltips.add(Component.literal("P(拍)----1E15")
                .withStyle(ChatFormatting.RED));
        tooltips.add(Component.literal("E(艾)----1E18")
                .withStyle(ChatFormatting.YELLOW));
        tooltips.add(Component.literal("Z(泽)----1E21")
                .withStyle(ChatFormatting.GREEN));
        tooltips.add(Component.literal("Y(尧)----1E24")
                .withStyle(ChatFormatting.AQUA));
        tooltips.add(Component.literal("R(容)----1E27")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltips.add(Component.literal("Q(昆)----1E30")
                .withStyle(ChatFormatting.DARK_AQUA));
    }
}
