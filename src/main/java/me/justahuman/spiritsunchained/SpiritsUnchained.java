package me.justahuman.spiritsunchained;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import me.justahuman.spiritsunchained.commands.AllCommands;
import me.justahuman.spiritsunchained.utils.LogUtils;
import me.justahuman.spiritsunchained.managers.ConfigManager;
import me.justahuman.spiritsunchained.managers.ListenerManager;
import me.justahuman.spiritsunchained.managers.RunnableManager;
import me.justahuman.spiritsunchained.managers.SpiritEntityManager;
import me.justahuman.spiritsunchained.managers.SpiritsManager;
import me.justahuman.spiritsunchained.slimefun.ItemStacks;

import me.justahuman.spiritsunchained.utils.MiscUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class SpiritsUnchained extends JavaPlugin implements SlimefunAddon {

    private static SpiritsUnchained instance;
    private ListenerManager listenerManager;
    private SpiritsManager spiritsManager;
    private SpiritEntityManager spiritEntityManager;
    private ConfigManager configManager;
    private ProtocolManager protocolManager;
    private RunnableManager runnableManager;

    public static PluginManager getPluginManager() {
        return instance.getServer().getPluginManager();
    }

    @Override
    public void onEnable() {
        instance = this;


        getLogger().info("========================================");
        getLogger().info("    SpiritsUnchained - By JustAHuman    ");
        getLogger().info("========================================");

        this.configManager = new ConfigManager();
        this.runnableManager = new RunnableManager();
        this.listenerManager = new ListenerManager();
        this.spiritsManager = new SpiritsManager();
        this.spiritEntityManager = new SpiritEntityManager();
        this.protocolManager = ProtocolLibrary.getProtocolManager();

        Setup.INSTANCE.init();

        saveDefaultConfig();

        if (getConfig().getBoolean("options.auto-update")) {
            //GitHubBuildsUpdater updater = new GitHubBuildsUpdater(this, this.getFile(), "JustAHuman-xD/SlimySpirits/master");
            //updater.start(); Disabled for Now
        }

        this.getCommand("spirits").setExecutor(new AllCommands());
    }

    @Nonnull
    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/JustAHuman-xD/SpiritsUnchained/issues";
    }

    public static SpiritsUnchained getInstance() {
        return instance;
    }
    public static RunnableManager getRunnableManager() {
        return instance.runnableManager;
    }
    public static ListenerManager getListenerManager() {
        return instance.listenerManager;
    }
    public static SpiritsManager getSpiritsManager() {
        return instance.spiritsManager;
    }
    public static ConfigManager getConfigManager() {
        return instance.configManager;
    }
    public static SpiritEntityManager getSpiritEntityManager() {
        return instance.spiritEntityManager;
    }
    public static ProtocolManager getProtocolManager() {
        return instance.protocolManager;
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public static SlimefunItemStack getSlimefunItem(String id) {
        try {
            return (SlimefunItemStack) Slimefun.getRegistry().getSlimefunItemIds().get(id).getItem();
        } catch(NullPointerException | ClassCastException e) {
            e.printStackTrace();
            LogUtils.LogInfo(id);
            return ItemStacks.SU_ECTOPLASM;
        }
    }

    public void onDisable() {
        configManager.save();
    }
}