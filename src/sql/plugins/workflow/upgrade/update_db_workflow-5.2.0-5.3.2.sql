-- liquibase formatted sql
-- changeset workflow:update_db_workflow-5.2.0-5.3.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
DROP TABLE IF EXISTS workflow_task_confirm_action_config;
CREATE TABLE workflow_task_confirm_action_config (
	 id_task INT DEFAULT 0 NOT NULL ,
	 message LONG VARCHAR NOT NULL
);
