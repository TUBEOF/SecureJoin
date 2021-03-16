package de.tubeof.securejoin.main;

import de.tubeof.securejoin.data.Cache;
import de.tubeof.securejoin.data.Data;
import de.tubeof.securejoin.files.Config;
import de.tubeof.securejoin.utils.IpFetcher;
import de.tubeof.securejoin.utils.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecureJoin extends JavaPlugin {

    private final ConsoleCommandSender ccs = Bukkit.getConsoleSender();

    private static final Cache cache = new Cache();
    private static final Data data = new Data();

    private static SecureJoin instance;
    private static Config config;

    @Override
    public void onEnable() {
        ccs.sendMessage(data.getPrefix() + "§aThe Plugin will be activated ...");

        ccs.sendMessage(data.getPrefix() + "==================================================");
        ccs.sendMessage(data.getPrefix() + "JOIN MY DISCORD NOW: §ehttps://discord.gg/73ZDfbx");
        ccs.sendMessage(data.getPrefix() + "==================================================");

        manageInstances();
        registerListener();
        registerCommands();
        manageFiles();
        startTasks();
        bStats();

        ccs.sendMessage(data.getPrefix() + "§aThe plugin was successfully activated!");
    }

    @Override
    public void onDisable() {
        ccs.sendMessage(data.getPrefix() + "§aThe Plugin will be deactivated ...");


        ccs.sendMessage(data.getPrefix() + "§aThe plugin was successfully deactivated!");
    }

    private void manageInstances() {
        ccs.sendMessage(data.getPrefix() + "§aLoading Config Files ...");

        //Instance
        instance = this;

        //Config
        config = new Config();

        ccs.sendMessage(data.getPrefix() + "§aConfig Files was successfully loaded!");
    }

    private void manageFiles() {
        ccs.sendMessage(data.getPrefix() + "§aLoading Config Files ...");

        config.cfgConfig();
        config.setCache();

        if(!cache.getIntegerValue("ConfigVersion").equals(data.getCurrentConfigVersion())) config.configUpdateMessage();

        ccs.sendMessage(data.getPrefix() + "§aConfig Files was successfully loaded!");
    }

    private void registerListener() {
        ccs.sendMessage(data.getPrefix() + "§aListeners will be registered ...");


        ccs.sendMessage(data.getPrefix() + "§aListeners have been successfully registered!");
    }

    private void registerCommands() {
        ccs.sendMessage(data.getPrefix() + "§aCommands will be registered ...");

        PluginManager pluginManager = Bukkit.getPluginManager();

        ccs.sendMessage(data.getPrefix() + "§aCommands have been successfully registered!");
    }

    private void startTasks() {
        ccs.sendMessage(data.getPrefix() + "§aStarting Tasks ...");


        ccs.sendMessage(data.getPrefix() + "§aTasks have been successfully started!");
    }

    private void bStats() {
        ccs.sendMessage(data.getPrefix() + "§aLoad and activate bStats ...");

        Metrics metrics = new Metrics(this, 10677);

        metrics.addCustomChart(new Metrics.SimplePie("server_provider", () -> {
            try {
                String ip = new IpFetcher().getIp();

                URL url = new URL("https://api.pool.tubeof.de/v1/ip/getIpInfo.php?ip=" + ip);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = bufferedReader.readLine()) != null) {
                    response.append(inputLine);
                }
                bufferedReader.close();

                JSONObject jsonObject = new JSONObject(response.toString());
                return jsonObject.getString("isp");
            } catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }
        }));

        metrics.addCustomChart(new Metrics.DrilldownPie("servers_using_update-checker", () -> {
            Map<String, Map<String, Integer>> map = new HashMap<>();
            Map<String, Integer> entry = new HashMap<>();

            Boolean updateChecker = cache.getBooleanValue("Updater.UseUpdateChecker");
            Boolean autoUpdate = cache.getBooleanValue("Updater.AutoUpdate.UseAutoUpdate");

            if(updateChecker && autoUpdate) entry.put("Auto-Updates: true", 1);
            else entry.put("Auto-Updates: false", 1);

            if(updateChecker) map.put("true", entry);
            else map.put("false", entry);

            return map;
        }));

        metrics.addCustomChart(new Metrics.SimplePie("verified_hoster", () -> {
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            List<String> jvmArgs = runtimeMXBean.getInputArguments();

            String host = null;
            for (String arg : jvmArgs) {
                if(arg.startsWith("-Dtubeof.verifyhost=")) {
                    String[] rawHost = arg.split("=");
                    host = rawHost[1];
                }
            }

            if(data.getVerifyedHosts().contains(host)) return "true";
            else return "false";
        }));

        ccs.sendMessage(data.getPrefix() + "§abStats was successfully loaded and activated!");
    }

    public static SecureJoin getInstance() {
        return instance;
    }

    public static Data getData() {
        return data;
    }

    public static Cache getCache() {
        return cache;
    }
}
