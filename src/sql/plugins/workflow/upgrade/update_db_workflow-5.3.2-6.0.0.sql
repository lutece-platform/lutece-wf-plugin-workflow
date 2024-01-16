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
ALTER TABLE workflow_action ADD COLUMN id_alternative_state_after INT DEFAULT NULL; 

-- add choice task
CREATE TABLE workflow_task_choice_config (
	 id_task INT DEFAULT 0 NOT NULL ,
	 message LONG VARCHAR NOT NULL
);

-- Add uuids for workflows, states, actions and tasks
ALTER TABLE workflow_workflow ADD COLUMN uid_workflow VARCHAR DEFAULT NULL;
UPDATE workflow_workflow SET uid_workflow = UUID();
ALTER TABLE workflow_state ADD COLUMN uid_state VARCHAR DEFAULT NULL;
UPDATE workflow_state SET uid_state = UUID();
ALTER TABLE workflow_action ADD COLUMN uid_action VARCHAR DEFAULT NULL;
UPDATE workflow_action SET uid_action = UUID();
ALTER TABLE workflow_task ADD COLUMN uid_task VARCHAR DEFAULT NULL;
UPDATE workflow_task SET uid_task = UUID();
ALTER TABLE workflow_prerequisite  ADD COLUMN uid_prerequisite VARCHAR DEFAULT NULL;
UPDATE workflow_prerequisite  SET uid_prerequisite = UUID();