package dev.coms4156.project.teamproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.coms4156.project.teamproject.model.AccountProfile;
import dev.coms4156.project.teamproject.model.ClientProfile;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Unit tests for the AccountProfile class.
 *
 * <p>These tests validate the behavior of AccountProfile-related API operations.
 */
@SpringBootTest
@ContextConfiguration
public class AccountProfileUnitTests {

  @Test
  public void accountProfileInvalidPhoneNumberTest1() {
    assertThrows(IllegalArgumentException.class, () -> {
      ClientProfile client = new ClientProfile();
      new AccountProfile(client, AccountProfile.AccountType.PROVIDER,
          "112389288a", "Mikey D's");
    });
  }

  @Test
  public void accountProfileInvalidPhoneNumberTest2() {
    // More than 11 digits
    assertThrows(IllegalArgumentException.class, () -> {
      ClientProfile client = new ClientProfile();
      new AccountProfile(client, AccountProfile.AccountType.PROVIDER,
          "112389111333333", "Mikey D's");
    });
    // Less than 10 digits
    assertThrows(IllegalArgumentException.class, () -> {
      ClientProfile client = new ClientProfile();
      new AccountProfile(client, AccountProfile.AccountType.PROVIDER,
          "112389", "Mikey D's");
    });
  }

  @Test
  public void accountProfileEmptyNameTest() {
    assertThrows(IllegalArgumentException.class, () -> {
      ClientProfile client = new ClientProfile();
      new AccountProfile(client, AccountProfile.AccountType.PROVIDER,
          "1123331929", "");
    });
  }

  @Test
  public void accountProfileOkTest() {
    ClientProfile client = new ClientProfile();
    AccountProfile account = new AccountProfile(client, AccountProfile.AccountType.PROVIDER,
        "1123331929", "Mikey D's");
    assertEquals("1123331929", account.getPhoneNumber());
    assertEquals(AccountProfile.AccountType.PROVIDER, account.getAccountType());
    assertEquals("Mikey D's", account.getName());
  }
}
