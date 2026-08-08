package com.raiiiden.warborn.client.renderer.item;

import com.raiiiden.warborn.common.item.WBArmorItem;
import com.raiiiden.warborn.common.object.capability.NVGBatteryStorage;
import com.raiiiden.warborn.common.util.BatteryDisplayCache;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemDecorator;

// Draws a green-to-red battery bar above the durability bar for helmets with a battery; returns false so vanilla still draws below it.
public class HelmetBatteryDecorator implements IItemDecorator {

    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        if (!WBArmorItem.canHaveGoggles(stack)) return false;

        ItemStack battery = WBArmorItem.getInsertedBattery(stack);
        if (battery.isEmpty()) return false; // nothing to show

        int energy = BatteryDisplayCache.isActive()
                ? BatteryDisplayCache.get()
                : NVGBatteryStorage.readEnergy(battery);
        float fraction = (float) energy / NVGBatteryStorage.MAX_CAPACITY;
        int barWidth = Math.max(1, (int) (13f * fraction));
        int color = batteryColor(fraction);

        int x = xOffset + 2;
        int y = yOffset + 11; // sits above vanilla durability bar (at y+13)

        // Background (black), 13 px wide, 2 px tall
        guiGraphics.fill(RenderType.guiOverlay(), x, y, x + 13, y + 2, 0xFF000000);
        // Colored fill (top pixel, matching vanilla bar style)
        guiGraphics.fill(RenderType.guiOverlay(), x, y, x + barWidth, y + 1, 0xFF000000 | color);

        return false; // do not suppress the vanilla durability bar
    }

    // Maps fraction [0,1] to RGB int: green → yellow → orange → red.
    private static int batteryColor(float f) {
        f = Math.max(0f, Math.min(1f, f));
        int r = (int) (255f * Math.min(1f, 2f * (1f - f)));
        int g = (int) (255f * Math.min(1f, 2f * f));
        return (r << 16) | (g << 8);
    }
}
