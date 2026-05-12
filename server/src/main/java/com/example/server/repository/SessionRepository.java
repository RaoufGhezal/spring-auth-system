package com.example.server.repository;

import com.example.server.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findById(Long id);
    void deleteById(Long id);
}
