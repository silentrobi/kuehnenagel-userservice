package com.silentrobi.userservice.model;

import java.util.UUID;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name="email",  nullable = false)
    private String email;

    @Column(name="age")
    private int age;

    @Column(name = "phone_number")
    private String phoneNumber;
}