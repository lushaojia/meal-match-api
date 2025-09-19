package dev.coms4156.project.teamproject.repository;

import dev.coms4156.project.teamproject.model.ClientProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link ClientProfile} entities. Provides CRUD operations for
 * client profiles.
 */
@Repository
public interface ClientProfileRepository extends JpaRepository<ClientProfile, Integer> {

}