-- =====================================================
-- V2: Create Token Blacklist table for JWT invalidation
-- =====================================================

CREATE SEQUENCE oc_token_blacklist_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE oc_token_blacklist (
    id              NUMBER          DEFAULT oc_token_blacklist_seq.NEXTVAL PRIMARY KEY,
    token_hash      VARCHAR2(512)   NOT NULL,
    expiry_date     TIMESTAMP       NOT NULL,
    created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP
);

CREATE INDEX idx_oc_token_blacklist_hash ON oc_token_blacklist(token_hash);
CREATE INDEX idx_oc_token_blacklist_expiry ON oc_token_blacklist(expiry_date);
