package net.aeronetwork.api.util;

import com.google.common.collect.Maps;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.UUID;

public class UUIDUtil {

    private static HashMap<String, UUID> fetchedUUIDs = Maps.newHashMap();

    /**
     * Read UUID data.
     * 
     * @param toRead The data to read
     * @param result The StringBuilder to append the data onto
     */
    private static void readData(String toRead, StringBuilder result) {
        int i = 7;

        if (toRead.length() < 7) {
            result.append("00000000000000000000000000000000");
            return;
        }

        while (i < 200) {
            if (!String.valueOf(toRead.charAt(i)).equalsIgnoreCase("\"")) {

                result.append(String.valueOf(toRead.charAt(i)));

            } else {
                break;
            }

            i++;
        }
    }

    /**
     * Call the a URL to retrieve JSON data.
     * 
     * @param URL The URL to be scanned
     * @return The data retrieved
     */
    private static String callURL(String URL) {
        StringBuilder sb = new StringBuilder();
        URLConnection urlConn = null;
        InputStreamReader in = null;
        try {
            URL url = new URL(URL);
            urlConn = url.openConnection();

            if (urlConn != null)
                urlConn.setReadTimeout(60 * 1000);

            if (urlConn != null && urlConn.getInputStream() != null) {
                in = new InputStreamReader(urlConn.getInputStream(), Charset.defaultCharset());
                BufferedReader bufferedReader = new BufferedReader(in);

                if (bufferedReader != null) {
                    int cp;

                    while ((cp = bufferedReader.read()) != -1) {
                        sb.append((char) cp);
                    }

                    bufferedReader.close();
                }
            }

            in.close();
        } catch (Exception e) {

        }

        return sb.toString();
    }

    public static HashMap<String, UUID> getFetchedUUIDS() {
        return fetchedUUIDs;
    }

    /**
     * Get a player's UUID from Mojang, so we know it's up to date.
     * 
     * @param playername The player whose UUID is being retrieved
     * @return The UUID retrieved
     */
    public static UUID getUUID(String playername) {
        if(getFetchedUUIDS().containsKey(playername)) {
            return getFetchedUUIDS().get(playername);
        }
        
        String output = callURL("https://api.mojang.com/users/profiles/minecraft/" + playername);

        StringBuilder result = new StringBuilder();

        readData(output, result);

        String u = result.toString();

        if (u == " ") {
            return UUID.fromString(" ");
        }

        String uuid = "";

        for (int i = 0; i <= 31; i++) {
            uuid = uuid + u.charAt(i);
            if (i == 7 || i == 11 || i == 15 || i == 19) {
                uuid = uuid + "-";
            }
        }

        getFetchedUUIDS().put(playername, UUID.fromString(uuid));
        return UUID.fromString(uuid);
    }

}
