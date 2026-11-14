package io.github.gizmo;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;

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

    private final Set<BlockPos> reported = new HashSet<>();
    private int tickCounter;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(Minecraft client) {
        if (client.player == null || client.level == null) {
            reported.clear();
            tickCounter = 0;
            return;
        }

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
