package dev.coms4156.project.teamproject.repository;

import dev.coms4156.project.teamproject.model.AccountProfile;
import dev.coms4156.project.teamproject.model.ClientProfile;
import dev.coms4156.project.teamproject.model.FoodListing;
import dev.coms4156.project.teamproject.model.FoodRequest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link FoodRequest} entities. Provides methods for querying
 * food requests based on client ID and food listing.
 */
@Repository
public interface FoodRequestRepository extends JpaRepository<FoodRequest, Integer> {

  List<FoodRequest> findByClientAndFoodListing(ClientProfile client, FoodListing foodListing);

  List<FoodRequest> findByClientAndAccount(ClientProfile client, AccountProfile account);
}
