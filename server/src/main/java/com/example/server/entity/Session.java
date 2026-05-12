package com.example.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")

@Getter
@Setter
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // tell spring/jpa to auto generate id
    private long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // ref
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @PrePersist
    public void onCreate(){// execute automatically before entity is first saved to db
        createdAt= LocalDateTime.now();
        expiresAt = LocalDateTime.now().plusDays(30);
    }
}
