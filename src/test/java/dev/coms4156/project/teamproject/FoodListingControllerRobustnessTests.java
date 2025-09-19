package dev.coms4156.project.teamproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import dev.coms4156.project.teamproject.controller.AccountProfileController;
import dev.coms4156.project.teamproject.controller.ClientProfileController;
import dev.coms4156.project.teamproject.controller.FoodListingController;
import dev.coms4156.project.teamproject.model.AccountProfile;
import dev.coms4156.project.teamproject.model.ClientProfile;
import dev.coms4156.project.teamproject.model.FoodListing;
import dev.coms4156.project.teamproject.repository.FoodListingRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

/**
 * Mock tests for FoodListingController.
 *
 * <p>These tests consider cases where wrong data is returned from
 * the repository during the creation of food listings, and makes sure that the
 * controller handles errors appropriately.
 */
@SpringBootTest
@ActiveProfiles("test")
public class FoodListingControllerRobustnessTests {

  @MockBean
  private FoodListingRepository foodListingRepository;

  @Autowired
  private FoodListingController foodListingController;

  @Autowired
  private ClientProfileController clientProfileController;

  @Autowired
  private AccountProfileController accountProfileController;

  @BeforeEach
  public void setup() {
    // Make sure repository is in a clean state
    foodListingRepository.deleteAll();
  }

  @Test
  public void createFoodListingSavedWrongFoodTypeTest() {
    ResponseEntity<ClientProfile> clientProfile = clientProfileController.createClientProfile();
    ClientProfile client = clientProfile.getBody();
    assert client != null;
    int clientId = client.getClientId();

    ResponseEntity<AccountProfile> accountProfile = accountProfileController.createAccountProfile(
        clientId, AccountProfile.AccountType.PROVIDER, "1234567890", "x");
    AccountProfile account = accountProfile.getBody();
    assert account != null;
    int accountId = account.getAccountId();

    // Create wrong food listing that will trigger Exception
    FoodListing differentFoodListing = new FoodListing(
        client, account, "yuck", 999,
        LocalDateTime.now(), 122.100f, 88.118f);

    // Mock foodListingRepository to return wrong food listing when we save something to it
    FoodListingRepository mockedFoodListingRepository = Mockito.mock(FoodListingRepository.class);
    doReturn(differentFoodListing).when(foodListingRepository).save(any());

    ResponseEntity<?> response = foodListingController.createFoodListing(
        clientId, accountId,
        "kiwi", 10,
        34.052f, -118.244f);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Failed to create food listing", response.getBody());
  }

  @Test
  public void createFoodListingSavedWrongQuantityTest() {
    ResponseEntity<ClientProfile> clientProfile = clientProfileController.createClientProfile();
    ClientProfile client = clientProfile.getBody();
    assert client != null;
    int clientId = client.getClientId();

    ResponseEntity<AccountProfile> accountProfile = accountProfileController.createAccountProfile(
        clientId, AccountProfile.AccountType.PROVIDER, "1234567890", "x");
    AccountProfile account = accountProfile.getBody();
    assert account != null;
    int accountId = account.getAccountId();

    // Create wrong food listing that will trigger Exception
    FoodListing differentFoodListing = new FoodListing(
        client, account, "kiwi", 999,
        LocalDateTime.now(), 122.100f, 88.118f);

    // Mock foodListingRepository to return wrong food listing when we save something to it
    FoodListingRepository mockedFoodListingRepository = Mockito.mock(FoodListingRepository.class);
    doReturn(differentFoodListing).when(foodListingRepository).save(any());

    ResponseEntity<?> response = foodListingController.createFoodListing(
        clientId, accountId,
        "kiwi", 10,
        34.052f, -118.244f);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Failed to create food listing", response.getBody());
  }

  @Test
  public void createFoodListingSavedWrongLatitudeTest() {
    ResponseEntity<ClientProfile> clientProfile = clientProfileController.createClientProfile();
    ClientProfile client = clientProfile.getBody();
    assert client != null;
    int clientId = client.getClientId();

    ResponseEntity<AccountProfile> accountProfile = accountProfileController.createAccountProfile(
        clientId, AccountProfile.AccountType.PROVIDER, "1234567890", "x");
    AccountProfile account = accountProfile.getBody();
    assert account != null;
    int accountId = account.getAccountId();

    // Create wrong food listing that will trigger Exception
    FoodListing differentFoodListing = new FoodListing(
        client, account, "kiwi", 10,
        LocalDateTime.now(), 122.100f, 88.118f);

    // Mock foodListingRepository to return wrong food listing when we save something to it
    FoodListingRepository mockedFoodListingRepository = Mockito.mock(FoodListingRepository.class);
    doReturn(differentFoodListing).when(foodListingRepository).save(any());

    ResponseEntity<?> response = foodListingController.createFoodListing(
        clientId, accountId,
        "kiwi", 10,
        34.052f, -118.244f);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Failed to create food listing", response.getBody());
  }

  @Test
  public void createFoodListingSavedWrongLongitudeTest() {
    ResponseEntity<ClientProfile> clientProfile = clientProfileController.createClientProfile();
    ClientProfile client = clientProfile.getBody();
    assert client != null;
    int clientId = client.getClientId();

    ResponseEntity<AccountProfile> accountProfile = accountProfileController.createAccountProfile(
        clientId, AccountProfile.AccountType.PROVIDER, "1234567890", "x");
    AccountProfile account = accountProfile.getBody();
    assert account != null;
    int accountId = account.getAccountId();

    // Create wrong food listing that will trigger Exception
    FoodListing differentFoodListing = new FoodListing(
        client, account, "kiwi", 10,
        LocalDateTime.now(), 34.052f, 88.118f);

    // Mock foodListingRepository to return wrong food listing when we save something to it
    FoodListingRepository mockedFoodListingRepository = Mockito.mock(FoodListingRepository.class);
    doReturn(differentFoodListing).when(foodListingRepository).save(any());

    ResponseEntity<?> response = foodListingController.createFoodListing(
        clientId, accountId,
        "kiwi", 10,
        34.052f, -118.244f);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Failed to create food listing", response.getBody());
  }
}
