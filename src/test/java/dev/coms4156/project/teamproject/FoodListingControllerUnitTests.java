package dev.coms4156.project.teamproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import dev.coms4156.project.teamproject.controller.FoodListingController;
import dev.coms4156.project.teamproject.model.AccountProfile;
import dev.coms4156.project.teamproject.model.ClientProfile;
import dev.coms4156.project.teamproject.model.FoodListing;
import dev.coms4156.project.teamproject.repository.AccountProfileRepository;
import dev.coms4156.project.teamproject.repository.ClientProfileRepository;
import dev.coms4156.project.teamproject.repository.FoodListingRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

/**
 * Unit tests for the FoodListingController class.
 *
 * <p>These tests validate the behavior of FoodListing-related API operations
 * such as creating and retrieving food listings.
 */
@SpringBootTest
@ActiveProfiles("test")
public class FoodListingControllerUnitTests {

  @MockBean
  private FoodListingRepository foodListingRepository;

  @Autowired
  private FoodListingController foodListingController;

  @MockBean
  private ClientProfileRepository clientProfileRepository;

  @MockBean
  private AccountProfileRepository accountProfileRepository;

  private ClientProfile client;
  private AccountProfile providerAccount;
  private final int providerId = 0;
  private AccountProfile recipientAccount;
  private final int recipientId = 1;
  private FoodListing listing1;
  private final int listing1Id = 0;
  private FoodListing listing2;
  private final int listing2Id = 1;
  private FoodListing listing3;

  /**
   * Set up for controller tests. Mocks normal behavior of repositories
   * to isolate the testing of internal implementation from external library.
   */
  @BeforeEach
  public void setup() {
    // Make sure repository is in a clean state
    foodListingRepository.deleteAll();

    client = new ClientProfile();
    providerAccount = new AccountProfile(
        client, AccountProfile.AccountType.PROVIDER,
        "8897263717", "sweetgreen"
    );
    recipientAccount = new AccountProfile(
        client, AccountProfile.AccountType.RECIPIENT,
        "1234567890", "r");
    listing1 = new FoodListing(client, providerAccount, "snack",
        25, LocalDateTime.of(2024, 10, 6, 11, 0),
        34.052f, -118.243f);
    listing2 = new FoodListing(client, providerAccount, "beverage",
        30, LocalDateTime.of(2024, 10, 7, 16, 30),
        78.122f, 120.281f);
    listing3 = new FoodListing(client, providerAccount, "rice",
        140, LocalDateTime.of(2024, 10, 8, 14, 00),
        33.989f, -118.243f);

    when(clientProfileRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
    when(accountProfileRepository.findById(any())).thenAnswer(invocation -> {
      int id = invocation.getArgument(0); // Retrieve the argument (ID) passed to findById
      if (id == 0) {
        return Optional.of(providerAccount);
      } else if (id == 1) {
        return Optional.of(recipientAccount);
      } else {
        return Optional.empty();
      }
    });

    when(foodListingRepository.save(any())).thenAnswer(invocation -> {
      FoodListing listing = invocation.getArgument(0, FoodListing.class);

      // Determine the listing to return based on the listingId
      if (listing.getListingId() == 0) {
        return listing1;
      } else if (listing.getListingId() == 1) {
        return listing2;
      } else if (listing.getListingId() == 2) {
        return listing3;
      } else {
        return null;
      }
    });
    when(foodListingRepository.findById(any())).thenAnswer(invocation -> {
      int id = invocation.getArgument(0); // Retrieve the argument (ID) passed to findById
      if (id == 0) {
        return Optional.of(listing1);
      } else if (id == 1) {
        return Optional.of(listing2);
      } else if (id == 2) {
        return Optional.of(listing3);
      } else {
        return Optional.empty();
      }
    });
  }

  @Test
  public void createFoodListingOkTest() {
    ResponseEntity<?> response = foodListingController.createFoodListing(
        client.getClientId(), providerId, "snack",
        25, 34.052f, -118.243f);

    // Check status code
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    // Check response body
    assert (((String) Objects.requireNonNull(response.getBody()))
        .contains("Food listing created successfully with ID: "));
  }

  @Test
  public void createFoodListingMissingClientTest() {
    ResponseEntity<?> response = foodListingController.createFoodListing(
        111, 222,
        "kiwi", 10,
        34.052f, -118.244f);

    // Check status code
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void createFoodListingMissingAccountTest() {

    ResponseEntity<?> response = foodListingController.createFoodListing(
        client.getClientId(), 222,
        "kiwi", 10,
        34.052f, -118.244f);

    // Check status code
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void getFoodListingNoneFoundTest() {
    // Mock repository to return empty list
    when(foodListingRepository.findByClient(client)).thenReturn(List.of());
    ResponseEntity<?> response = foodListingController.getFoodListings(client.getClientId());
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void getFoodListingsOneClientTest() {
    List<FoodListing> mockedReturn = new ArrayList<>();
    mockedReturn.add(listing1);
    mockedReturn.add(listing2);
    when(foodListingRepository.findByClient(client)).thenReturn(mockedReturn);

    ResponseEntity<?> response = foodListingController.getFoodListings(client.getClientId());
    FoodListing expected1 = listing1;
    FoodListing expected2 = listing2;
    Set<FoodListing> expected = Set.of(expected1, expected2);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    @SuppressWarnings("unchecked") // suppress warning for unchecked cast...
    List<FoodListing> foodListings = (List<FoodListing>) response.getBody();
    assert (Objects.requireNonNull(foodListings).size() == 2);

    for (FoodListing listing : foodListings) {
      assert (expected.contains(listing));
    }
  }

  @Test
  public void getFoodListingMultipleClientsTest() {

    // Let listings 1 and 2 be under the same client
    List<FoodListing> mockedReturn1 = new ArrayList<>();
    mockedReturn1.add(listing1);
    mockedReturn1.add(listing2);
    when(foodListingRepository.findByClient(client)).thenReturn(mockedReturn1);
    ResponseEntity<?> response = foodListingController.getFoodListings(client.getClientId());
    Set<FoodListing> expectedSet = Set.of(listing1, listing2);

    assertEquals(HttpStatus.OK, response.getStatusCode()); // Check status code

    // Check body
    @SuppressWarnings("unchecked") // suppress warning for unchecked cast...
    List<FoodListing> foodListings = (List<FoodListing>) response.getBody();
    assert (Objects.requireNonNull(foodListings).size() == 2);
    for (FoodListing listing : foodListings) {
      assert (expectedSet.contains(listing));
    }

    // Let listing 3 be under a new client
    ClientProfile client2 = new ClientProfile();
    List<FoodListing> mockedReturn2 = new ArrayList<>();
    mockedReturn2.add(listing3);
    when(clientProfileRepository.findById(client2.getClientId())).thenReturn(Optional.of(client2));
    when(foodListingRepository.findByClient(client2)).thenReturn(mockedReturn2);
    ResponseEntity<?> response2 = foodListingController.getFoodListings(client2.getClientId());
    assertEquals(HttpStatus.OK, response2.getStatusCode()); // Check status code

    // Check body
    @SuppressWarnings("unchecked") // suppress warning for unchecked cast...
    List<FoodListing> foodListings2 = (List<FoodListing>) response2.getBody();
    assert (Objects.requireNonNull(foodListings2).size() == 1);
    assert (foodListings2.get(0).equals(listing3));
  }

  @Test
  public void getFoodListingsMissingClientTest() {

    when(clientProfileRepository.findById(any())).thenReturn(Optional.ofNullable(client));

    // No client has this ID
    int badClientId = client.getClientId() + 2;

    ResponseEntity<?> response = foodListingController.getFoodListings(badClientId);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void getNearbyListingsZeroFoundTest() {
    ResponseEntity<?> response = foodListingController.getNearbyListings(
        client.getClientId(), 30.000f, -100.000f, 10);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void getNearbyListingsMissingClientTest() {
    // No client has this ID
    int badClientId = client.getClientId() + 2;

    // This query location is within 10 (units) of listing 1, but
    // response should still be NOT_FOUND since client doesn't exist
    ResponseEntity<?> response = foodListingController.getNearbyListings(
        badClientId, 78.121f, 120.282f, 10);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void getNearbyListingsOneFoundSingleClientTest() {
    // Mock the return from findByClient()
    List<FoodListing> mockedReturn = new ArrayList<>();
    mockedReturn.add(listing1);
    mockedReturn.add(listing2);
    when(foodListingRepository.findByClient(client)).thenReturn(mockedReturn);
    // Query from a location near listing 1
    ResponseEntity<?> response = foodListingController.getNearbyListings(
        client.getClientId(), 34.060f, -118.250f, 10);
    // Check status code
    assertEquals(HttpStatus.OK, response.getStatusCode());
    // Check body
    @SuppressWarnings("unchecked") // suppress warning for unchecked cast...
    List<FoodListing> listingsFound = (List<FoodListing>) response.getBody();
    assert (Objects.requireNonNull(listingsFound).size() == 1);
    FoodListing expected1 = listing1;
    assert (expected1.equals(listingsFound.get(0)));

    // Query form a location near listing 2
    ResponseEntity<?> response2 = foodListingController.getNearbyListings(
        client.getClientId(), 78.121f, 120.282f, 10);
    // Check status code
    assertEquals(HttpStatus.OK, response2.getStatusCode());
    // Check body
    @SuppressWarnings("unchecked")
    List<FoodListing> listingsFound2 = (List<FoodListing>) response2.getBody();
    assert (Objects.requireNonNull(listingsFound2).size() == 1);
    FoodListing expected2 = listing2;
    assert (expected2.equals(listingsFound2.get(0)));
  }

  @Test
  public void getNearbyListingsOneFoundMultipleClientsTest() {

    // Mock
    List<FoodListing> mockedReturn = new ArrayList<>();
    mockedReturn.add(listing1);
    mockedReturn.add(listing2);
    when(foodListingRepository.findByClient(client)).thenReturn(mockedReturn);

    // Should only find listing1
    ResponseEntity<?> response = foodListingController.getNearbyListings(
        client.getClientId(), 34.060f, -118.250f, 10);
    // Check status code
    assertEquals(HttpStatus.OK, response.getStatusCode());
    // Check body
    @SuppressWarnings("unchecked") // suppress warning for unchecked cast...
    List<FoodListing> listingsFound = (List<FoodListing>) response.getBody();
    assert (Objects.requireNonNull(listingsFound).size() == 1);
    FoodListing expected1 = listing1;
    assert (expected1.equals(listingsFound.get(0)));

    // Create client profile 2
    ClientProfile client2 = new ClientProfile();
    final int client2Id = client2.getClientId();
    // Create account under client 2
    final AccountProfile account2 = new AccountProfile(
        client2, AccountProfile.AccountType.PROVIDER, "0987654321", "a");
    // Mock
    List<FoodListing> mockedReturn2 = new ArrayList<>();
    mockedReturn2.add(listing2);
    mockedReturn2.add(listing3);
    when(foodListingRepository.findByClient(client2)).thenReturn(mockedReturn2);
    when(clientProfileRepository.findById(client2Id)).thenReturn(Optional.of(client2));
    when(accountProfileRepository.findById(account2.getAccountId()))
        .thenReturn(Optional.of(account2));

    // Should only find listing2
    ResponseEntity<?> response2 = foodListingController.getNearbyListings(
        client2Id, 78.121f, 120.282f, 10);
    // Check status code
    assertEquals(HttpStatus.OK, response2.getStatusCode());
    // Check body
    @SuppressWarnings("unchecked") // suppress warning for unchecked cast...
    List<FoodListing> listingsFound2 = (List<FoodListing>) response2.getBody();
    assert (Objects.requireNonNull(listingsFound2).size() == 1);
    FoodListing expected2 = listing2;
    assert (expected2.equals(listingsFound2.get(0)));
  }

  @Test
  public void getNearbyListingsTwoFoundTest() {

    List<FoodListing> mockedReturn = new ArrayList<>();
    mockedReturn.add(listing1);
    mockedReturn.add(listing2);
    mockedReturn.add(listing3);
    when(foodListingRepository.findByClient(client)).thenReturn(mockedReturn);

    // Test endpoint
    ResponseEntity<?> response = foodListingController.getNearbyListings(
        client.getClientId(), 34.021f, -118.243f, 10);
    // Check status code
    assertEquals(HttpStatus.OK, response.getStatusCode());
    // Check body
    @SuppressWarnings("unchecked")
    List<FoodListing> listingsFound = (List<FoodListing>) response.getBody();
    assert listingsFound != null;
    assertEquals(2, listingsFound.size());

    FoodListing expected1 = listing1;
    FoodListing expected3 = listing3;
    Set<FoodListing> expectedSet = Set.of(expected1, expected3);
    for (FoodListing listing : listingsFound) {
      assert (expectedSet.contains(listing));
    }
  }

  @Test
  public void getFoodListingsUnderAccountMissingClientTest() {
    ResponseEntity<?> response = foodListingController.getFoodListingsUnderAccount(
        111, 222);

    // Check status code
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void getFoodListingsUnderAccountMissingAccountTest() {

    ResponseEntity<?> response = foodListingController.getFoodListingsUnderAccount(
        client.getClientId(), 222);

    // Check status code
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void getFoodListingsUnderAccountNoneFoundTest() {

    final AccountProfile account2 = new AccountProfile(client, AccountProfile.AccountType.PROVIDER,
        "1234567890", "x");

    // Save listings under account 2, then should find no listings when querying account 1
    List<FoodListing> mockedReturn = new ArrayList<>();
    mockedReturn.add(listing1);
    mockedReturn.add(listing2);
    mockedReturn.add(listing3);
    when(foodListingRepository.findByClientAndAccount(eq(client), eq(account2)))
                              .thenReturn(mockedReturn);
    // Check status code
    ResponseEntity<?> response = foodListingController.getFoodListingsUnderAccount(
        client.getClientId(), providerId);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void getFoodListingsUnderAccountSomeFoundTest() {

    // Save listings under account 2, then should find no listings when querying account 1
    List<FoodListing> mockedReturn = new ArrayList<>();
    mockedReturn.add(listing1);
    mockedReturn.add(listing2);
    mockedReturn.add(listing3);
    int accountId = providerId;
    when(foodListingRepository.findByClientAndAccount(eq(client), eq(providerAccount)))
        .thenReturn(mockedReturn);

    // Check status code
    ResponseEntity<?> response = foodListingController.getFoodListingsUnderAccount(
        client.getClientId(), accountId);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    // Check body
    @SuppressWarnings("unchecked") // suppress warning for unchecked cast...
    List<FoodListing> foodListings = (List<FoodListing>) response.getBody();
    assert (Objects.requireNonNull(foodListings).size() == 3);

    FoodListing expected1 = listing1;
    FoodListing expected2 = listing2;
    FoodListing expected3 = listing3;
    Set<FoodListing> expectedSet = Set.of(expected1, expected2, expected3);

    for (FoodListing listing : foodListings) {
      assert (expectedSet.contains(listing));
    }
  }

  @Test
  public void fulfillRequestMissingClientTest() {
    ResponseEntity<?> response = foodListingController.fulfillRequest(111, 1, 1);

    // Check status code
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void fulfillRequestMissingAccountTest() {

    ResponseEntity<?> response = foodListingController.fulfillRequest(client.getClientId(), 1, 1);

    // Check status code
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void fulfillRequestMissingListingIdTest() {

    int badListingId = listing1Id + 2; // No listing has this ID

    ResponseEntity<?> response = foodListingController.fulfillRequest(
        client.getClientId(), badListingId, 1);

    // Check status code
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void fulfillRequestNegativeQuantityTest() {
    when(foodListingRepository.findByClientAndListingId(
        eq(client), eq(listing2Id)
    )).thenReturn(Optional.of(listing2));
    ResponseEntity<?> response = foodListingController
        .fulfillRequest(client.getClientId(), listing2Id, -30);

    // Check status code
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  public void fulfillRequestOkTest() {
    when(foodListingRepository.findByClientAndListingId(
        eq(client), eq(listing2Id)
    )).thenReturn(Optional.of(listing2));
    ResponseEntity<?> response = foodListingController
        .fulfillRequest(client.getClientId(), listing2Id, 30);

    // Check status code
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void fulfillRequestBadRequestTest() {
    when(foodListingRepository.findByClientAndListingId(
        eq(client), eq(listing1Id)
    )).thenReturn(Optional.of(listing1));
    ResponseEntity<?> response = foodListingController.fulfillRequest(
        client.getClientId(), listing1Id, 31);

    // Check status code
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  public void updateFoodListingMissingClientTest() {
    ResponseEntity<?> response = foodListingController.updateFoodListing(
        111, 10, 10,
        null, null, null, null);

    // Check status code
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void updateFoodListingMissingAccountTest() {
    int listing3Id = 2;
    when(foodListingRepository.findByClientAndAccountAndListingId(
        eq(client), eq(providerAccount), eq(listing3Id)
    )).thenReturn(Optional.of(listing3));
    ResponseEntity<?> response = foodListingController.updateFoodListing(
        client.getClientId(), 2, listing3Id,
        null, null, null, null);

    // Check status code
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void updateFoodListingMissingListingIdTest() {
    ResponseEntity<?> response = foodListingController.updateFoodListing(
        client.getClientId(), providerId, 5,
        null, null, null, null);

    // Check status code
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void updateFoodListingUnauthorizedAccountTest() {
    ResponseEntity<?> response = foodListingController.updateFoodListing(
        client.getClientId(), recipientId, listing2Id,
        "OJ", 112.2f, 359.3f, 10);

    // Check status code
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  public void updateFoodListingNewFoodTypeTest() {
    when(foodListingRepository.findByClientAndAccountAndListingId(
        eq(client), eq(providerAccount), eq(listing2Id)
    )).thenReturn(Optional.of(listing2));

    ResponseEntity<?> response = foodListingController.updateFoodListing(
        client.getClientId(), providerId, listing2Id,
        "hot cocoa", null, null, null);
    // Check status
    assertEquals(HttpStatus.OK, response.getStatusCode());
    // Check that the food type of the saved listing has been correctly updated
    Optional<FoodListing> listingOptional = foodListingRepository.findById(listing2Id);
    if (listingOptional.isPresent()) {
      FoodListing updatedListing = listingOptional.get();
      assert (updatedListing.getFoodType().equals("hot cocoa")); // the only field that was changed
      assert (updatedListing.getLatitude() == 78.122f);
      assert (updatedListing.getLongitude() == 120.281f);
      assert (updatedListing.getQuantityListed() == 30);
    } else {
      fail();
    }
  }

  @Test
  public void updateFoodListingNewLatitudeTest() {
    when(foodListingRepository.findByClientAndAccountAndListingId(
        eq(client), eq(providerAccount), eq(listing2Id)
    )).thenReturn(Optional.of(listing2));

    ResponseEntity<?> response = foodListingController.updateFoodListing(
        client.getClientId(), providerId, listing2Id,
        null, 120f, null, null);
    // Check status
    assertEquals(HttpStatus.OK, response.getStatusCode());
    // Check that the food type of the saved listing has been correctly updated
    Optional<FoodListing> listingOptional = foodListingRepository.findById(listing2Id);
    if (listingOptional.isPresent()) {
      FoodListing updatedListing = listingOptional.get();
      assert (updatedListing.getFoodType().equals("beverage"));
      assert (updatedListing.getLatitude() == 120f);
      assert (updatedListing.getLongitude() == 120.281f);
      assert (updatedListing.getQuantityListed() == 30);
    } else {
      fail();
    }
  }

  @Test
  public void updateFoodListingNewLongitudeTest() {
    when(foodListingRepository.findByClientAndAccountAndListingId(
        eq(client), eq(providerAccount), eq(listing2Id)
    )).thenReturn(Optional.of(listing2));

    ResponseEntity<?> response = foodListingController.updateFoodListing(
        client.getClientId(), providerId, listing2Id,
        null, null, 320.94f, null);
    // Check status
    assertEquals(HttpStatus.OK, response.getStatusCode());
    // Check that the food type of the saved listing has been correctly updated
    Optional<FoodListing> listingOptional = foodListingRepository.findById(listing2Id);
    if (listingOptional.isPresent()) {
      FoodListing updatedListing = listingOptional.get();
      assert (updatedListing.getFoodType().equals("beverage"));
      assert (updatedListing.getLatitude() == 78.122f);
      assert (updatedListing.getLongitude() == 320.94f);
      assert (updatedListing.getQuantityListed() == 30);
    } else {
      fail();
    }
  }

  @Test
  public void updateFoodListingNewQuantityTest() {
    when(foodListingRepository.findByClientAndAccountAndListingId(
        eq(client), eq(providerAccount), eq(listing2Id)
    )).thenReturn(Optional.of(listing2));

    ResponseEntity<?> response = foodListingController.updateFoodListing(
        client.getClientId(), providerId, listing2Id,
        null, null, null, 1);
    // Check status
    assertEquals(HttpStatus.OK, response.getStatusCode());
    // Check that the food type of the saved listing has been correctly updated
    Optional<FoodListing> listingOptional = foodListingRepository.findById(listing2Id);
    if (listingOptional.isPresent()) {
      FoodListing updatedListing = listingOptional.get();
      assert (updatedListing.getFoodType().equals("beverage"));
      assert (updatedListing.getLatitude() == 78.122f);
      assert (updatedListing.getLongitude() == 120.281f);
      assert (updatedListing.getQuantityListed() == 1);
    } else {
      fail();
    }
  }

  @Test
  public void updateFoodListingNewComboTest() {
    when(foodListingRepository.findByClientAndAccountAndListingId(
        eq(client), eq(providerAccount), eq(listing2Id)
    )).thenReturn(Optional.of(listing2));

    ResponseEntity<?> response = foodListingController.updateFoodListing(
        client.getClientId(), providerId, listing2Id,
        "espresso beans", 120f, null, 1000);
    // Check status
    assertEquals(HttpStatus.OK, response.getStatusCode());
    // Check that the food type of the saved listing has been correctly updated
    Optional<FoodListing> listingOptional = foodListingRepository.findById(listing2Id);
    if (listingOptional.isPresent()) {
      FoodListing updatedListing = listingOptional.get();
      assert (updatedListing.getFoodType().equals("espresso beans"));
      assert (updatedListing.getLatitude() == 120f);
      assert (updatedListing.getLongitude() == 120.281f);
      assert (updatedListing.getQuantityListed() == 1000);
    } else {
      fail();
    }
  }

  @Test
  public void updateFoodListingNewEveryTest() {
    when(foodListingRepository.findByClientAndAccountAndListingId(
        eq(client), eq(providerAccount), eq(listing2Id)
    )).thenReturn(Optional.of(listing2));

    ResponseEntity<?> response = foodListingController.updateFoodListing(
        client.getClientId(), providerId, listing2Id,
        "BLT sandwich", 119.275f, 67.982f, 21);
    // Check status
    assertEquals(HttpStatus.OK, response.getStatusCode());
    // Check that the food type of the saved listing has been correctly updated
    Optional<FoodListing> listingOptional = foodListingRepository.findById(listing2Id);
    if (listingOptional.isPresent()) {
      FoodListing updatedListing = listingOptional.get();
      assert (updatedListing.getFoodType().equals("BLT sandwich"));
      assert (updatedListing.getLatitude() == 119.275f);
      assert (updatedListing.getLongitude() == 67.982f);
      assert (updatedListing.getQuantityListed() == 21);
    } else {
      fail();
    }
  }
}
