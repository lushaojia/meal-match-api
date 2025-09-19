package dev.coms4156.project.teamproject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.coms4156.project.teamproject.model.AccountProfile;
import dev.coms4156.project.teamproject.model.ClientProfile;
import dev.coms4156.project.teamproject.model.FoodListing;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Unit tests for the FoodListing class.
 * Tests the getters and setters, as well
 * as methods for determining equality of two FoodListing objects.
 */
@SpringBootTest
@ContextConfiguration
public class FoodListingUnitTests {
  private ClientProfile client;
  private AccountProfile account;
  private FoodListing foodListing;
  private LocalDateTime earliestPickUpTime;

  @BeforeEach
  void setUp() {
    client = new ClientProfile();
    account = new AccountProfile(client, AccountProfile.AccountType.PROVIDER,
        "1234567890", "t");
    earliestPickUpTime = LocalDateTime.of(2024, 11, 22, 10, 0);
    foodListing = new FoodListing(client, account, "Fruits", 10,
        earliestPickUpTime, 34.0522f, -118.2437f);
  }

  @Test
  void testParameterizedConstructor() {
    assertEquals("Fruits", foodListing.getFoodType());
    assertEquals(10, foodListing.getQuantityListed());
    assertEquals(earliestPickUpTime, foodListing.getEarliestPickUpTime());
    assertEquals(34.0522f, foodListing.getLatitude());
    assertEquals(-118.2437f, foodListing.getLongitude());
  }

  @Test
  void testGettersAndSetters() {
    foodListing.setFoodType("Vegetables");
    assertEquals("Vegetables", foodListing.getFoodType());

    foodListing.setQuantityListed(20);
    assertEquals(20, foodListing.getQuantityListed());

    LocalDateTime newPickUpTime = LocalDateTime.of(2024, 12, 1, 15, 30);
    foodListing.setEarliestPickUpTime(newPickUpTime);
    assertEquals(newPickUpTime, foodListing.getEarliestPickUpTime());

    foodListing.setLatitude(40.7128f);
    assertEquals(40.7128f, foodListing.getLatitude());

    foodListing.setLongitude(-74.0060f);
    assertEquals(-74.0060f, foodListing.getLongitude());
  }

  @Test
  void testEqualsAndHashCode() {
    FoodListing anotherListing = new FoodListing(client, account, "Fruits",
        10, earliestPickUpTime, 34.0522f, -118.2437f);
    assert foodListing.equals(anotherListing);
    FoodListing anotherListing2 = new FoodListing(client, account, "Jack fruits",
        10, earliestPickUpTime, 34.0522f, -118.2437f);
    assert !foodListing.equals(anotherListing2);
    FoodListing anotherListing3 = new FoodListing(client, account, "Fruits",
        11, earliestPickUpTime, 34.0522f, -118.2437f);
    assert !foodListing.equals(anotherListing3);
    FoodListing anotherListing4 = new FoodListing(client, account, "Fruits",
        10, LocalDateTime.now(), 34.0522f, -118.2437f);
    assert !foodListing.equals(anotherListing4);
    FoodListing anotherListing5 = new FoodListing(client, account, "Fruits",
        10, earliestPickUpTime, 34.02f, -118.2437f);
    assert !foodListing.equals(anotherListing5);
    FoodListing anotherListing6 = new FoodListing(client, account, "Fruits",
        10, earliestPickUpTime, 34.0522f, 118.2437f);
    assert !foodListing.equals(anotherListing6);
    assertEquals(foodListing.hashCode(), anotherListing.hashCode());
  }
}
