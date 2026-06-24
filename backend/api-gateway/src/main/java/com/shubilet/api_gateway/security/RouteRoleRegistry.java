package com.shubilet.api_gateway.security;

import java.util.Map;
import java.util.Set;

/**
 * Maps URI prefixes to the set of roles permitted to access them.
 * Paths not listed here are open (no role requirement).
 */
public final class RouteRoleRegistry {

    private RouteRoleRegistry() {}

    private static final Map<String, Set<String>> RULES = Map.of(
        "/api/admin",    Set.of("ROLE_ADMIN"),
        "/api/company",  Set.of("ROLE_ADMIN", "ROLE_COMPANY"),
        "/api/profile",  Set.of("ROLE_ADMIN", "ROLE_COMPANY", "ROLE_CUSTOMER")
    );

    /**
     * Returns null when no role restriction applies to this path.
     * Returns an empty set when the path is restricted but the registry has no match (deny-all fallback).
     */
    public static Set<String> requiredRolesFor(String path) {
        return RULES.entrySet().stream()
                .filter(e -> path.startsWith(e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }
}
