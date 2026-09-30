-- =====================================================
-- V1: Create User, Role, and Permission tables
-- =====================================================

-- Sequences
CREATE SEQUENCE oc_users_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE oc_roles_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE oc_permissions_seq START WITH 1 INCREMENT BY 1;

-- Users table
CREATE TABLE oc_users (
    id              NUMBER          DEFAULT oc_users_seq.NEXTVAL PRIMARY KEY,
    username        VARCHAR2(50)    NOT NULL,
    email           VARCHAR2(100)   NOT NULL,
    full_name       VARCHAR2(100)   NOT NULL,
    password_hash   VARCHAR2(255)   NOT NULL,
    is_active       NUMBER(1)       DEFAULT 1,
    created_by      NUMBER,
    created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP,
    updated_by      NUMBER,
    updated_at      TIMESTAMP,
    CONSTRAINT uk_oc_users_username UNIQUE (username),
    CONSTRAINT uk_oc_users_email UNIQUE (email),
    CONSTRAINT fk_oc_users_created_by FOREIGN KEY (created_by) REFERENCES oc_users(id),
    CONSTRAINT fk_oc_users_updated_by FOREIGN KEY (updated_by) REFERENCES oc_users(id)
);

-- Roles table
CREATE TABLE oc_roles (
    id              NUMBER          DEFAULT oc_roles_seq.NEXTVAL PRIMARY KEY,
    role_name       VARCHAR2(50)    NOT NULL,
    description     VARCHAR2(255),
    is_system       NUMBER(1)       DEFAULT 0,
    created_at      TIMESTAMP       DEFAULT SYSTIMESTAMP,
    CONSTRAINT uk_oc_roles_name UNIQUE (role_name)
);

-- Permissions table
CREATE TABLE oc_permissions (
    id              NUMBER          DEFAULT oc_permissions_seq.NEXTVAL PRIMARY KEY,
    permission_key  VARCHAR2(50)    NOT NULL,
    description     VARCHAR2(255),
    CONSTRAINT uk_oc_permissions_key UNIQUE (permission_key)
);

-- Role-Permission join table
CREATE TABLE oc_role_permissions (
    role_id         NUMBER          NOT NULL,
    permission_id   NUMBER          NOT NULL,
    CONSTRAINT pk_oc_role_permissions PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_oc_rp_role FOREIGN KEY (role_id) REFERENCES oc_roles(id),
    CONSTRAINT fk_oc_rp_permission FOREIGN KEY (permission_id) REFERENCES oc_permissions(id)
);

-- User-Role join table
CREATE TABLE oc_user_roles (
    user_id         NUMBER          NOT NULL,
    role_id         NUMBER          NOT NULL,
    CONSTRAINT pk_oc_user_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_oc_ur_user FOREIGN KEY (user_id) REFERENCES oc_users(id),
    CONSTRAINT fk_oc_ur_role FOREIGN KEY (role_id) REFERENCES oc_roles(id)
);

-- Indexes
CREATE INDEX idx_oc_users_active ON oc_users(is_active);
