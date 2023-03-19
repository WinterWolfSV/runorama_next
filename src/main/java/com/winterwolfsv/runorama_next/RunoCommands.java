package com.winterwolfsv.runorama_next;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.LiteralText;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class RunoCommands {
    public static void register() {

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(CommandManager.literal("runoramanext")
                .then(CommandManager.literal("config")
                        .then(CommandManager.literal("setpanoramafov")
                                .executes(context -> commandSetPanoramaFov(-1))
                                .then(CommandManager.argument("fov", DoubleArgumentType.doubleArg(30, 110))
                                        .executes(context -> commandSetPanoramaFov(DoubleArgumentType.getDouble(context, "fov")))))
                        .then(CommandManager.literal("rotationSpeed")
                                .executes(context -> commandSetRotationSpeed(-1))
                                .then(CommandManager.argument("rotationSpeed", DoubleArgumentType.doubleArg())
                                        .executes(context -> commandSetRotationSpeed(DoubleArgumentType.getDouble(context, "rotationSpeed")))))
                        .then(CommandManager.literal("poolsize")
                                .executes(context -> commandSetPoolSize(-1))
                                .then(CommandManager.argument("poolsize", IntegerArgumentType.integer(1))
                                        .executes(context -> commandSetPoolSize(IntegerArgumentType.getInteger(context, "poolsize")))))
                        .then(CommandManager.literal("includevanillapanorama")
                                .executes(context -> commandSetIncludeVanillaPanorama(null))
                                .then(CommandManager.argument("includevanillapanorama", BoolArgumentType.bool())
                                        .executes(context -> commandSetIncludeVanillaPanorama(BoolArgumentType.getBool(context, "includevanillapanorama")))))
                        .then(CommandManager.literal("replaceTitleScreen")
                                .executes(context -> commandSetReplaceTitleScreen(null))
                                .then(CommandManager.argument("replaceTitleScreen", BoolArgumentType.bool())
                                        .executes(context -> commandSetReplaceTitleScreen(BoolArgumentType.getBool(context, "replaceTitleScreen"))))))
                .then(CommandManager.literal("createresourcepack")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .executes(context -> commandCreateResourcePack(StringArgumentType.getString(context, "name"), null))
                                .then(CommandManager.argument("description", StringArgumentType.string())
                                        .executes(context -> commandCreateResourcePack(StringArgumentType.getString(context, "name"), StringArgumentType.getString(context, "description"))))))));
    }

    private static int commandCreateResourcePack(String name, String description) {
        if (description == null) {
            description = "RunoramaNext Resource Pack";
        }
        RunoramaNext mod = RunoramaNext.getInstance();
        Path resourcepackDir = Paths.get(FabricLoader.getInstance().getGameDir() + "\\resourcepacks\\" + name);
        if (Files.exists(resourcepackDir)) {
            sendPlayerMessage("Resource pack already exists.");
            return 0;
        }
        Path screenshotDir = Paths.get(resourcepackDir + "\\assets\\minecraft\\textures\\gui\\title\\background");
        System.out.println(screenshotDir);
        MinecraftClient.getInstance().execute(() -> {
            try {
                mod.createPanorama(screenshotDir);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
        try {
            String packMcMeta = "{\n" +
                    "  \"pack\": {\n" +
                    "    \"pack_format\": 5,\n" +
                    "    \"description\": \"" + description + "\"\n" +
                    "  }\n" +
                    "}";
            while (!new File(screenshotDir + "\\panorama_0.png").exists()) {
                Thread.sleep(10);
            }
            while (Files.size(Paths.get(screenshotDir + "\\panorama_0.png")) < 10) {
                Thread.sleep(10);
            }
            Files.write(resourcepackDir.resolve("pack.mcmeta"), packMcMeta.getBytes());
            Files.copy(Paths.get(screenshotDir + "\\panorama_0.png"), Paths.get(resourcepackDir + "\\pack.png"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return 1;
    }

    private static int commandSetReplaceTitleScreen(Boolean replaceTitleScreen) {
        RunoramaNext mod = RunoramaNext.getInstance();
        if (replaceTitleScreen == null) {
            replaceTitleScreen = mod.getSettings().replaceTitleScreen.get();
            sendPlayerMessage("Replace title screen is currently set to: " + replaceTitleScreen);
        } else {
            mod.getSettings().replaceTitleScreen.set(replaceTitleScreen);
            mod.getSettings().save();
            sendPlayerMessage("Replace title screen set to: " + replaceTitleScreen);
        }
        return 1;
    }


    private static int commandSetIncludeVanillaPanorama(Boolean includeVanillaPanorama) {
        RunoramaNext mod = RunoramaNext.getInstance();
        if (includeVanillaPanorama == null) {
            includeVanillaPanorama = mod.getSettings().includeVanillaPanorama.get();
            sendPlayerMessage("Include vanilla panorama is currently set to: " + includeVanillaPanorama);
        } else {
            mod.getSettings().includeVanillaPanorama.set(includeVanillaPanorama);
            mod.getSettings().save();
            sendPlayerMessage("Include vanilla panorama set to: " + includeVanillaPanorama);
        }
        return 1;
    }

    private static int commandSetPoolSize(int poolSize) {
        RunoramaNext mod = RunoramaNext.getInstance();
        if (poolSize == -1) {
            poolSize = mod.getSettings().poolSize.get();
            sendPlayerMessage("Pool size is currently set to: " + poolSize);
        } else {
            mod.getSettings().poolSize.set(poolSize);
            mod.getSettings().save();
            sendPlayerMessage("Set the pool size to: " + poolSize);
        }
        return 1;
    }

    private static int commandSetRotationSpeed(double rotationSpeed) {
        RunoramaNext mod = RunoramaNext.getInstance();
        if (rotationSpeed == -1) {
            rotationSpeed = mod.getSettings().rotationSpeed.get();
            sendPlayerMessage("Rotation speed is currently set to: " + rotationSpeed);
        } else {
            mod.getSettings().rotationSpeed.set(rotationSpeed);
            mod.getSettings().save();
            sendPlayerMessage("Rotation speed set to: " + rotationSpeed);
        }
        return 1;
    }


    private static int commandSetPanoramaFov(double fov) {
        RunoramaNext mod = RunoramaNext.getInstance();
        if (fov == -1) {
            fov = mod.getSettings().panoramaFov.get();
            sendPlayerMessage("Panorama FOV is currently set to " + fov + " degrees.");
        } else {
            mod.getSettings().panoramaFov.set(fov);
            mod.getSettings().save();
            sendPlayerMessage("Panorama FOV set to " + fov + " degrees.");

        }
        return 1;
    }

    public static void sendPlayerMessage(String message) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            player.sendMessage(new LiteralText(message));
        }
    }

    public static void setSettings(RunoNextSettings settingsNew) {
        RunoNextSettings.settings = settingsNew;
    }
}
