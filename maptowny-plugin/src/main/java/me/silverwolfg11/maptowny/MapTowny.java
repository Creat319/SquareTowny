/*
 * Copyright (c) 2021 Silverwolfg11
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.silverwolfg11.maptowny;

import com.palmergames.bukkit.towny.Towny;
import me.silverwolfg11.maptowny.events.MapReloadEvent;
import me.silverwolfg11.maptowny.listeners.TownClaimListener;
import me.silverwolfg11.maptowny.managers.TownyLayerManager;
import me.silverwolfg11.maptowny.objects.MapConfig;
import me.silverwolfg11.maptowny.platform.MapPlatform;
import me.silverwolfg11.maptowny.tasks.RenderTownsTask;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.logging.Level;

public final class MapTowny extends JavaPlugin implements MapTownyPlugin {

    private TownyLayerManager layerManager;
    private MapPlatform mapPlatform;
    private MapConfig config;

    @Override
    public void onEnable() {
        // Check that Towny is enabled
        // While the plugin.yml makes sure Towny is installed, Towny can be disabled
        if (!Bukkit.getPluginManager().isPluginEnabled("Towny")) {
            setEnabled(false);
            return;
        }

        // Plugin startup logic
        try {
            config = MapConfig.loadConfig(getDataFolder(), getLogger());
        } catch (IOException e) {
            // IOException caused by creating new file usually, so disabling is a valid option
            getLogger().log(Level.SEVERE, "Error loading config. Disabling plugin...", e);
            setEnabled(false);
            return;
        }

        // Try to load the map platform
        mapPlatform = loadPlatform();

        // No map platform found so disable
        if (mapPlatform == null) {
            getLogger().severe("Error no valid map plugin found! SquareTowny requires squaremap.");
            setEnabled(false);
            return;
        }

        getLogger().info("Using web-map plugin: " + mapPlatform.getPlatformName());

        // Load layer manager
        layerManager = new TownyLayerManager(this, mapPlatform);

        // Register command
        TownyMapCommand commandExec = new TownyMapCommand(this);
        PluginCommand command = getCommand("maptowny");
        command.setExecutor(commandExec);
        command.setTabCompleter(commandExec);

        // Register listeners
        Bukkit.getPluginManager().registerEvents(new TownClaimListener(this), this);

        // If towny is in safe-mode, do not attempt to render towns.
        if (!Towny.getPlugin().isError()) {
            // Schedule render task when the layer manager is initialized.
            layerManager.onInitialize(() ->
                    new RenderTownsTask(this).runTaskTimer(this, 20, config.getUpdatePeriod() * 20L * 60)
            );
        }
    }

    @Override
    public void onDisable() {
        // Initiaize map platform shutdown
        if (mapPlatform != null)
            mapPlatform.startShutdown();

        // Plugin shutdown logic
        if (layerManager != null)
            layerManager.close();

        // Finalize map platform shutdown
        if (mapPlatform != null)
            mapPlatform.shutdown();
    }

    // Load the squaremap platform. squaremap is the only supported web-map plugin.
    @Nullable
    private MapPlatform loadPlatform() {
        if (!Bukkit.getPluginManager().isPluginEnabled("squaremap"))
            return null;

        return loadPlatformClass("squaremap.SquareMapPlatform");
    }

    // Load class using reflection because the platform module is compiled for a newer Java version
    // than the plugin itself.
    @Nullable
    private MapPlatform loadPlatformClass(@NotNull String abridgedPath) {
        String platformClassPrefix = "me.silverwolfg11.maptowny.platform.";
        String platformClassPath = platformClassPrefix + abridgedPath;
        try {
            Class<?> platformClass = Class.forName(platformClassPath);
            return (MapPlatform) platformClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException ex) {
            String msg = String.format("Error trying to load class '%s'", platformClassPath);
            getLogger().log(Level.SEVERE, msg, ex);
            return null;
        }
    }

    @NotNull
    public MapConfig config() {
        return config;
    }

    @Override
    @NotNull
    public TownyLayerManager getLayerManager() {
        return layerManager;
    }

    @Override
    @Nullable
    public MapPlatform getPlatform() {
        return mapPlatform;
    }

    public void reload() throws IOException {
        config = MapConfig.loadConfig(getDataFolder(), getLogger());
        Bukkit.getScheduler().cancelTasks(this);
        layerManager.close();
        layerManager = new TownyLayerManager(this, mapPlatform);
        Bukkit.getPluginManager().callEvent(new MapReloadEvent()); // API Event
        new RenderTownsTask(this).runTaskTimer(this, 0, config.getUpdatePeriod() * 20L * 60);
    }

    public void async(Runnable run) {
        Bukkit.getScheduler().runTaskAsynchronously(this, run);
    }
}
