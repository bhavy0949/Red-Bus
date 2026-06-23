package com.shubilet.member_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "company_info")
public class CompanyInfo implements Serializable {

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
    private String title;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(name = "ref_admin_id")
    private Long refAdminId;

    @Column
    private Instant verifiedAt;

    public CompanyInfo() {}

    public CompanyInfo(User user, String title) {
        this.user = user;
        this.title = title;
    }

    public void verify(Long adminId) {
        this.verified = true;
        this.refAdminId = adminId;
        this.verifiedAt = Instant.now();
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public Long getRefAdminId() { return refAdminId; }
    public void setRefAdminId(Long refAdminId) { this.refAdminId = refAdminId; }
    public Instant getVerifiedAt() { return verifiedAt; }
}
