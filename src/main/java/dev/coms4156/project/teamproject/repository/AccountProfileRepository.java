package dev.coms4156.project.teamproject.repository;

import dev.coms4156.project.teamproject.model.AccountProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link AccountProfile} entities. Provides CRUD operations for
 * account profiles.
 */
@Repository
public interface AccountProfileRepository extends JpaRepository<AccountProfile, Integer> {

}
