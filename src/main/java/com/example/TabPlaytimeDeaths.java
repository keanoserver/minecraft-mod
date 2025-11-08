package com.keano.tabplaytimedeaths;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.stat.Stats;

public class TabPlaytimeDeaths implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        // background thread to refresh every 10 seconds
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ignored) {}

                MinecraftServer server = Util.getMainThreadServer();
                if (server == null) continue;

                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    // playtime in ticks (20 ticks = 1 sec)
                    int ticks = player.getStatHandler().getStat(Stats.CUSTOM.get(Stats.PLAY_TIME));
                    int hours = ticks / (20 * 60 * 60);
                    int minutes = (ticks / (20 * 60)) % 60;

                    int deaths = player.getStatHandler().getStat(Stats.CUSTOM.get(Stats.DEATHS));

                    String label = player.getGameProfile().getName()
                            + " §7[" + hours + "h " + minutes + "m | " + deaths + " deaths]";

                    player.setDisplayName(Text.of(label));
                    player.refreshPosition(); // refresh for safety
                }
            }
        }, "TabUpdater").start();
    }
}
