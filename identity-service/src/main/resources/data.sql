-- Default roles
INSERT IGNORE INTO roles (name) VALUES ('ADMIN');
INSERT IGNORE INTO roles (name) VALUES ('PROFESSOR');
INSERT IGNORE INTO roles (name) VALUES ('STUDENT');
INSERT IGNORE INTO roles (name) VALUES ('ACCOUNTING');
INSERT IGNORE INTO roles (name) VALUES ('SUPPORT');

-- Default permissions
INSERT IGNORE INTO permissions (name, description) VALUES ('catalog.read', 'Read catalog data');
INSERT IGNORE INTO permissions (name, description) VALUES ('enroll.write', 'Create enrollments');
INSERT IGNORE INTO permissions (name, description) VALUES ('billing.write', 'Manage billing');
INSERT IGNORE INTO permissions (name, description) VALUES ('assessment.write', 'Manage assessments');
INSERT IGNORE INTO permissions (name, description) VALUES ('notify.write', 'Send notifications');
INSERT IGNORE INTO permissions (name, description) VALUES ('admin.all', 'Full admin access');
