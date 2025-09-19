package dev.coms4156.project.teamproject.controller;

import dev.coms4156.project.teamproject.model.AccountProfile;
import dev.coms4156.project.teamproject.model.ClientProfile;
import dev.coms4156.project.teamproject.repository.AccountProfileRepository;
import dev.coms4156.project.teamproject.repository.ClientProfileRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing account profiles.
 *
 * <p>This controller provides API endpoints to create and retrieve
 * account profiles, which represent individual user accounts under a client.
 */
@RestController
@RequestMapping("/api/accountProfiles")
public class AccountProfileController {

  @Autowired
  private ClientProfileRepository clientProfileRepository;

  @Autowired
  private AccountProfileRepository accountProfileRepository;

  /**
   * Creates a new AccountProfile for a specific client.
   *
   * <p>This endpoint creates a new account profile associated with a client,
   * including details such as the account type, phone number, and name.
   *
   * @param clientId    the ID of the client associated with the account profile
   * @param accountType the type of the account (e.g., PROVIDER or RECIPIENT)
   * @param phoneNumber the phone number associated with the account
   * @param name        the name of the account holder
   * @return a ResponseEntity containing the created AccountProfile and HTTP status code 201, or a
   *     404 Not Found if the client ID is not found
   */
  @PostMapping("/create")
  public ResponseEntity<AccountProfile> createAccountProfile(
      @RequestParam int clientId,
      @RequestParam AccountProfile.AccountType accountType,
      @RequestParam String phoneNumber,
      @RequestParam String name) {

    Optional<ClientProfile> clientOptional = clientProfileRepository.findById(clientId);
    if (!clientOptional.isPresent()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    ClientProfile client = clientOptional.get();
    AccountProfile accountProfile = new AccountProfile(client, accountType, phoneNumber, name);
    accountProfileRepository.save(accountProfile);
    return new ResponseEntity<>(accountProfile, HttpStatus.CREATED);
  }

  /**
   * Retrieves an AccountProfile by account ID.
   *
   * <p>This endpoint fetches the details of an account profile using the provided account ID.
   *
   * @param accountId the ID of the account profile to retrieve
   * @return a ResponseEntity containing the account profile details and HTTP status code 200, or an
   *     error message and HTTP status code 404 if the account ID is not found
   */
  @GetMapping("/get")
  public ResponseEntity<?> getAccountProfile(@RequestParam int accountId) {
    Optional<AccountProfile> accountOptional = accountProfileRepository.findById(accountId);
    if (!accountOptional.isPresent()) {
      Map<String, Object> body = new HashMap<>();
      body.put("error", "Account ID not found.");
      return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    AccountProfile account = accountOptional.get();
    Map<String, Object> body = new HashMap<>();
    body.put("account_id", account.getAccountId());
    body.put("name", account.getName());
    return new ResponseEntity<>(body, HttpStatus.OK);
  }
}