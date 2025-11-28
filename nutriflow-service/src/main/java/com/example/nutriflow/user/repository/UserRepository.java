package com.example.nutriflow.user.repository;

import com.example.nutriflow.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

     /**
     * Find a user by their user ID.
     *
     * @param userId the ID of the user
     * @return Optional containing the user if found, empty otherwise
     */
    @Query("SELECT u FROM User u WHERE u.userId = :userId")
    Optional<User> findUserById(Integer userId);

}
