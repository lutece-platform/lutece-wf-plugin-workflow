DROP TABLE IF EXISTS workflow_prerequisite_duration_cf;
CREATE TABLE workflow_prerequisite_duration_cf
(
	id_prerequisite INT NOT NULL,
	duration INT NOT NULL,
	PRIMARY KEY (id_prerequisite)
);

