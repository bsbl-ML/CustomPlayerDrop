package CustomPlayerDrop..config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginConfig {
  private final JavaPlugin plugin;
  private final File configFile;

  private FileConfiguration config;

  private boolean pluginEnabled;
  private String permissionMessage;
  private boolean playerKillOnly;
  private boolean dropPlayerInventory;
  private boolean dropPlayerHead;
  private String headName;
  private List<String> headLore;
  private int headDropRate;
  private boolean specificWorldOnly;
  private List<String> worlds;
  private boolean xpOnKill;
  private int xpGiveCount;
  private boolean moneyOnKill;
  private int moneyGiveCount;
  private boolean enableExtraDrop;
  private boolean enableExtraCommands;
  private String nonPlayerKiller;
  private List<String> commands;
  private List<ItemDrop> extraDrops;

  public PluginConfig(JavaPlugin plugin) {
    this.plugin = plugin;
    this.configFile = new File(plugin.getDataFolder(), "config.yml");
    load();
  }

  public void reload() {
    load();
  }

  private void load() {
      ensureConfigExists();
      config = YamlConfiguration.loadConfiguration(configFile);
      loadValues();
  }

  private void ensureConfigExists() {
      if (!plugin.getDataFolder().exists()) {
          if (!plugin.getDataFolder().mkdirs()) {
              plugin.getLogger().warning("Could not create plugin data folder.");
          }
      }
      File defaultConfig = new File(plugin.getDataFolder(), "DefaultConfig.yml");

      if (!defaultConfig.exists()) {
          plugin.saveResource("DefaultConfig.yml", false);
      }
      if (!configFile.exists()) {
          plugin.saveResource("config.yml", false);
      }
  }

  private void loadValues() {
      pluginEnabled = config.getBoolean("CustomPlayerDrop", true);
      permissionMessage = color(config.getString("permissionMessage", "&cYou are not allowed"));

      playerKillOnly = config.getBoolean("PlayerKillonly", false);
      nonPlayerKiller = config.getString("NonPlayerKiller", "Something");

      dropPlayerInventory = config.getBoolean("DropPlayerInventory", true);
      dropPlayerHead = config.getBoolean("DropPlayerHead", true);

      headName = config.getString("HeadName", "&e{player}'s head");

      headLore = new ArrayList<>(config.getStringList("HeadLore"));

      headDropRate = clampPercentage(config.getInt("HeadDropRate", 100));

      specificWorldOnly = config.getBoolean("SpecificWorldOnly", false);

      worlds = new ArrayList<>(config.getStringList("Worlds"));

      xpOnKill = config.getBoolean("XpOnKill", false);
      xpGiveCount = config.getInt("XpGiveCount", 100);

      moneyOnKill = config.getBoolean("MoneyOnKill", false);
      moneyGiveCount = config.getInt("MoneyGiveCount", 100);

      enableExtraCommands = config.getBoolean("EnableExtraCommands", false);

      commands = new ArrayList<>(config.getStringList("ExtraCommands"));

      enableExtraDrop = config.getBoolean("EnableExtraDrop", true);

      extraDrops = loadExtraDrops();
  }

  private List<ItemDrop> loadExtraDrops() {
      if (!enableExtraDrop) {
          return new ArrayList<>();
      }

      if (config.getConfigurationSection("ExtraDrop") == null) {
          return new ArrayList<>();
      }

      List<ItemDrop> drops = new ArrayList<>();

      for (String key : config.getConfigurationSection("ExtraDrop").getKeys(false)) {
          try {
              int rate = Integer.parseInt(key);
              ItemStack item = config.getItemStack("ExtraDrop." + key);
              if (item == null) {
                  plugin.getLogger().warning("Could not load ExtraDrop." + key + " because the item is invalid.");
                  continue;
              }

              drops.add(new ItemDrop(item, clampPercentage(rate)));

          } catch (NumberFormatException exception) {
              plugin.getLogger().warning("Invalid ExtraDrop rate: " + key);
          }
      }

      return drops;
  }

  private int clampPercentage(int value) {
    return Math.max(0, Math.min(100, value));
  }

  private String color(String text) {
    if (text == null) {
        return "";
    }

    return text.replace("&", "§");
  }

  private String colorAndFormat(String text) {
    return color(text);
  }

  public boolean isPluginEnabled() {
    return pluginEnabled;
  }

  public String getPermissionMessage() {
    return permissionMessage;
  }

  public boolean isPlayerKillOnly() {
    return playerKillOnly;
  }

  public boolean shouldDropPlayerInventory() {
    return dropPlayerInventory;
  }

  public boolean shouldDropPlayerHead() {
    return dropPlayerHead;
  }

  public String getHeadName() {
    return headName;
  }

  public List<String> getHeadLore() {
    return Collections.unmodifiableList(headLore);
  }

  public int getHeadDropRate() {
      return headDropRate;
  }

  public boolean isSpecificWorldOnly() {
    return specificWorldOnly;
  }

  public List<String> getWorlds() {
    return Collections.unmodifiableList(worlds);
  }

  public boolean isXpOnKill() {
    return xpOnKill;
  }

  public int getXpGiveCount() {
    return xpGiveCount;
  }

  public boolean isMoneyOnKill() {
    return moneyOnKill;
  }

  public int getMoneyGiveCount() {
    return moneyGiveCount;
  }

  public boolean isExtraDropEnabled() {
    return enableExtraDrop;
  }

  public boolean areExtraCommandsEnabled() {
    return enableExtraCommands;
  }

  public String getNonPlayerKiller() {
    return nonPlayerKiller;
  }

  public List<String> getCommands() {
    return Collections.unmodifiableList(commands);
  }

  public List<ItemDrop> getExtraDrops() {
    return Collections.unmodifiableList(extraDrops);
  }

  public void save() {
    try {
      config.save(configFile);
    } catch (IOException exception) {
      plugin.getLogger().severe("Could not save config.yml: " + exception.getMessage());
      }
  }

  public static class ItemDrop {
    private final ItemStack item;
    private final int rate;

    public ItemDrop(ItemStack item, int rate) {
      this.item = item;
      this.rate = rate;
    }

    public ItemStack getItem() {
      return item;
    }

    public int getRate() {
      return rate;
    }
  }
}
