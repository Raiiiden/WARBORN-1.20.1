package com.raiiiden.warborn.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class NVGScreenFadeOverlay {

    // Fade in quickly, hold briefly, fade out slowly — total ~1 second
    private static final int FADE_IN_TICKS = 3;
    private static final int HOLD_TICKS = 3;
    private static final int FADE_OUT_TICKS = 3;
    static final int TOTAL_TICKS = FADE_IN_TICKS + HOLD_TICKS + FADE_OUT_TICKS; // 20

    // -1 = inactive
    private static int fadeTicksElapsed = -1;

    public static final IGuiOverlay OVERLAY = (gui, graphics, partialTick, width, height) -> {
        if (fadeTicksElapsed < 0) return;

        float smoothElapsed = fadeTicksElapsed + partialTick;
        float alpha;

        if (smoothElapsed < FADE_IN_TICKS) {
            alpha = smoothElapsed / FADE_IN_TICKS;
        } else if (smoothElapsed < FADE_IN_TICKS + HOLD_TICKS) {
            alpha = 1.0f;
        } else {
            float outProgress = smoothElapsed - FADE_IN_TICKS - HOLD_TICKS;
            alpha = 1.0f - outProgress / FADE_OUT_TICKS;
        }

        alpha = Mth.clamp(alpha, 0f, 1f);
        int a = (int) (alpha * 255);
        if (a <= 0) return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.fill(0, 0, width, height, (a << 24)); // ARGB black with alpha
        RenderSystem.disableBlend();
    };

    /** Call this to start a fade-to-black flash. */
    public static void triggerFade() {
        fadeTicksElapsed = 0;
    }

    /** Must be called every client tick. */
    public static void tick() {
        if (fadeTicksElapsed < 0) return;
        fadeTicksElapsed++;
        if (fadeTicksElapsed >= TOTAL_TICKS) {
            fadeTicksElapsed = -1;
        }
    }
}
