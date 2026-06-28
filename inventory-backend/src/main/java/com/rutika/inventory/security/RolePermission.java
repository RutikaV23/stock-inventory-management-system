package com.rutika.inventory.security;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class RolePermission {

    private static final Map<String, Set<Permission>> ROLE_PERMISSIONS = new ConcurrentHashMap<>();

    static {
        ROLE_PERMISSIONS.put("SUPER_ADMIN", Collections.unmodifiableSet(EnumSet.allOf(Permission.class)));

        ROLE_PERMISSIONS.put("ADMIN", Collections.unmodifiableSet(EnumSet.of(
                Permission.PRODUCT_CREATE,
                Permission.PRODUCT_READ,
                Permission.PRODUCT_UPDATE,
                Permission.PRODUCT_DELETE,
                Permission.STOCK_IN,
                Permission.STOCK_OUT,
                Permission.DASHBOARD,
                Permission.REPORT,
                Permission.EXCEL_EXPORT,
                Permission.PROFILE,
                Permission.CHANGE_PASSWORD
        )));
    }

    private RolePermission() {}

    public static Set<Permission> getPermissionsForRole(String roleName) {
        return ROLE_PERMISSIONS.getOrDefault(roleName, Collections.emptySet());
    }

    public static boolean hasPermission(String roleName, Permission permission) {
        Set<Permission> permissions = ROLE_PERMISSIONS.get(roleName);
        return permissions != null && permissions.contains(permission);
    }
}
