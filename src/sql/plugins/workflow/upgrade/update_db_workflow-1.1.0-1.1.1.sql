--liquibase formatted sql
--changeset workflow:update_db_workflow-1.1.0-1.1.1.sql
--preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE workflow_action ADD COLUMN is_automatic SMALLINT DEFAULT 0;

-- /!\ WARNING THIS UPDATE MAY BREAK COMPATIBILITY
ALTER TABLE workflow_resource_workflow ADD COLUMN id_external_parent INT DEFAULT NULL;