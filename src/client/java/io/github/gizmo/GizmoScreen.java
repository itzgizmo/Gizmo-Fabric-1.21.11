package io.github.gizmo;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Simple in-game control panel for Gizmo. */
public final class GizmoScreen extends Screen {
    private final GizmoClient gizmo;
    private Button scanningButton;

    public GizmoScreen(GizmoClient gizmo) {
        super(Component.literal("Gizmo"));
        this.gizmo = gizmo;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int buttonWidth = 180;

        scanningButton = Button.builder(scanningLabel(), button -> {
            gizmo.setScanningEnabled(!gizmo.isScanningEnabled());
            button.setMessage(scanningLabel());
        }).bounds(centerX - buttonWidth / 2, centerY - 10, buttonWidth, 20).build();
        addRenderableWidget(scanningButton);

        addRenderableWidget(Button.builder(Component.literal("Close"), button -> onClose())
            .bounds(centerX - buttonWidth / 2, centerY + 18, buttonWidth, 20)
            .build());
    }

    private Component scanningLabel() {
        return Component.literal("Scanning: " + (gizmo.isScanningEnabled() ? "ON" : "OFF"));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 55, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font,
            Component.literal("Nearby block scanner"), this.width / 2, this.height / 2 - 35, 0xB0B0B0);
        guiGraphics.drawCenteredString(this.font,
            Component.literal("Open menu key: G (change it in Options > Controls)"),
            this.width / 2, this.height / 2 + 55, 0xB0B0B0);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(null);
    }
}
