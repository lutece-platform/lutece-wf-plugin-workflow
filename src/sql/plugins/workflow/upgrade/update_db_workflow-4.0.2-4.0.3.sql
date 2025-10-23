-- liquibase formatted sql
-- changeset workflow:update_db_workflow-4.0.2-4.0.3.sql
-- preconditions onFail:MARK_RAN onError:WARN
DROP TABLE IF EXISTS workflow_prerequisite;
CREATE TABLE workflow_prerequisite
(
	id_prerequisite INT NOT NULL,
	id_action INT NOT NULL,
	prerequisite_type VARCHAR(255) NOT NULL,
	PRIMARY KEY (id_prerequisite)
);