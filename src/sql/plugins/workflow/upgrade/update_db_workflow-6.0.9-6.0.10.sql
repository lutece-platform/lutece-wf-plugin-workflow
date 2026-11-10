-- liquibase formatted sql
-- changeset workflow:update_db_workflow-6.0.9-6.0.10.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- Fingerprints written by the import, to tell what was modified on the target since the last import
ALTER TABLE workflow_workflow ADD COLUMN last_import_date TIMESTAMP NULL DEFAULT NULL;
ALTER TABLE workflow_state ADD COLUMN import_hash VARCHAR(64) DEFAULT NULL;
ALTER TABLE workflow_action ADD COLUMN import_hash VARCHAR(64) DEFAULT NULL;
ALTER TABLE workflow_task ADD COLUMN import_hash VARCHAR(64) DEFAULT NULL;
ALTER TABLE workflow_prerequisite ADD COLUMN import_hash VARCHAR(64) DEFAULT NULL;
