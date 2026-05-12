package com.example.server.repository;

import com.example.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> { // Long: type of the entity primary key
    Optional<User> findById(Long id);
Optional<User> findByEmail(String email);
}

//// FIND
//List<User> findAll();
//
//Optional<User> findById(Long id);
//
//List<User> findByName(String name);
//
//List<User> findByNameContaining(String keyword);
//
//Optional<User> findByEmail(String email);
//
//List<User> findByEmailContaining(String keyword);
//
//List<User> findByNameAndEmail(String name, String email);
//
//List<User> findByNameOrEmail(String name, String email);
//
//List<User> findByNameStartingWith(String prefix);
//
//List<User> findByNameEndingWith(String suffix);
//
//List<User> findByNameIgnoreCase(String name);
//
//List<User> findByNameOrderByIdDesc(String name);
//
//List<User> findTop5ByOrderByIdDesc();
//
//boolean existsByEmail(String email);
//
//long countByName(String name);
//
//// DELETE
//void deleteByEmail(String email);
//
//long deleteByName(String name);
