-- liquibase formatted sql
-- changeset workflow:update_db_workflow-6.0.2-7.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE core_admin_right SET icon_url='ti ti-arrow-guide' WHERE id_right='WORKFLOW_MANAGEMENT';