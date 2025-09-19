package dev.coms4156.project.teamproject;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import dev.coms4156.project.teamproject.controller.ClientProfileController;
import dev.coms4156.project.teamproject.model.ClientProfile;
import dev.coms4156.project.teamproject.repository.ClientProfileRepository;
import java.util.HashMap;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

/**
 * Tests for ClientProfileController.
 */
@SpringBootTest
@ActiveProfiles("test")
public class ClientProfileControllerUnitTests {
  @Autowired
  private ClientProfileController clientProfileController;

  @MockBean
  private ClientProfileRepository clientProfileRepository;

  @Test
  public void createClientProfileOkTest() {
    ResponseEntity<?> response = clientProfileController.createClientProfile();
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }

  @Test
  public void getClientProfileOkTest() {
    ClientProfile client = new ClientProfile();
    when(clientProfileRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
    ResponseEntity<?> response = clientProfileController.getClientProfile(client.getClientId());
    assertEquals(HttpStatus.OK, response.getStatusCode());
    if (response.getBody() instanceof HashMap<?, ?>) {
      @SuppressWarnings("unchecked")
      HashMap<String, Object> responseAcct = (HashMap<String, Object>) response.getBody();
      assertEquals(client.getClientId(), responseAcct.get("client_id"));
    } else {
      fail();
    }
  }

  @Test
  public void getClientProfileNotFoundTest() {
    ClientProfile client = new ClientProfile();
    ResponseEntity<?> response = clientProfileController.getClientProfile(client.getClientId());
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }
}
