package com.helpdesk.domain.entity;

/**
 * Sistemdeki yetkileri tanimlar.
 * Her yetki bir kaynak (resource) ve aksiyon (action) ciftidir.
 * Ornek: resource = "ticket", action = "create" → ticket olusturma yetkisi.
 * Saf POJO — framework bagimliligi yok.
 *
 * Yetkiler rollere atanir (N:M)
 */
public class Permission {

    private Long id;
    private String name;
    private String resource;
    private String action;
    private String description;

    public Permission() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
