-- =====================================================
-- V3: Seed default Roles, Permissions, and Admin user
-- =====================================================

-- -----------------------------------------------
-- 1. Insert all granular permissions
-- -----------------------------------------------
INSERT INTO oc_permissions (id, permission_key, description) VALUES (oc_permissions_seq.NEXTVAL, 'USER_MANAGE', 'Create, edit, and deactivate users');
INSERT INTO oc_permissions (id, permission_key, description) VALUES (oc_permissions_seq.NEXTVAL, 'ROLE_MANAGE', 'Create, edit roles and assign permissions');
INSERT INTO oc_permissions (id, permission_key, description) VALUES (oc_permissions_seq.NEXTVAL, 'CUSTOMER_VIEW', 'View customer list and details');
INSERT INTO oc_permissions (id, permission_key, description) VALUES (oc_permissions_seq.NEXTVAL, 'CUSTOMER_MANAGE', 'Create, edit, and delete customers');
INSERT INTO oc_permissions (id, permission_key, description) VALUES (oc_permissions_seq.NEXTVAL, 'VENDOR_VIEW', 'View vendor list and details');
INSERT INTO oc_permissions (id, permission_key, description) VALUES (oc_permissions_seq.NEXTVAL, 'VENDOR_MANAGE', 'Create, edit, and delete vendors');
INSERT INTO oc_permissions (id, permission_key, description) VALUES (oc_permissions_seq.NEXTVAL, 'PRODUCT_VIEW', 'View product catalog');
INSERT INTO oc_permissions (id, permission_key, description) VALUES (oc_permissions_seq.NEXTVAL, 'PRODUCT_MANAGE', 'Create, edit, and delete products and BOMs');
INSERT INTO oc_permissions (id, permission_key, description) VALUES (oc_permissions_seq.NEXTVAL, 'SO_VIEW', 'View sales orders');
INSERT INTO oc_permissions (id, permission_key, description) VALUES (oc_permissions_seq.NEXTVAL, 'SO_CREATE', 'Create new sales orders');
INSERT INTO oc_permissions (id, permission_key, description) VALUES (oc_permissions_seq.NEXTVAL, 'SO_EDIT', 'Edit draft or pending sales orders');
INSERT INTO oc_permissions (id, permission_key, description) VALUES (oc_permissions_seq.NEXTVAL, 'SO_APPROVE', 'Approve or reject sales orders');
INSERT INTO oc_permissions (id, permission_key, description) VALUES (oc_permissions_seq.NEXTVAL, 'PO_VIEW', 'View purchase orders');
INSERT INTO oc_permissions (id, permission_key, description) VALUES (oc_permissions_seq.NEXTVAL, 'PO_CREATE', 'Create purchase orders');
INSERT INTO oc_permissions (id, permission_key, description) VALUES (oc_permissions_seq.NEXTVAL, 'PO_APPROVE', 'Approve or reject purchase orders');
INSERT INTO oc_permissions (id, permission_key, description) VALUES (oc_permissions_seq.NEXTVAL, 'INVENTORY_VIEW', 'View stock levels');
INSERT INTO oc_permissions (id, permission_key, description) VALUES (oc_permissions_seq.NEXTVAL, 'INVENTORY_ADJUST', 'Manually adjust stock levels');
INSERT INTO oc_permissions (id, permission_key, description) VALUES (oc_permissions_seq.NEXTVAL, 'INVOICE_VIEW', 'View invoices');
INSERT INTO oc_permissions (id, permission_key, description) VALUES (oc_permissions_seq.NEXTVAL, 'INVOICE_CREATE', 'Generate invoices');
INSERT INTO oc_permissions (id, permission_key, description) VALUES (oc_permissions_seq.NEXTVAL, 'PAYMENT_VIEW', 'View payment records');
INSERT INTO oc_permissions (id, permission_key, description) VALUES (oc_permissions_seq.NEXTVAL, 'PAYMENT_RECORD', 'Record incoming and outgoing payments');
INSERT INTO oc_permissions (id, permission_key, description) VALUES (oc_permissions_seq.NEXTVAL, 'APPROVAL_CONFIG', 'Configure approval workflows');
INSERT INTO oc_permissions (id, permission_key, description) VALUES (oc_permissions_seq.NEXTVAL, 'DASHBOARD_VIEW', 'Access dashboards and reports');
INSERT INTO oc_permissions (id, permission_key, description) VALUES (oc_permissions_seq.NEXTVAL, 'REPORT_EXPORT', 'Export reports as PDF or CSV');
INSERT INTO oc_permissions (id, permission_key, description) VALUES (oc_permissions_seq.NEXTVAL, 'CONFIG_MANAGE', 'Manage system configuration settings');
INSERT INTO oc_permissions (id, permission_key, description) VALUES (oc_permissions_seq.NEXTVAL, 'AUDIT_VIEW', 'View audit trail logs');

-- -----------------------------------------------
-- 2. Insert default system roles
-- -----------------------------------------------
INSERT INTO oc_roles (id, role_name, description, is_system) VALUES (oc_roles_seq.NEXTVAL, 'Admin', 'Full system access — all permissions', 1);
INSERT INTO oc_roles (id, role_name, description, is_system) VALUES (oc_roles_seq.NEXTVAL, 'General User', 'Standard user with limited permissions', 1);

-- -----------------------------------------------
-- 3. Assign ALL permissions to Admin role
-- -----------------------------------------------
INSERT INTO oc_role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM oc_roles r, oc_permissions p
WHERE r.role_name = 'Admin';

-- -----------------------------------------------
-- 4. Assign selected permissions to General User
-- -----------------------------------------------
INSERT INTO oc_role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM oc_roles r, oc_permissions p
WHERE r.role_name = 'General User'
  AND p.permission_key IN (
    'CUSTOMER_VIEW', 'VENDOR_VIEW', 'PRODUCT_VIEW',
    'SO_VIEW', 'SO_CREATE', 'SO_EDIT',
    'PO_VIEW', 'INVENTORY_VIEW',
    'INVOICE_VIEW', 'PAYMENT_VIEW',
    'DASHBOARD_VIEW', 'REPORT_EXPORT'
  );

-- -----------------------------------------------
-- 5. Insert default admin user (password: Admin@123)
-- -----------------------------------------------
INSERT INTO oc_users (id, username, email, full_name, password_hash, is_active)
VALUES (oc_users_seq.NEXTVAL, 'admin', 'admin@ordercraft.com', 'System Administrator',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1);

-- -----------------------------------------------
-- 6. Assign Admin role to admin user
-- -----------------------------------------------
INSERT INTO oc_user_roles (user_id, role_id)
SELECT u.id, r.id
FROM oc_users u, oc_roles r
WHERE u.username = 'admin' AND r.role_name = 'Admin';

COMMIT;
