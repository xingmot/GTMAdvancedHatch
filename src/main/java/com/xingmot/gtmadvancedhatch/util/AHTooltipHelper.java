package com.xingmot.gtmadvancedhatch.util;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;

import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** modify from gtm TooltipHelper */
public class AHTooltipHelper {

    private static final List<GTFormattingCode> CODES = new ArrayList<>();
    private static final ChatFormatting[] ALL_COLORS;
    public static final GTFormattingCode RAINBOW_FAST;
    public static final GTFormattingCode RAINBOW_FAST2;
    public static final GTFormattingCode RAINBOW_FAST3;
    public static final GTFormattingCode RAINBOW_FAST4;
    public static final GTFormattingCode RAINBOW;
    public static final GTFormattingCode RAINBOW_SLOW;
    public static final GTFormattingCode BLINKING_CYAN;
    public static final GTFormattingCode BLINKING_RED;
    public static final GTFormattingCode BLINKING_ORANGE;
    public static final GTFormattingCode BLINKING_GRAY;

    public static void onClientTick() {
        CODES.forEach(GTFormattingCode::updateIndex);
    }

    static {
        ALL_COLORS = new ChatFormatting[] { ChatFormatting.RED, ChatFormatting.GOLD, ChatFormatting.YELLOW, ChatFormatting.GREEN, ChatFormatting.AQUA, ChatFormatting.DARK_AQUA, ChatFormatting.DARK_BLUE, ChatFormatting.BLUE, ChatFormatting.DARK_PURPLE, ChatFormatting.LIGHT_PURPLE };
        RAINBOW_FAST = createNewCode(1, ALL_COLORS);
        RAINBOW_FAST2 = createNewCode(2, ALL_COLORS);
        RAINBOW_FAST3 = createNewCode(3, ALL_COLORS);
        RAINBOW_FAST4 = createNewCode(4, ALL_COLORS);
        RAINBOW = createNewCode(5, ALL_COLORS);
        RAINBOW_SLOW = createNewCode(25, ALL_COLORS);
        BLINKING_CYAN = createNewCode(5, ChatFormatting.AQUA, ChatFormatting.WHITE);
        BLINKING_RED = createNewCode(5, ChatFormatting.RED, ChatFormatting.WHITE);
        BLINKING_ORANGE = createNewCode(25, ChatFormatting.GOLD, ChatFormatting.YELLOW);
        BLINKING_GRAY = createNewCode(25, ChatFormatting.GRAY, ChatFormatting.DARK_GRAY);
    }

    public static GTFormattingCode createNewCode(int rate, ChatFormatting... codes) {
        if (rate <= 0) {
            GTCEu.LOGGER.error("Could not create GT Formatting Code with rate {}, must be greater than zero!", rate);
            return null;
        } else if (codes != null && codes.length > 1) {
            GTFormattingCode code = new GTFormattingCode(rate, codes);
            CODES.add(code);
            return code;
        } else {
            GTCEu.LOGGER.error("Could not create GT Formatting Code with codes {}, must have length greater than one!", Arrays.toString(codes));
            return null;
        }
    }

    public static class GTFormattingCode {

        private final int rate;
        private final ChatFormatting[] codes;
        private int index = 0;

        public GTFormattingCode(int rate, ChatFormatting... codes) {
            this.rate = rate;
            this.codes = codes;
        }

        public void updateIndex() {
            if (GTValues.CLIENT_TIME % (long) this.rate == 0L) {
                if (this.index + 1 >= this.codes.length) {
                    this.index = 0;
                } else {
                    ++this.index;
                }
            }
        }

        public ChatFormatting getCurrent() {
            return this.codes[this.index];
        }

        /** here */
        public ChatFormatting getOffset(int offset) {
            int temp = this.index + offset;
            if (temp >= this.codes.length)
                temp %= this.codes.length;
            else if (temp < 0)
                temp += this.codes.length;
            return this.codes[temp];
        }

        public String toString() {
            return this.codes[this.index].toString();
        }
    }
}
