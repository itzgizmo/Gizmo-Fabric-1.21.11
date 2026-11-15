package io.github.gizmo;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

/**
 * Standalone Fabric client mod. It only examines block states in chunks that
 * Minecraft has already loaded on the client; it does not reveal hidden chunks,
 * send unusual packets, or attempt to bypass server protections.
 */
public final class GizmoClient implements ClientModInitializer {
    private static final int HORIZONTAL_RADIUS = 16;
    private static final int VERTICAL_RADIUS = 8;
    private static final int SCAN_INTERVAL_TICKS = 20;

    private static final KeyMapping.Category KEY_CATEGORY = KeyMapping.Category.register(
        ResourceLocation.fromNamespaceAndPath("gizmo", "main")
    );
    private static final KeyMapping OPEN_MENU_KEY = KeyBindingHelper.registerKeyBinding(
        new KeyMapping(
            "key.gizmo.open_menu",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            KEY_CATEGORY
        )
    );

    private final Set<BlockPos> reported = new HashSet<>();
    private int tickCounter;
    private boolean scanningEnabled = true;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(Minecraft client) {
        while (OPEN_MENU_KEY.consumeClick()) {
            client.setScreen(new GizmoScreen(this));
        }

        if (client.player == null || client.level == null) {
            reported.clear();
            tickCounter = 0;
            return;
        }

        if (!scanningEnabled) return;

        if (++tickCounter < SCAN_INTERVAL_TICKS) return;
        tickCounter = 0;

        BlockPos center = client.player.blockPosition();
        int minY = Math.max(client.level.getMinY(), center.getY() - VERTICAL_RADIUS);
        int maxY = Math.min(client.level.getMaxY() - 1, center.getY() + VERTICAL_RADIUS);

        for (int x = -HORIZONTAL_RADIUS; x <= HORIZONTAL_RADIUS; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = -HORIZONTAL_RADIUS; z <= HORIZONTAL_RADIUS; z++) {
                    BlockPos pos = center.offset(x, y - center.getY(), z);
                    if (!client.level.hasChunkAt(pos)) continue;

                    Block block = client.level.getBlockState(pos).getBlock();
                    String label = classify(block);
                    if (label == null) continue;

                    BlockPos immutablePos = pos.immutable();
                    if (reported.add(immutablePos)) {
                        client.player.displayClientMessage(
                            Component.literal("[Gizmo] " + label + " at "
                                + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()),
                            false
                        );
                    }
                }
            }
        }
    }

    boolean isScanningEnabled() {
        return scanningEnabled;
    }

    void setScanningEnabled(boolean enabled) {
        scanningEnabled = enabled;
        if (enabled) {
            // Allow previously seen blocks to be reported again after resuming.
            reported.clear();
            tickCounter = 0;
        }
    }

    private static String classify(Block block) {
        if (block == Blocks.CHEST) return "Chest";
        if (block == Blocks.TRAPPED_CHEST) return "Trapped chest";
        if (block == Blocks.BARREL) return "Barrel";
        if (block == Blocks.ENDER_CHEST) return "Ender chest";
        if (block instanceof ShulkerBoxBlock) return "Shulker box";
        if (block == Blocks.OBSIDIAN) return "Obsidian";
        if (block == Blocks.BEACON) return "Beacon";
        if (block == Blocks.SPAWNER) return "Spawner";
        return null;
    }
}
