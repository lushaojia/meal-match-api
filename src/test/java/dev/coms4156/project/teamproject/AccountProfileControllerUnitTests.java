package dev.coms4156.project.teamproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import dev.coms4156.project.teamproject.controller.AccountProfileController;
import dev.coms4156.project.teamproject.model.AccountProfile;
import dev.coms4156.project.teamproject.model.ClientProfile;
import dev.coms4156.project.teamproject.repository.AccountProfileRepository;
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
 * Tests for AccountProfileController.
 */
@SpringBootTest
@ActiveProfiles("test")
public class AccountProfileControllerUnitTests {

  @Autowired
  private AccountProfileController accountProfileController;

  @MockBean
  private ClientProfileRepository clientProfileRepository;

  @MockBean
  private AccountProfileRepository accountProfileRepository;

  private static final ClientProfile client = new ClientProfile();

  @Test
  public void createAccountProfileOkTest() {
    when(clientProfileRepository.findById(client.getClientId())).thenReturn(Optional.of(client));
    ResponseEntity<?> response = accountProfileController.createAccountProfile(
        client.getClientId(), AccountProfile.AccountType.RECIPIENT,
        "1234567890", "test");
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }

  @Test
  public void createAccountProfileClientNotFoundTest() {
    ResponseEntity<?> response = accountProfileController.createAccountProfile(
        1, AccountProfile.AccountType.RECIPIENT,
        "1234567890", "test");
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void getAccountProfileOkTest() {
    AccountProfile acct = new AccountProfile(client, AccountProfile.AccountType.PROVIDER,
        "1827472919", "if you're reading this, remember to drink water!");

    when(accountProfileRepository.findById(acct.getAccountId())).thenReturn(Optional.of(acct));
    ResponseEntity<?> response = accountProfileController.getAccountProfile(acct.getAccountId());
    assertEquals(HttpStatus.OK, response.getStatusCode());
    if (response.getBody() instanceof HashMap<?, ?>) {
      @SuppressWarnings("unchecked")
      HashMap<String, Object> responseAcct = (HashMap<String, Object>) response.getBody();
      assertEquals(acct.getAccountId(), responseAcct.get("account_id"));
      assertEquals("if you're reading this, remember to drink water!",
          responseAcct.get("name"));
    } else {
      fail();
    }
  }

  @Test
  public void getAccountProfileNotFoundTest() {
    ResponseEntity<?> response = accountProfileController.getAccountProfile(1);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }
}

