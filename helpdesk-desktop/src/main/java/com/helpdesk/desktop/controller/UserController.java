package com.helpdesk.desktop.controller;

import java.util.List;

import org.springframework.stereotype.Component;

import com.helpdesk.application.dto.UserDTO;
import com.helpdesk.application.service.UserService;

/**
 * Kullanici yonetimi islemlerini UI katmanindan servis katmanina aktaran kopru sinif.
 * Yalnizca Admin rolune ozel UserManagementFrame tarafindan kullanilir.
 *
 * Gorevler:
 * - getAllUsers  : Tum kullanicilari listeler (tablo gosterimi icin).
 * - createUser  : Yeni kullanici olusturur ve rol atar.
 * - updateUser  : Kullanicinin kimlik ve iletisim bilgilerini gunceller.
 * - deleteUser  : Kullaniciyi kalici olarak siler (user_roles dahil).
 */
@Component
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public List<UserDTO> getAllUsers() {
        return userService.findAll();
    }

    public UserDTO createUser(String username, String email, String password,
            String firstName, String lastName, String department, String roleName) {
        UserDTO dto = new UserDTO();
        dto.setUsername(username);
        dto.setEmail(email);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setDepartment(department);
        return userService.createUser(dto, password, roleName);
    }

    public void deleteUser(Long id) {
        userService.deleteById(id);
    }

    public void updateUser(Long id, String username, String email, String firstName,
            String lastName, String department, String roleName) {
        UserDTO dto = new UserDTO();
        dto.setId(id);
        dto.setUsername(username);
        dto.setEmail(email);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setDepartment(department);
        dto.setIsActive(true);
        userService.update(dto);
    }

}
