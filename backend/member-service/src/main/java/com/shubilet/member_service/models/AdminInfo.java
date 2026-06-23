package com.shubilet.member_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

@Entity
@Table(name = "admin_info")
public class AdminInfo implements Serializable {

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

    @Column(name = "ref_admin_id")
    private Long refAdminId;

    public AdminInfo() {}

    public AdminInfo(User user, String name, String surname) {
        this.user = user;
        this.name = name;
        this.surname = surname;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public Long getRefAdminId() { return refAdminId; }
    public void setRefAdminId(Long refAdminId) { this.refAdminId = refAdminId; }
}
