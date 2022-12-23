package me.justahuman.spiritsunchained;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.updater.GitHubBuildsUpdater;
import lombok.Getter;
import me.justahuman.spiritsunchained.managers.CommandManager;
import me.justahuman.spiritsunchained.managers.ConfigManager;
import me.justahuman.spiritsunchained.managers.ListenerManager;
import me.justahuman.spiritsunchained.managers.RunnableManager;
import me.justahuman.spiritsunchained.managers.SpiritEntityManager;
import me.justahuman.spiritsunchained.managers.SpiritsManager;
import me.justahuman.spiritsunchained.slimefun.ItemStacks;
import me.justahuman.spiritsunchained.slimefun.Researches;
import me.justahuman.spiritsunchained.utils.LogUtils;
import net.guizhanss.guizhanlibplugin.updater.GuizhanBuildsUpdaterWrapper;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

public class SpiritsUnchained extends JavaPlugin implements SlimefunAddon {

    @Getter
    private static SpiritsUnchained instance;
    @Getter
    private static ListenerManager listenerManager;
    @Getter
    private static SpiritsManager spiritsManager;
    @Getter
    private static SpiritEntityManager spiritEntityManager;
    @Getter
    private static ConfigManager configManager;
    @Getter
    private static RunnableManager runnableManager;
    @Getter
    private static CommandManager commandManager;

    @Override
    public void onEnable() {
        instance = this;


        getLogger().info("========================================");
        getLogger().info("    SpiritsUnchained - By JustAHuman    ");
        getLogger().info("        灵魂巧匠 - 粘液科技简中汉化组        ");
        getLogger().info("========================================");

        saveDefaultConfig();

        configManager = new ConfigManager();
        runnableManager = new RunnableManager();
        listenerManager = new ListenerManager();
        spiritsManager = new SpiritsManager();
        spiritEntityManager = new SpiritEntityManager();
        commandManager = new CommandManager();

        Setup.INSTANCE.init();

        if (getConfig().getBoolean("options.auto-update") && getDescription().getVersion().startsWith("Build")) {
            GuizhanBuildsUpdaterWrapper.start(this, getFile(), "SlimefunGuguProject", "SpiritsUnchained", "master",
                false);
        }

        if (getConfig().getBoolean("options.enable-researches")) {
            Researches.init();
        }

        final Metrics metrics = new Metrics(this, 16817);

        this.getCommand("spirits").setExecutor(commandManager);
    }

    @Nonnull
    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/SlimefunGuguProject/SpiritsUnchained/issues";
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public static SlimefunItemStack getSlimefunItem(String id) {
        try {
            return (SlimefunItemStack) Slimefun.getRegistry().getSlimefunItemIds().get(id).getItem();
        } catch(NullPointerException | ClassCastException e) {
            e.printStackTrace();
            LogUtils.logInfo(id);
            return ItemStacks.SU_ECTOPLASM;
        }
    }

    @Override
    public void onDisable() {
        configManager.save();
        for (LivingEntity livingEntity : getSpiritEntityManager().getCustomLivingEntities()) {
            livingEntity.remove();
        }
        for (UUID uuid : getCommandManager().ghostBlocks) {
            final Entity fallingBlock = Bukkit.getEntity(uuid);
            if (fallingBlock != null) {
                fallingBlock.remove();
            }
        }
    }
}