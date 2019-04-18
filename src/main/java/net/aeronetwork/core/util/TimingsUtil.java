package net.aeronetwork.core.util;

import java.text.DecimalFormat;
import java.util.HashMap;

public class TimingsUtil {

    private static HashMap<String, Long> timingsMap = new HashMap<>();
    private static DecimalFormat decimalFormat = new DecimalFormat("0.000");

    public static void start(String id) {
        if(timingsMap.containsKey(id)) {
            end(id);
            return;
        }

        timingsMap.put(id, System.currentTimeMillis());
        System.out.println("[Aero Core] Timings: Starting tracking timing for '" + id + "'");
    }

    public static void end(String id) {
        if(timingsMap.containsKey(id)) {
            long time = System.currentTimeMillis() - timingsMap.get(id);
            System.out.println("[Aero Core] Timings: Finished timing '" + id + "' completed in " + decimalFormat.format(time / 1000.0) + " seconds");
            timingsMap.remove(id);
        }
    }

}
