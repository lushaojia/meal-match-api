package dev.coms4156.project.teamproject.controller;

import dev.coms4156.project.teamproject.model.ClientProfile;
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
 * Controller that provides API endpoints for managing client profiles.
 *
 * <p>This controller handles creating a new client profile and retrieving client profile details by
 * client ID.
 */
@RestController
@RequestMapping("/api/clientProfiles")
public class ClientProfileController {

  @Autowired
  private ClientProfileRepository clientProfileRepository;

  /**
   * Creates a new client profile.
   *
   * <p>This endpoint is used to create a new client profile with a unique ID.
   *
   * @return a ResponseEntity containing the created ClientProfile and HTTP status code 201
   */
  @PostMapping("/create")
  public ResponseEntity<ClientProfile> createClientProfile() {
    ClientProfile client = new ClientProfile();
    clientProfileRepository.save(client);
    return new ResponseEntity<>(client, HttpStatus.CREATED);
  }

  /**
   * Retrieves a client profile by client ID.
   *
   * <p>This endpoint fetches the details of a client profile using the provided client ID.
   *
   * @param clientId the ID of the client profile to retrieve
   * @return a ResponseEntity containing the client profile details and HTTP status code 200, or an
   *     error message and HTTP status code 404 if the client ID is not found
   */
  @GetMapping("/get")
  public ResponseEntity<?> getClientProfile(@RequestParam int clientId) {
    Optional<ClientProfile> clientOptional = clientProfileRepository.findById(clientId);
    if (!clientOptional.isPresent()) {
      Map<String, Object> body = new HashMap<>();
      body.put("error", "Client ID not found.");
      return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    ClientProfile client = clientOptional.get();
    Map<String, Object> body = new HashMap<>();
    body.put("client_id", client.getClientId());
    return new ResponseEntity<>(body, HttpStatus.OK);
  }
}
