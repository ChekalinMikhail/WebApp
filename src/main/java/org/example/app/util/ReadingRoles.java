package org.example.app.util;

import org.example.app.domain.UserWithRole;
import org.example.framework.security.Roles;

import java.util.ArrayList;
import java.util.List;

public class ReadingRoles {
    public ReadingRoles() {
    }
    public static List<String> readRoles(List<UserWithRole> roles) {
        final var result = new ArrayList<String>();
        for (UserWithRole role : roles) {
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
