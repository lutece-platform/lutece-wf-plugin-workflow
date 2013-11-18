DROP TABLE IF EXISTS workflow_prerequisite;
CREATE TABLE workflow_prerequisite
(
	id_prerequisite INT NOT NULL,
	id_action INT NOT NULL,
	prerequisite_type VARCHAR(255) NOT NULL,
	PRIMARY KEY (id_prerequisite)
);