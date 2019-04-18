package net.aeronetwork.api.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeConverter {

    private static Pattern NUMBER_PATTERN = Pattern.compile("(\\d+)");
    private static Pattern LETTER_PATTERN = Pattern.compile("[A-Za-z]");

    /**
     * Check if a string is a valid time string.
     *
     * @param string The string being checked.
     * @return Whether the specified string is valid.
     */
    public static boolean isTimeString(String string) {
        for(String split : string.split(" ")) {
            if(!split.matches("((\\d+)[A-Za-z])")) {
                return false;
            }
        }

        return true;
    }

    /**
     * Converts a string into a time value.
     *
     * @param combindedArgs The string being converted.
     * @return Converted time value.
     */
    public static long convert(String combindedArgs) {
        String[] args = combindedArgs.split(" ");
        long time = 0;

        for (String arg : args) {
            if(arg.matches("((\\d+)[A-Za-z])")) {
                Matcher numberMatcher = NUMBER_PATTERN.matcher(arg);
                numberMatcher.find();
                Matcher letterMatcher = LETTER_PATTERN.matcher(arg);
                letterMatcher.find();
                long tempTime = Long.valueOf(numberMatcher.group(0));
                TimeAbbreviation abbreviation = getAbbreviation(letterMatcher.group(0));
                time+= abbreviation.getTime(tempTime);
            }
        }

        return time;
    }

    /**
     * Get TimeAbbreviation from string.
     *
     * @param data The string being converted.
     * @return Matched TimeAbbreviation instance.
     */
    private static TimeAbbreviation getAbbreviation(String data) {
        for (TimeAbbreviation abbreviation : TimeAbbreviation.values()) {
            for (String prefix : abbreviation.getPrefix()) {
                if(data.equalsIgnoreCase(prefix)) {
                    return abbreviation;
                }
            }
        }

        return null;
    }

    public enum TimeAbbreviation {

        SECOND(1, "s", "second"),
        MINUTE(60, "m", "minute"),
        HOUR(3600, "h", "hour"),
        DAY(86400, "d", "day"),
        WEEK(604800, "w", "week"),
        MONTH(2592000, "mh", "month"),
        YEAR(31556952, "y", "year"),
        PERM(-1, "p");

        String[] prefix;
        long multiplier;

        TimeAbbreviation(long multiplier, String... prefix) {
            this.prefix = prefix;
            this.multiplier = multiplier;
        }

        public String[] getPrefix() {
            return prefix;
        }

        public long getTime(long time) {
            if(multiplier == -1) {
                return -1;
            }

            return (time * multiplier) * 1000;
        }

    }

}
