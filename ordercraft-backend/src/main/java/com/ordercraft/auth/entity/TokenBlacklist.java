package com.ordercraft.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Stores hashes of invalidated (logged-out) JWT tokens until their natural expiry.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "oc_token_blacklist")
public class TokenBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "oc_token_blacklist_gen")
    @SequenceGenerator(name = "oc_token_blacklist_gen", sequenceName = "oc_token_blacklist_seq", allocationSize = 1)
    private Long id;

    @Column(name = "token_hash", length = 512, nullable = false)
    private String tokenHash;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
