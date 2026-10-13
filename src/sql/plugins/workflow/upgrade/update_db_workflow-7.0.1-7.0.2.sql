-- liquibase formatted sql
-- changeset workflow:update_db_workflow-7.0.1-7.0.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = database() AND LOWER(table_name) = 'workflow_resource_workflow' AND LOWER(column_name) = 'lock_date'
-- Claim of a resource by the action being processed on it, shared by the instances of the application (NULL: no action in progress)
ALTER TABLE workflow_resource_workflow ADD COLUMN lock_date TIMESTAMP DEFAULT NULL;
