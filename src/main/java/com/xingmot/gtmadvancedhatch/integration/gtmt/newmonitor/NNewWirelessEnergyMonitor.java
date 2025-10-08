//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
package com.xingmot.gtmadvancedhatch.integration.gtmt.newmonitor;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.hepdd.gtmthings.api.misc.WirelessEnergyManager;
import com.hepdd.gtmthings.common.block.machine.electric.WirelessEnergyMonitor;
import com.hepdd.gtmthings.utils.TeamUtil;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.mojang.datafixers.util.Pair;
import com.xingmot.gtmadvancedhatch.util.NumberUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import static com.xingmot.gtmadvancedhatch.integration.gtmt.newmonitor.FormatUtil.formatWithConstantWidth;
import static com.xingmot.gtmadvancedhatch.util.NumberUtils.formatBigDecimalNumberOrSic;
import static com.xingmot.gtmadvancedhatch.util.NumberUtils.formatBigIntegerNumberOrSic;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NNewWirelessEnergyMonitor extends MetaMachine implements IFancyUIMachine {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER;
    private static final BigInteger BIG_INTEGER_MAX_LONG;
    public static int p;
    public static BlockPos pPos;
    private UUID userid;
    private BigInteger beforeEnergy;
    private ArrayList<BigInteger> longArrayList;
    private List<Map.Entry<Pair<UUID, MetaMachine>, Long>> sortedEntries = null;
    private boolean all = false;

    public NNewWirelessEnergyMonitor(IMachineBlockEntity holder) {
        super(holder);
    }

    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public void onLoad() {
        super.onLoad();
    }

    public void onUnload() {
        super.onUnload();
    }

    private void handleDisplayClick(String componentData, ClickData clickData) {
        if (!clickData.isRemote) {
            if (componentData.equals("all")) {
                this.all = !this.all;
            } else {
                p = 100;
                String[] parts = componentData.split(", ");
                pPos = new BlockPos(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
            }
        }

    }

    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 220+8+8, 117+8);
        group.addWidget((new DraggableScrollableWidgetGroup(4, 4, 220+8, 117)).setBackground(GuiTextures.DISPLAY)
                .addWidget(new LabelWidget(4, 5, this.self().getBlockState().getBlock().getDescriptionId()))
                .addWidget((new ComponentPanelWidget(4, 17, this::addDisplayText))
                        .setMaxWidthLimit(220)
                        .clickHandler(this::handleDisplayClick)));
        group.setBackground(new IGuiTexture[]{GuiTextures.BACKGROUND_INVERSE});
        return group;
    }

    public boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        if (this.userid == null || !this.userid.equals(player.getUUID())) {
            this.userid = player.getUUID();
            this.longArrayList = new ArrayList();
        }

        this.beforeEnergy = WirelessEnergyManager.getUserEU(this.userid);
        return true;
    }

    private void addDisplayText(@NotNull List<Component> textList) {
        BigInteger energyTotal = WirelessEnergyManager.getUserEU(this.userid);
        textList.add(Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.0", TeamUtil.GetName(this.holder.level(), this.userid)).withStyle(ChatFormatting.AQUA));
        textList.add(formatWithConstantWidth("gtmthings.machine.wireless_energy_monitor.tooltip.1", 180, Component.literal(formatBigIntegerNumberOrSic(energyTotal))).withStyle(ChatFormatting.GOLD));

        var stat = EnergyStat.createOrgetEnergyStat(this.userid);
        textList.add(Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.net_power"));

        BigDecimal avgMinute = stat.getMinuteAvg();
        textList.add(formatWithConstantWidth("gtmthings.machine.wireless_energy_monitor.tooltip.last_minute", 180, Component.literal((avgMinute.signum() < 0 ? "-" : "") + formatBigDecimalNumberOrSic(avgMinute)).withStyle(ChatFormatting.DARK_AQUA)));
        BigDecimal avgHour = stat.getHourAvg();
        textList.add(formatWithConstantWidth("gtmthings.machine.wireless_energy_monitor.tooltip.last_hour", 180, Component.literal((avgHour.signum() < 0 ? "-" : "") + formatBigDecimalNumberOrSic(avgHour)).withStyle(ChatFormatting.YELLOW)));
        BigDecimal avgDay = stat.getDayAvg();
        textList.add(formatWithConstantWidth("gtmthings.machine.wireless_energy_monitor.tooltip.last_day", 180, Component.literal((avgDay.signum() < 0 ? "-" : "") + formatBigDecimalNumberOrSic(avgDay)).withStyle(ChatFormatting.DARK_GREEN)));
        // average useage
        BigDecimal avgEnergy = stat.getAvgEnergy();
        textList.add(formatWithConstantWidth("gtmthings.machine.wireless_energy_monitor.tooltip.now", 180, Component.literal((energyTotal.signum() < 0 ? "-" : "") + formatBigDecimalNumberOrSic(avgEnergy)).withStyle(ChatFormatting.DARK_PURPLE)));

        textList.add(Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.statistics").append(ComponentPanelWidget.withButton(this.all ? Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.all") : Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.team"), "all")));

        int compare = avgEnergy.compareTo(BigDecimal.valueOf(0));
        if (compare > 0) {
            textList.add(Component.translatable("gtceu.multiblock.power_substation.time_to_fill",
                    Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.time_to_fill")).withStyle(ChatFormatting.GRAY));
        } else if (compare < 0) {
            textList.add(Component.translatable("gtceu.multiblock.power_substation.time_to_drain",
                    getTimeToFillDrainText(energyTotal.divide(avgEnergy.abs().toBigInteger().multiply(BigInteger.valueOf(20))))).withStyle(ChatFormatting.GRAY));
        }

        for (Map.Entry<Pair<UUID, MetaMachine>, Long> m : this.getSortedEntries()) {
            UUID uuid = (UUID) ((Pair) m.getKey()).getFirst();
            if (this.all || TeamUtil.getTeamUUID(uuid) == TeamUtil.getTeamUUID(this.userid)) {
                MetaMachine machine = (MetaMachine) ((Pair) m.getKey()).getSecond();
                long eut = (Long) m.getValue();
                String pos = machine.getPos().toShortString();
                if (eut > 0L) {
                    textList.add(Component.translatable(machine.getBlockState().getBlock().getDescriptionId()).withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("recipe.condition.dimension.tooltip", new Object[] { machine.getLevel().dimension().location() }).append(" [").append(pos).append("] ").append(Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.0", new Object[] { TeamUtil.GetName(this.holder.level(), uuid) }))))).append(" +").append(NumberUtils.formatBigDecimalNumberOrSic(BigDecimal.valueOf(eut))).append(" EU/t (").append(GTValues.VNF[GTUtil.getFloorTierByVoltage(eut)]).append(")").append(" [ ] "));
                } else {
                    textList.add(Component.translatable(machine.getBlockState().getBlock().getDescriptionId()).withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("recipe.condition.dimension.tooltip", new Object[] { machine.getLevel().dimension().location() }).append(" [").append(pos).append("] ").append(Component.translatable("gtmthings.machine.wireless_energy_monitor.tooltip.0", new Object[] { TeamUtil.GetName(this.holder.level(), uuid) }))))).append(" -").append(NumberUtils.formatBigDecimalNumberOrSic(BigDecimal.valueOf(-eut))).append(" EU/t (").append(GTValues.VNF[GTUtil.getFloorTierByVoltage(-eut)]).append(")").append(" [ ] "));
                }
            }
        }

    }

    private List<Map.Entry<Pair<UUID, MetaMachine>, Long>> getSortedEntries() {
        if (this.sortedEntries == null || this.getOffsetTimer() % 20L == 0L) {
            this.sortedEntries = WirelessEnergyManager.MachineData.entrySet().stream().sorted(Entry.comparingByValue()).toList();
            WirelessEnergyManager.MachineData.clear();
        }

        return this.sortedEntries;
    }

    private static Component getTimeToFillDrainText(BigInteger timeToFillSeconds) {
        if (timeToFillSeconds.compareTo(BIG_INTEGER_MAX_LONG) > 0) {
            timeToFillSeconds = BIG_INTEGER_MAX_LONG;
        }

        Duration duration = Duration.ofSeconds(timeToFillSeconds.longValue());
        String key;
        long fillTime;
        if (duration.getSeconds() <= 180L) {
            fillTime = duration.getSeconds();
            key = "gtceu.multiblock.power_substation.time_seconds";
        } else if (duration.toMinutes() <= 180L) {
            fillTime = duration.toMinutes();
            key = "gtceu.multiblock.power_substation.time_minutes";
        } else if (duration.toHours() <= 72L) {
            fillTime = duration.toHours();
            key = "gtceu.multiblock.power_substation.time_hours";
        } else if (duration.toDays() <= 730L) {
            fillTime = duration.toDays();
            key = "gtceu.multiblock.power_substation.time_days";
        } else {
            if (duration.toDays() / 365L >= 1000000L) {
                return Component.translatable("gtceu.multiblock.power_substation.time_forever");
            }

            fillTime = duration.toDays() / 365L;
            key = "gtceu.multiblock.power_substation.time_years";
        }

        return Component.translatable(key, new Object[]{FormattingUtil.formatNumbers(fillTime)});
    }

    private BigDecimal getAvgUsage(BigInteger now) {
        BigInteger changed = now.subtract(this.beforeEnergy);
        this.beforeEnergy = now;
        if (this.longArrayList.size() >= 20) {
            this.longArrayList.remove(0);
        }

        this.longArrayList.add(changed);
        return calculateAverage(this.longArrayList);
    }

    private static BigDecimal calculateAverage(ArrayList<BigInteger> bigIntegers) {
        BigInteger sum = BigInteger.ZERO;

        for(BigInteger bi : bigIntegers) {
            sum = sum.add(bi);
        }

        return (new BigDecimal(sum)).divide(new BigDecimal(bigIntegers.size()), RoundingMode.HALF_UP);
    }

    static {
        MANAGED_FIELD_HOLDER = new ManagedFieldHolder(WirelessEnergyMonitor.class, MetaMachine.MANAGED_FIELD_HOLDER);
        BIG_INTEGER_MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
    }
}
