package com.helpdesk.domain.entity;

/**
 * Ticket'larin atanabilecegi departman veya ekip gruplarini temsil eder.
 * Ornek: "IT Destek", "Ag Yonetimi", "Yazilim Gelistirme".
 * Saf POJO — framework bagimliligi yok.
 *
 * NOT: Veritabaninda tablo adi "groups_" cunku MySQL'de "groups" reserved keyword.
 *
 * Iliskiler:
 * - Bir gruba birden fazla kullanici atanabilir (N:M)
 * - Ticket'lar bir gruba yonlendirilir (1:N)
 */
public class Group {

    private Long id;
    private String name;
    private String description;
    private String email;
    private Boolean isActive = true;

    public Group() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
