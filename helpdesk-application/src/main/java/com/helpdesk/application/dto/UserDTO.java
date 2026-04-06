package com.helpdesk.application.dto;

/**
 * Kullanici bilgilerini katmanlar arasi tasimak icin kullanilan veri transfer nesnesi (DTO).
 * Domain entity'sinden (User) UI ve servis katmanini izole eder — servis katmani
 * hicbir zaman ham User entity'sini disariya vermez, bunun yerine bu nesneyi kullanir.
 *
 * Icerdigi alanlar:
 * - Kimlik : id, username, email
 * - Kisisel: firstName, lastName, department
 * - Durum  : isActive (hesabin aktif olup olmadigi)
 * - Rol    : role (login sirasinda AuthService tarafindan veritabanindan doldurulur)
 *
 * Not: Bu nesne dogrudan veritabanina yazilmaz.
 *      UserMapper uzerinden User entity'sine donusturulur.
 */
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String department;
    private Boolean isActive;
    private String role;

    public UserDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getFullName() { return firstName + " " + lastName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
