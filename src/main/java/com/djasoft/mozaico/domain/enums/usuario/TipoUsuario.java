package com.djasoft.mozaico.domain.enums.usuario;

import java.util.Set;

public enum TipoUsuario {
    SUPER_ADMIN("Super Administrador", Set.of("ALL_PERMISSIONS")),
    ADMIN("Administrador", Set.of("MANAGE_USERS", "MANAGE_COMPANY", "VIEW_REPORTS", "MANAGE_PAYMENTS", "MANAGE_ORDERS", "VIEW_ORDERS", "MANAGE_RESERVATIONS", "VIEW_RESERVATIONS", "MANAGE_INVENTORY", "MANAGE_TABLES", "MANAGE_CASH_REGISTER", "MANAGE_KITCHEN", "UPDATE_ORDER_STATUS")),
    GERENTE("Gerente", Set.of("VIEW_REPORTS", "MANAGE_PAYMENTS", "MANAGE_ORDERS", "VIEW_ORDERS", "MANAGE_RESERVATIONS", "VIEW_RESERVATIONS", "MANAGE_INVENTORY")),
    CAJERO("Cajero", Set.of("MANAGE_PAYMENTS", "VIEW_ORDERS", "MANAGE_CASH_REGISTER")),
    MESERO("Mesero", Set.of("MANAGE_ORDERS", "VIEW_ORDERS", "VIEW_RESERVATIONS", "MANAGE_TABLES")),
    COCINERO("Cocinero", Set.of("VIEW_ORDERS", "UPDATE_ORDER_STATUS", "MANAGE_KITCHEN"));

    private final String displayName;
    private final Set<String> permissions;

    TipoUsuario(String displayName, Set<String> permissions) {
        this.displayName = displayName;
        this.permissions = permissions;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public boolean hasPermission(String permission) {
        return permissions.contains("ALL_PERMISSIONS") || permissions.contains(permission);
    }
}
