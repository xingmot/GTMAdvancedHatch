package com.xingmot.gtmadvancedhatch.common.data;

import com.xingmot.gtmadvancedhatch.GTMAdvancedHatch;
import com.xingmot.gtmadvancedhatch.common.AHRegistration;
import com.xingmot.gtmadvancedhatch.common.machines.LockItemOutputBus;
import com.xingmot.gtmadvancedhatch.common.machines.NetEnergyHatchPartMachine;
import com.xingmot.gtmadvancedhatch.common.machines.NetLaserHatchPartMachine;
import com.xingmot.gtmadvancedhatch.common.machines.adaptivehatch.AdaptiveNetEnergyHatchPartMachine;
import com.xingmot.gtmadvancedhatch.common.machines.adaptivehatch.AdaptiveNetEnergyTerminal;
import com.xingmot.gtmadvancedhatch.common.machines.adaptivehatch.AdaptiveNetLaserHatchPartMachine;
import com.xingmot.gtmadvancedhatch.config.AHConfig;

import org.gtlcore.gtlcore.utils.TextUtil;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.gregtechceu.gtceu.client.renderer.machine.OverlayTieredMachineRenderer;
import com.gregtechceu.gtceu.client.renderer.machine.WorkableTieredHullMachineRenderer;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.ModList;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.xingmot.gtmadvancedhatch.common.data.MachinesConstants.getLockItemOutputBusSlot;

import com.hepdd.gtmthings.GTMThings;
import org.jetbrains.annotations.NotNull;

public class AHMachines {

    static {
        AHRegistration.registrate.creativeModeTab(() -> AHTabs.BASE_TAB);
    }
    public static final int[] ALL_TIERS = GTValues.tiersBetween(ULV, GTCEuAPI.isHighTier() ? MAX : UV);
    public static final int[] NET_TIERS = GTValues.tiersBetween(LV, MAX);
    public static final int[] NET_HIGH_TIERS = GTValues.tiersBetween(EV, MAX); // 64A 电网能源仓 + 激光仓
    public static final int[] NET_HIGH_TIERS2 = GTValues.tiersBetween(LuV, MAX); // 大安培 电网能源仓
    // region 》物品仓室
    public static final MachineDefinition[] LOCK_ITEM_OUTPUT_BUS = registerTieredMachines("lock_item_output_bus", LockItemOutputBus::new,
            (tier, builder) -> builder.langValue(VNF[tier] + " Output Bus")
                    .rotationState(RotationState.ALL)
                    .abilities(PartAbility.EXPORT_ITEMS)
                    .renderer(() -> new OverlayTieredMachineRenderer(tier, GTCEu.id("block/machine/part/item_bus.import")))
                    // .overlayTieredHullRenderer("item_bus.export")
                    .tooltips(Component.translatable("gtmadvancedhatch.machine.lock_item_output.tooltip"),
                            Component.translatable("gtmadvancedhatch.machine.lock_item_output.tooltip2"),
                            Component.translatable("gtceu.machine.item_bus.export.tooltip"),
                            Component.translatable("gtceu.universal.tooltip.item_storage_capacity",
                                    getLockItemOutputBusSlot(tier)))
                    .compassNode("item_bus")
                    .register(),
            GTValues.tiersBetween(ULV, GTCEuAPI.isHighTier() ? MAX : UV));
    // endregion

    // region 》能源仓室
    public static final Set<MachineDefinition> ALL_NET_ENERGY_OUTPUT_HATCH = new HashSet<>();
    public static final Set<MachineDefinition> ALL_NET_ENERGY_INPUT_HATCH = new HashSet<>();
    public static final Set<MachineDefinition> ALL_NET_LASER_OUTPUT_HATCH = new HashSet<>();
    public static final Set<MachineDefinition> ALL_NET_LASER_INPUT_HATCH = new HashSet<>();

    public static final MachineDefinition[] NET_ENERGY_INPUT_HATCH = registerNetEnergyHatch(IO.IN, 2, PartAbility.INPUT_ENERGY, NET_TIERS);
    public static final MachineDefinition[] NET_ENERGY_INPUT_HATCH_4A = registerNetEnergyHatch(IO.IN, 4, PartAbility.INPUT_ENERGY, NET_TIERS);
    public static final MachineDefinition[] NET_ENERGY_INPUT_HATCH_16A = registerNetEnergyHatch(IO.IN, 16, PartAbility.INPUT_ENERGY, NET_TIERS);
    public static final MachineDefinition[] NET_ENERGY_INPUT_HATCH_64A = registerNetEnergyHatch(IO.IN, 64, PartAbility.INPUT_ENERGY, NET_HIGH_TIERS);
    public static final MachineDefinition[] NET_ENERGY_OUTPUT_HATCH = registerNetEnergyHatch(IO.OUT, 2, PartAbility.OUTPUT_ENERGY, NET_TIERS);
    public static final MachineDefinition[] NET_ENERGY_OUTPUT_HATCH_4A = registerNetEnergyHatch(IO.OUT, 4, PartAbility.OUTPUT_ENERGY, NET_TIERS);
    public static final MachineDefinition[] NET_ENERGY_OUTPUT_HATCH_16A = registerNetEnergyHatch(IO.OUT, 16, PartAbility.OUTPUT_ENERGY, NET_TIERS);
    public static final MachineDefinition[] NET_ENERGY_OUTPUT_HATCH_64A = registerNetEnergyHatch(IO.OUT, 64, PartAbility.OUTPUT_ENERGY, NET_HIGH_TIERS);
    public static final MachineDefinition[] NET_ENERGY_INPUT_HATCH_16384A = registerNetEnergyHatch(IO.IN, 16384, PartAbility.INPUT_ENERGY, NET_HIGH_TIERS2);
    public static final MachineDefinition[] NET_ENERGY_INPUT_HATCH_65536A = registerNetEnergyHatch(IO.IN, 65536, PartAbility.INPUT_ENERGY, NET_HIGH_TIERS2);
    public static final MachineDefinition[] NET_ENERGY_OUTPUT_HATCH_16384A = registerNetEnergyHatch(IO.OUT, 16384, PartAbility.OUTPUT_ENERGY, NET_HIGH_TIERS2);
    public static final MachineDefinition[] NET_ENERGY_OUTPUT_HATCH_65536A = registerNetEnergyHatch(IO.OUT, 65536, PartAbility.OUTPUT_ENERGY, NET_HIGH_TIERS2);
    public static final MachineDefinition[] NET_ENERGY_INPUT_HATCH_256A = registerNetLaserHatch(IO.IN, 256, PartAbility.INPUT_LASER, NET_HIGH_TIERS);
    public static final MachineDefinition[] NET_ENERGY_INPUT_HATCH_1024A = registerNetLaserHatch(IO.IN, 1024, PartAbility.INPUT_LASER, NET_HIGH_TIERS);
    public static final MachineDefinition[] NET_ENERGY_INPUT_HATCH_4096A = registerNetLaserHatch(IO.IN, 4096, PartAbility.INPUT_LASER, NET_HIGH_TIERS);
    public static final MachineDefinition[] NET_LASER_INPUT_HATCH_16384A = registerNetLaserHatch(IO.IN, 16384, PartAbility.INPUT_LASER, NET_HIGH_TIERS);
    public static final MachineDefinition[] NET_LASER_INPUT_HATCH_65536A = registerNetLaserHatch(IO.IN, 65536, PartAbility.INPUT_LASER, NET_HIGH_TIERS);
    public static final MachineDefinition[] NET_ENERGY_OUTPUT_HATCH_256A = registerNetLaserHatch(IO.OUT, 256, PartAbility.OUTPUT_LASER, NET_HIGH_TIERS);
    public static final MachineDefinition[] NET_ENERGY_OUTPUT_HATCH_1024A = registerNetLaserHatch(IO.OUT, 1024, PartAbility.OUTPUT_LASER, NET_HIGH_TIERS);
    public static final MachineDefinition[] NET_ENERGY_OUTPUT_HATCH_4096A = registerNetLaserHatch(IO.OUT, 4096, PartAbility.OUTPUT_LASER, NET_HIGH_TIERS);
    public static final MachineDefinition[] NET_LASER_OUTPUT_HATCH_16384A = registerNetLaserHatch(IO.OUT, 16384, PartAbility.OUTPUT_LASER, NET_HIGH_TIERS);
    public static final MachineDefinition[] NET_LASER_OUTPUT_HATCH_65536A = registerNetLaserHatch(IO.OUT, 65536, PartAbility.OUTPUT_LASER, NET_HIGH_TIERS);

    public static MachineDefinition[] registerTieredMachines(String name, BiFunction<IMachineBlockEntity, Integer, MetaMachine> factory, BiFunction<Integer, MachineBuilder<MachineDefinition>, MachineDefinition> builder, int... tiers) {
        MachineDefinition[] definitions = new MachineDefinition[GTValues.TIER_COUNT];
        for (int tier : tiers) {
            var register = AHRegistration.registrate.machine(GTValues.VN[tier].toLowerCase(Locale.ROOT) + "_" + name, holder -> factory.apply(holder, tier)).tier(tier);
            definitions[tier] = builder.apply(tier, register);
        }
        return definitions;
    }

    public static MachineDefinition[] registerNetEnergyHatch(IO io, int amperage, PartAbility ability, int[] tiers) {
        var name = io == IO.IN ? "input" : "output";
        String finalRender = getRender(amperage);
        MachineDefinition[] energyHatches = registerTieredMachines(amperage + "a_net_energy_" + name + "_hatch", (holder, tier) -> new NetEnergyHatchPartMachine(holder, tier, io, amperage), (tier, builder) -> builder.langValue(VNF[tier] + (io == IO.IN ? " Energy Hatch" : " Dynamo Hatch")).rotationState(RotationState.ALL).abilities(ability).tooltips(Component.translatable(GTMAdvancedHatch.MODID + ".machine.net_energy_hatch." + name + ".tooltip"), Component.translatable(GTMThings.MOD_ID + ".machine.energy_hatch." + name + ".tooltip"), Component.translatable(GTMThings.MOD_ID + ".machine.wireless_energy_hatch." + name + ".tooltip")).renderer(() -> new OverlayTieredMachineRenderer(tier, GTMThings.id("block/machine/part/" + finalRender)))
                // .overlayTieredHullRenderer(finalRender)
                .compassNode("energy_hatch").register(), tiers);
        if (io == IO.IN) {
            Collections.addAll(ALL_NET_ENERGY_INPUT_HATCH, energyHatches);
        } else {
            Collections.addAll(ALL_NET_ENERGY_OUTPUT_HATCH, energyHatches);
        }
        return energyHatches;
    }

    public static MachineDefinition[] registerNetLaserHatch(IO io, int amperage, PartAbility ability, int[] tiers) {
        var name = io == IO.IN ? "target" : "source";
        String finalRender = getRender(amperage);
        MachineDefinition[] laserHatches = registerTieredMachines(amperage + "a_net_laser_" + name + "_hatch", (holder, tier) -> new NetLaserHatchPartMachine(holder, tier, io, amperage), (tier, builder) -> {
            Component[] components = { Component.translatable(GTMAdvancedHatch.MODID + ".machine.net_energy_hatch." + name + ".tooltip"), Component.translatable(GTMThings.MOD_ID + ".machine.energy_hatch." + name + ".tooltip"), Component.translatable(GTMThings.MOD_ID + ".machine.wireless_energy_hatch." + name + ".tooltip") };
            if (amperage >= 16777216) {
                if (ModList.get().isLoaded("gtlcore") && amperage == 2147483647) {
                    components = Stream.concat(Arrays.stream(new Component[] { Component.literal(TextUtil.full_color("干得好，你有新玩具了")) }), Arrays.stream(components)).toArray(Component[]::new);
                } else if (!ModList.get().isLoaded("gtlcore") && AHConfig.INSTANCE.isDisplayNoFixCrashWarning) {
                    components = Stream.concat(Arrays.stream(components), Arrays.stream(new Component[] { Component.translatable(GTMAdvancedHatch.MODID + ".machine.no_fix_crash_warning") })).toArray(Component[]::new);
                }
            }
            return builder.langValue(VNF[tier] + " " + FormattingUtil.formatNumbers(amperage) + "A Laser " + FormattingUtil.toEnglishName(name) + " Hatch").rotationState(RotationState.ALL).abilities(ability).tooltips(components).renderer(() -> new OverlayTieredMachineRenderer(tier, GTMThings.id("block/machine/part/" + finalRender))).compassNode("laser_hatch." + name).register();
        }, tiers);
        if (io == IO.IN) {
            Collections.addAll(ALL_NET_LASER_INPUT_HATCH, laserHatches);
        } else {
            Collections.addAll(ALL_NET_LASER_OUTPUT_HATCH, laserHatches);
        }
        return laserHatches;
    }

    public static @NotNull String getRender(int amperage) {
        String render = "wireless_energy_hatch";
        render = switch (amperage) {
            case 2 -> render;
            case 4 -> render + "_4a";
            case 16 -> render + "_16a";
            case 64 -> render + "_64a";
            default -> "wireless_laser_hatch.target";
        };
        return render;
    }
    // endregion

    // region 电网适配系统
    public static final MachineDefinition ADAPTIVE_NET_ENERGY_INPUT_HATCH = AHRegistration.registrate.machine("adaptive_net_energy_input_hatch", holder -> new AdaptiveNetEnergyHatchPartMachine(holder, IO.IN))
            .rotationState(RotationState.ALL)
            .renderer(() -> new OverlayTieredMachineRenderer(14, GTMThings.id("block/machine/part/energy_hatch.input")))
            .abilities(PartAbility.INPUT_ENERGY)
            .compassNode("energy_hatch").tier(14).register();

    public static final MachineDefinition ADAPTIVE_NET_ENERGY_OUTPUT_HATCH = AHRegistration.registrate.machine("adaptive_net_energy_output_hatch", holder -> new AdaptiveNetEnergyHatchPartMachine(holder, IO.OUT))
            .rotationState(RotationState.ALL)
            .renderer(() -> new OverlayTieredMachineRenderer(14, GTMThings.id("block/machine/part/energy_hatch.output")))
            .abilities(PartAbility.INPUT_ENERGY)
            .compassNode("energy_hatch").tier(14).register();
    public static final MachineDefinition ADAPTIVE_NET_LASER_INPUT_HATCH = AHRegistration.registrate.machine("adaptive_net_laser_target_hatch", holder -> new AdaptiveNetLaserHatchPartMachine(holder, IO.IN))
            .rotationState(RotationState.ALL)
            .renderer(() -> new OverlayTieredMachineRenderer(14, GTMThings.id("block/machine/part/laser_hatch.target")))
            .abilities(PartAbility.INPUT_ENERGY)
            .compassNode("energy_hatch").tier(14).register();

    public static final MachineDefinition ADAPTIVE_NET_LASER_OUTPUT_HATCH = AHRegistration.registrate.machine("adaptive_net_laser_source_hatch", holder -> new AdaptiveNetLaserHatchPartMachine(holder, IO.OUT))
            .rotationState(RotationState.ALL)
            .renderer(() -> new OverlayTieredMachineRenderer(14, GTMThings.id("block/machine/part/laser_hatch.target")))
            .abilities(PartAbility.INPUT_ENERGY)
            .compassNode("energy_hatch").tier(14).register();

    public static final MachineDefinition ADAPTIVE_NET_ENERGY_TERMINAL = AHRegistration.registrate.machine("adaptive_net_energy_terminal", AdaptiveNetEnergyTerminal::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .renderer(() -> new WorkableTieredHullMachineRenderer(14, GTMThings.id("block/machines/wireless_energy_monitor")))
            .compassNodeSelf()
            .tier(14).register();
    // endregion

    public static void init() {}
}
