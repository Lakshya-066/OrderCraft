package com.ordercraft.common.entity;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;

/**
 * JPA entity listener that auto-sets audit timestamps.
 */
public class AuditEntityListener {

    @PrePersist
    public void setCreatedAt(Object entity) {
        if (entity instanceof BaseEntity baseEntity) {
            baseEntity.setCreatedAt(LocalDateTime.now());
        }
    }

    @PreUpdate
    public void setUpdatedAt(Object entity) {
        if (entity instanceof BaseEntity baseEntity) {
            baseEntity.setUpdatedAt(LocalDateTime.now());
        }
    }
}
