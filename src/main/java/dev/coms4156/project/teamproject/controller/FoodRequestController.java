package dev.coms4156.project.teamproject.controller;

import dev.coms4156.project.teamproject.model.AccountProfile;
import dev.coms4156.project.teamproject.model.ClientProfile;
import dev.coms4156.project.teamproject.model.FoodListing;
import dev.coms4156.project.teamproject.model.FoodRequest;
import dev.coms4156.project.teamproject.repository.AccountProfileRepository;
import dev.coms4156.project.teamproject.repository.ClientProfileRepository;
import dev.coms4156.project.teamproject.repository.FoodListingRepository;
import dev.coms4156.project.teamproject.repository.FoodRequestRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that provides API endpoints for creating, retrieving, and updating food requests made
 * by accounts for specific food listings.
 *
 * <p>FoodRequestController handles the creation of new food requests, fetching existing requests
 * by their ID, and updating the quantity of food requested.
 */
@RestController
@RequestMapping("/api/foodRequests")
public class FoodRequestController {

  private final FoodRequestRepository foodRequestRepository;
  private final ClientProfileRepository clientProfileRepository;
  private final AccountProfileRepository accountProfileRepository;
  private final FoodListingRepository foodListingRepository;

  /**
   * Constructs a FoodRequestController with the necessary repositories.
   *
   * @param foodRequestRepository    the repository for managing FoodRequest entities
   * @param clientProfileRepository  the repository for managing ClientProfile entities
   * @param accountProfileRepository the repository for managing AccountProfile entities
   * @param foodListingRepository    the repository for managing FoodListing entities
   */
  public FoodRequestController(FoodRequestRepository foodRequestRepository,
      ClientProfileRepository clientProfileRepository,
      AccountProfileRepository accountProfileRepository,
      FoodListingRepository foodListingRepository) {
    this.foodRequestRepository = foodRequestRepository;
    this.clientProfileRepository = clientProfileRepository;
    this.accountProfileRepository = accountProfileRepository;
    this.foodListingRepository = foodListingRepository;
  }

  /**
   * Creates a new FoodRequest with the provided clientId, accountId, listingId, and
   * quantityRequested.
   *
   * @param clientId          the ID of the client making the request
   * @param accountId         the ID of the account making the request
   * @param listingId         the ID of the food listing being requested
   * @param quantityRequested the quantity of food requested
   * @return a ResponseEntity containing the created FoodRequest and HTTP status code
   */
  @PostMapping("/create")
  public ResponseEntity<FoodRequest> createFoodRequest(
      @RequestParam int clientId,
      @RequestParam int accountId,
      @RequestParam int listingId,
      @RequestParam int quantityRequested) {

    Optional<ClientProfile> clientOptional = clientProfileRepository.findById(clientId);
    Optional<AccountProfile> accountOptional = accountProfileRepository.findById(accountId);
    Optional<FoodListing> listingOptional = foodListingRepository.findById(listingId);

    if (!clientOptional.isPresent()
        || !accountOptional.isPresent()
        || !listingOptional.isPresent()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    ClientProfile client = clientOptional.get();
    AccountProfile account = accountOptional.get();
    FoodListing foodListing = listingOptional.get();

    FoodRequest foodRequest = new FoodRequest(client, account, foodListing, quantityRequested);
    foodRequestRepository.save(foodRequest);

    return new ResponseEntity<>(foodRequest, HttpStatus.CREATED);
  }


  /**
   * Retrieves a FoodRequest by its requestId.
   *
   * @param requestId the ID of the food request to retrieve
   * @return a ResponseEntity containing the FoodRequest details if found, or an error message and
   *     HTTP status code if not found
   */
  @GetMapping("/get")
  public ResponseEntity<?> getFoodRequest(@RequestParam int requestId) {
    try {
      Optional<FoodRequest> foodRequestOptional = foodRequestRepository.findById(requestId);
      if (!foodRequestOptional.isPresent()) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Food request ID not found.");
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
      }
      FoodRequest foodRequest = foodRequestOptional.get();
      Map<String, Object> body = new HashMap<>();
      body.put("request_id", foodRequest.getRequestId());
      body.put("client_id", foodRequest.getClient().getClientId());
      body.put("account_id", foodRequest.getAccountId().getAccountId());
      body.put("listing_id", foodRequest.getListing().getListingId());
      body.put("quantity_requested", foodRequest.getQuantityRequested());
      body.put("request_time", foodRequest.getRequestTime());
      return new ResponseEntity<>(body, HttpStatus.OK);
    } catch (Exception e) {
      Map<String, Object> errorBody = new HashMap<>();
      errorBody.put("error", "Error while processing the request.");
      errorBody.put("details", e.getMessage());
      return new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * TODO: Fill this in.
   */
  @GetMapping("/getUnderAccount")
  public ResponseEntity<?> getFoodRequestsUnderAccount(@RequestParam int clientId, @RequestParam int accountId) {

    Optional<ClientProfile> clientOptional = clientProfileRepository.findById(clientId);
    Optional<AccountProfile> accountOptional = accountProfileRepository.findById(accountId);
    if (clientOptional.isEmpty() || accountOptional.isEmpty()) {
      Map<String, Object> body = new HashMap<>();
      body.put("error", "Client ID or account ID not found.");
      return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    ClientProfile client = clientOptional.get();
    AccountProfile account = accountOptional.get();
    List<FoodRequest> accountListings = foodRequestRepository.findByClientAndAccount(client,
        account);
    if (!accountListings.isEmpty()) {
      return ResponseEntity.ok().body(accountListings);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Updates the quantityRequested of an existing FoodRequest.
   *
   * @param requestId         the ID of the food request to update
   * @param quantityRequested the new quantity of food requested
   * @return ResponseEntity containing a success message and HTTP status code, or an error message
   *     and HTTP status code if the request ID was not found
   */
  @PutMapping("/update")
  public ResponseEntity<?> updateFoodRequest(
      @RequestParam int requestId,
      @RequestParam int quantityRequested) {

    Optional<FoodRequest> foodRequestOptional = foodRequestRepository.findById(requestId);
    if (!foodRequestOptional.isPresent()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    FoodRequest foodRequest = foodRequestOptional.get();
    foodRequest.setQuantityRequested(quantityRequested);
    foodRequestRepository.save(foodRequest);
    Map<String, Object> body = new HashMap<>();
    body.put("message", "Updated Successfully.");
    return new ResponseEntity<>(body, HttpStatus.OK);
  }
}
