--liquibase formatted sql
--changeset workflow:update_db_workflow-5.1.0-5.2.0.sql
--preconditions onFail:MARK_RAN onError:WARN
DROP TABLE IF EXISTS workflow_task_change_state_config;
CREATE TABLE workflow_task_change_state_config (
	 id_task INT DEFAULT 0 NOT NULL ,
	 id_next_state INT NOT NULL
);

DROP TABLE IF EXISTS workflow_task_change_state_information;
CREATE TABLE workflow_task_change_state_information (
  id_history INT NOT NULL,
  id_task INT NOT NULL,
  new_state VARCHAR(255) NOT NULL
);
