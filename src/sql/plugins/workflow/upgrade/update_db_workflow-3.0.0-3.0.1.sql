--liquibase formatted sql
--changeset workflow:update_db_workflow-3.0.0-3.0.1.sql
--preconditions onFail:MARK_RAN onError:WARN
-- WORKFLOW-58 : Add the possibility to order the states, actions and tasks
ALTER TABLE workflow_state ADD display_order INT;
ALTER TABLE workflow_action ADD display_order INT;
ALTER TABLE workflow_task ADD display_order INT;
