package com.alrex.throwability.client.animation;

import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

public class ModelRotation {
    private final Quaternion rotation;
    private final Vector3f center;
    private final Vector3f translation;

    public ModelRotation(Quaternion rotation) {
        this.rotation = rotation.copy();
        this.center = this.translation = new Vector3f();
    }

    public ModelRotation(Quaternion rotation, Vector3f center) {
        this.rotation = rotation.copy();
        this.center = center.copy();
        this.translation = new Vector3f();
    }

    public ModelRotation(Quaternion rotation, Vector3f center, Vector3f translation) {
        this.rotation = rotation.copy();
        this.center = center.copy();
        this.translation = translation;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public Vector3f getCenter() {
        return center;
    }

    public Vector3f getTranslation() {
        return translation;
    }
}
