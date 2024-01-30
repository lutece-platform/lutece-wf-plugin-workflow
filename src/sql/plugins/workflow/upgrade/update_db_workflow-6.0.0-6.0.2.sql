-- Add uuids for workflows, states, actions and tasks
ALTER TABLE workflow_workflow ADD COLUMN uid_workflow VARCHAR(255) DEFAULT NULL;
UPDATE workflow_workflow SET uid_workflow = UUID();
ALTER TABLE workflow_state ADD COLUMN uid_state VARCHAR(255) DEFAULT NULL;
UPDATE workflow_state SET uid_state = UUID();
ALTER TABLE workflow_action ADD COLUMN uid_action VARCHAR(255) DEFAULT NULL;
UPDATE workflow_action SET uid_action = UUID();
ALTER TABLE workflow_task ADD COLUMN uid_task VARCHAR(255) DEFAULT NULL;
UPDATE workflow_task SET uid_task = UUID();
ALTER TABLE workflow_prerequisite ADD COLUMN uid_prerequisite VARCHAR(255) DEFAULT NULL;
UPDATE workflow_prerequisite  SET uid_prerequisite = UUID();
