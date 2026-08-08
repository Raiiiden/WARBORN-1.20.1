package com.raiiiden.warborn.client.icon;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.raiiiden.warborn.WARBORN;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

// Bakes each piece into a texture atlas once and draws icons as a single quad; rendering the geo live per slot per frame would not scale. Keyed by item, not stack.
public final class WarbornIconAtlas {
    // Icon resolution. 64 is 1:1 with a 16px slot at GUI scale 4, and downfilters cleanly below that.
    private static final int CELL = 64;
    private static final int PAGE_COLUMNS = 16;
    private static final int PAGE_SIZE = CELL * PAGE_COLUMNS;
    private static final int CELLS_PER_PAGE = PAGE_COLUMNS * PAGE_COLUMNS;
    private static final int MAX_PAGES = 8;
    // Half-depth of the bake camera, in blocks. Nothing wearable comes close to this.
    private static final float DEPTH = 10.0f;
    // Matches the z vanilla draws item models at, so decorations still layer on top.
    private static final float ICON_Z = 150.0f;

    // Diffuse lights aimed at the icon camera; Lighting.setupFor3DItems pre-rotates its constants for a GUI block camera, which leaves our visible faces near-unlit.
    private static final Vector3f LIGHT_KEY = new Vector3f(0.6f, 0.9f, 0.6f).normalize();
    private static final Vector3f LIGHT_FILL = new Vector3f(-0.6f, 0.5f, 0.5f).normalize();

    private static final Map<Item, Cell> CELLS = new IdentityHashMap<>();
    private static final List<RenderTarget> PAGES = new ArrayList<>();
    // Marks an item we tried and can't draw, so we don't retry it every frame.
    private static final Cell UNAVAILABLE = new Cell(-1, 0, 0);

    private static int nextCell;

    private WarbornIconAtlas() {}

    private record Cell(int page, int x, int y) {}

    // Draws the baked icon, baking on first sight; returns false if the caller should fall back to vanilla item rendering.
    public static boolean draw(PoseStack poseStack, ItemStack stack, int x, int y) {
        if (stack.isEmpty() || !RenderSystem.isOnRenderThread()) return false;

        Item item = stack.getItem();
        if (!WarbornArmorIcons.isSupported(item)) return false;

        Cell cell = CELLS.get(item);
        if (cell == null) {
            cell = bake(item);
            CELLS.put(item, cell);
        }
        if (cell.page() < 0) return false;

        blit(poseStack, cell, x, y);
        return true;
    }

    // Drops every page. Called on resource reload, since models and textures may have changed.
    public static void invalidate() {
        for (RenderTarget page : PAGES) {
            page.destroyBuffers();
        }
        PAGES.clear();
        CELLS.clear();
        nextCell = 0;
        WarbornArmorIcons.clear();
    }

    private static Cell bake(Item item) {
        WarbornArmorIcons.Fit fit = WarbornArmorIcons.fit(item);
        if (fit == null) return UNAVAILABLE;

        Cell cell = allocate();
        if (cell == null) return UNAVAILABLE;

        Minecraft minecraft = Minecraft.getInstance();
        PoseStack modelView = RenderSystem.getModelViewStack();

        RenderSystem.backupProjectionMatrix();
        modelView.pushPose();
        modelView.setIdentity();
        RenderSystem.applyModelViewMatrix();

        try {
            PAGES.get(cell.page()).bindWrite(false);
            RenderSystem.viewport(cell.x(), cell.y(), CELL, CELL);
            RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(
                    fit.centerX() - fit.half(), fit.centerX() + fit.half(),
                    fit.centerY() - fit.half(), fit.centerY() + fit.half(),
                    -DEPTH, DEPTH), VertexSorting.ORTHOGRAPHIC_Z);
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.setShaderLights(LIGHT_KEY, LIGHT_FILL);

            MultiBufferSource.BufferSource buffers = MultiBufferSource.immediate(new BufferBuilder(2048));
            PoseStack poseStack = new PoseStack();
            WarbornArmorIcons.applyView(poseStack, item);
            WarbornArmorIcons.draw(item, poseStack, buffers);
            buffers.endBatch();
        } catch (Exception exception) {
            WARBORN.LOGGER.error("Failed to bake inventory icon for {}", item, exception);
            return UNAVAILABLE;
        } finally {
            modelView.popPose();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.restoreProjectionMatrix();
            minecraft.getMainRenderTarget().bindWrite(true);
            // Vanilla only re-arms lighting for flat models, so anything 3D drawn after us inherits whatever
            // is left set. Put the GUI's own lights back.
            Lighting.setupFor3DItems();
        }

        return cell;
    }

    @Nullable
    private static Cell allocate() {
        int page = nextCell / CELLS_PER_PAGE;
        if (page >= MAX_PAGES) return null;

        while (PAGES.size() <= page) {
            TextureTarget target = new TextureTarget(PAGE_SIZE, PAGE_SIZE, true, Minecraft.ON_OSX);
            target.setFilterMode(GL11.GL_LINEAR);
            PAGES.add(target);
        }

        int local = nextCell++ % CELLS_PER_PAGE;
        return new Cell(page, (local % PAGE_COLUMNS) * CELL, (local / PAGE_COLUMNS) * CELL);
    }

    private static void blit(PoseStack poseStack, Cell cell, int x, int y) {
        float u0 = cell.x() / (float) PAGE_SIZE;
        float u1 = (cell.x() + CELL) / (float) PAGE_SIZE;
        // Framebuffer rows run bottom-up, GUI rows run top-down, so V is flipped.
        float vTop = (cell.y() + CELL) / (float) PAGE_SIZE;
        float vBottom = cell.y() / (float) PAGE_SIZE;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, PAGES.get(cell.page()).getColorTextureId());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        Matrix4f pose = poseStack.last().pose();
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.vertex(pose, x, y, ICON_Z).uv(u0, vTop).endVertex();
        builder.vertex(pose, x, y + 16, ICON_Z).uv(u0, vBottom).endVertex();
        builder.vertex(pose, x + 16, y + 16, ICON_Z).uv(u1, vBottom).endVertex();
        builder.vertex(pose, x + 16, y, ICON_Z).uv(u1, vTop).endVertex();
        BufferUploader.drawWithShader(builder.end());

        RenderSystem.disableBlend();
    }
}
