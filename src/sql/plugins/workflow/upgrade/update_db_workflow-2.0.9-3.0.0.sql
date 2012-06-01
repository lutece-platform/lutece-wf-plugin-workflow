--
-- WORKFLOW-56 : Add the possibility to link actions between each other
--
DROP TABLE IF EXISTS workflow_action_action;
CREATE TABLE workflow_action_action
(
	id_action INT DEFAULT 0 NOT NULL,
	id_linked_action INT DEFAULT 0 NOT NULL,
	PRIMARY KEY (id_action, id_linked_action)
);
