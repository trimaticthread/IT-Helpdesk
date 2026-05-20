package com.helpdesk.domain.entity;

public class Department {

    private Long id;
    private String name;
    private Boolean isActive = true;

    public Department() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    @Override
    public String toString() { return name; }
}
