package com.ordercraft.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

/**
 * Permission entity mapped to oc_permissions table.
 * Represents a granular access control permission (e.g., SO_CREATE, INVOICE_VIEW).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "oc_permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "oc_permissions_gen")
    @SequenceGenerator(name = "oc_permissions_gen", sequenceName = "oc_permissions_seq", allocationSize = 1)
    private Long id;

    @Column(name = "permission_key", length = 50, nullable = false, unique = true)
    private String permissionKey;

    @Column(name = "description", length = 255)
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
