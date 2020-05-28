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
