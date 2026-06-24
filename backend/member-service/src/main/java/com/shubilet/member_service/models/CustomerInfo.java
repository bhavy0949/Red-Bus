package com.shubilet.member_service.models;

import com.shubilet.member_service.common.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

@Entity
@Table(name = "customer_info")
public class CustomerInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String name;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String surname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Gender gender;

    public CustomerInfo() {}

    public CustomerInfo(User user, String name, String surname, Gender gender) {
        this.user = user;
        this.name = name;
        this.surname = surname;
        this.gender = (gender != null ? gender : Gender.MALE);
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
}
