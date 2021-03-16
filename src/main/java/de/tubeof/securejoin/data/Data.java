package de.tubeof.securejoin.data;

import de.tubeof.securejoin.main.SecureJoin;

import java.util.ArrayList;
import java.util.Arrays;

public class Data {

    private final Cache cache = SecureJoin.getCache();

    public Data() {}

    private final ArrayList<String> verifyedHosts = new ArrayList<>(Arrays.asList("ZAP", "Nitrado", "Aternos", "Pingperfect"));

    public String getPrefix() {
        return cache.getStringValue("prefix");
    }

    public Integer getCurrentConfigVersion() {
        return 1;
    }

    public ArrayList<String> getVerifyedHosts() {
        return verifyedHosts;
    }
}
