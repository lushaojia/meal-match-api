package dev.coms4156.project.teamproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import dev.coms4156.project.teamproject.controller.FoodRequestController;
import dev.coms4156.project.teamproject.model.AccountProfile;
import dev.coms4156.project.teamproject.model.ClientProfile;
import dev.coms4156.project.teamproject.model.FoodListing;
import dev.coms4156.project.teamproject.model.FoodRequest;
import dev.coms4156.project.teamproject.repository.AccountProfileRepository;
import dev.coms4156.project.teamproject.repository.ClientProfileRepository;
import dev.coms4156.project.teamproject.repository.FoodListingRepository;
import dev.coms4156.project.teamproject.repository.FoodRequestRepository;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

/**
 * Unit tests for FoodListingController.
 * Tests every single method for success case, different failure cases,
 * and when applicable, functionality under multiple clients.
 */
@SpringBootTest
@ActiveProfiles("test")
public class FoodRequestControllerUnitTests {
  @Autowired
  private FoodRequestController foodRequestController;

  @MockBean
  private FoodRequestRepository foodRequestRepository;

  @MockBean
  private FoodListingRepository foodListingRepository;

  @MockBean
  private ClientProfileRepository clientProfileRepository;

  @MockBean
  private AccountProfileRepository accountProfileRepository;

  private ClientProfile client;
  private static final int clientId = 0;
  private static final int providerId = 0;
  private AccountProfile recipientAccount;
  private static final int recipientId = 1;
  private FoodListing listing1;
  private static final int listing1Id = 0;
  private FoodListing listing2;

  /**
   * Set up for controller tests. Mocks normal behavior of repositories
   * to isolate the testing of internal implementation from external library.
   */
  @BeforeEach
  public void setUp() {
    // Create and store a client
    client = new ClientProfile();

    // Create and store a provider account
    AccountProfile providerAccount = new AccountProfile(
        client, AccountProfile.AccountType.PROVIDER,
        "8897263717", "sweetgreen"
    );

    // Create and store a recipient account
    recipientAccount = new AccountProfile(
        client, AccountProfile.AccountType.RECIPIENT,
        "2178271028", "Anne");

    // Create and store some listings
    listing1 = new FoodListing(client, providerAccount, "snack",
        25, LocalDateTime.of(2024, 10, 6, 11, 0),
        34.052f, -118.243f);

    listing2 = new FoodListing(client, providerAccount, "beverage",
        20, LocalDateTime.of(2024, 10, 7, 16, 30),
        78.122f, 120.281f);

    // Mock the behavior of JPA repositories
    when(clientProfileRepository.findById(any())).thenReturn(Optional.of(client));
    when(accountProfileRepository.findById(0)).thenReturn(Optional.of(providerAccount));
    when(accountProfileRepository.findById(1)).thenReturn(Optional.of(recipientAccount));
    when(foodListingRepository.findById(any())).thenAnswer(invocation -> {
      int id = invocation.getArgument(0); // Retrieve the argument (ID) passed to findById
      if (id == 0) {
        return Optional.of(listing1);
      } else if (id == 1) {
        return Optional.of(listing2);
      } else {
        return Optional.empty();
      }
    });
  }

  @Test
  public void createRequestOkTest() {
    ResponseEntity<?> response = foodRequestController.createFoodRequest(
        clientId, recipientId, listing1Id, 2
    );

    // Check status code
    assertEquals(HttpStatus.CREATED, response.getStatusCode());

    // Check response body
    if (!(response.getBody() instanceof FoodRequest)) {
      fail();
    }
    FoodRequest foodRequest = (FoodRequest) response.getBody();
    assertEquals(clientId, foodRequest.getClient().getClientId());
    assertEquals(AccountProfile.AccountType.RECIPIENT,
        foodRequest.getAccountId().getAccountType());
    assertEquals("snack",
        foodRequest.getListing().getFoodType());
    assertEquals(2, foodRequest.getQuantityRequested());
  }

  @Test
  public void getRequestOkTest() {
    // Save a request to repository
    FoodRequest savedRequest = new FoodRequest(client, recipientAccount, listing2, 2);

    // Get this request
    when(foodRequestRepository.findById(any())).thenReturn(Optional.of(savedRequest));
    ResponseEntity<?> response =
        foodRequestController.getFoodRequest(savedRequest.getRequestId());

    // Check status code
    assertEquals(HttpStatus.OK, response.getStatusCode());

    // Check body
    if (response.getBody() instanceof Map<?, ?> request) {
      assertEquals(savedRequest.getClient().getClientId(),
          request.get("request_id"));
      assertEquals(savedRequest.getAccountId().getAccountId(),
          request.get("account_id"));
      assertEquals(savedRequest.getRequestTime(),
          request.get("request_time"));
      assertEquals(savedRequest.getListing().getListingId(),
          request.get("listing_id"));
      assertEquals(savedRequest.getQuantityRequested(),
          request.get("quantity_requested"));
    } else {
      fail();
    }
  }

  @Test
  public void getRequestNotFoundTest() {
    ResponseEntity<?> response = foodRequestController.getFoodRequest(1);
    // Check status code
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void updateRequestOkTest() {
    FoodRequest request = new FoodRequest(client, recipientAccount, listing2, 2);
    when(foodRequestRepository.findById(request.getRequestId())).thenReturn(Optional.of(request));
    ResponseEntity<?> response = foodRequestController.updateFoodRequest(request.getRequestId(), 5);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    Optional<FoodRequest> optional = foodRequestRepository.findById(request.getRequestId());
    if (optional.isPresent()) {
      FoodRequest updated = optional.get();
      assert updated.getQuantityRequested() == 5;
    } else {
      fail();
    }
  }

  @Test
  public void updateRequestNotFoundTest() {
    ResponseEntity<?> response = foodRequestController.updateFoodRequest(1, 5);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }
}
