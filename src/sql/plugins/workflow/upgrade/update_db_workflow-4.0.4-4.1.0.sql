-- liquibase formatted sql
-- changeset workflow:update_db_workflow_4.0.4-4.1.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
DROP TABLE IF EXISTS workflow_test_resource;