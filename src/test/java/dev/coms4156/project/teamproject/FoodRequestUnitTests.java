package dev.coms4156.project.teamproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.coms4156.project.teamproject.model.AccountProfile;
import dev.coms4156.project.teamproject.model.ClientProfile;
import dev.coms4156.project.teamproject.model.FoodListing;
import dev.coms4156.project.teamproject.model.FoodRequest;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FoodRequestUnitTests {

  private ClientProfile client;
  private AccountProfile account;
  private FoodListing foodListing;
  private FoodRequest foodRequest;

  @BeforeEach
  void setUp() {
    client = new ClientProfile();
    account = new AccountProfile(client, AccountProfile.AccountType.PROVIDER,
        "1234567890", "t");
    LocalDateTime pickupTime = LocalDateTime.of(2024, 11, 22, 10, 0);

    foodListing = new FoodListing(client, account, "chickpeas", 11,
        pickupTime, 124.1f, 9.1f);

    foodRequest = new FoodRequest(client, account, foodListing, 5);
  }


  @Test
  void testParameterizedConstructor() {
    assertEquals(client, foodRequest.getClient());
    assertEquals(account, foodRequest.getAccountId());
    assertEquals(foodListing, foodRequest.getListing());
    assertEquals(5, foodRequest.getQuantityRequested());
    assertNotNull(foodRequest.getRequestTime());
  }

  @Test
  void testGettersAndSetters() {
    foodRequest.setQuantityRequested(10);
    assertEquals(10, foodRequest.getQuantityRequested());
  }

  @Test
  void testEqualsAndHashCode() {
    FoodRequest anotherRequest = new FoodRequest(client, account, foodListing, 5);
    anotherRequest.setQuantityRequested(foodRequest.getQuantityRequested());
    assertNotEquals(foodRequest, anotherRequest);

    FoodRequest anotherRequest2 = new FoodRequest(client, account, foodListing, 15);
    assertNotEquals(foodRequest, anotherRequest2);
    LocalDateTime pickupTime = LocalDateTime.of(2024, 11, 22, 10, 0);
    FoodListing newListing = new FoodListing(client, account, "burger", 11,
        pickupTime, 124.1f, 9.1f);
    FoodRequest anotherRequest3 = new FoodRequest(client, account, newListing, 5);
    assertNotEquals(foodRequest, anotherRequest3);
    assertNotEquals(foodRequest.hashCode(), anotherRequest.hashCode());
  }

  @Test
  void testGetRequestTime() {
    LocalDateTime before = LocalDateTime.now();
    FoodRequest timeRequest = new FoodRequest(client, account, foodListing, 5);
    LocalDateTime after = LocalDateTime.now();

    assertTrue(timeRequest.getRequestTime().isAfter(before)
        || timeRequest.getRequestTime().isEqual(before));
    assertTrue(timeRequest.getRequestTime().isBefore(after)
        || timeRequest.getRequestTime().isEqual(after));
  }
}