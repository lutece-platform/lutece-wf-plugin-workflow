DROP TABLE IF EXISTS workflow_task_archive_resource;
CREATE TABLE workflow_task_archive_resource
(
	id_resource INT DEFAULT 0 NOT NULL,
	id_task INT DEFAULT 0 NOT NULL,
	initial_date TIMESTAMP NULL,
	archival_date TIMESTAMP NULL,
	is_archived SMALLINT DEFAULT 0,
	PRIMARY KEY (id_resource,id_task)
);
/*
  Add workflow_resource_user_history
*/
DROP TABLE IF EXISTS workflow_resource_user_history;

CREATE TABLE workflow_resource_user_history (
	id_history INT NOT NULL ,
	user_access_code varchar(255) default NULL,
	email varchar(255) default '',
	first_name varchar(255) default '',
	last_name varchar(255) default '',
	realm varchar(255) default '',
	PRIMARY KEY (id_history)
);
ALTER TABLE workflow_resource_user_history ADD CONSTRAINT fk_user_id_history FOREIGN KEY (id_history)
	REFERENCES workflow_resource_history(id_history) ON DELETE RESTRICT ON UPDATE RESTRICT;
	
DROP TABLE IF EXISTS workflow_task_archive_cf;
CREATE TABLE workflow_task_archive_cf
(
	id_task INT DEFAULT 0 NOT NULL,
	next_state INT NOT NULL,
	type_archival VARCHAR(255) DEFAULT NULL, 
	delay_archival INT DEFAULT NULL,
	PRIMARY KEY (id_task)
);
