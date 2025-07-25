--liquibase formatted sql
--changeset workflow:update_db_workflow-5.0.0-5.1.0.sql
--preconditions onFail:MARK_RAN onError:WARN
DROP TABLE IF EXISTS workflow_task_choose_state_config;
CREATE TABLE workflow_task_choose_state_config (
	 id_task INT DEFAULT 0 NOT NULL ,
	 controller_name varchar(100) default NULL,
	 id_state_ok INT NOT NULL,
	 id_state_ko INT NOT NULL,
	 PRIMARY KEY (id_task)
);

DROP TABLE IF EXISTS workflow_task_choose_state_information;
CREATE TABLE workflow_task_choose_state_information (
  id_history INT NOT NULL,
  id_task INT NOT NULL,
  new_state VARCHAR(255) NOT NULL
);