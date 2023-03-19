/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.winterwolfsv.runorama_next;


import com.winterwolfsv.runorama_next.client.BoundImage;
import com.winterwolfsv.runorama_next.client.CloseableBinder;
import com.winterwolfsv.runorama_next.client.VanillaPanorama;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * RunoramaNext mod class.
 */
public final class RunoramaNext implements ClientModInitializer {

    public static final String ID = "runorama-next";
    public static final Logger LOGGER = LogManager.getLogger(ID);
    private static RunoramaNext instance;
    private Path panoramaDir;
    private Path settingsFile;
    private RunoNextSettings settings;

    public static Identifier name(String name) {
        return new Identifier(ID, name);
    }

    public static RunoramaNext getInstance() {
        return instance;
    }

    private static final KeyBinding screenshotKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Take panorama screenshot", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, "RunoramaNext Next"));

    @Override
    public void onInitializeClient() {
        instance = this;
        Path configRoot = FabricLoader.getInstance().getConfigDirectory().toPath();
        panoramaDir = configRoot.resolve(ID + "-panoramas");
        settingsFile = configRoot.resolve(ID + ".properties");
        readSettings();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (screenshotKey.wasPressed()) {
                createPanorama(getSettings().getCurrentRunoramaNextFolder());
            }
        });
    }

    public void createPanorama(Path contentFolder) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return;
        }
        double oldFov = client.options.fov;
        float oldPitch = client.player.pitch;
        float oldYaw = client.player.yaw;
        boolean hudStatus = client.options.hudHidden;

        client.options.fov = getSettings().panoramaFov.get();
        client.options.hudHidden = true;
        client.gameRenderer.renderWorld(10, Util.getMeasuringTimeNano(), new MatrixStack());

        for (int i = 0; i < 6; i++) {
            changeRotationForScreenshot(client.player, oldYaw, i);
            client.gameRenderer.renderWorld(1, Util.getMeasuringTimeNano(), new MatrixStack());
            takeScreenshot(contentFolder, i);
        }

        client.options.hudHidden = hudStatus;
        client.options.fov = oldFov;
        client.player.pitch = oldPitch;
        client.player.yaw = oldYaw;

        client.player.sendMessage(new TranslatableText("runoramanext.shot", new LiteralText(FabricLoader.getInstance().getConfigDir().relativize(contentFolder).toString()).styled(style -> {
            style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, contentFolder.toAbsolutePath().toString()));
        })));

    }

    public void readSettings() {
        try {
            Files.createDirectories(panoramaDir);
        } catch (IOException ex) {
            LOGGER.error("Cannot create directory {} for storing auto screenshots!", panoramaDir, ex);
        }

        RunoNextConfig config = Files.exists(settingsFile) ? RunoNextConfig.load(settingsFile) : new RunoNextConfig(new Properties());

        settings = new RunoNextSettings(this, config, settingsFile);

        config.save(settingsFile);
    }

    Path getCacheImagePath(String id) {
        return panoramaDir.resolve("panorama-" + id);
    }

    public List<Supplier</* Nullable */CloseableBinder>> makeScreenshotBinders() {
        List<Supplier</* Nullable */CloseableBinder>> ret = new ArrayList<>();

        try (DirectoryStream<Path> paths = Files.newDirectoryStream(panoramaDir, eachFolder -> {
            if (!Files.isDirectory(eachFolder)) {
                return false;
            }
            for (int i = 0; i < 6; i++) {
                Path part = eachFolder.resolve("panorama_" + i + ".png");
                if (!Files.isRegularFile(part)) {
                    return false;
                }
            }
            return true;
        })) {
            for (final Path eachFolder : paths) {
                ret.add(() -> {
                    NativeImage[] images = new NativeImage[6];
                    for (int j = 0; j < 6; j++) {
                        Path part = eachFolder.resolve("panorama_" + j + ".png");
                        try (InputStream stream = Files.newInputStream(part)) {
                            images[j] = NativeImage.read(stream);
                        } catch (IOException ex) {
                            RunoramaNext.LOGGER.error("Failed to bind custom screenshot part at {}!", part, ex);
                            return null;
                        }
                    }
                    return new BoundImage(images);
                });
            }
        } catch (IOException ex) {
            return Collections.emptyList();
        }

        if (settings.includeVanillaPanorama.get()) {
            ret.add(VanillaPanorama::new);
        }

        Collections.shuffle(ret);
        return ret;
    }

    public void saveScreenshot(NativeImage screenshot, Path folder, int i) {
        File folderPath = new File(folder.toUri());
        if (!folderPath.exists()) {
            folderPath.mkdirs();
        }
        ResourceImpl.RESOURCE_IO_EXECUTOR.execute(() -> {
            try {
                int width = screenshot.getWidth();
                int height = screenshot.getHeight();
                int int_3 = 0;
                int int_4 = 0;
                if (width > height) {
                    int_3 = (width - height) / 2;
                    width = height;
                } else {
                    int_4 = (height - width) / 2;
                    height = width;
                }
                NativeImage saved = new NativeImage(width, height, false);
                screenshot.resizeSubRectTo(int_3, int_4, width, height, saved);
                saved.writeFile(folder.resolve("panorama_" + i + ".png"));
            } catch (IOException var27) {
                RunoramaNext.LOGGER.warn("Couldn't save screenshot", var27);
            } finally {
                screenshot.close();
            }
        });
    }

    public RunoNextSettings getSettings() {
        return this.settings;
    }

    private void takeScreenshot(Path folder, int id) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        NativeImage shot = ScreenshotUtils.takeScreenshot(minecraftClient.getWindow().getFramebufferWidth(), minecraftClient.getWindow().getFramebufferHeight(),
                minecraftClient.getFramebuffer());
        saveScreenshot(shot, folder, id);
    }

    public void changeRotationForScreenshot(PlayerEntity player, float baseYaw, int stage) {
        switch (stage) {
            case 0: {
                player.yaw = (baseYaw);
                player.pitch = (0.0F);
                break;
            }
            case 1: {
                player.yaw = ((baseYaw + 90.0F) % 360.0F);
                player.pitch = (0.0F);
                break;
            }
            case 2: {
                player.yaw = ((baseYaw + 180.0F) % 360.0F);
                player.pitch = (0.0F);
                break;
            }
            case 3: {
                player.yaw = ((baseYaw + 270) % 360.0F);
                player.pitch = (0.0F);
                break;
            }
            case 4: {
                player.yaw = (baseYaw);
                player.pitch = (-90.0F);
                break;
            }
            case 5: {
                player.yaw = (baseYaw);
                player.pitch = (90.0F);
                break;
            }
        }
    }

}
