/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.winterwolfsv.runorama_next;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class RunoNextSettings {

    public static RunoNextSettings settings;
    public final RunoNextConfig.Property<Boolean> replaceTitleScreen;
    public final RunoNextConfig.Property<Integer> poolSize;
    public final RunoNextConfig.Property<Double> rotationSpeed;
    public final RunoNextConfig.Property<Boolean> includeVanillaPanorama;
    public final RunoNextConfig.Property<Double> panoramaFov;
    private final RunoramaNext mod;
    private final RunoNextConfig config;
    private final Path path;

    public RunoNextSettings(RunoramaNext mod, RunoNextConfig backend, Path path) {
        this.mod = mod;
        this.config = backend;
        this.path = path;
        this.replaceTitleScreen = config.booleanProperty("replace-title-screen", true);
        this.poolSize = config.integerProperty("pool-size", 1000);
        this.rotationSpeed = config.doubleProperty("clockwise-rotation-speed", 1.0D);
        this.includeVanillaPanorama = config.booleanProperty("include-vanilla-panorama", false);
        this.panoramaFov = config.doubleProperty("panorama-fov", 82);
    }

    public Path getCurrentRunoramaNextFolder() {
        Path parent = null;
        for (int i = 0; i < this.poolSize.get(); i++) {
            parent = mod.getCacheImagePath(String.valueOf(i));
            if (!Files.exists(parent)) {
                try {
                    Files.createDirectories(parent);
                } catch (IOException ex) {
                    RunoramaNext.LOGGER.error("Cannot create folder {} for RunoramaNext!", parent, ex);
                }
                break;
            }
            if (i == this.poolSize.get() - 1) {
                parent = mod.getCacheImagePath("overflow");
                RunoCommands.sendPlayerMessage("runorama-next-panoramas folder is full, please delete some panoramas to make room for new ones or increase the pool size.");
            }
        }
        return parent;
    }


    public void save() {
        this.config.save(path);
    }

}
