package com.example.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")

@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // tell spring/jpa to auto generate id
    private long id;

//    @Column(name = "username", nullable = false)
//    private String username;

    @Column(name = "full_name", nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email; // default to "email" column

    @Column(nullable = false, length = 60)
    private String password;

}
