package com.helpdesk.desktop.security;

import com.helpdesk.application.dto.UserDTO;

/**
 * Aktif kullanici oturumunu statik olarak tutan yardimci sinif.
 * Spring bean olmayan Swing view'larinin mevcut kullaniciya erisebilmesi icin
 * kasitli olarak statik tasarlanmistir.
 *
 * Kullanim:
 * - login(UserDTO)    : Basarili giris sonrasi AuthController tarafindan cagirilir.
 * - logout()          : Cikis butonu tiklandiginda session temizlenir.
 * - getCurrentUser()  : Herhangi bir view'dan mevcut kullaniciyi okumak icin.
 * - isLoggedIn()      : Oturum kontrolu gerektiren islemler icin.
 *
 * Not: Statik yapisindan dolayi thread-safe degildir.
 *      Bu masaustu uygulamasi tek kullanicili oldugu icin bu bir sorun teskil etmez.
 */
public class SessionManager {

    private static UserDTO currentUser;

    public static void login(UserDTO user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static UserDTO getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
