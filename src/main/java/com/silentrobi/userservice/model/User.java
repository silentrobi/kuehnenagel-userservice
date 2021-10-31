package com.silentrobi.userservice.model;

import java.util.UUID;
import javax.persistence.*;

import lombok.*;

@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends BaseModel {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name="email",  nullable = false)
    private String email;

    @Column(name="age")
    private Integer age;

    @Column(name = "phone_number")
    private String phoneNumber;
}