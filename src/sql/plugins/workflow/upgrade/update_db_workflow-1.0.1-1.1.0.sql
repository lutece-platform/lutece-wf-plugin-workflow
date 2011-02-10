ALTER TABLE workflow_workgroup_cf ADD COLUMN id_mailing_list INT DEFAULT NULL;

ALTER TABLE workflow_task_assignment_cf ADD COLUMN is_notify SMALLINT DEFAULT 0;
ALTER TABLE workflow_task_assignment_cf ADD COLUMN message VARCHAR(255) DEFAULT NULL;
ALTER TABLE workflow_task_assignment_cf ADD COLUMN subject VARCHAR(255) DEFAULT NULL;
ALTER TABLE workflow_task_assignment_cf ADD COLUMN is_use_user_name SMALLINT DEFAULT 0;


