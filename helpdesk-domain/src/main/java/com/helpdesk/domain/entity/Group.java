package com.helpdesk.domain.entity;

import jakarta.persistence.*;

/**
 * Ticket'larin atanabilecegi departman veya ekip gruplarini temsil eder.
 * Ornek: "IT Destek", "Ag Yonetimi", "Yazilim Gelistirme".
 * Ticket'lar bir gruba yonlendirilir, gruptaki agent'lar o ticket'i cozer.
 */
@Entity
@Table(name = "groups_")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public Group() {}

    // Getter ve Setter, Encapsulation

    public Long getId() {
        return id;
    } public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    } public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    } public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getIsActive() {
        return isActive;
    }public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

}