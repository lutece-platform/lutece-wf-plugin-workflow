-- liquibase formatted sql
-- changeset workflow:update_db_workflow-6.0.2-7.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- Update icon_url for WORKFLOW_MANAGEMENT right
UPDATE core_admin_right SET icon_url='ti ti-binary-tree' WHERE id_right='WORKFLOW_MANAGEMENT';