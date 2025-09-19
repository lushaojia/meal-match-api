package dev.coms4156.project.teamproject.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;

/**
 * Represents an account profile for users of the service. An account can either be a provider
 * profile or a recipient profile.
 */
@Entity
public class AccountProfile implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "account_id", updatable = false, nullable = false)
  private int accountId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "client_id", nullable = false)
  private ClientProfile client;

  private AccountType accountType;
  private String phoneNumber;
  private String name;

  /**
   * Default constructor for the FoodListing class. This constructor is required by JPA for object
   * instantiation. We suppress the warning since the PMD default ruleset does not comply with
   * this.
   */
  @SuppressWarnings("PMD.UncommentedEmptyConstructor")
  public AccountProfile() {
  }

  /**
   * Constructs a new Account Profile object.
   *
   * @param client      client for whom this account is being created
   * @param accountType type of account (provider or recipient)
   * @param phoneNumber phone number for contact (validated for length)
   * @param name        name associated with the account
   */
  public AccountProfile(ClientProfile client, AccountType accountType,
      String phoneNumber, String name) {
    this.client = client;
    this.accountType = accountType;
    if (!phoneNumber.matches("\\d+")) {
      throw new IllegalArgumentException("Phone number must consist of numeric digits only.");
    }
    if (!(phoneNumber.length() == 10 || phoneNumber.length() == 11)) {
      throw new IllegalArgumentException("Phone number must be 10 or 11 digits.");
    }
    this.phoneNumber = phoneNumber;
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Name cannot be empty.");
    }
    this.name = name;
  }

  /**
   * Gets the account ID.
   *
   * @return The account ID.
   */
  public int getAccountId() {
    return accountId;
  }

  /**
   * Gets the account type (provider or recipient).
   *
   * @return The account type.
   */
  public AccountType getAccountType() {
    return accountType;
  }

  /**
   * Gets the phone number.
   *
   * @return The phone number.
   */
  public String getPhoneNumber() {
    return phoneNumber;
  }

  /**
   * Gets the name.
   *
   * @return The name.
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the client.
   *
   * @return The client.
   */
  public ClientProfile getClient() {
    return client;
  }

  /**
   * Enum for specifying whether the account is a food provider or a recipient.
   */
  public enum AccountType {
    PROVIDER,
    RECIPIENT
  }
}
