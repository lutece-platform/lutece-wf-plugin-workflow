-- liquibase formatted sql
-- changeset workflow:update_db_workflow-5.3.2-6.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- select multiple states before action 
DROP TABLE IF EXISTS workflow_action_state_before;
CREATE TABLE workflow_action_state_before (
id_action int NOT NULL,
id_state_before int NOT NULL,
PRIMARY KEY (id_action, id_state_before)
);

insert into workflow_action_state_before select id_action, id_state_before from workflow_action;
ALTER TABLE workflow_action DROP COLUMN id_state_before; 

-- add alternative state (set if a task returns false)
ALTER TABLE workflow_action ADD COLUMN id_alternative_state_after INT NOT NULL DEFAULT -1;

-- add choice task
CREATE TABLE workflow_task_choice_config (
	 id_task INT DEFAULT 0 NOT NULL ,
	 message LONG VARCHAR NOT NULL
);
