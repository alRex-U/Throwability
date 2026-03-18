package com.alrex.throwability.client.input;

public enum EntityThrowControlType {
    PRESS_DEDICATED_KEY(new PressDedicatedKeyControl()),
    PRESS_KEY_TO_TOGGLE(new PressToToggleControl());

    private final IControlHandler handler;

    private EntityThrowControlType(IControlHandler handler) {
        this.handler = handler;
    }

    public void onTick() {
        handler.onTick();
    }

    public boolean isActive() {
        return handler.shouldThrowEntity();
    }

    private interface IControlHandler {
        void onTick();

        boolean shouldThrowEntity();
    }

    private static class PressDedicatedKeyControl implements IControlHandler {
        @Override
        public void onTick() {
        }

        @Override
        public boolean shouldThrowEntity() {
            return KeyBindings.getKeySpecialModifier().isDown();
        }
    }

    private static class PressToToggleControl implements IControlHandler {
        private boolean isToggled = false;

        @Override
        public void onTick() {
            if (KeyRecorder.getStateSpecialThrow().isPressed()) {
                isToggled = !isToggled;
            }
        }

        @Override
        public boolean shouldThrowEntity() {
            return isToggled;
        }
    }
}
