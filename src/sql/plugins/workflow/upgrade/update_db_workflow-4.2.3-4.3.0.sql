--liquibase formatted sql
--changeset workflow:update_db_workflow-4.2.3-4.3.0.sql
--preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE workflow_task_comment_config ADD COLUMN	is_richtext SMALLINT DEFAULT 0;
