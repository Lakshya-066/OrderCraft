CREATE SEQUENCE oc_users_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE oc_roles_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE oc_permissions_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE oc_token_blacklist_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE oc_users (
  id                   NUMBER PRIMARY KEY,
  username             VARCHAR2(50)  NOT NULL UNIQUE,
  email                VARCHAR2(100) NOT NULL UNIQUE,
  full_name            VARCHAR2(100) NOT NULL,
  password_hash        VARCHAR2(255) NOT NULL,
  is_active            NUMBER(1) DEFAULT 1 NOT NULL,
  must_change_password NUMBER(1) DEFAULT 0 NOT NULL,   -- added for UC-1.5
  created_by           NUMBER REFERENCES oc_users(id),
  created_at           TIMESTAMP DEFAULT SYSTIMESTAMP,
  updated_by           NUMBER REFERENCES oc_users(id),
  updated_at           TIMESTAMP
);

CREATE TABLE oc_roles (
  id          NUMBER PRIMARY KEY,
  role_name   VARCHAR2(50) NOT NULL UNIQUE,
  description VARCHAR2(255),
  is_system   NUMBER(1) DEFAULT 0,
  created_at  TIMESTAMP DEFAULT SYSTIMESTAMP
);

CREATE TABLE oc_permissions (
  id             NUMBER PRIMARY KEY,
  permission_key VARCHAR2(50) NOT NULL UNIQUE,
  description    VARCHAR2(255)
);

CREATE TABLE oc_role_permissions (
  role_id       NUMBER REFERENCES oc_roles(id),
  permission_id NUMBER REFERENCES oc_permissions(id),
  PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE oc_user_roles (
  user_id NUMBER REFERENCES oc_users(id),
  role_id NUMBER REFERENCES oc_roles(id),
  PRIMARY KEY (user_id, role_id)
);

CREATE TABLE oc_token_blacklist (
  id          NUMBER PRIMARY KEY,
  token_hash  VARCHAR2(512) NOT NULL,
  expiry_date TIMESTAMP NOT NULL,
  created_at  TIMESTAMP DEFAULT SYSTIMESTAMP
);
CREATE INDEX idx_token_blacklist_hash ON oc_token_blacklist(token_hash);