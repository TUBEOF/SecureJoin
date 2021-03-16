package de.tubeof.securejoin.files;

import de.tubeof.securejoin.data.Cache;
import de.tubeof.securejoin.data.Data;
import de.tubeof.securejoin.main.SecureJoin;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Config {

    private static ConsoleCommandSender ccs = Bukkit.getConsoleSender();
    private final Cache cache = SecureJoin.getCache();
    private final Data data = SecureJoin.getData();

    public Config() {}

    private final File file = new File("plugins/SecureLogin", "Config.yml");
    private final FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

    public void configUpdateMessage() {
        ccs.sendMessage(data.getLoggerPrefix() + "§e######################################################################");
        ccs.sendMessage(data.getLoggerPrefix() + "§cA new config is included in the update!");
        ccs.sendMessage(data.getLoggerPrefix() + "§cPlease delete the old config so that the changes will be applied.");
        ccs.sendMessage(data.getLoggerPrefix() + "§e######################################################################");
    }

    private void saveCFG() {
        try {
            cfg.save(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void cfgConfig() {
        cfg.options().copyDefaults(true);

        cfg.addDefault("General.Prefix", "§7[§2SecureJoin§7] §f");
        cfg.addDefault("General.NoPerms", "§cNo permissions!");

        cfg.addDefault("DataPool.MySQL.Host", "127.0.0.1");
        cfg.addDefault("DataPool.MySQL.Port", "3306");
        cfg.addDefault("DataPool.MySQL.Prefix", "sj01_");
        cfg.addDefault("DataPool.MySQL.Database", "databasename");
        cfg.addDefault("DataPool.MySQL.Username", "databaseusername");
        cfg.addDefault("DataPool.MySQL.Password", "databasepassword");

        cfg.addDefault("Updater.UseUpdateChecker", true);
        cfg.addDefault("Updater.AutoUpdate.UseAutoUpdate", false);
        cfg.addDefault("Updater.AutoUpdate.LicenseKey", "YourKey");
        cfg.addDefault("Updater.IngameNotifyForUpdate", true);

        cfg.addDefault("ConfigVersion", data.getCurrentConfigVersion());

        saveCFG();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception localExeption) {
                ccs.sendMessage(cache.getStringValue("prefix") + "§cConfig.yml could not be created!");
            }
        }
    }

    public void setCache() {
        ccs.sendMessage(data.getLoggerPrefix() + "§aConfig values are loaded into the cache ...");

        cache.addStringValue("General.Prefix", cfg.getString("General.Prefix"));
        data.setPrefix(cache.getStringValue("General.Prefix"));

        cache.addStringValue("General.NoPerms", cfg.getString("General.NoPerms"));

        cache.addStringValue("DataPool.MySQL.Host", cfg.getString("DataPool.MySQL.Host"));
        cache.addStringValue("DataPool.MySQL.Port", cfg.getString("DataPool.MySQL.Port"));
        cache.addStringValue("DataPool.MySQL.Prefix", cfg.getString("DataPool.MySQL.Prefix"));
        cache.addStringValue("DataPool.MySQL.Database", cfg.getString("DataPool.MySQL.Database"));
        cache.addStringValue("DataPool.MySQL.Username", cfg.getString("DataPool.MySQL.Username"));
        cache.addStringValue("DataPool.MySQL.Password", cfg.getString("DataPool.MySQL.Password"));

        cache.addBooleanValue("Updater.UseUpdateChecker", cfg.getBoolean("Updater.UseUpdateChecker"));
        cache.addBooleanValue("Updater.AutoUpdate.UseAutoUpdate", cfg.getBoolean("Updater.AutoUpdate.UseAutoUpdate"));
        cache.addStringValue("Updater.AutoUpdate.LicenseKey", cfg.getString("Updater.AutoUpdate.LicenseKey"));
        cache.addBooleanValue("Updater.IngameNotifyForUpdate", cfg.getBoolean("Updater.IngameNotifyForUpdate"));

        cache.addIntegerValue("ConfigVersion", cfg.getInt("ConfigVersion"));

        ccs.sendMessage(data.getLoggerPrefix() + "§aConfig values were successfully cached!");
    }
}
