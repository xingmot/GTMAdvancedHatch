package com.xingmot.gtmadvancedhatch.common.data.recipe;

import com.xingmot.gtmadvancedhatch.GTMAdvancedHatch;
import com.xingmot.gtmadvancedhatch.common.AHItems;
import com.xingmot.gtmadvancedhatch.common.data.AHMachines;
import com.xingmot.gtmadvancedhatch.common.data.MachinesConstants;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;
import static com.xingmot.gtmadvancedhatch.common.data.AHMachines.NET_HIGH_TIERS2;

import com.hepdd.gtmthings.GTMThings;
import com.hepdd.gtmthings.data.CustomItems;
import com.hepdd.gtmthings.data.WirelessMachines;

public class AHRecipes {

    public static void initRecipes(Consumer<FinishedRecipe> provider) {
        // region 留存输出总线
        for (int tier : GTValues.tiersBetween(ULV, GTCEuAPI.isHighTier() ? MAX : UV)) {
            if (tier <= EV)
                ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("lock_item_output_bus_" + GTValues.VN[tier].toLowerCase()))
                        .outputItems(AHMachines.LOCK_ITEM_OUTPUT_BUS[tier].asStack())
                        .inputItems(GTMachines.ITEM_EXPORT_BUS[tier].asStack())
                        .inputItems(GTItems.COVER_ITEM_DETECTOR)
                        .duration(100)
                        .EUt(GTValues.VA[tier])
                        .save(provider);
            else
                ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("lock_item_output_bus_" + GTValues.VN[tier].toLowerCase()))
                        .outputItems(AHMachines.LOCK_ITEM_OUTPUT_BUS[tier].asStack())
                        .inputItems(GTMachines.ITEM_EXPORT_BUS[tier].asStack())
                        .inputItems(GTItems.COVER_ITEM_DETECTOR_ADVANCED)
                        .duration(200)
                        .EUt(GTValues.VA[tier])
                        .save(provider);
        }
        // endregion

        // region 电网仓
        for (int tier : GTValues.tiersBetween(GTValues.LV, GTCEuAPI.isHighTier() ? GTValues.MAX : GTValues.UHV)) {
            // region 1A电网仓配方
            if (tier != GTValues.MAX || isRegistedRecipe(GTMThings.id("wireless_energy_input_hatch_" + GTValues.VN[GTValues.MAX].toLowerCase())))
                ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_energy_input_hatch_" + GTValues.VN[tier].toLowerCase()))
                        .outputItems(AHMachines.NET_ENERGY_INPUT_HATCH[tier].asStack())
                        .inputItems(WirelessMachines.WIRELESS_ENERGY_INPUT_HATCH[tier].asStack(4))
                        .inputItems(GTMachines.WORLD_ACCELERATOR[getTierWorldAccelerator(tier)].asStack())
                        .inputItems(GTMachines.TRANSFORMER[getTierTransformer(tier)].asStack())
                        .inputItems(MachinesConstants.WIRELESS_ENERGY_RECEIVE_COVER.get(getTierWirelessEnergyRecieverCover(tier)).asStack())
                        .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                        .duration(400)
                        .EUt(GTValues.VA[tier])
                        .save(provider);
            else
                ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_energy_input_hatch_" + GTValues.VN[GTValues.MAX].toLowerCase()))
                        .outputItems(AHMachines.NET_ENERGY_INPUT_HATCH[GTValues.MAX].asStack())
                        .inputItems(AHMachines.NET_ENERGY_INPUT_HATCH[GTValues.OpV].asStack(4))
                        .inputItems(GTMachines.WORLD_ACCELERATOR[getTierWorldAccelerator(GTValues.MAX)].asStack())
                        .inputItems(GTMachines.TRANSFORMER[getTierTransformer(tier)].asStack())
                        .inputItems(MachinesConstants.WIRELESS_ENERGY_RECEIVE_COVER.get(getTierWirelessEnergyRecieverCover(tier)).asStack())
                        .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                        .duration(400)
                        .EUt(GTValues.VA[GTValues.MAX])
                        .save(provider);
            if (tier != GTValues.MAX || isRegistedRecipe(GTMThings.id("wireless_energy_output_hatch_" + GTValues.VN[GTValues.MAX].toLowerCase())))
                ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_energy_output_hatch_" + GTValues.VN[tier].toLowerCase()))
                        .outputItems(AHMachines.NET_ENERGY_OUTPUT_HATCH[tier].asStack())
                        .inputItems(WirelessMachines.WIRELESS_ENERGY_OUTPUT_HATCH[tier].asStack(4))
                        .inputItems(GTMachines.WORLD_ACCELERATOR[getTierWorldAccelerator(tier)].asStack())
                        .inputItems(GTMachines.TRANSFORMER[getTierTransformer(tier)].asStack())
                        .inputItems(MachinesConstants.WIRELESS_ENERGY_RECEIVE_COVER.get(getTierWirelessEnergyRecieverCover(tier)).asStack())
                        .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                        .duration(400)
                        .EUt(GTValues.VA[tier])
                        .save(provider);
            else
                ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_energy_output_hatch_" + GTValues.VN[GTValues.MAX].toLowerCase()))
                        .outputItems(AHMachines.NET_ENERGY_OUTPUT_HATCH[GTValues.MAX].asStack())
                        .inputItems(AHMachines.NET_ENERGY_OUTPUT_HATCH[GTValues.OpV].asStack(4))
                        .inputItems(GTMachines.WORLD_ACCELERATOR[getTierWorldAccelerator(GTValues.MAX)].asStack())
                        .inputItems(GTMachines.TRANSFORMER[getTierTransformer(tier)].asStack())
                        .inputItems(MachinesConstants.WIRELESS_ENERGY_RECEIVE_COVER.get(getTierWirelessEnergyRecieverCover(tier)).asStack())
                        .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                        .duration(400)
                        .EUt(GTValues.VA[GTValues.MAX])
                        .save(provider);
            // endregion
            // region 4A电网仓配方
            if (tier != GTValues.MAX || isRegistedRecipe(GTMThings.id("wireless_energy_input_hatch_" + GTValues.VN[GTValues.MAX].toLowerCase() + "_4a")))
                ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_energy_input_hatch_" + GTValues.VN[tier].toLowerCase() + "_4a"))
                        .outputItems(AHMachines.NET_ENERGY_INPUT_HATCH_4A[tier].asStack())
                        .inputItems(WirelessMachines.WIRELESS_ENERGY_INPUT_HATCH_4A[tier].asStack(4))
                        .inputItems(GTMachines.WORLD_ACCELERATOR[getTierWorldAccelerator(tier)].asStack())
                        .inputItems(GTMachines.HI_AMP_TRANSFORMER_2A[getTierTransformer(tier)].asStack())
                        .inputItems(MachinesConstants.WIRELESS_ENERGY_RECEIVE_COVER.get(getTierWirelessEnergyRecieverCover(tier)).asStack())
                        .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack())
                        .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                        .duration(400)
                        .EUt(GTValues.VA[tier])
                        .save(provider);
            else
                ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_energy_input_hatch_" + GTValues.VN[GTValues.MAX].toLowerCase() + "_4a"))
                        .outputItems(AHMachines.NET_ENERGY_INPUT_HATCH_4A[GTValues.MAX].asStack())
                        .inputItems(AHMachines.NET_ENERGY_INPUT_HATCH_4A[GTValues.OpV].asStack(4))
                        .inputItems(GTMachines.WORLD_ACCELERATOR[getTierWorldAccelerator(GTValues.MAX)].asStack())
                        .inputItems(GTMachines.HI_AMP_TRANSFORMER_2A[getTierTransformer(tier)].asStack())
                        .inputItems(MachinesConstants.WIRELESS_ENERGY_RECEIVE_COVER.get(getTierWirelessEnergyRecieverCover(tier)).asStack())
                        .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack())
                        .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                        .duration(400)
                        .EUt(GTValues.VA[GTValues.MAX])
                        .save(provider);
            if (tier != GTValues.MAX || isRegistedRecipe(GTMThings.id("wireless_energy_output_hatch_" + GTValues.VN[GTValues.MAX].toLowerCase() + "_4a")))
                ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_energy_output_hatch_" + GTValues.VN[tier].toLowerCase() + "_4a"))
                        .outputItems(AHMachines.NET_ENERGY_OUTPUT_HATCH_4A[tier].asStack())
                        .inputItems(WirelessMachines.WIRELESS_ENERGY_OUTPUT_HATCH_4A[tier].asStack(4))
                        .inputItems(GTMachines.WORLD_ACCELERATOR[getTierWorldAccelerator(tier)].asStack())
                        .inputItems(GTMachines.HI_AMP_TRANSFORMER_2A[getTierTransformer(tier)].asStack())
                        .inputItems(MachinesConstants.WIRELESS_ENERGY_RECEIVE_COVER.get(getTierWirelessEnergyRecieverCover(tier)).asStack())
                        .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack())
                        .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                        .duration(400)
                        .EUt(GTValues.VA[tier])
                        .save(provider);
            else
                ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_energy_output_hatch_" + GTValues.VN[GTValues.MAX].toLowerCase() + "_4a"))
                        .outputItems(AHMachines.NET_ENERGY_OUTPUT_HATCH_16A[GTValues.MAX].asStack())
                        .inputItems(AHMachines.NET_ENERGY_OUTPUT_HATCH_16A[GTValues.OpV].asStack(4))
                        .inputItems(GTMachines.WORLD_ACCELERATOR[getTierWorldAccelerator(GTValues.MAX)].asStack())
                        .inputItems(GTMachines.HI_AMP_TRANSFORMER_2A[getTierTransformer(tier)].asStack())
                        .inputItems(MachinesConstants.WIRELESS_ENERGY_RECEIVE_COVER.get(getTierWirelessEnergyRecieverCover(tier)).asStack())
                        .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack())
                        .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                        .duration(400)
                        .EUt(GTValues.VA[GTValues.MAX])
                        .save(provider);
            // endregion
            // region 16A电网仓配方
            if (tier != GTValues.MAX || isRegistedRecipe(GTMThings.id("wireless_energy_input_hatch_" + GTValues.VN[GTValues.MAX].toLowerCase() + "_16a")))
                ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_energy_input_hatch_" + GTValues.VN[tier].toLowerCase() + "_16a"))
                        .outputItems(AHMachines.NET_ENERGY_INPUT_HATCH_16A[tier].asStack())
                        .inputItems(WirelessMachines.WIRELESS_ENERGY_INPUT_HATCH_16A[tier].asStack(4))
                        .inputItems(GTMachines.WORLD_ACCELERATOR[getTierWorldAccelerator(tier)].asStack())
                        .inputItems(GTMachines.HI_AMP_TRANSFORMER_4A[getTierTransformer(tier)].asStack())
                        .inputItems(MachinesConstants.WIRELESS_ENERGY_RECEIVE_COVER.get(getTierWirelessEnergyRecieverCover(tier)).asStack())
                        .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack())
                        .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                        .duration(400)
                        .EUt(GTValues.VA[tier])
                        .save(provider);
            else
                ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_energy_input_hatch_" + GTValues.VN[GTValues.MAX].toLowerCase() + "_16a"))
                        .outputItems(AHMachines.NET_ENERGY_INPUT_HATCH_16A[GTValues.MAX].asStack())
                        .inputItems(AHMachines.NET_ENERGY_INPUT_HATCH_16A[GTValues.OpV].asStack(4))
                        .inputItems(GTMachines.WORLD_ACCELERATOR[getTierWorldAccelerator(GTValues.MAX)].asStack())
                        .inputItems(GTMachines.HI_AMP_TRANSFORMER_4A[getTierTransformer(tier)].asStack())
                        .inputItems(MachinesConstants.WIRELESS_ENERGY_RECEIVE_COVER.get(getTierWirelessEnergyRecieverCover(tier)).asStack())
                        .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack())
                        .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                        .duration(400)
                        .EUt(GTValues.VA[GTValues.MAX])
                        .save(provider);
            if (tier != GTValues.MAX || isRegistedRecipe(GTMThings.id("wireless_energy_output_hatch_" + GTValues.VN[GTValues.MAX].toLowerCase() + "_16a")))
                ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_energy_output_hatch_" + GTValues.VN[tier].toLowerCase() + "_16a"))
                        .outputItems(AHMachines.NET_ENERGY_OUTPUT_HATCH_16A[tier].asStack())
                        .inputItems(WirelessMachines.WIRELESS_ENERGY_OUTPUT_HATCH_16A[tier].asStack(4))
                        .inputItems(GTMachines.WORLD_ACCELERATOR[getTierWorldAccelerator(tier)].asStack())
                        .inputItems(GTMachines.HI_AMP_TRANSFORMER_4A[getTierTransformer(tier)].asStack())
                        .inputItems(MachinesConstants.WIRELESS_ENERGY_RECEIVE_COVER.get(getTierWirelessEnergyRecieverCover(tier)).asStack())
                        .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack())
                        .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                        .duration(400)
                        .EUt(GTValues.VA[tier])
                        .save(provider);
            else
                ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_energy_output_hatch_" + GTValues.VN[GTValues.MAX].toLowerCase() + "_16a"))
                        .outputItems(AHMachines.NET_ENERGY_OUTPUT_HATCH_16A[GTValues.MAX].asStack())
                        .inputItems(AHMachines.NET_ENERGY_OUTPUT_HATCH_16A[GTValues.OpV].asStack(4))
                        .inputItems(GTMachines.WORLD_ACCELERATOR[getTierWorldAccelerator(GTValues.MAX)].asStack())
                        .inputItems(GTMachines.HI_AMP_TRANSFORMER_4A[getTierTransformer(tier)].asStack())
                        .inputItems(MachinesConstants.WIRELESS_ENERGY_RECEIVE_COVER.get(getTierWirelessEnergyRecieverCover(tier)).asStack())
                        .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack())
                        .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                        .duration(400)
                        .EUt(GTValues.VA[GTValues.MAX])
                        .save(provider);
            // endregion
            if (tier >= GTValues.EV) {
                // region 64A电网仓配方
                ItemStack stack;
                ResourceLocation wireless_input_64 = GTMThings.id(GTValues.VN[tier].toLowerCase() + "_64a_wireless_energy_input_hatch");
                ResourceLocation wireless_output_64 = GTMThings.id(GTValues.VN[tier].toLowerCase() + "_64a_wireless_energy_output_hatch");
                stack = WirelessMachines.WIRELESS_ENERGY_INPUT_HATCH_16A[tier].asStack(16);
                // 若 GTMT 64A仓方块已注册则使用64A仓
                if (isRegisted(wireless_input_64))
                    stack = new ItemStack(Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(wireless_input_64)), 4);
                if (tier != GTValues.MAX || isRegistedRecipe(GTMThings.id("wireless_energy_input_hatch_" + GTValues.VN[GTValues.MAX].toLowerCase() + "_64a")))
                    ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_energy_input_hatch_" + GTValues.VN[tier].toLowerCase() + "_64a"))
                            .outputItems(AHMachines.NET_ENERGY_INPUT_HATCH_64A[tier].asStack())
                            .inputItems(stack)
                            .inputItems(GTMachines.WORLD_ACCELERATOR[getTierWorldAccelerator(tier)].asStack())
                            .inputItems(GTMachines.POWER_TRANSFORMER[getTierTransformer(tier)].asStack())
                            .inputItems(MachinesConstants.WIRELESS_ENERGY_RECEIVE_COVER_4A.get(getTierWirelessEnergyRecieverCover(tier)).asStack())
                            .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack())
                            .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                            .duration(400)
                            .EUt(GTValues.VA[tier])
                            .save(provider);
                else
                    ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_energy_input_hatch_" + GTValues.VN[GTValues.MAX].toLowerCase() + "_64a"))
                            .outputItems(AHMachines.NET_ENERGY_INPUT_HATCH_64A[GTValues.MAX].asStack())
                            .inputItems(AHMachines.NET_ENERGY_INPUT_HATCH_64A[GTValues.OpV].asStack(16))
                            .inputItems(GTMachines.WORLD_ACCELERATOR[getTierWorldAccelerator(GTValues.MAX)].asStack())
                            .inputItems(GTMachines.POWER_TRANSFORMER[getTierTransformer(tier)].asStack())
                            .inputItems(MachinesConstants.WIRELESS_ENERGY_RECEIVE_COVER_4A.get(getTierWirelessEnergyRecieverCover(tier)).asStack())
                            .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack())
                            .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                            .duration(400)
                            .EUt(GTValues.VA[GTValues.MAX])
                            .save(provider);
                stack = WirelessMachines.WIRELESS_ENERGY_OUTPUT_HATCH_16A[tier].asStack(16);
                if (isRegisted(wireless_output_64))
                    stack = new ItemStack(Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(wireless_output_64)), 4);
                if (tier != GTValues.MAX || isRegistedRecipe(GTMThings.id("wireless_energy_output_hatch_" + GTValues.VN[GTValues.MAX].toLowerCase() + "_64a")))
                    ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_energy_output_hatch_" + GTValues.VN[tier].toLowerCase() + "_64a"))
                            .outputItems(AHMachines.NET_ENERGY_OUTPUT_HATCH_64A[tier].asStack())
                            .inputItems(stack)
                            .inputItems(GTMachines.WORLD_ACCELERATOR[getTierWorldAccelerator(tier)].asStack())
                            .inputItems(GTMachines.POWER_TRANSFORMER[getTierTransformer(tier)].asStack())
                            .inputItems(MachinesConstants.WIRELESS_ENERGY_RECEIVE_COVER_4A.get(getTierWirelessEnergyRecieverCover(tier)).asStack())
                            .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack())
                            .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                            .duration(400)
                            .EUt(GTValues.VA[tier])
                            .save(provider);
                else
                    ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_energy_output_hatch_" + GTValues.VN[GTValues.MAX].toLowerCase() + "_64a"))
                            .outputItems(AHMachines.NET_ENERGY_OUTPUT_HATCH_64A[GTValues.MAX].asStack())
                            .inputItems(AHMachines.NET_ENERGY_OUTPUT_HATCH_64A[GTValues.OpV].asStack(16))
                            .inputItems(GTMachines.WORLD_ACCELERATOR[getTierWorldAccelerator(GTValues.MAX)].asStack())
                            .inputItems(GTMachines.POWER_TRANSFORMER[getTierTransformer(tier)].asStack())
                            .inputItems(MachinesConstants.WIRELESS_ENERGY_RECEIVE_COVER_4A.get(getTierWirelessEnergyRecieverCover(tier)).asStack())
                            .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack())
                            .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                            .duration(400)
                            .EUt(GTValues.VA[GTValues.MAX])
                            .save(provider);
                // endregion
                // region 256A激光仓配方
                ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_laser_input_hatch_" + GTValues.VN[tier].toLowerCase() + "_256a"))
                        .outputItems(AHMachines.NET_ENERGY_INPUT_HATCH_256A[tier].asStack())
                        .inputItems(WirelessMachines.WIRELESS_ENERGY_INPUT_HATCH_256A[tier].asStack(4))
                        .inputItems(GTMachines.WORLD_ACCELERATOR[getTierWorldAccelerator(tier)].asStack())
                        .inputItems(GTMachines.POWER_TRANSFORMER[getTierTransformer(tier)].asStack())
                        .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack())
                        .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                        .duration(800)
                        .EUt(GTValues.VA[tier])
                        .save(provider);
                ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_laser_output_hatch_" + GTValues.VN[tier].toLowerCase() + "_256a"))
                        .outputItems(AHMachines.NET_ENERGY_OUTPUT_HATCH_256A[tier].asStack())
                        .inputItems(WirelessMachines.WIRELESS_ENERGY_OUTPUT_HATCH_256A[tier].asStack(4))
                        .inputItems(GTMachines.WORLD_ACCELERATOR[getTierWorldAccelerator(tier)].asStack())
                        .inputItems(GTMachines.POWER_TRANSFORMER[getTierTransformer(tier)].asStack())
                        .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack())
                        .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                        .duration(800)
                        .EUt(GTValues.VA[tier])
                        .save(provider);
                // endregion
                // region 1024A激光仓配方
                ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_laser_input_hatch_" + GTValues.VN[tier].toLowerCase() + "_1024a"))
                        .outputItems(AHMachines.NET_ENERGY_INPUT_HATCH_1024A[tier].asStack())
                        .inputItems(WirelessMachines.WIRELESS_ENERGY_INPUT_HATCH_1024A[tier].asStack(4))
                        .inputItems(GTMachines.WORLD_ACCELERATOR[getTierWorldAccelerator(tier)].asStack())
                        .inputItems(GTMachines.POWER_TRANSFORMER[getTierTransformer(tier)].asStack())
                        .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack())
                        .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                        .duration(800)
                        .EUt(GTValues.VA[tier])
                        .save(provider);
                ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_laser_output_hatch_" + GTValues.VN[tier].toLowerCase() + "_1024a"))
                        .outputItems(AHMachines.NET_ENERGY_OUTPUT_HATCH_1024A[tier].asStack())
                        .inputItems(WirelessMachines.WIRELESS_ENERGY_OUTPUT_HATCH_1024A[tier].asStack(4))
                        .inputItems(GTMachines.WORLD_ACCELERATOR[getTierWorldAccelerator(tier)].asStack())
                        .inputItems(GTMachines.POWER_TRANSFORMER[getTierTransformer(tier)].asStack())
                        .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack())
                        .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                        .duration(800)
                        .EUt(GTValues.VA[tier])
                        .save(provider);
                // endregion
                // region 4096A激光仓配方
                ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_laser_input_hatch_" + GTValues.VN[tier].toLowerCase() + "_4096a"))
                        .outputItems(AHMachines.NET_ENERGY_INPUT_HATCH_4096A[tier].asStack())
                        .inputItems(WirelessMachines.WIRELESS_ENERGY_INPUT_HATCH_4096A[tier].asStack(4))
                        .inputItems(GTMachines.WORLD_ACCELERATOR[getTierWorldAccelerator(tier)].asStack())
                        .inputItems(GTMachines.POWER_TRANSFORMER[getTierTransformer(tier)].asStack())
                        .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack())
                        .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                        .duration(800)
                        .EUt(GTValues.VA[tier])
                        .save(provider);
                ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_laser_output_hatch_" + GTValues.VN[tier].toLowerCase() + "_4096a"))
                        .outputItems(AHMachines.NET_ENERGY_OUTPUT_HATCH_4096A[tier].asStack())
                        .inputItems(WirelessMachines.WIRELESS_ENERGY_OUTPUT_HATCH_4096A[tier].asStack(4))
                        .inputItems(GTMachines.WORLD_ACCELERATOR[getTierWorldAccelerator(tier)].asStack())
                        .inputItems(GTMachines.POWER_TRANSFORMER[getTierTransformer(tier)].asStack())
                        .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack())
                        .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                        .duration(800)
                        .EUt(GTValues.VA[tier])
                        .save(provider);
                // endregion
                // region 16384A激光仓配方
                ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_laser_input_hatch_" + GTValues.VN[tier].toLowerCase() + "_16384a"))
                        .outputItems(AHMachines.NET_LASER_INPUT_HATCH_16384A[tier].asStack())
                        .inputItems(WirelessMachines.WIRELESS_ENERGY_INPUT_HATCH_16384A[tier].asStack(4))
                        .inputItems(GTMachines.WORLD_ACCELERATOR[getTierWorldAccelerator(tier)].asStack())
                        .inputItems(GTMachines.POWER_TRANSFORMER[getTierTransformer(tier)].asStack())
                        .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack())
                        .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                        .duration(800)
                        .EUt(GTValues.VA[tier])
                        .save(provider);
                ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_laser_output_hatch_" + GTValues.VN[tier].toLowerCase() + "_16384a"))
                        .outputItems(AHMachines.NET_LASER_OUTPUT_HATCH_16384A[tier].asStack())
                        .inputItems(WirelessMachines.WIRELESS_ENERGY_OUTPUT_HATCH_16384A[tier].asStack(4))
                        .inputItems(GTMachines.WORLD_ACCELERATOR[getTierWorldAccelerator(tier)].asStack())
                        .inputItems(GTMachines.POWER_TRANSFORMER[getTierTransformer(tier)].asStack())
                        .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack())
                        .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                        .duration(800)
                        .EUt(GTValues.VA[tier])
                        .save(provider);
                // endregion
                // region 65536A激光仓配方
                ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_laser_input_hatch_" + GTValues.VN[tier].toLowerCase() + "_65536a"))
                        .outputItems(AHMachines.NET_LASER_INPUT_HATCH_65536A[tier].asStack())
                        .inputItems(WirelessMachines.WIRELESS_ENERGY_INPUT_HATCH_65536A[tier].asStack(4))
                        .inputItems(GTMachines.WORLD_ACCELERATOR[getTierWorldAccelerator(tier)].asStack())
                        .inputItems(GTMachines.POWER_TRANSFORMER[getTierTransformer(tier)].asStack())
                        .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack())
                        .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                        .duration(800)
                        .EUt(GTValues.VA[tier])
                        .save(provider);
                ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_laser_output_hatch_" + GTValues.VN[tier].toLowerCase() + "_65536a"))
                        .outputItems(AHMachines.NET_LASER_OUTPUT_HATCH_65536A[tier].asStack())
                        .inputItems(WirelessMachines.WIRELESS_ENERGY_OUTPUT_HATCH_65536A[tier].asStack(4))
                        .inputItems(GTMachines.WORLD_ACCELERATOR[getTierWorldAccelerator(tier)].asStack())
                        .inputItems(GTMachines.POWER_TRANSFORMER[getTierTransformer(tier)].asStack())
                        .inputItems(GTItems.COVER_ENERGY_DETECTOR_ADVANCED.asStack())
                        .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                        .duration(800)
                        .EUt(GTValues.VA[tier])
                        .save(provider);
                // endregion
            }

        }
        // endregion
        // region 电网激光仓换大安培电网仓
        for (int tier : NET_HIGH_TIERS2) {
            ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_energy_input_hatch_" + GTValues.VN[tier].toLowerCase() + "_16384a"))
                    .outputItems(AHMachines.NET_ENERGY_INPUT_HATCH_16384A[tier].asStack())
                    .inputItems(AHMachines.NET_LASER_INPUT_HATCH_16384A[tier].asStack())
                    .duration(400)
                    .EUt(GTValues.VA[tier])
                    .save(provider);
            ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_energy_input_hatch_" + GTValues.VN[tier].toLowerCase() + "_65536a"))
                    .outputItems(AHMachines.NET_ENERGY_INPUT_HATCH_65536A[tier].asStack())
                    .inputItems(AHMachines.NET_LASER_INPUT_HATCH_65536A[tier].asStack())
                    .duration(400)
                    .EUt(GTValues.VA[tier])
                    .save(provider);
            ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_energy_output_hatch_" + GTValues.VN[tier].toLowerCase() + "_16384a"))
                    .outputItems(AHMachines.NET_ENERGY_OUTPUT_HATCH_16384A[tier].asStack())
                    .inputItems(AHMachines.NET_LASER_OUTPUT_HATCH_16384A[tier].asStack())
                    .duration(400)
                    .EUt(GTValues.VA[tier])
                    .save(provider);
            ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("net_energy_output_hatch_" + GTValues.VN[tier].toLowerCase() + "_65536a"))
                    .outputItems(AHMachines.NET_ENERGY_OUTPUT_HATCH_65536A[tier].asStack())
                    .inputItems(AHMachines.NET_LASER_OUTPUT_HATCH_65536A[tier].asStack())
                    .duration(400)
                    .EUt(GTValues.VA[tier])
                    .save(provider);
        }
        // endregion

        // region 适配系统
        ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("tool_net_data_stick"))
                .outputItems(AHItems.TOOL_NET_DATA_STICK.asStack())
                .inputItems(GTItems.EPOXY_BOARD.asStack())
                .inputItems(GTItems.WORKSTATION_EV.asStack())
                .inputItems(TagPrefix.wireFine, GTMaterials.Electrum, 16)
                .inputItems(TagPrefix.plate, GTMaterials.PolyvinylChloride, 4)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                .duration(400)
                .EUt(GTValues.VA[HV])
                .save(provider);
        ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("adaptive_net_energy_output_hatch"))
                .outputItems(AHMachines.ADAPTIVE_NET_ENERGY_OUTPUT_HATCH.asStack())
                .inputItems(AHMachines.NET_ENERGY_OUTPUT_HATCH[HV].asStack(4))
                .inputItems(GTItems.SENSOR_HV.asStack(4))
                .inputItems(WirelessMachines.WIRELESS_ENERGY_INTERFACE.asStack())
                .inputItems(CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_HV_4A.asStack())
                .inputItems(GTBlocks.COIL_NICHROME.asStack())
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                .duration(400)
                .EUt(GTValues.VA[HV])
                .save(provider);
        ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("adaptive_net_energy_input_hatch"))
                .outputItems(AHMachines.ADAPTIVE_NET_ENERGY_INPUT_HATCH.asStack())
                .inputItems(AHMachines.NET_ENERGY_INPUT_HATCH[HV].asStack(4))
                .inputItems(GTItems.SENSOR_HV.asStack(4))
                .inputItems(WirelessMachines.WIRELESS_ENERGY_INTERFACE.asStack())
                .inputItems(CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_HV_4A.asStack())
                .inputItems(GTBlocks.COIL_NICHROME.asStack())
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                .duration(400)
                .EUt(GTValues.VA[HV])
                .save(provider);
        ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("adaptive_net_laser_output_hatch"))
                .outputItems(AHMachines.ADAPTIVE_NET_LASER_OUTPUT_HATCH.asStack())
                .inputItems(AHMachines.NET_ENERGY_OUTPUT_HATCH_256A[LuV].asStack(4))
                .inputItems(GTItems.SENSOR_LuV.asStack(4))
                .inputItems(WirelessMachines.WIRELESS_ENERGY_INTERFACE.asStack())
                .inputItems(CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_LUV_4A.asStack())
                .inputItems(GTBlocks.COIL_HSSG.asStack())
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                .duration(400)
                .EUt(GTValues.VA[IV])
                .save(provider);
        ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("adaptive_net_laser_input_hatch"))
                .outputItems(AHMachines.ADAPTIVE_NET_LASER_INPUT_HATCH.asStack())
                .inputItems(AHMachines.NET_ENERGY_INPUT_HATCH_256A[LuV].asStack(4))
                .inputItems(GTItems.SENSOR_LuV.asStack(4))
                .inputItems(WirelessMachines.WIRELESS_ENERGY_INTERFACE.asStack())
                .inputItems(CustomItems.WIRELESS_ENERGY_RECEIVE_COVER_LUV_4A.asStack())
                .inputItems(GTBlocks.COIL_HSSG.asStack())
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                .duration(400)
                .EUt(GTValues.VA[IV])
                .save(provider);
        ASSEMBLER_RECIPES.recipeBuilder(GTMAdvancedHatch.id("adaptive_net_energy_terminal"))
                .outputItems(AHMachines.ADAPTIVE_NET_ENERGY_TERMINAL.asStack())
                .inputItems(GTItems.SENSOR_HV.asStack(4))
                .inputItems(GTItems.EMITTER_HV.asStack(4))
                .inputItems(WirelessMachines.WIRELESS_ENERGY_INTERFACE.asStack())
                .inputItems(WirelessMachines.WIRELESS_ENERGY_MONITOR.asStack())
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                .duration(400)
                .EUt(GTValues.VA[HV])
                .save(provider);
        // endregion
    }

    public static int getTierWorldAccelerator(int tier) {
        if (tier <= GTValues.MV) tier = GTValues.MV;
        if (tier >= GTValues.UV) tier = GTValues.UV;
        return tier;
    }

    public static int getTierWirelessEnergyRecieverCover(int tier) {
        if (tier <= GTValues.LV) tier = GTValues.LV;
        if (tier >= GTValues.OpV) tier = GTValues.OpV;
        return tier - 1;
    }

    // 限制变压器电压不要超出已有物品
    public static int getTierTransformer(int tier) {
        if (tier >= GTValues.OpV) tier = GTValues.OpV;
        return tier;
    }

    // 判断当前机器是否被注册
    private static boolean isRegisted(ResourceLocation name) {
        return ForgeRegistries.BLOCKS.containsKey(name);
    }

    // 判断当前机器是否被注册配方
    private static boolean isRegistedRecipe(ResourceLocation recipe_name) {
        return ForgeRegistries.RECIPE_SERIALIZERS.containsKey(recipe_name);
    }
}
