DROP TABLE IF EXISTS workflow_action_state_before;
CREATE TABLE workflow_action_state_before (
id_action int NOT NULL,
id_state_before int NOT NULL,
CONSTRAINT workflow_action_state_before_unique UNIQUE (id_action, id_state_before)
);

insert into workflow_action_state_before select id_action, id_state_before from workflow_action;