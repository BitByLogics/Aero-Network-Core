package net.aeronetwork.core.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LocationUtil {

	/**
	 * 
	 * Used to center locations, makes it easier.
	 * 
	 * @param loc The location to be centered.
	 * @return The location, but centered.
	 */
	public static Location centerLocation(Location loc) {
		return loc.add(0.5, 0, 0.5);
	}

	/**
	 * 
	 * Used to convert a location to string.
	 * 
	 * @param loc The location to be converted.
	 * @return A stringed location.
	 */
	public static String locationToString(Location loc) {
		DecimalFormat df = new DecimalFormat("0");
		String sloc = loc.getWorld().getName() + ":" + df.format(loc.getX()) + ":" + df.format(loc.getY()) + ":"
				+ df.format(loc.getZ());
		return sloc;
	}

	/**
	 * 
	 * Used to convert a location to string. (includes Yaw/Pitch)
	 * 
	 * @param loc The location to be converted.
	 * @return A stringed location with Yaw/Pitch.
	 */
	public static String locationToStringYP(Location loc) {
		DecimalFormat df = new DecimalFormat("0");
		String sloc = loc.getWorld().getName() + ":" + df.format(loc.getX()) + ":" + df.format(loc.getY()) + ":"
				+ df.format(loc.getZ()) + ":" + df.format(loc.getYaw()) + ":" + df.format(loc.getPitch());
		return sloc;
	}

	/**
	 * 
	 * Used to get a location from a stringed location.
	 * 
	 * @param s The string to be converted.
	 * @param center Check to see if the returned location should be centered.
	 * @return The converted location.
	 */
	public static Location stringToLocation(String s, boolean center) {
		String[] sloc = s.split(":");
		if (sloc.length == 4) {
			if (center) {
				return centerLocation(new Location(Bukkit.getWorld(sloc[0]), Integer.valueOf(sloc[1]),
						Integer.valueOf(sloc[2]), Integer.valueOf(sloc[3])));
			} else {
				return new Location(Bukkit.getWorld(sloc[0]), Integer.valueOf(sloc[1]), Integer.valueOf(sloc[2]),
						Integer.valueOf(sloc[3]));
			}
		} else if (sloc.length == 6) {
			if (center) {
				return centerLocation(
						new Location(Bukkit.getWorld(sloc[0]), Integer.valueOf(sloc[1]), Integer.valueOf(sloc[2]),
								Integer.valueOf(sloc[3]), Float.valueOf(sloc[4]), Float.valueOf(sloc[5])));
			} else {
				return new Location(Bukkit.getWorld(sloc[0]), Integer.valueOf(sloc[1]), Integer.valueOf(sloc[2]),
						Integer.valueOf(sloc[3]), Float.valueOf(sloc[4]), Float.valueOf(sloc[5]));
			}
		} else {
			return null;
		}
	}

	/**
	 * Convert a list to strings to a list of locations.
	 *
	 * @param strings List of strings to be converted.
	 * @return List of converted locations.
	 */
	public static List<Location> stringsToLocations(List<String> strings) {
		List<Location> locs = new ArrayList<>();
		for (String s : strings) {
			locs.add(stringToLocation(s, true));
		}
		return locs;
	}

}