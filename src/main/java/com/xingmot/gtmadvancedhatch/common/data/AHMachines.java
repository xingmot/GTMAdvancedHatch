package com.xingmot.gtmadvancedhatch.common.data;

import com.xingmot.gtmadvancedhatch.GTMAdvancedHatch;
import com.xingmot.gtmadvancedhatch.common.AHRegistration;
import com.xingmot.gtmadvancedhatch.common.machines.LockItemOutputBus;
import com.xingmot.gtmadvancedhatch.common.machines.NetEnergyHatchPartMachine;
import com.xingmot.gtmadvancedhatch.common.machines.NetLaserHatchPartMachine;

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
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.fml.ModList;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.BiFunction;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.xingmot.gtmadvancedhatch.common.data.ConstantsMachines.getLockItemOutputBusSlot;

import com.hepdd.gtmthings.GTMThings;
import org.jetbrains.annotations.NotNull;

public class AHMachines {

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

    // public static final MachineDefinition ME_MAINTAIN_EXPORT_BUFFER =
    // GTRegistration.REGISTRATE.machine("me_maintain_export_buffer", MEExtendedOutputPartMachine::new)
    // .langValue("ME Maintain Export Buffer")
    // .rotationState(RotationState.ALL)
    // .abilities(PartAbility.EXPORT_FLUIDS, PartAbility.EXPORT_ITEMS)
    // .overlayTieredHullRenderer("me_extended_export_buffer")
    // .tooltips(Component.translatable("gtmthings.machine.me_export_buffer.tooltip"),
    // Component.translatable("gtceu.machine.me_extended_export_buffer.tooltip.0"))
    // .tooltipBuilder(GTLMachines.GTL_ADD)
    // .tier(9)
    // .register();
    // endregion

    // region 》能源仓室
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

    // endregion
    static {
        AHRegistration.registrate.creativeModeTab(() -> AHTabs.BASE_TAB);
    }

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
        return registerTieredMachines(amperage + "a_net_energy_" + name + "_hatch", (holder, tier) -> new NetEnergyHatchPartMachine(holder, tier, io, amperage), (tier, builder) -> builder.langValue(VNF[tier] + (io == IO.IN ? " Energy Hatch" : " Dynamo Hatch")).rotationState(RotationState.ALL).abilities(ability).tooltips(Component.translatable(GTMAdvancedHatch.MODID + ".machine.net_energy_hatch." + name + ".tooltip"), (Component.translatable(GTMThings.MOD_ID + ".machine.energy_hatch." + name + ".tooltip")), (Component.translatable(GTMThings.MOD_ID + ".machine.wireless_energy_hatch." + name + ".tooltip"))).renderer(() -> new OverlayTieredMachineRenderer(tier, GTMThings.id("block/machine/part/" + finalRender)))
                // .overlayTieredHullRenderer(finalRender)
                .compassNode("energy_hatch").register(), tiers);
    }

    public static MachineDefinition[] registerNetLaserHatch(IO io, int amperage, PartAbility ability, int[] tiers) {
        var name = io == IO.IN ? "target" : "source";
        String finalRender = getRender(amperage);
        return registerTieredMachines(amperage + "a_net_laser_" + name + "_hatch", (holder, tier) -> new NetLaserHatchPartMachine(holder, tier, io, amperage), (tier, builder) -> {
            Component[] components = { Component.translatable(GTMAdvancedHatch.MODID + ".machine.net_energy_hatch." + name + ".tooltip"), (Component.translatable(GTMThings.MOD_ID + ".machine.energy_hatch." + name + ".tooltip")), (Component.translatable(GTMThings.MOD_ID + ".machine.wireless_energy_hatch." + name + ".tooltip")) };
            if (!ModList.get().isLoaded("gtlcore") && amperage >= 16777216) {
                components = Arrays.copyOf(components, components.length + 1);
                MutableComponent translatable = Component.translatable(GTMAdvancedHatch.MODID + ".machine.no_fix_crash_warning");
                components[components.length - 1] = translatable;
            }
            return builder.langValue(VNF[tier] + " " + FormattingUtil.formatNumbers(amperage) + "A Laser " + FormattingUtil.toEnglishName(name) + " Hatch").rotationState(RotationState.ALL).abilities(ability).tooltips(components).renderer(() -> new OverlayTieredMachineRenderer(tier, GTMThings.id("block/machine/part/" + finalRender))).compassNode("laser_hatch." + name).register();
        }, tiers);
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

    public static void init() {}
}
