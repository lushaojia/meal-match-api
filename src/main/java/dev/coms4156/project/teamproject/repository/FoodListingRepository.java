package dev.coms4156.project.teamproject.repository;

import dev.coms4156.project.teamproject.model.AccountProfile;
import dev.coms4156.project.teamproject.model.ClientProfile;
import dev.coms4156.project.teamproject.model.FoodListing;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link FoodListing} entities. Provides methods to retrieve food
 * listings based on client, account, and listing ID.
 */
@Repository
public interface FoodListingRepository extends JpaRepository<FoodListing, Integer> {

  List<FoodListing> findByClient(ClientProfile client);

  List<FoodListing> findByClientAndAccount(ClientProfile client, AccountProfile account);

  Optional<FoodListing> findByClientAndAccountAndListingId(ClientProfile client,
      AccountProfile account,
      int listingId);

  Optional<FoodListing> findByClientAndListingId(ClientProfile client, int listingId);
}
