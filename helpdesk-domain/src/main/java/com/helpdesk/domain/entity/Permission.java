package com.helpdesk.domain.entity;

import jakarta.persistence.*;

/**
 * Sistemdeki yetkileri tanimlar.
 * Yetkiler rollere atanir (N:M → role_permissions tablosu).
 */

@Entity
@Table(name = "permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "resource", nullable = false, length = 50)
    private String resource;

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "description")
    private String description;

    public Permission() {}

    // Getter ve Setter , Encapsulation

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


    public String getResource() {
        return resource;
    } public void setResource(String resource) {
        this.resource = resource;
    }


    public String getAction() {
        return action;
    }public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }  public void setDescription(String description) {
        this.description = description;
    }

}