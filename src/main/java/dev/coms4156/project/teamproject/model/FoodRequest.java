package dev.coms4156.project.teamproject.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a food request made by an account (user) of a client (app) for a specific food
 * listing. A food request includes information about the account making the request, the food
 * listing, the quantity requested, and the request and pickup times.
 */
@Entity
public class FoodRequest implements Serializable {

  @Serial
  private static final long serialVersionUID = 345678L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "request_id", unique = true)
  private int requestId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "client_id", nullable = false)
  private ClientProfile client;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "account_id", nullable = false)
  private AccountProfile account;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "listing_id", nullable = false)
  private FoodListing foodListing;

  private int quantityRequested;
  private LocalDateTime requestTime;

  /**
   * Default constructor for the FoodListing class. This constructor is required by JPA for object
   * instantiation. We suppress the warning since the PMD default ruleset does not comply with
   * this.
   */
  @SuppressWarnings("PMD.UncommentedEmptyConstructor")
  public FoodRequest() {
  }

  /**
   * Constructs a new FoodRequest object.
   *
   * @param client            client for whom this account is being created
   * @param account           the account (user) making the request
   * @param foodListing       the food listing being requested
   * @param quantityRequested Quantity of the food requested
   */
  public FoodRequest(ClientProfile client, AccountProfile account, FoodListing foodListing,
      int quantityRequested) {
    this.client = client;
    this.account = account;
    this.foodListing = foodListing;
    this.quantityRequested = quantityRequested;
    this.requestTime = LocalDateTime.now();  // Automatically set to current time
  }

  /**
   * Returns the ID of this food request.
   *
   * @return the food request ID
   */
  public int getRequestId() {
    return requestId;
  }

  /**
   * Returns the food listing associated with this request.
   *
   * @return the food listing
   */
  public FoodListing getListing() {
    return foodListing;
  }

  /**
   * Returns the account making this request.
   *
   * @return the account associated with the request
   */
  public AccountProfile getAccountId() {
    return account;
  }

  /**
   * Returns the client making this request.
   *
   * @return the client associated with the request
   */
  public ClientProfile getClient() {
    return client;
  }


  /**
   * Returns the quantity of food requested.
   *
   * @return the quantity requested
   */
  public int getQuantityRequested() {
    return quantityRequested;
  }

  /**
   * Sets the quantity of food requested.
   *
   * @param quantityRequested the quantity to set
   */
  public void setQuantityRequested(int quantityRequested) {
    this.quantityRequested = quantityRequested;
  }

  /**
   * Returns the time the request was made.
   *
   * @return the request time
   */
  public LocalDateTime getRequestTime() {
    return requestTime;
  }

  /**
   * Compares this FoodRequest to another object for equality based on the request ID, food listing,
   * and request details.
   *
   * @param other the object to compare with
   * @return true if the two objects are equal, false otherwise
   */
  @Override
  public boolean equals(Object other) {
    if (!(other instanceof FoodRequest otherListing)) {
      return false;
    }

    return this.requestId == otherListing.getRequestId()
        && this.foodListing.equals(otherListing.getListing())
        && this.quantityRequested == otherListing.getQuantityRequested()
        && this.requestTime.toString().equals(otherListing.getRequestTime().toString());
  }

  /**
   * Generates a hash code for this FoodRequest based on its fields.
   *
   * @return the hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(requestId, client, account, quantityRequested, requestTime);
  }
}
