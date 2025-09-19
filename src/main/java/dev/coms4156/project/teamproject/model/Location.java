package dev.coms4156.project.teamproject.model;

import java.io.Serializable;

/**
 * Represents a geographic location with latitude and longitude. Provides utility methods to
 * calculate distance using the Haversine formula.
 */
public class Location implements Serializable {

  private static final int EARTH_RADIUS_KM = 6378;
  public double latitude;
  public double longitude;

  /**
   * Constructs a new Location with the specified latitude and longitude.
   *
   * @param latitude  the latitude of the location
   * @param longitude the longitude of the location
   */
  public Location(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  /**
   * Applies the Haversine formula for calculating the distance between two points.
   *
   * @param val the difference in latitude or longitude in radians
   * @return the Haversine of the value
   */
  private static double haversine(double val) {
    return Math.pow(Math.sin(val / 2), 2);
  }

  /**
   * Calculates the distance between this location and another location using the Haversine
   * formula.
   *
   * @param other the other location
   * @return the distance in kilometers
   */
  public double distance(Location other) {
    double deltaLat = Math.toRadians(latitude - other.latitude);
    double deltaLong = Math.toRadians(longitude - other.longitude);

    double startLat = Math.toRadians(latitude);
    double endLat = Math.toRadians(other.latitude);

    double a = haversine(deltaLat) + Math.cos(startLat) * Math.cos(endLat) * haversine(deltaLong);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return EARTH_RADIUS_KM * c;
  }
}
