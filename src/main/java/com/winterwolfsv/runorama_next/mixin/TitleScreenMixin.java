/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.winterwolfsv.runorama_next.mixin;


import com.winterwolfsv.runorama_next.RunoramaNext;
import com.winterwolfsv.runorama_next.client.CloseableBinder;
import com.winterwolfsv.runorama_next.client.RunoramaCubeMapRenderer;
import com.winterwolfsv.runorama_next.client.RunoramaRotatingCubeMapRenderer;
import com.winterwolfsv.runorama_next.client.VanillaPanorama;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    @Shadow
    private @Final
    @Mutable RotatingCubeMapRenderer backgroundRenderer;
    private CloseableBinder binder;

    protected TitleScreenMixin(Text text_1) {
        super(text_1);
    }

    @Inject(method = "init()V", at = @At("RETURN"))
    public void runorama$onOpenScreen(CallbackInfo ci) {
        if (RunoramaNext.getInstance().getSettings().replaceTitleScreen.get()) {
            for (Supplier<CloseableBinder> supplier : RunoramaNext.getInstance().makeScreenshotBinders()) {
                CloseableBinder binder = supplier.get();
                if (binder != null) {
                    this.binder = binder;
                    break;
                }
            }
        }
        if (this.binder == null) {
            this.binder = new VanillaPanorama();
        }
        this.backgroundRenderer = new RunoramaRotatingCubeMapRenderer(new RunoramaCubeMapRenderer(binder));
    }

    @Inject(method = "removed()V", at = @At("RETURN"))
    public void runorama$onDiscard(CallbackInfo ci) {
        binder.close();
        binder = null;
    }
}
