--liquibase formatted sql
--changeset workflow:update_db_workflow-4.0.0-4.0.1.sql
--preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE workflow_action ADD COLUMN is_automatic_reflexive_action SMALLINT DEFAULT 0