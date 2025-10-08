package com.xingmot.gtmadvancedhatch.integration.gtlcore;

import com.xingmot.gtmadvancedhatch.GTMAdvancedHatch;
import com.xingmot.gtmadvancedhatch.common.AHRegistration;
import com.xingmot.gtmadvancedhatch.common.data.AHTabs;
import com.xingmot.gtmadvancedhatch.common.machines.NetLaserHatchPartMachine;

import org.gtlcore.gtlcore.utils.TextUtil;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.client.renderer.machine.OverlayTieredMachineRenderer;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.ModList;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.hepdd.gtmthings.data.WirelessMachines.registerWirelessLaserHatch;
import static com.xingmot.gtmadvancedhatch.common.data.AHMachines.*;

import com.gtladd.gtladditions.common.machine.GTLAddMachines;
import com.hepdd.gtmthings.GTMThings;

public class ExGTLMachines {

    // 1600万安培以上 仅限最高的两个电压
    public static final int[] HIGHEST_TIERS = GTCEuAPI.isHighTier() ? GTValues.tiersBetween(OpV, MAX) : GTValues.tiersBetween(ZPM, UV);
    static {
        AHRegistration.registrate.creativeModeTab(() -> AHTabs.BASE_TAB);
    }

    public static void init() {
        if (!ModList.get().isLoaded("gtladditions")) {
            WIRELESS_ENERGY_INPUT_HATCH_16777216A = registerWirelessLaserHatch(IO.IN, 16777216, PartAbility.INPUT_LASER, HIGHEST_TIERS);
            WIRELESS_ENERGY_INPUT_HATCH_67108864A = registerWirelessLaserHatch(IO.IN, 67108864, PartAbility.INPUT_LASER, HIGHEST_TIERS);
            WIRELESS_ENERGY_OUTPUT_HATCH_16777216A = registerWirelessLaserHatch(IO.OUT, 16777216, PartAbility.OUTPUT_LASER, HIGHEST_TIERS);
            WIRELESS_ENERGY_OUTPUT_HATCH_67108863A = registerWirelessLaserHatch(IO.OUT, 67108863, PartAbility.OUTPUT_LASER, HIGHEST_TIERS);
        } else {
            WIRELESS_ENERGY_INPUT_HATCH_16777216A = GTLAddMachines.INSTANCE.getWIRELESS_LASER_INPUT_HATCH_16777216A();
            WIRELESS_ENERGY_INPUT_HATCH_67108864A = GTLAddMachines.INSTANCE.getWIRELESS_LASER_INPUT_HATCH_67108864A();
            WIRELESS_ENERGY_OUTPUT_HATCH_16777216A = GTLAddMachines.INSTANCE.getWIRELESS_LASER_OUTPUT_HATCH_16777216A();
            WIRELESS_ENERGY_OUTPUT_HATCH_67108863A = GTLAddMachines.INSTANCE.getWIRELESS_LASER_OUTPUT_HATCH_67108863A();
        }
    }

    // 电网仓（非激光
    public static final MachineDefinition[] NET_ENERGY_INPUT_HATCH_262144A = registerNetEnergyHatch(IO.IN, 262144, PartAbility.INPUT_ENERGY, NET_HIGH_TIERS2);
    public static final MachineDefinition[] NET_ENERGY_INPUT_HATCH_1048576A = registerNetEnergyHatch(IO.IN, 1048576, PartAbility.INPUT_ENERGY, NET_HIGH_TIERS2);
    public static final MachineDefinition[] NET_ENERGY_OUTPUT_HATCH_262144A = registerNetEnergyHatch(IO.OUT, 262144, PartAbility.OUTPUT_ENERGY, NET_HIGH_TIERS2);
    public static final MachineDefinition[] NET_ENERGY_OUTPUT_HATCH_1048576A = registerNetEnergyHatch(IO.OUT, 1048576, PartAbility.OUTPUT_ENERGY, NET_HIGH_TIERS2);

    // 普通无线仓
    public static MachineDefinition[] WIRELESS_ENERGY_INPUT_HATCH_16777216A;
    public static MachineDefinition[] WIRELESS_ENERGY_INPUT_HATCH_67108864A;
    public static MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_16777216A;
    public static MachineDefinition[] WIRELESS_ENERGY_OUTPUT_HATCH_67108863A;

    // 电网激光仓
    public static final MachineDefinition[] NET_LASER_INPUT_HATCH_262144A = registerNetLaserHatch(IO.IN, 262144, PartAbility.INPUT_LASER, NET_HIGH_TIERS);
    public static final MachineDefinition[] NET_LASER_INPUT_HATCH_1048576A = registerNetLaserHatch(IO.IN, 1048576, PartAbility.INPUT_LASER, NET_HIGH_TIERS);
    public static final MachineDefinition[] NET_ENERGY_INPUT_HATCH_4194304A = registerNetLaserHatch(IO.IN, 4194304, PartAbility.INPUT_LASER, NET_HIGH_TIERS);
    public static final MachineDefinition[] NET_ENERGY_INPUT_HATCH_16777216A = registerNetLaserHatch(IO.IN, 16777216, PartAbility.INPUT_LASER, HIGHEST_TIERS);
    public static final MachineDefinition[] NET_ENERGY_INPUT_HATCH_67108864A = registerNetLaserHatch(IO.IN, 67108864, PartAbility.INPUT_LASER, HIGHEST_TIERS);
    public static final MachineDefinition[] NET_ENERGY_INPUT_HATCH_2147483647A = register21ENetLaserHatch(IO.IN, 2147483647, PartAbility.INPUT_LASER, HIGHEST_TIERS);
    public static final MachineDefinition[] NET_LASER_OUTPUT_HATCH_262144A = registerNetLaserHatch(IO.OUT, 262144, PartAbility.OUTPUT_LASER, NET_HIGH_TIERS);
    public static final MachineDefinition[] NET_LASER_OUTPUT_HATCH_1048576A = registerNetLaserHatch(IO.OUT, 1048576, PartAbility.OUTPUT_LASER, NET_HIGH_TIERS);
    public static final MachineDefinition[] NET_ENERGY_OUTPUT_HATCH_4194304A = registerNetLaserHatch(IO.OUT, 4194304, PartAbility.OUTPUT_LASER, NET_HIGH_TIERS);
    public static final MachineDefinition[] NET_ENERGY_OUTPUT_HATCH_16777216A = registerNetLaserHatch(IO.OUT, 16777216, PartAbility.OUTPUT_LASER, HIGHEST_TIERS);
    public static final MachineDefinition[] NET_ENERGY_OUTPUT_HATCH_67108864A = registerNetLaserHatch(IO.OUT, 67108864, PartAbility.OUTPUT_LASER, HIGHEST_TIERS);
    public static final MachineDefinition[] NET_ENERGY_OUTPUT_HATCH_2147483647A = register21ENetLaserHatch(IO.OUT, 2147483647, PartAbility.OUTPUT_LASER, HIGHEST_TIERS);

    public static MachineDefinition[] register21ENetLaserHatch(IO io, int amperage, PartAbility ability, int[] tiers) {
        var name = io == IO.IN ? "target" : "source";
        String finalRender = getRender(amperage);
        if (ModList.get().isLoaded("gtlcore"))
            return registerTieredMachines(amperage + "a_net_laser_" + name + "_hatch",
                    (holder, tier) -> new NetLaserHatchPartMachine(holder, tier, io, amperage),
                    (tier, builder) -> builder
                            .langValue(VNF[tier] + " " + FormattingUtil.formatNumbers(amperage) + "A Laser " +
                                    FormattingUtil.toEnglishName(name) + " Hatch")
                            .rotationState(RotationState.ALL)
                            .abilities(ability)
                            .tooltips(Component.literal(TextUtil.full_color("干得好，你有新玩具了")), (Component.translatable(GTMAdvancedHatch.MODID + ".machine.net_energy_hatch." + name + ".tooltip")), (Component.translatable(GTMThings.MOD_ID + ".machine.energy_hatch." + name + ".tooltip")), (Component.translatable(GTMThings.MOD_ID + ".machine.wireless_energy_hatch." + name + ".tooltip")))
                            .renderer(() -> new OverlayTieredMachineRenderer(tier, GTMThings.id("block/machine/part/" + finalRender)))
                            .compassNode("laser_hatch." + name)
                            .register(),
                    tiers);
        else
            return registerNetLaserHatch(io, amperage, ability, tiers);
    }
}
