package dev.coms4156.project.teamproject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.coms4156.project.teamproject.controller.AccountProfileController;
import dev.coms4156.project.teamproject.controller.ClientProfileController;
import dev.coms4156.project.teamproject.controller.FoodListingController;
import dev.coms4156.project.teamproject.model.AccountProfile;
import dev.coms4156.project.teamproject.model.ClientProfile;
import dev.coms4156.project.teamproject.model.FoodListing;
import dev.coms4156.project.teamproject.model.FoodRequest;
import dev.coms4156.project.teamproject.repository.FoodListingRepository;
import dev.coms4156.project.teamproject.repository.FoodRequestRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Integration tests for the ClientProfileController, AccountProfileController,
 * and FoodListingController classes.
 *
 * <p>This class tests various operations related to food listings, such as
 * creating listings, making requests for listings, etc. These operations integrate with
 * CRUD operations from the ClientProfileController and AccountProfileController classes,
 * since they must be performed by existing accounts under existing clients. Further,
 * FoodListingController integrates with FoodRequestController, as requests must be made by
 * recipients for existing listings posted by providers under the same client.
 */
@DataJpaTest
@Import({FoodListingController.class,
    ClientProfileController.class,
    AccountProfileController.class})
public class InternalIntegrationTests {

  @Autowired
  private FoodListingController foodListingController;
  @Autowired
  private FoodListingRepository foodListingRepository;
  @Autowired
  private FoodRequestRepository foodRequestRepository;
  @Autowired
  private ClientProfileController clientProfileController;
  @Autowired
  private AccountProfileController accountProfileController;

  private FoodListing saveListing1(ClientProfile client, AccountProfile account) {
    FoodListing listing1 = new FoodListing(client, account, "snack",
        25, LocalDateTime.of(2024, 10, 6, 11, 0),
        34.052f, -118.243f);
    return foodListingRepository.save(listing1);
  }

  private FoodListing saveListing2(ClientProfile client, AccountProfile account) {
    FoodListing listing2 = new FoodListing(client, account, "beverage",
        30, LocalDateTime.of(2024, 10, 7, 16, 30),
        78.122f, 120.281f);
    return foodListingRepository.save(listing2);
  }

  private FoodRequest requestListing(ClientProfile client, AccountProfile account,
      FoodListing listing, int quantityRequested) {
    FoodRequest foodRequest = new FoodRequest(client, account, listing, quantityRequested);
    return foodRequestRepository.save(foodRequest);
  }

  @Test
  public void getRequestsForListingMissingClientTest() {
    // Create client
    ResponseEntity<ClientProfile> clientProfile = clientProfileController.createClientProfile();
    ClientProfile client = clientProfile.getBody();
    assert client != null;
    int clientId = client.getClientId();

    // Create provider account
    ResponseEntity<AccountProfile> providerProfile = accountProfileController.createAccountProfile(
        clientId, AccountProfile.AccountType.PROVIDER, "1234567890", "p");
    AccountProfile providerAccount = providerProfile.getBody();
    assert providerAccount != null;
    int providerId = providerAccount.getAccountId();

    // Create recipient account
    ResponseEntity<AccountProfile> recipientProfile = accountProfileController.createAccountProfile(
        clientId, AccountProfile.AccountType.RECIPIENT, "1234567890", "r");
    AccountProfile recipientAccount = recipientProfile.getBody();

    // Create listing
    FoodListing listing1 = saveListing1(client, providerAccount);
    int listingId = listing1.getListingId();
    // Send request for listing
    requestListing(client, recipientAccount, listing1, 1);

    // No client has this ID
    int badClientId = client.getClientId() + 2;

    ResponseEntity<?> response = foodListingController.getRequestsForListing(
        badClientId, providerId, listingId);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void getRequestsForListingMissingAccountTest() {
    // Create client
    ResponseEntity<ClientProfile> clientProfile = clientProfileController.createClientProfile();
    ClientProfile client = clientProfile.getBody();
    assert client != null;
    int clientId = client.getClientId();

    // Create provider account
    ResponseEntity<AccountProfile> providerProfile = accountProfileController.createAccountProfile(
        clientId, AccountProfile.AccountType.PROVIDER, "1234567890", "p");
    AccountProfile providerAccount = providerProfile.getBody();
    assert providerAccount != null;
    int providerId = providerAccount.getAccountId();

    // Create recipient account
    ResponseEntity<AccountProfile> recipientProfile = accountProfileController.createAccountProfile(
        clientId, AccountProfile.AccountType.RECIPIENT, "1234567890", "r");
    AccountProfile recipientAccount = recipientProfile.getBody();

    // Create listing
    FoodListing listing1 = saveListing1(client, providerAccount);
    int listingId = listing1.getListingId();
    // Send request for listing
    requestListing(client, recipientAccount, listing1, 1);

    // No account has this ID
    int badProviderId = providerId + 2;

    ResponseEntity<?> response = foodListingController.getRequestsForListing(
        clientId, badProviderId, listingId);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void getRequestsForListingUnauthorizedAccountTest() {
    // Create client
    ResponseEntity<ClientProfile> clientProfile = clientProfileController.createClientProfile();
    ClientProfile client = clientProfile.getBody();
    assert client != null;
    int clientId = client.getClientId();

    // Create provider account
    ResponseEntity<AccountProfile> providerProfile = accountProfileController.createAccountProfile(
        clientId, AccountProfile.AccountType.PROVIDER, "1234567890", "p");
    AccountProfile providerAccount = providerProfile.getBody();

    // Create recipient account
    ResponseEntity<AccountProfile> recipientProfile = accountProfileController.createAccountProfile(
        clientId, AccountProfile.AccountType.RECIPIENT, "1234567890", "r");
    AccountProfile recipientAccount = recipientProfile.getBody();
    int recipientAccountId = recipientAccount.getAccountId();

    // Create listing
    FoodListing listing1 = saveListing1(client, providerAccount);
    int listingId = listing1.getListingId();
    // Send request for listing
    requestListing(client, recipientAccount, listing1, 1);

    // Try to call this endpoint as a recipient type account
    ResponseEntity<?> response = foodListingController.getRequestsForListing(
        clientId, recipientAccountId, listingId);
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  public void getRequestsForListingMissingListingTest() {
    // Create client
    ResponseEntity<ClientProfile> clientProfile = clientProfileController.createClientProfile();
    ClientProfile client = clientProfile.getBody();
    assert client != null;
    int clientId = client.getClientId();

    // Create provider account
    ResponseEntity<AccountProfile> providerProfile = accountProfileController.createAccountProfile(
        clientId, AccountProfile.AccountType.PROVIDER, "1234567890", "p");
    AccountProfile providerAccount = providerProfile.getBody();
    assert providerAccount != null;
    int providerId = providerAccount.getAccountId();

    // Create recipient account
    ResponseEntity<AccountProfile> recipientProfile = accountProfileController.createAccountProfile(
        clientId, AccountProfile.AccountType.RECIPIENT, "1234567890", "r");
    AccountProfile recipientAccount = recipientProfile.getBody();

    // Create listing
    FoodListing listing1 = saveListing1(client, providerAccount);
    int listingId = listing1.getListingId(); // default is 1
    // Send request for listing
    requestListing(client, recipientAccount, listing1, 1);

    // No listing has this ID
    int badListingId = listingId + 2;

    ResponseEntity<?> response = foodListingController.getRequestsForListing(
        clientId, providerId, badListingId);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void getRequestsForListingNoRequestsFoundTest() {
    // Create client
    ResponseEntity<ClientProfile> clientProfile = clientProfileController.createClientProfile();
    ClientProfile client = clientProfile.getBody();
    assert client != null;
    int clientId = client.getClientId();

    // Create provider account
    ResponseEntity<AccountProfile> providerProfile = accountProfileController.createAccountProfile(
        clientId, AccountProfile.AccountType.PROVIDER, "1234567890", "p");
    AccountProfile providerAccount = providerProfile.getBody();
    assert providerAccount != null;
    int providerId = providerAccount.getAccountId();

    // Create recipient account
    ResponseEntity<AccountProfile> recipientProfile = accountProfileController.createAccountProfile(
        clientId, AccountProfile.AccountType.RECIPIENT, "1234567890", "r");
    AccountProfile recipientAccount = recipientProfile.getBody();

    // Create listing
    FoodListing listing1 = saveListing1(client, providerAccount);
    // Send request for this listing
    requestListing(client, recipientAccount, listing1, 1);
    // Create another listing with no requests; query this listing
    FoodListing listing2 = saveListing2(client, providerAccount);
    int queryListingId = listing2.getListingId();

    ResponseEntity<?> response = foodListingController.getRequestsForListing(
        clientId, providerId, queryListingId);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void getRequestsForListingSomeRequestsFoundTest() {
    // Create client
    ResponseEntity<ClientProfile> clientProfile = clientProfileController.createClientProfile();
    ClientProfile client = clientProfile.getBody();
    assert client != null;
    int clientId = client.getClientId();

    // Create provider account
    ResponseEntity<AccountProfile> providerProfile = accountProfileController.createAccountProfile(
        clientId, AccountProfile.AccountType.PROVIDER, "1234567890", "p");
    AccountProfile providerAccount = providerProfile.getBody();
    assert providerAccount != null;
    int providerId = providerAccount.getAccountId();

    // Create recipient account
    ResponseEntity<AccountProfile> recipientProfile = accountProfileController.createAccountProfile(
        clientId, AccountProfile.AccountType.RECIPIENT, "1234567891", "r1");
    AccountProfile recipientAccount = recipientProfile.getBody();

    // Create another recipient account
    ResponseEntity<AccountProfile> recipientProfile2 = accountProfileController
        .createAccountProfile(clientId, AccountProfile.AccountType.RECIPIENT,
            "1234567892", "r2");
    AccountProfile recipientAccount2 = recipientProfile2.getBody();

    // Create listing
    FoodListing listing1 = saveListing1(client, providerAccount);
    int queryListingId = listing1.getListingId();
    // Send request for this listing
    FoodRequest request1 = requestListing(client, recipientAccount, listing1, 1);
    FoodRequest request2 = requestListing(client, recipientAccount, listing1, 2);
    FoodRequest request3 = requestListing(client, recipientAccount2, listing1, 3);

    ResponseEntity<?> response = foodListingController.getRequestsForListing(
        clientId, providerId, queryListingId);
    // Check status code
    assertEquals(HttpStatus.OK, response.getStatusCode());
    // Check body
    @SuppressWarnings("unchecked") // suppress warning for unchecked cast...
    List<FoodRequest> requests = (List<FoodRequest>) response.getBody();
    assert (Objects.requireNonNull(requests).size() == 3);
    Set<FoodRequest> expectedRequests = Set.of(request1, request2, request3);

    for (FoodRequest request : requests) {
      assert (expectedRequests.contains(request));
    }
  }
}
