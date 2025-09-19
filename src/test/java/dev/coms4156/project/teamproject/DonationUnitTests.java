package dev.coms4156.project.teamproject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.coms4156.project.teamproject.model.AccountProfile;
import dev.coms4156.project.teamproject.model.ClientProfile;
import dev.coms4156.project.teamproject.model.Donation;
import dev.coms4156.project.teamproject.model.FoodListing;
import dev.coms4156.project.teamproject.model.FoodRequest;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Donation class.
 * Tests the getters and setters.
 */
public class DonationUnitTests {
  private FoodListing foodListing;
  private Donation donation;
  private LocalDateTime pickUpTime;

  /**
   * Set up for testing.
   * Create a client, account, and listing to create a donation.
   */
  @BeforeEach
  void setUp() {
    // Initialize dependencies
    ClientProfile client = new ClientProfile();
    AccountProfile account = new AccountProfile();
    foodListing = new FoodListing();
    FoodRequest foodRequest = new FoodRequest();
    pickUpTime = LocalDateTime.of(2024, 11, 22, 10, 0);

    // Initialize Donation object
    donation = new Donation(client, account, foodListing, foodRequest, 5, pickUpTime);
  }

  @Test
  void testConstructor() {
    assertEquals(foodListing.getListingId(), donation.getFoodListing().getListingId());
    assertEquals(foodListing.getQuantityListed(), donation.getFoodListing().getQuantityListed());
    assertEquals(foodListing.getFoodType(), donation.getFoodListing().getFoodType());
    assertEquals(5, donation.getQuantityPickedUp());
    assertEquals(pickUpTime, donation.getPickUpTime());
  }

  @Test
  void testGetDonationId() {
    // Use reflection to simulate setting the donationId (JPA-managed field)
    int expectedId = 123;
    setDonationIdUsingReflection(donation, expectedId);
    assertEquals(expectedId, donation.getDonationId());
  }

  @Test
  void testGetAndSetQuantityPickedUp() {
    // Test setter and getter for quantityPickedUp
    donation.setQuantityPickedUp(10);
    assertEquals(10, donation.getQuantityPickedUp());
  }

  @Test
  void testGetAndSetPickUpTime() {
    LocalDateTime newPickUpTime = LocalDateTime.of(2024, 12, 1, 15, 30);
    donation.setPickUpTime(newPickUpTime);
    assertEquals(newPickUpTime, donation.getPickUpTime());
  }

  @Test
  void testGetFoodListing() {
    assertEquals(foodListing.getListingId(), donation.getFoodListing().getListingId());
    assertEquals(foodListing.getQuantityListed(), donation.getFoodListing().getQuantityListed());
    assertEquals(foodListing.getFoodType(), donation.getFoodListing().getFoodType());
  }

  private void setDonationIdUsingReflection(Donation donation, int donationId) {
    try {
      java.lang.reflect.Field field = Donation.class.getDeclaredField("donationId");
      field.setAccessible(true);
      field.set(donation, donationId);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException("Failed to set donationId using reflection", e);
    }
  }
}
