package de.tubeof.securejoin.main;

import de.tubeof.securejoin.data.Cache;
import de.tubeof.securejoin.data.Data;
import de.tubeof.securejoin.data.MySQL;
import de.tubeof.securejoin.files.Config;
import de.tubeof.securejoin.listener.CodeVerifier;
import de.tubeof.securejoin.listener.Join;
import de.tubeof.securejoin.utils.GoogleAuthenticatorManager;
import de.tubeof.securejoin.utils.IpFetcher;
import de.tubeof.securejoin.utils.Metrics;
import de.tubeof.tubetils.main.TubeTils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class SecureJoin extends JavaPlugin {

    private final String prefixTubeTilsChecker = "§7[§eTubeTilsChecker§7] §f";
    private final ConsoleCommandSender ccs = Bukkit.getConsoleSender();
    private final PluginManager pluginManager = Bukkit.getPluginManager();

    private static SecureJoin instance;
    private static MySQL mySQL;
    private static Cache cache;
    private static Data data;
    private static Config config;
    private static GoogleAuthenticatorManager googleAuthenticatorManager;

    @Override
    public void onEnable() {
        instance = this;

        checkTubeTils();
        TubeTils.Properties.setDebuggingStatus(false);

        cache = new Cache();
        data = new Data();

        config = new Config();
        manageFiles();

        mySQL = new MySQL(true);
        googleAuthenticatorManager = new GoogleAuthenticatorManager();

        ccs.sendMessage(data.getLoggerPrefix() + "§7==================================================");
        ccs.sendMessage(data.getLoggerPrefix() + "§aJOIN MY DISCORD NOW: §ehttps://discord.gg/73ZDfbx");
        ccs.sendMessage(data.getLoggerPrefix() + "§7==================================================");

        registerListener();
        registerCommands();
        startTasks();
        bStats();

        ccs.sendMessage(data.getLoggerPrefix() + "§aThe plugin was successfully activated!");
    }

    @Override
    public void onDisable() {
        ccs.sendMessage(data.getLoggerPrefix() + "§aThe Plugin will be deactivated ...");


        ccs.sendMessage(data.getLoggerPrefix() + "§aThe plugin was successfully deactivated!");
    }

    private void checkTubeTils() {
        Plugin tubetils = pluginManager.getPlugin("TubeTils");
        if(tubetils == null) {
            ccs.sendMessage(prefixTubeTilsChecker + "§cTubeTils are not installed! Downloading ...");
            getTubeTils();
        } else {
            ccs.sendMessage(prefixTubeTilsChecker + "§aTubeTils §ev" + tubetils.getDescription().getVersion() + " §ais installed!");
            return;
        }
    }

    private float downloadProgress = 0;
    private void getTubeTils() {
        try {
            URL url = new URL("https://hub.tubeof.de/repo/de/tubeof/TubeTils/SNAPSHOT-28/TubeTils-SNAPSHOT-28.jar");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "TubeApiBridgeConnector");
            connection.setRequestProperty("Header-Token", "SD998FS0FG07");
            int filesize = connection.getContentLength();

            Timer timer = new Timer();
            Thread thread = new Thread(() -> {
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        ccs.sendMessage(prefixTubeTilsChecker + "§cDownload-Progress: §e" + (int)downloadProgress + "%");
                    }
                };
                timer.schedule(timerTask, 0, 250);
            });
            thread.start();

            float totalDataRead = 0;
            BufferedInputStream in = new java.io.BufferedInputStream(connection.getInputStream());
            FileOutputStream fos = new java.io.FileOutputStream("plugins/TubeTils.jar");
            BufferedOutputStream bout = new BufferedOutputStream(fos,1024);
            byte[] byteData = new byte[1024];
            int i = 0;

            while((i=in.read(byteData,0,1024))>=0) {
                totalDataRead=totalDataRead+i;
                bout.write(byteData,0,i);
                downloadProgress = (totalDataRead*100) / filesize;
            }
            timer.cancel();
            thread.interrupt();
            ccs.sendMessage(prefixTubeTilsChecker + "§cDownload-Progress: §e" + (int)downloadProgress + "%");

            bout.close();
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        File file = new File("plugins/TubeTils.jar");
        try {
            Plugin plugin = pluginManager.loadPlugin(file);
            pluginManager.enablePlugin(plugin);
        } catch (Exception e) {
            e.printStackTrace();

            ccs.sendMessage(prefixTubeTilsChecker + "§cError while enabling TubeTils! Stopping Plugin ...");
            pluginManager.disablePlugin(this);
        }
    }

    private void manageFiles() {
        ccs.sendMessage(data.getLoggerPrefix() + "§aLoading Config Files ...");

        config.cfgConfig();
        config.setCache();

        if(!cache.getIntegerValue("ConfigVersion").equals(data.getCurrentConfigVersion())) config.configUpdateMessage();

        ccs.sendMessage(data.getLoggerPrefix() + "§aConfig Files was successfully loaded!");
    }

    private void registerListener() {
        ccs.sendMessage(data.getLoggerPrefix() + "§aListeners will be registered ...");

        pluginManager.registerEvents(new Join(), this);
        pluginManager.registerEvents(new CodeVerifier(), this);

        ccs.sendMessage(data.getLoggerPrefix() + "§aListeners have been successfully registered!");
    }

    private void registerCommands() {
        ccs.sendMessage(data.getLoggerPrefix() + "§aCommands will be registered ...");



        ccs.sendMessage(data.getLoggerPrefix() + "§aCommands have been successfully registered!");
    }

    private void startTasks() {
        ccs.sendMessage(data.getLoggerPrefix() + "§aStarting Tasks ...");


        ccs.sendMessage(data.getLoggerPrefix() + "§aTasks have been successfully started!");
    }

    private void bStats() {
        ccs.sendMessage(data.getLoggerPrefix() + "§aLoad and activate bStats ...");

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

        ccs.sendMessage(data.getLoggerPrefix() + "§abStats was successfully loaded and activated!");
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

    public static MySQL getMySQL() {
        return mySQL;
    }

    public static GoogleAuthenticatorManager getGoogleAuthenticatorManager() {
        return googleAuthenticatorManager;
    }
}
