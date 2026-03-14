package com.helpdesk.domain.entity;

import jakarta.persistence.*;

/*
    Bilgilendirme

    Sistemdeki kullanıcı rollerini tanımlar.
    Her rolun bir adi ve aciklamasi var.
    Bir role birden fazla kullanıcı atayabilirsin.

 */
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;


    public Role (){}

    // Getter - Setter Encapsulation

    public Long getId(){
        return id;
    } public void setId(Long id){
        this.id = id;
    }


    public String getName(){
        return name;
    }public void setName(String name){
        this.name = name;
    }

    public String getDescription(){
        return description;
    }public void setDescription(String description){
        this.description = description;
    }


    public Boolean getIsActive(){
        return isActive;
    }public void setIsActive(Boolean isActive){
        this.isActive = isActive;
    }

}
