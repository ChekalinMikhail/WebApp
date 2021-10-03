package org.example.app.util;

import org.example.app.domain.UserRole;
import org.example.framework.security.Roles;

import java.util.ArrayList;
import java.util.List;

public class ReadingRoles {
    private ReadingRoles() {
    }
    public static List<String> readRoles(List<UserRole> roles) {
        final var result = new ArrayList<String>();
        for (UserRole role : roles) {
            if (role.getRole() == 1) {
                result.add(Roles.ROLE_ADMIN);
            }
            if (role.getRole() == 2) {
                result.add(Roles.ROLE_USER);
            }
        }
        return result;
    }
}
