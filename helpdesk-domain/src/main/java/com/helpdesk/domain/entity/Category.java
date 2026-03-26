package com.helpdesk.domain.entity;

/**
 * Ticket kategorilerini tanimlar.
 * Her ticket bir kategoriye aittir.
 * Ornek: "Ag Sorunu", "Yazilim Hatasi", "Donanim Arizasi".
 * Saf POJO — framework bagimliligi yok.
 *
 * Kategoriler ticket'larin dogru ekibe yonlendirilmesini saglar.
 */
public class Category {

    private Long id;
    private String name;
    private String description;
    private Boolean isActive = true;

    public Category() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
