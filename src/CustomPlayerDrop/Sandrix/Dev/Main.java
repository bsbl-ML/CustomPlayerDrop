package CustomPlayerDrop.Sandrix.Dev;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import CustomPlayerDrop.Sandrix.Dev.command.ReloadCommand;
import CustomPlayerDrop.Sandrix.Dev.config.PluginConfig;
import CustomPlayerDrop.Sandrix.Dev.listener.PlayerDeathListener;
import CustomPlayerDrop.Sandrix.Dev.service.DropService;

public class Main extends JavaPlugin{
	private PluginConfig pluginConfig;
	
	@Override
	public void onEnable(){
		pluginConfig = new PluginConfig(this);
		DropService dropService = new DropService(pluginConfig);
		getServer().getPluginManager().registerEvents(new PlayerDeathListener(pluginConfig, dropService), this);
		if (getCommand("CustomPlayerDrop") != null) {
			getCommand("CustomPlayerDrop").setExecutor(new ReloadCommand(pluginConfig, dropService));
		}
		getLogger().info("CustomPlayerDrop has been enabled!");
		Config.CheckConfig();
		Config.loadConfig();
	}
	public PluginConfig getPluginConfig() {
		return pluginConfig;
	}
}
