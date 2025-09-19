package dev.coms4156.project.teamproject.controller;

import dev.coms4156.project.teamproject.model.AccountProfile;
import dev.coms4156.project.teamproject.model.ClientProfile;
import dev.coms4156.project.teamproject.model.FoodListing;
import dev.coms4156.project.teamproject.model.FoodRequest;
import dev.coms4156.project.teamproject.model.Location;
import dev.coms4156.project.teamproject.repository.AccountProfileRepository;
import dev.coms4156.project.teamproject.repository.ClientProfileRepository;
import dev.coms4156.project.teamproject.repository.FoodListingRepository;
import dev.coms4156.project.teamproject.repository.FoodRequestRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class contains all the API routes for the system.
 */
@RestController
public class FoodListingController {

  @Autowired
  private FoodListingRepository foodListingRepository;
  @Autowired
  private ClientProfileRepository clientProfileRepository;
  @Autowired
  private AccountProfileRepository accountProfileRepository;
  @Autowired
  private FoodRequestRepository foodRequestRepository;

  /**
   * API endpoint to create a new food listing under the account with `accountId` in the client with
   * `clientId`. Expects that the specified account exists.
   *
   * @param clientId       ID of the client
   * @param accountId      ID of the account creating the listing
   * @param foodType       Type of the food
   * @param quantityListed Quantity of food available
   * @param latitude       Latitude of the food's location
   * @param longitude      Longitude of the food's location
   * @return If there is no account with `accountId` in the client with `clientId`, returns a
   *     ResponseEntity with status code NOT_FOUND and a corresponding error message.
   *     If the listing was successfully created and saved to the database, returns with status
   *     code OK and a message with the ID of the listing. Otherwise, returns with status code
   *     INTERNAL_SERVER_ERROR and a corresponding message.
   */
  @PostMapping("/createFoodListing")
  public ResponseEntity<?> createFoodListing(
      @RequestParam int clientId, @RequestParam int accountId,
      @RequestParam String foodType, @RequestParam int quantityListed,
      @RequestParam float latitude, @RequestParam float longitude) {

    Optional<ClientProfile> clientOptional = clientProfileRepository.findById(clientId);
    Optional<AccountProfile> accountOptional = accountProfileRepository.findById(accountId);
    if (clientOptional.isEmpty() || accountOptional.isEmpty()) {
      Map<String, Object> body = new HashMap<>();
      body.put("error", "Client ID or account ID not found.");
      return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    ClientProfile client = clientOptional.get();
    AccountProfile account = accountOptional.get();
    FoodListing foodListing = new FoodListing(client, account, foodType, quantityListed,
        LocalDateTime.now(), latitude, longitude);

    FoodListing savedFoodListing = foodListingRepository.save(foodListing);
    if (savedFoodListing.getFoodType().equals(foodType)
        && savedFoodListing.getQuantityListed() == quantityListed
        && savedFoodListing.getLatitude() == latitude
        && savedFoodListing.getLongitude() == longitude) {
      return ResponseEntity.status(HttpStatus.CREATED)
          .body("Food listing created successfully with ID: "
              + savedFoodListing.getListingId());
    } else {
      return ResponseEntity.internalServerError().body("Failed to create food listing");
    }
  }

  /**
   * API endpoint to get all food listings in a client with `clientId`.
   *
   * @param clientId ID of the client
   * @return If there is no client with `clientId`, returns a ResponseEntity with status code
   *     NOT_FOUND and a corresponding error message. If there is at least one listing in the
   *     specified client, returns with status code OK and a collection of such listings. Otherwise,
   *     returns with status code NOT_FOUND.
   */
  @GetMapping("/getFoodListings")
  public ResponseEntity<?> getFoodListings(@RequestParam int clientId) {
    Optional<ClientProfile> clientOptional = clientProfileRepository.findById(clientId);
    if (clientOptional.isEmpty()) {
      Map<String, Object> body = new HashMap<>();
      body.put("error", "Client ID not found.");
      return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    ClientProfile client = clientOptional.get();
    List<FoodListing> listings = foodListingRepository.findByClient(client);
    if (!listings.isEmpty()) {
      return ResponseEntity.ok().body(listings);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * API endpoint for getting food listings in the client with `clientId` with pick-up locations
   * within `maxDistance` of (`latitude`, `longitude`).
   *
   * @param clientId    ID of client
   * @param latitude    Latitude of the query location
   * @param longitude   Longitude of the query location
   * @param maxDistance An optional parameter for the maximum distance from the specified location
   *                    to consider when searching for food listings; expected to be greater than
   *                    0.
   * @return If there is no client with `clientId`, returns a ResponseEntity with status code
   *     NOT_FOUND and a corresponding error message. If there is at least one listing within the
   *     specified distance of the specified location, returns with status code OK and a
   *     collection of such listings. Otherwise, returns with status code NOT_FOUND.
   */
  @GetMapping("/getNearbyListings")
  public ResponseEntity<?> getNearbyListings(@RequestParam int clientId,
      @RequestParam float latitude, @RequestParam float longitude,
      @RequestParam(required = false, defaultValue = "5") int maxDistance) {

    Optional<ClientProfile> clientOptional = clientProfileRepository.findById(clientId);
    if (clientOptional.isEmpty()) {
      Map<String, Object> body = new HashMap<>();
      body.put("error", "Client ID not found.");
      return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    ClientProfile client = clientOptional.get();
    List<FoodListing> allListings = foodListingRepository.findByClient(client);

    // Then find listings within `maxDistance` of query location
    List<FoodListing> nearbyListings = new ArrayList<>();
    Location queryLocation = new Location(latitude, longitude);

    for (FoodListing listing : allListings) {
      Location listingLocation = new Location(listing.getLatitude(), listing.getLongitude());
      if (queryLocation.distance(listingLocation) <= maxDistance) {
        nearbyListings.add(listing);
      }
    }

    if (!nearbyListings.isEmpty()) {
      return ResponseEntity.ok().body(nearbyListings);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * API endpoint to get all food listings under an account with `accountId` in the client with
   * `clientId`.
   *
   * @param clientId  ID of the client
   * @param accountId ID of the account trying to fetch all their listings
   * @return If there is no account with `accountId` in the client with `clientId`, returns a
   *     ResponseEntity with status code NOT_FOUND. If the account with `accountId` is not of type
   *     `AccountType.PROVIDER`, returns with status code UNAUTHORIZED. If there is at least one
   *     listing under the specified account, returns with status code OK and a collection of such
   *     listings. Otherwise, returns with status code NOT_FOUND.
   */
  @GetMapping("/getFoodListingsUnderAccount")
  public ResponseEntity<?> getFoodListingsUnderAccount(@RequestParam int clientId,
      @RequestParam int accountId) {

    Optional<ClientProfile> clientOptional = clientProfileRepository.findById(clientId);
    Optional<AccountProfile> accountOptional = accountProfileRepository.findById(accountId);
    if (clientOptional.isEmpty() || accountOptional.isEmpty()) {
      Map<String, Object> body = new HashMap<>();
      body.put("error", "Client ID or account ID not found.");
      return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    ClientProfile client = clientOptional.get();
    AccountProfile account = accountOptional.get();
    List<FoodListing> accountListings = foodListingRepository.findByClientAndAccount(client,
        account);
    if (!accountListings.isEmpty()) {
      return ResponseEntity.ok().body(accountListings);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * API endpoint for fetching the requests made for a listing with `listingId` under a provider
   * account with `accountId` in the client with `clientId`.
   *
   * @param clientId  ID of the client
   * @param accountId ID of the provider account trying to get the requests for a listing
   * @param listingId ID of the listing to find requests for
   * @return If there is no listing with `listingId` under an account with `accountId` in the client
   *     with `clientId`, returns a ResponseEntity with status code NOT_FOUND. If the account with
   *     `accountId` is not of type `AccountType.PROVIDER`, returns with status code UNAUTHORIZED.
   *     If at least one request has been made for the specified listing, returns with status code
   *     OK and a collection of such requests. Otherwise, returns with status code NOT_FOUND.
   */
  @GetMapping("/getRequestsForListing")
  public ResponseEntity<?> getRequestsForListing(
      @RequestParam int clientId, @RequestParam int accountId, @RequestParam int listingId) {

    Optional<ClientProfile> clientOptional = clientProfileRepository.findById(clientId);
    Optional<AccountProfile> accountOptional = accountProfileRepository.findById(accountId);
    if (clientOptional.isEmpty() || accountOptional.isEmpty()) {
      Map<String, Object> body = new HashMap<>();
      body.put("error", "Client ID or account ID not found.");
      return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    ClientProfile client = clientOptional.get();
    AccountProfile account = accountOptional.get();

    // Only a provider should be calling this endpoint
    if (account.getAccountType() != AccountProfile.AccountType.PROVIDER) {
      Map<String, Object> body = new HashMap<>();
      body.put("error", "Expected account holder to be a PROVIDER.");
      return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    // Find the listing that the provider wishes to see all requests for
    Optional<FoodListing> listingOptional =
        foodListingRepository.findByClientAndAccountAndListingId(
            client, account, listingId);

    if (listingOptional.isEmpty()) {
      Map<String, Object> body = new HashMap<>();
      body.put("error", "Listing with ID " + listingId + " not found"
          + "under client with ID " + clientId + " and account with ID " + accountId + ".");
      return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    FoodListing listing = listingOptional.get();
    // Find requests for this listing under the same client
    List<FoodRequest> requestsForListing =
        foodRequestRepository.findByClientAndFoodListing(client, listing);

    if (!requestsForListing.isEmpty()) {
      return ResponseEntity.ok().body(requestsForListing);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * API endpoint for fulfilling a request for the listing with `listingId` under a provider account
   * in the client with `clientId`. Updates the listing by decrementing `quantityListed` by
   * `quantityRequested`. If `quantityRequested` is not specified, its default value is 1. If the
   * current `quantityListed` is less than`quantityRequested`, returns a response with status code
   * BAD_REQUEST.
   *
   * @param clientId  ID of the client
   * @return If there is no listing with `listingId` under an account with `accountId` in the client
   *     with `clientId`, returns a ResponseEntity with status code NOT_FOUND. If the account with
   *     `accountId` is not of type `AccountType.PROVIDER`, returns with status code UNAUTHORIZED.
   *     If the request was fulfilled and the listing was updated, returns a ResponseEntity with
   *     status code OK. Otherwise, returns with status code BAD_REQUEST.
   */
  @PatchMapping("/fulfillRequest")
  public ResponseEntity<?> fulfillRequest(@RequestParam int clientId, @RequestParam int listingId,
                                          @RequestParam int quantityRequested) {
    // Throw error if quantity requested is negative
    if (quantityRequested <= 0) {
      Map<String, Object> body = new HashMap<>();
      body.put("error", "Cannot request negative quantity.");
      return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Fetch client and account data from database
    Optional<ClientProfile> clientOptional = clientProfileRepository.findById(clientId);
    if (clientOptional.isEmpty()) {
      Map<String, Object> body = new HashMap<>();
      body.put("error", "Client ID not found.");
      return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    ClientProfile client = clientOptional.get();

    // Find the listing that will satisfy a request
    Optional<FoodListing> listingOptional =
        foodListingRepository.findByClientAndListingId(client, listingId);
    if (listingOptional.isEmpty()) {
      Map<String, Object> body = new HashMap<>();
      body.put("error", "Listing with ID " + listingId + " not found "
          + "under client with ID " + clientId + ".");
      return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // Do the actual update now that we found the correct listing
    FoodListing listing = listingOptional.get();
    int currQuantityListed = listing.getQuantityListed();
    if (currQuantityListed < quantityRequested) {
      Map<String, Object> body = new HashMap<>();
      body.put("error", "Quantity listed (" + currQuantityListed + ") for listing with ID "
          + listingId + " cannot satisfy quantity requested (" + quantityRequested + ").");
      return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    int newQuantityListed = currQuantityListed - quantityRequested;
    listing.setQuantityListed(newQuantityListed);
    foodListingRepository.save(listing);

    Map<String, Object> body = new HashMap<>();
    body.put("message", "Updated Successfully.");
    return new ResponseEntity<>(body, HttpStatus.OK);
  }

  /**
   * API endpoint for updating the food type, latitude, longitude, and/or quantity listed of a
   * listing with `listingId` under an account with `accountId` in the client with `clientId`.
   * Expects account to be of type `AccountType.PROVIDER`.
   *
   * @param clientId          ID of the client
   * @param accountId         ID of the account in the client
   * @param listingId         ID of the listing to update
   * @param newFoodType       An optional parameter for the new food type
   * @param newLatitude       An optional parameter for the new latitude
   * @param newLongitude      An optional parameter for the new longitude
   * @param newQuantityListed An optional parameter for the new quantity listed
   * @return If there is no listing with `listingId` under an account with `accountId` in the client
   *     with `clientId`, returns a ResponseEntity with status code NOT_FOUND. If the account with
   *     `accountId` is not of type `AccountType.PROVIDER`, returns with status code UNAUTHORIZED.
   *     If the listing was successfully updated, returns with status code OK and a corresponding
   *     message.
   */
  @PatchMapping("/updateFoodListing")
  public ResponseEntity<?> updateFoodListing(
      @RequestParam int clientId, @RequestParam int accountId, @RequestParam int listingId,
      @RequestParam(required = false) String newFoodType,
      @RequestParam(required = false) Float newLatitude,
      @RequestParam(required = false) Float newLongitude,
      @RequestParam(required = false) Integer newQuantityListed
  ) {

    // Fetch client and account data from database
    Optional<ClientProfile> clientOptional = clientProfileRepository.findById(clientId);
    Optional<AccountProfile> accountOptional = accountProfileRepository.findById(accountId);
    if (clientOptional.isEmpty() || accountOptional.isEmpty()) {
      Map<String, Object> body = new HashMap<>();
      body.put("error", "Client ID or account ID not found.");
      return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    ClientProfile client = clientOptional.get();
    AccountProfile account = accountOptional.get();

    // Only a provider should be calling this endpoint
    if (account.getAccountType() != AccountProfile.AccountType.PROVIDER) {
      Map<String, Object> body = new HashMap<>();
      body.put("error", "Expected account holder to be a PROVIDER.");
      return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    // Find the specified listing
    Optional<FoodListing> listingOptional =
        foodListingRepository.findByClientAndAccountAndListingId(client, account, listingId);
    if (listingOptional.isEmpty()) {
      Map<String, Object> body = new HashMap<>();
      body.put("error", "Listing with ID " + listingId + " not found "
          + "under client with ID " + clientId + " and account with ID " + accountId + ".");
      return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    FoodListing listing = listingOptional.get();
    if (newFoodType != null) {
      listing.setFoodType(newFoodType);
    }
    if (newLatitude != null) {
      listing.setLatitude(newLatitude);
    }
    if (newLongitude != null) {
      listing.setLongitude(newLongitude);
    }
    if (newQuantityListed != null) {
      listing.setQuantityListed(newQuantityListed);
    }

    foodListingRepository.save(listing);
    Map<String, Object> body = new HashMap<>();
    body.put("message", "Updated Successfully.");
    return new ResponseEntity<>(body, HttpStatus.OK);
  }


  /**
   * Redirects to the homepage.
   *
   * @return A String containing the name of the html file to be loaded.
   */
  @GetMapping({"/", "/index", "/home"})
  public @ResponseBody String index() {
    return "Welcome, in order to make an API call direct your browser or Postman to an "
        + "endpoint "
        + "\n\n This can be done using the following format: \n\n http:127.0.0"
        + ".1:8080/endpoint?arg=value";
  }

}
