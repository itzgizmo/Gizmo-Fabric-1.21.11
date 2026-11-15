# Gizmo — Fabric 1.21.11 (standalone)

Gizmo is a standalone Fabric **client-side** mod. It does not use or depend on Meteor Client.

## Current features

- Press **G** in-game to open the Gizmo menu. The key can be changed in **Options → Controls → Key Binds → Gizmo**.
- The menu lets you turn the nearby block scanner on or off.
- Every second, checks blocks in a 16-block horizontal radius and 8-block vertical radius around the player.
- Reports chests, trapped chests, barrels, ender chests, shulker boxes, obsidian, beacons and spawners in chat with their coordinates.
- Scans only chunks already loaded by Minecraft on the client. It does not reveal unloaded/hidden chunks, inspect server-side data, send unusual packets, or bypass anti-cheat/server protections.
- Requires Minecraft Java 1.21.11, Fabric Loader, Fabric API, and Java 21.

This is a starter recreation, not the missing original DonutFinder implementation. The original README features such as NewChunks, stash detection, ESP overlays, sound/particle/entity analysis, StealthMode and AutoLogger are not included.

## Build locally

From this folder, with Java 21 and Gradle 9.2.1 or newer installed, run:

```bash
gradle build
```

The installable remapped JAR will be in `build/libs/` and will be named similar to `gizmo-1.0.0.jar`. Do not install a `-dev` JAR.

## Build on GitHub (no local Gradle installation needed)

1. Sign in at [GitHub](https://github.com).
2. Open [Create a new repository](https://github.com/new). Name it `Gizmo-Fabric-1.21.11`; choose Public or Private, then click **Create repository**.
3. Download this project ZIP and extract it on your computer.
4. Open the extracted `Gizmo-Fabric-1.21.11` folder. Upload **the contents of that folder** to the repository root, not an extra enclosing folder. Include the hidden `.github` folder and its `workflows/build.yml` file. If you do not see `.github` in Windows File Explorer, enable **View → Show → Hidden items**.
5. Commit the files to the `main` branch. The workflow should start automatically after the push.
6. In the repository, open **Actions** → **Build Gizmo Fabric JAR**. If needed, choose **Run workflow** → **Run workflow**.
7. Wait for the run to finish with a green check. Open the run and scroll to **Artifacts**. Download `gizmo-fabric-1.21.11` and extract that ZIP.
8. Put the `gizmo-1.0.0.jar` file (the exact version may differ) in your Minecraft 1.21.11 `mods` folder.

If GitHub does not show the workflow, check that `.github/workflows/build.yml` is at the repository root and that the repository's **Actions** tab is enabled.

## Minecraft installation

Install Fabric Loader for Minecraft 1.21.11 and the matching Fabric API version. Put Gizmo's JAR and Fabric API's JAR in the same `mods` folder, then launch the Fabric 1.21.11 profile.
