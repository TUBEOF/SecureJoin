package de.tubeof.securejoin.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class IpFetcher {

    public IpFetcher() {}

    /**
     * API-Call to get Remote-IP
     * @return The IP-Address from the API-Response
     */
    public String getIp() {
        try {
            URL url = new URL("https://api.pool.tubeof.de/v1/ip/getRemoteIp.php");
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
            return jsonObject.getString("remoteIp");
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
