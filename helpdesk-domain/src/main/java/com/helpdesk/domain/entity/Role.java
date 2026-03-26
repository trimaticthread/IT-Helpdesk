package com.helpdesk.domain.entity;

/**
 * Sistemdeki rolleri tanimlar.
 * Her rolun bir adi ve aciklamasi vardir.
 * Saf POJO — framework bagimliligi yok.
 *
 * Bir role birden fazla kullanici atanabilir (N:M)
 * Bir role birden fazla yetki atanabilir (N:M)
 */
public class Role {

    private Long id;
    private String name;
    private String description;
    private Boolean isActive = true;

    public Role() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
