-- liquibase formatted sql
-- changeset workflow:update_db_workflow-7.0.2-7.1.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment: assignment task, the subject and the message (HTML) accept what the configuration form allows
ALTER TABLE workflow_task_assignment_cf MODIFY COLUMN subject VARCHAR(255) DEFAULT NULL;
ALTER TABLE workflow_task_assignment_cf MODIFY COLUMN message LONG VARCHAR DEFAULT NULL;
