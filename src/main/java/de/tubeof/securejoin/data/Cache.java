package de.tubeof.securejoin.data;

import de.tubeof.securejoin.main.SecureJoin;
import de.tubeof.tubetils.api.cache.CacheContainer;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

public class Cache {

    private final ConsoleCommandSender ccs = Bukkit.getConsoleSender();
    private final Data data = SecureJoin.getData();

    private CacheContainer cacheContainer;

    public Cache() {
        setup();
    }

    private void setup() {
        cacheContainer = new CacheContainer("SecureLoginCacheContainer");
        cacheContainer.registerCacheType(String.class);
        cacheContainer.registerCacheType(Integer.class);
        cacheContainer.registerCacheType(Boolean.class);
    }

    public void addStringValue(String valueName, String value) {
        cacheContainer.add(String.class, valueName, value);
    }

    public void addIntegerValue(String valueName, Integer value) {
        cacheContainer.add(Integer.class, valueName, value);
    }

    public void addBooleanValue(String valueName, Boolean value) {
        cacheContainer.add(Boolean.class, valueName, value);
    }

    public String getStringValue(String valueName) {
        return (String) cacheContainer.get(String.class, valueName);
    }

    public Integer getIntegerValue(String valueName) {
        return (Integer) cacheContainer.get(Integer.class, valueName);
    }

    public Boolean getBooleanValue(String valueName) {
        return (Boolean) cacheContainer.get(Boolean.class, valueName);
    }

}
