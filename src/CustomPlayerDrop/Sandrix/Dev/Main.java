package CustomPlayerDrop.Sandrix.Dev;

import org.bukkit.plugin.java.JavaPlugin;
import CustomPlayerDrop.Sandrix.Dev.ReloadCommand;
import CustomPlayerDrop.Sandrix.Dev.PluginConfig;

public final class SoulfulHeads extends JavaPlugin {
    private PluginConfig pluginConfig;

    @Override
    public void onEnable() {
        // Plugin startup logic
        pluginConfig = new PluginConfig(this);
        DropService dropService = new DropService(pluginConfig);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(pluginConfig, dropService), this);
        if(getCommand("CustomPlayerDrop") != null) {
            getCommand("CustomPlayerDrop").setExecutor(new ReloadCommand(pluginConfig, dropService));
        }
        getLogger().info("CustomPlayerDrop has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
