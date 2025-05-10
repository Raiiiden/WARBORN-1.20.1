package com.raiiiden.warborn.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.CameraType;
import net.minecraft.client.model.geom.ModelPart;

import java.util.HashSet;
import java.util.Set;

public class WBRenderState {
    public static boolean IGNORE_HIDDEN = false;
    public static final Set<ModelPart> HIDDEN_PARTS = new HashSet<>();

    public static boolean isFirstPerson() {
        return Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON;
    }
}