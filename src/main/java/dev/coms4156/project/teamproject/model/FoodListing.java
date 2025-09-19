package dev.coms4156.project.teamproject.model;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;


/**
 * Represents a Food Listing.
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = "food_listing")
public class FoodListing implements Serializable {

  @Serial
  private static final long serialVersionUID = 123456L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "listing_id", unique = true)
  private int listingId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "client_id", nullable = false)
  private ClientProfile client;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "account_id", nullable = false)
  private AccountProfile account;

  @Column(name = "food_type")
  private String foodType;

  @Column(name = "quantity")
  private int quantityListed;

  @Column(name = "earliest_pickup")
  private LocalDateTime earliestPickUpTime;

  @Column(name = "latitude")
  private float latitude;

  @Column(name = "longitude")
  private float longitude;

  /**
   * Default constructor for the FoodListing class. This constructor is required by JPA for object
   * instantiation. We suppress the warning since the PMD default ruleset does not comply with
   * this.
   */
  @SuppressWarnings("PMD.UncommentedEmptyConstructor")
  public FoodListing() {
  }

  /**
   * Constructs a food listing object with the given parameters.
   *
   * @param client             ClientProfile
   * @param account            account of the provider who listed the food
   * @param foodType           Type of food
   * @param quantityListed     Quantity of food in bags
   * @param earliestPickUpTime Earliest pick up time for the food listing
   * @param latitude           latitude of the pick up location
   * @param longitude          longitude of the pick up location
   */
  public FoodListing(ClientProfile client, AccountProfile account, String foodType,
      int quantityListed,
      LocalDateTime earliestPickUpTime, float latitude, float longitude) {
    this.client = client;
    this.account = account;
    this.foodType = foodType;
    this.quantityListed = quantityListed;
    this.earliestPickUpTime = earliestPickUpTime;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  /**
   * Gets the food type.
   *
   * @return The food type.
   */
  public String getFoodType() {
    return this.foodType;
  }

  /**
   * Sets the food type.
   *
   * @param foodType The new food type.
   */
  public void setFoodType(String foodType) {
    this.foodType = foodType;
  }

  /**
   * Gets the quantity listed.
   *
   * @return The quantity listed
   */
  public int getQuantityListed() {
    return this.quantityListed;
  }

  /**
   * Sets the quantity listed.
   *
   * @param quantityListed The new quantity listed.
   */
  public void setQuantityListed(int quantityListed) {
    this.quantityListed = quantityListed;
  }

  /**
   * Gets earliest pick-up time.
   *
   * @return The earliest pick-up time
   */
  public LocalDateTime getEarliestPickUpTime() {
    return this.earliestPickUpTime;
  }

  /**
   * Sets the earliest pick-up time.
   *
   * @param earliestPickUpTime The new earliest pick-up time
   */
  public void setEarliestPickUpTime(LocalDateTime earliestPickUpTime) {
    this.earliestPickUpTime = earliestPickUpTime;
  }

  /**
   * Gets the latitude.
   *
   * @return The latitude.
   */
  public float getLatitude() {
    return this.latitude;
  }

  /**
   * Sets the latitude.
   *
   * @param latitude The new latitude
   */
  public void setLatitude(float latitude) {
    this.latitude = latitude;
  }

  /**
   * Gets the longitude.
   *
   * @return The longitude.
   */
  public float getLongitude() {
    return this.longitude;
  }

  /**
   * Sets the longitude.
   *
   * @param longitude The new longitude
   */
  public void setLongitude(float longitude) {
    this.longitude = longitude;
  }

  /**
   * Gets the listing ID.
   *
   * @return The listing ID.
   */
  public int getListingId() {
    return this.listingId;
  }

  /**
   * Returns whether two FoodListing objects are equal based on their attributes.
   *
   * @param other The other FoodListing object
   * @return Whether this FoodListing is equal to the other FoodListing
   */
  @Override
  public boolean equals(Object other) {
    if (!(other instanceof FoodListing otherListing)) {
      return false;
    }

    return ((FoodListing) other).getFoodType().equals(this.foodType)
        && this.quantityListed == otherListing.getQuantityListed()
        && this.earliestPickUpTime.toString()
        .equals(otherListing.getEarliestPickUpTime().toString())
        && this.latitude == otherListing.getLatitude()
        && this.longitude == otherListing.getLongitude();
  }

  /**
   * Computes a hash of this FoodListing object based on its attributes.
   *
   * @return A hash of this FoodListing object
   */
  @Override
  public int hashCode() {
    return Objects.hash(client, account, foodType, quantityListed, earliestPickUpTime,
        latitude, longitude);
  }
}
