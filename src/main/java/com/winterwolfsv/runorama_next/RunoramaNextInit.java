package com.winterwolfsv.runorama_next;

import net.fabricmc.api.ModInitializer;

public class RunoramaNextInit implements ModInitializer {
    @Override
    public void onInitialize() {
        RunoCommands.register();
    }
}
