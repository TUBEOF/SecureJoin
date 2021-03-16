package de.tubeof.securejoin.data;

import de.tubeof.securejoin.main.SecureJoin;

import java.util.ArrayList;
import java.util.Arrays;

public class Data {

    private final Cache cache = SecureJoin.getCache();

    public Data() {}

    private final String prefixLogger = "§7[§2SecureJoinLogger§7] §f";
    private final ArrayList<String> verifyedHosts = new ArrayList<>(Arrays.asList("ZAP", "Nitrado", "Aternos", "Pingperfect"));
    private String prefix = "§7[§2SecureJoin§7] §f";

    public String getLoggerPrefix() {
        return prefixLogger;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Integer getCurrentConfigVersion() {
        return 1;
    }

    public ArrayList<String> getVerifyedHosts() {
        return verifyedHosts;
    }
}
