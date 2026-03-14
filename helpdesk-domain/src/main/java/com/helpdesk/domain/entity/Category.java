package com.helpdesk.domain.entity;

import jakarta.persistence.*;

/**
 * Ticket kategorilerini tanimlar.
 * Her ticket bir kategoriye aittir (ornek: "Ag Sorunu", "Printer Tırt Oldu").
 * Kategoriler ticket'larin siniflandirilmasini ve dogru ekibe yonlendirilmesini saglar.
 */

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public Category() {}

    // Getter ve Setter, Encapsulation

    public Long getId() {
        return id;
    }public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    } public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

}