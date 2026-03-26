package com.helpdesk.domain.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Sisteme erisen tum kullanicilari temsil eder.
 * Musteri, agent, supervisor veya admin olabilir.
 * Saf POJO — hicbir framework bagimliligi yok, sadece Java.
 *
 * Iliskiler:
 * - Bir kullanicinin birden fazla rolu olabilir (N:M)
 * - Bir kullanici birden fazla gruba ait olabilir (N:M)
 * - Bir kullanici birden fazla ticket olusturabilir (1:N)
 * - Bir agent'a birden fazla ticket atanabilir (1:N)
 */
public class User {

    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String phone;
    private String department;
    private Boolean isActive = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<Role> roles = new HashSet<>();
    private Set<Group> groups = new HashSet<>();

    public User() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    public Set<Group> getGroups() { return groups; }
    public void setGroups(Set<Group> groups) { this.groups = groups; }
}
