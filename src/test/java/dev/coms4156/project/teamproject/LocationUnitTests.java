package dev.coms4156.project.teamproject;

import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.coms4156.project.teamproject.model.Location;
import org.junit.jupiter.api.Test;

/**
 * This class contains unit tests for the Location class.
 */
public class LocationUnitTests {

  @Test
  public void distanceTest1() {
    Location newYorkCity = new Location(40.714268, -74.005974);
    Location losAngeles = new Location(34.0522, -118.2437);

    double expectedDistance = 3944;
    assertTrue(Math.abs(newYorkCity.distance(losAngeles) - expectedDistance) < 10);
  }
}
