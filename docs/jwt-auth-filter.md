# JwtAuthFilter — Authentication & RBAC at the Gateway

**Location:** `backend/api-gateway/src/main/java/com/shubilet/api_gateway/security/JwtAuthFilter.java`

Every request into the RedBus backend passes through the API gateway, and every
request through the gateway passes through this filter. It is the single place
where a caller's identity is established and their access is authorised, so
individual microservices don't each have to implement auth.

---

## Responsibilities

1. Let public endpoints (login / logout / register) through without a token.
2. Read the JWT from the `jwt` cookie.
3. Verify the token's signature and expiry.
4. Extract the caller's `userId` and `role` from the token claims.
5. Check that role against the roles the requested URL requires (RBAC).
6. Forward the caller's identity to downstream services as headers.

---

## Collaborators

| Class | Role |
|---|---|
| `JwtService` | Verifies the signature/expiry and returns the token claims. |
| `RouteRoleRegistry` | Maps URL prefixes to the roles allowed to access them. |
| `SecurityConfig` | Registers this filter into the Spring Security chain. |

---

## Request flow

```
Request
  │
  ├─ path is public (/api/auth/login, /api/auth/logout, /api/members/register)?
  │     └─ yes → pass through, no auth
  │
  ├─ read "jwt" cookie
  │     └─ missing → 401 "Missing JWT cookie"
  │
  ├─ JwtService.validateAndExtract(token)
  │     └─ bad signature or expired → 401 "Invalid or expired JWT"
  │
  ├─ claims → role, userId
  │
  ├─ RouteRoleRegistry.requiredRolesFor(path)
  │     └─ role not permitted → 403 "Access denied"
  │
  ├─ attach X-User-Id and X-User-Role headers
  │
  └─ continue to controller
```

---

## Route rules

Defined in `RouteRoleRegistry`, matched by **URL prefix**:

| Path prefix | Allowed roles |
|---|---|
| `/api/admin` | `ROLE_ADMIN` |
| `/api/company` | `ROLE_ADMIN`, `ROLE_COMPANY` |
| `/api/profile` | `ROLE_ADMIN`, `ROLE_COMPANY`, `ROLE_CUSTOMER` |

A path that matches no rule has **no role restriction**. The JWT stores the role
bare (`ADMIN`); the filter prefixes it with `ROLE_` before comparing.

---

## Status codes

| Code | Meaning | Cause |
|---|---|---|
| `401 Unauthorized` | We don't know who you are | Cookie missing, signature invalid, or token expired |
| `403 Forbidden` | We know who you are, but you may not do this | Valid token, but role not permitted for that URL |

---

## Why the token is trusted

`JwtService` builds an HMAC signing key once at startup from the `jwt.secret`
property. `parseSignedClaims()` recomputes the signature over the token's
contents and compares it. If a caller edits any claim — for example changing
their role to `ADMIN` — the recomputed signature no longer matches and the call
is rejected. This is why the claims can be trusted after step 3.

The secret is supplied via the `JWT_SECRET` environment variable, injected from
the `redbus-secrets` Kubernetes Secret.

---

## Identity propagation

Downstream services do not re-parse the JWT. The filter wraps the request so
that two headers are visible to them:

- `X-User-Id`
- `X-User-Role`

---

## Known limitations

- **Fail-open routing.** `requiredRolesFor()` returns `null` for unmatched
  paths, so a new protected endpoint added outside the registered prefixes would
  be unrestricted by default. Deny-by-default would be safer.
- **No server-side revocation.** A JWT stays valid until it expires; logout
  clears the cookie but cannot invalidate an already-issued token. Short
  expiry plus refresh tokens, or a revocation list, would address this.
