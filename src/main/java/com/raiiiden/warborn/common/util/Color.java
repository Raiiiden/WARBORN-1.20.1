package com.raiiiden.warborn.common.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;

/**
 * I don't like having all the colors scattered we will just reference them here radien
 * color stuff
 */
public class Color {

//    public static final Color WHITE = new Color(255, 255, 255);
//    public static final Color BLACK = new Color(0, 0, 0);
//    public static final Color RED = new Color(255, 0, 0);
//    public static final Color GREEN = new Color(0, 255, 0);
//    public static final Color BLUE = new Color(0, 0, 255);
//    public static final Color YELLOW = new Color(255, 255, 0);
//    public static final Color CYAN = new Color(0, 255, 255);
//    public static final Color MAGENTA = new Color(255, 0, 255);
//    public static final Color GRAY = new Color(128, 128, 128);
//    public static final Color LIGHT_GRAY = new Color(192, 192, 192);
//    public static final Color DARK_GRAY = new Color(64, 64, 64);


    // Default color
    public static final Color DEFAULT = new Color(51, 51, 51);

    // Material colors
    public static final Color STEEL = new Color(80, 80, 80);
    public static final Color CERAMIC = new Color(255, 255, 255);
    public static final Color SOFT_KEVLAR = new Color(212, 212, 212);
    public static final Color POLYETHYLENE = new Color(85, 170, 85);
    public static final Color COMPOSITE = new Color(255, 170, 0);

    // Protection tier colors - blue gradient
    public static final Color TIER_IIA = new Color(159, 197, 232);
    public static final Color TIER_II = new Color(111, 168, 220);
    public static final Color TIER_IIIA = new Color(61, 133, 198);
    public static final Color TIER_III = new Color(11, 83, 148);
    public static final Color TIER_IV = new Color(7, 55, 99);

    private final int r;
    private final int g;
    private final int b;
    private final int rgb;

    public Color(int r, int g, int b) {
        this.r = Mth.clamp(r, 0, 255);
        this.g = Mth.clamp(g, 0, 255);
        this.b = Mth.clamp(b, 0, 255);
        this.rgb = (r << 16) | (g << 8) | b;
    }

    public Color(int rgb) {
        this.rgb = rgb;
        this.r = (rgb >> 16) & 0xFF;
        this.g = (rgb >> 8) & 0xFF;
        this.b = rgb & 0xFF;
    }

    public static Color fromHex(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }

        try {
            int rgb = Integer.parseInt(hex, 16);
            return new Color(rgb);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex color: " + hex, e);
        }
    }

    public int getRed() {
        return r;
    }

    public int getGreen() {
        return g;
    }

    public int getBlue() {
        return b;
    }

    public int getRGB() {
        return rgb;
    }

    public int getARGB() {
        return 0xFF000000 | rgb;
    }

    public String toHexString() {
        return String.format("#%06X", rgb);
    }

    // don't mind this just from old code from other project
    public Color lighter(float factor) {
        factor = Mth.clamp(factor, 0.0f, 1.0f);
        int newR = r + (int) ((255 - r) * factor);
        int newG = g + (int) ((255 - g) * factor);
        int newB = b + (int) ((255 - b) * factor);
        return new Color(newR, newG, newB);
    }

    public Color darker(float factor) {
        factor = Mth.clamp(factor, 0.0f, 1.0f);
        int newR = (int) (r * (1 - factor));
        int newG = (int) (g * (1 - factor));
        int newB = (int) (b * (1 - factor));
        return new Color(newR, newG, newB);
    }

    public Color blend(Color other, float ratio) {
        ratio = Mth.clamp(ratio, 0.0f, 1.0f);
        int newR = (int) (r * (1 - ratio) + other.r * ratio);
        int newG = (int) (g * (1 - ratio) + other.g * ratio);
        int newB = (int) (b * (1 - ratio) + other.b * ratio);
        return new Color(newR, newG, newB);
    }

    /**
     * Creates a text component with this color
     */
    public Component colorize(String text) {
        return Component.literal(text)
                .withStyle(Style.EMPTY.withColor(getRGB()));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Color color = (Color) obj;
        return rgb == color.rgb;
    }

    @Override
    public int hashCode() {
        return rgb;
    }

    @Override
    public String toString() {
        return String.format("Color[r=%d,g=%d,b=%d,hex=%s]", r, g, b, toHexString());
    }
} 