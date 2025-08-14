-- liquibase formatted sql
-- changeset workflow:update_db_workflow-4.2.1-4.2.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
DROP TABLE IF EXISTS workflow_prerequisite_duration_cf;
CREATE TABLE workflow_prerequisite_duration_cf
(
	id_prerequisite INT NOT NULL,
	duration INT NOT NULL,
	PRIMARY KEY (id_prerequisite)
);

