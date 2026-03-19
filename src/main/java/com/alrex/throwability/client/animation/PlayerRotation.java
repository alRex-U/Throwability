package com.alrex.throwability.client.animation;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PlayerRotation {
    private final Quaternionf rotation;
    private final Vector3f center;
    private final Vector3f translation;

    public PlayerRotation(Quaternionf rotation) {
        this.rotation = new Quaternionf(rotation);
        this.center = this.translation = new Vector3f();
    }

    public PlayerRotation(Quaternionf rotation, Vector3f center) {
        this.rotation = new Quaternionf(rotation);
        this.center = new Vector3f(center);
        this.translation = new Vector3f();
    }

    public PlayerRotation(Quaternionf rotation, Vector3f center, Vector3f translation) {
        this.rotation = new Quaternionf(rotation);
        this.center = new Vector3f(center);
        this.translation = new Vector3f(translation);
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public Vector3f getCenter() {
        return center;
    }

    public Vector3f getTranslation() {
        return translation;
    }
}
