package com.helpdesk.desktop.security;

import com.helpdesk.application.dto.UserDTO;

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
