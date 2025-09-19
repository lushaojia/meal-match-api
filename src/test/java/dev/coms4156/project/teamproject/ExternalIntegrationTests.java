package dev.coms4156.project.teamproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.coms4156.project.teamproject.model.AccountProfile;
import dev.coms4156.project.teamproject.model.ClientProfile;
import dev.coms4156.project.teamproject.model.FoodListing;
import dev.coms4156.project.teamproject.model.FoodRequest;
import dev.coms4156.project.teamproject.repository.AccountProfileRepository;
import dev.coms4156.project.teamproject.repository.ClientProfileRepository;
import dev.coms4156.project.teamproject.repository.FoodListingRepository;
import dev.coms4156.project.teamproject.repository.FoodRequestRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * This class is used to perform external integration testing
 * for CRUD operations in JPA repositories. JPA repository
 * is the backbone for the persistent data storage of our service.
 */
@DataJpaTest
class ExternalIntegrationTests {
  @Autowired
  private FoodRequestRepository foodRequestRepository;

  @Autowired
  private ClientProfileRepository clientProfileRepository;

  @Autowired
  private AccountProfileRepository accountProfileRepository;

  @Autowired
  private FoodListingRepository foodListingRepository;

  @Test
  void testCreateFoodRequest() {
    // Setup related entities
    ClientProfile client = clientProfileRepository.save(new ClientProfile());
    AccountProfile account = accountProfileRepository.save(
        new AccountProfile(client, AccountProfile.AccountType.RECIPIENT,
            "1234567890", "John Doe"));
    FoodListing foodListing = foodListingRepository.save(new FoodListing(client, account,
        "Apples", 10,  LocalDateTime.now(),
        12.345f, 67.890f));

    // Create FoodRequest
    FoodRequest foodRequest = new FoodRequest(client, account, foodListing, 5);
    FoodRequest savedRequest = foodRequestRepository.save(foodRequest);

    // Assertions
    assertNotNull(savedRequest);
    assertEquals(5, savedRequest.getQuantityRequested());
    assertEquals(account, savedRequest.getAccountId());
    assertEquals(foodListing, savedRequest.getListing());
  }

  @Test
  void testReadFoodRequest() {
    // Setup related entities
    ClientProfile client = clientProfileRepository.save(new ClientProfile());
    AccountProfile account = accountProfileRepository.save(
        new AccountProfile(client, AccountProfile.AccountType.RECIPIENT,
            "1234567890", "John Doe"));
    FoodListing foodListing = foodListingRepository.save(
        new FoodListing(client, account, "Bananas", 15,  LocalDateTime.now(), 12.345f, 67.890f));

    // Create and save FoodRequest
    FoodRequest foodRequest = new FoodRequest(client, account, foodListing, 8);
    FoodRequest savedRequest = foodRequestRepository.save(foodRequest);

    // Read FoodRequest by ID
    Optional<FoodRequest> retrievedRequest = foodRequestRepository.findById(
        savedRequest.getRequestId());

    // Assertions
    assertTrue(retrievedRequest.isPresent());
    assertEquals(8, retrievedRequest.get().getQuantityRequested());
    assertEquals(account, retrievedRequest.get().getAccountId());
  }

  @Test
  void testUpdateFoodRequest() {
    // Setup related entities
    ClientProfile client = clientProfileRepository.save(new ClientProfile());
    AccountProfile account = accountProfileRepository.save(
        new AccountProfile(client, AccountProfile.AccountType.RECIPIENT,
            "1234567890", "John Doe"));
    FoodListing foodListing = foodListingRepository.save(
        new FoodListing(client, account, "Carrots", 20,
            LocalDateTime.now(), 12.345f, 67.890f));

    // Create and save FoodRequest
    FoodRequest foodRequest = new FoodRequest(client, account, foodListing, 10);
    FoodRequest savedRequest = foodRequestRepository.save(foodRequest);

    // Update FoodRequest
    savedRequest.setQuantityRequested(15);
    FoodRequest updatedRequest = foodRequestRepository.save(savedRequest);

    // Assertions
    assertEquals(15, updatedRequest.getQuantityRequested());
  }

  @Test
  void testDeleteFoodRequest() {
    // Setup related entities
    ClientProfile client = clientProfileRepository.save(new ClientProfile());
    AccountProfile account = accountProfileRepository.save(
        new AccountProfile(client, AccountProfile.AccountType.RECIPIENT, "1234567890", "John Doe"));
    FoodListing foodListing = foodListingRepository.save(
        new FoodListing(client, account, "Potatoes", 50,
            LocalDateTime.now(), 12.345f, 67.890f));

    // Create and save FoodRequest
    FoodRequest foodRequest = new FoodRequest(client, account, foodListing, 20);
    FoodRequest savedRequest = foodRequestRepository.save(foodRequest);

    // Delete FoodRequest
    foodRequestRepository.deleteById(savedRequest.getRequestId());

    // Verify deletion
    Optional<FoodRequest> deletedRequest = foodRequestRepository
        .findById(savedRequest.getRequestId());
    assertFalse(deletedRequest.isPresent());
  }
}
