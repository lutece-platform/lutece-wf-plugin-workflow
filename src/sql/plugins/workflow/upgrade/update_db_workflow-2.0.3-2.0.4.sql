--liquibase formatted sql
--changeset workflow:update_db_workflow-2.0.3-2.0.4.sql
--preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE workflow_action ADD COLUMN is_mass_action SMALLINT DEFAULT 0;
