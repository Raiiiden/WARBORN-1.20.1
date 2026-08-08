package com.raiiiden.warborn.client.icon;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

// Swallows a geo render pass and keeps only the vertex bounding box, so the icon bake can measure a model with zero GPU work.
final class IconBounds implements VertexConsumer, MultiBufferSource {
    float minX = Float.MAX_VALUE;
    float minY = Float.MAX_VALUE;
    float maxX = -Float.MAX_VALUE;
    float maxY = -Float.MAX_VALUE;

    boolean isEmpty() {
        return this.minX > this.maxX || this.minY > this.maxY;
    }

    @Override
    public @NotNull VertexConsumer getBuffer(@NotNull RenderType renderType) {
        return this;
    }

    @Override
    public @NotNull VertexConsumer vertex(double x, double y, double z) {
        if (x < this.minX) this.minX = (float) x;
        if (x > this.maxX) this.maxX = (float) x;
        if (y < this.minY) this.minY = (float) y;
        if (y > this.maxY) this.maxY = (float) y;
        return this;
    }

    @Override
    public @NotNull VertexConsumer color(int red, int green, int blue, int alpha) {
        return this;
    }

    @Override
    public @NotNull VertexConsumer uv(float u, float v) {
        return this;
    }

    @Override
    public @NotNull VertexConsumer overlayCoords(int u, int v) {
        return this;
    }

    @Override
    public @NotNull VertexConsumer uv2(int u, int v) {
        return this;
    }

    @Override
    public @NotNull VertexConsumer normal(float x, float y, float z) {
        return this;
    }

    @Override
    public void endVertex() {}

    @Override
    public void defaultColor(int red, int green, int blue, int alpha) {}

    @Override
    public void unsetDefaultColor() {}
}
