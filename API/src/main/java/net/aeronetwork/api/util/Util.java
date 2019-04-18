package net.aeronetwork.api.util;

import lombok.NoArgsConstructor;

import java.util.Arrays;

/**
 * General purpose utility class.
 */
@NoArgsConstructor
public class Util {

    /**
     * Joins a String array starting from the specified
     * index using spaces.
     *
     * @param index The index to start from.
     * @param array The array to join.
     * @return The joined array as a single String.
     */
    public static String join(int index, String[] array) {
        if(index >= array.length)
            throw new IndexOutOfBoundsException("Specified index is greater than array length");
        String[] newArray = new String[array.length - index];
        for(int i = index; i < array.length; i++) {
            newArray[i - index] = array[i];
        }
        return String.join(" ", newArray);
    }

    /**
     * Joins a String array starting from the specified
     * index using the specified delimiter.
     *
     * @param index The index to start from.
     * @param array The array to join.
     * @param delimiter The delimiter that separates each element.
     * @return The joined array as a single String.
     */
    public static String join(int index, String[] array, String delimiter) {
        if(index >= array.length)
            throw new IndexOutOfBoundsException("Specified index is greater than array length");
        String[] newArray = new String[array.length - index];
        for(int i = index; i < array.length; i++) {
            newArray[i - index] = array[i];
        }
        return String.join(delimiter, newArray);
    }

    /**
     * Finds the specified constant in an enum. Constants are checked
     * by name, and case is ignored.
     *
     * @param t The enum class.
     * @param name The constant to find.
     * @param <T> The type of enum.
     * @return The matched constant, or null if none was matched.
     */
    public static <T extends Enum> T matchEnum(Class<T> t, String name) {
        return Arrays.stream(t.getEnumConstants())
                .filter(t1 -> t1.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
