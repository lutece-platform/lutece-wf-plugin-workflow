-- ---------------
-- Drop tables --
-- ---------------
DROP TABLE IF EXISTS workflow_assignment_history;
DROP TABLE IF EXISTS workflow_task_comment_value;
DROP TABLE IF EXISTS workflow_resource_user_history;
DROP TABLE IF EXISTS workflow_resource_history;
DROP TABLE IF EXISTS workflow_task;
DROP TABLE IF EXISTS workflow_action;
DROP TABLE IF EXISTS workflow_resource_workflow;
DROP TABLE IF EXISTS workflow_state;
DROP TABLE IF EXISTS workflow_icon;
DROP TABLE IF EXISTS workflow_task_comment_config;
DROP TABLE IF EXISTS workflow_task_notification_cf;
DROP TABLE IF EXISTS workflow_task_assignment_cf;
DROP TABLE IF EXISTS workflow_workgroup_cf;
DROP TABLE IF EXISTS workflow_resource_workgroup;
DROP TABLE IF EXISTS workflow_workflow;
DROP TABLE IF EXISTS workflow_action_action;
DROP TABLE IF EXISTS workflow_prerequisite;
DROP TABLE IF EXISTS workflow_prerequisite_duration_cf;
DROP TABLE IF EXISTS workflow_task_archive_resource;
DROP TABLE IF EXISTS workflow_task_archive_cf;

-- -----------------------------------------------
-- Table structure for table workflow_workflow --
-- -----------------------------------------------
CREATE TABLE workflow_workflow
(
	id_workflow INT AUTO_INCREMENT,
	name VARCHAR(255) DEFAULT NULL,
	description LONG VARCHAR DEFAULT NULL,
	creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	is_enabled SMALLINT DEFAULT 0,
	workgroup_key VARCHAR(255) DEFAULT NULL,
	PRIMARY KEY (id_workflow)
);

-- --------------------------------------------
-- Table structure for table workflow_state --
-- --------------------------------------------
CREATE TABLE workflow_state
(
	id_state INT AUTO_INCREMENT,
	name VARCHAR(255) DEFAULT NULL,
	description LONG VARCHAR DEFAULT NULL,
	id_workflow INT DEFAULT NULL,
	is_initial_state SMALLINT DEFAULT 0,
	is_required_workgroup_assigned SMALLINT DEFAULT 0,
	id_icon int DEFAULT NULL,
	display_order INT DEFAULT 0,
	PRIMARY KEY (id_state)
);

-- ---------------------------------------------
-- Table structure for table workflow_action --
-- ---------------------------------------------
CREATE TABLE workflow_action
(
	id_action INT AUTO_INCREMENT,
	name VARCHAR(255) DEFAULT NULL,
	description LONG VARCHAR DEFAULT NULL,
	id_workflow INT DEFAULT NULL,
	id_state_before INT DEFAULT NULL,
	id_state_after INT DEFAULT NULL,
	id_icon INT DEFAULT NULL,
	is_automatic SMALLINT DEFAULT 0,
	is_mass_action SMALLINT DEFAULT 0,
	display_order INT DEFAULT 0,
	is_automatic_reflexive_action SMALLINT DEFAULT 0,
	PRIMARY KEY (id_action)
);

CREATE INDEX action_id_workflow_fk ON workflow_action(id_workflow);
CREATE INDEX action_id_state_before_fk ON workflow_action(id_state_before);
CREATE INDEX action_id_state_after_fk ON workflow_action(id_state_after);
CREATE INDEX action_id_icon_fk ON workflow_action(id_icon);

-- --------------------------------------------------------
-- Table structure for table workflow_resource_workflow --
-- --------------------------------------------------------
CREATE TABLE workflow_resource_workflow
(
	id_resource INT DEFAULT 0 NOT NULL,
	resource_type VARCHAR(255) NOT NULL,
	id_state INT DEFAULT NULL,
	id_workflow INT NOT NULL,
	id_external_parent INT DEFAULT NULL,
	is_associated_workgroups SMALLINT DEFAULT 0,
	PRIMARY KEY (id_resource, resource_type, id_workflow)
);

CREATE INDEX workflow_resource_workflow_id_resource_fk ON workflow_resource_workflow(id_resource);
CREATE INDEX workflow_resource_workflow_resource_type_fk ON workflow_resource_workflow(resource_type);
CREATE INDEX workflow_resource_workflow_id_workflow_fk ON workflow_resource_workflow(id_workflow);


-- -------------------------------------------------------
-- Table structure for table workflow_resource_history --
-- -------------------------------------------------------
CREATE TABLE workflow_resource_history
(
	id_history INT AUTO_INCREMENT,
	id_resource INT DEFAULT NULL,
	resource_type VARCHAR(255) DEFAULT NULL,
	id_workflow INT DEFAULT NULL,
	id_action INT DEFAULT NULL,
	creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	user_access_code VARCHAR(255) DEFAULT NULL,
	PRIMARY KEY (id_history)
);

CREATE INDEX history_id_workflow_fk ON workflow_resource_history(id_workflow);
CREATE INDEX history_id_action_fk ON workflow_resource_history(id_action);

--
-- Table Structure for table workflow_resource_user_history
--
CREATE TABLE workflow_resource_user_history (
	id_history INT NOT NULL ,
	user_access_code varchar(255) default NULL,
	email varchar(255) default '',
	first_name varchar(255) default '',
	last_name varchar(255) default '',
	realm varchar(255) default '',
	PRIMARY KEY (id_history)
);

-- -------------------------------------------
-- Table structure for table workflow_icon --
-- -------------------------------------------
CREATE TABLE workflow_icon
(
 	id_icon INT AUTO_INCREMENT,
	name VARCHAR(255) DEFAULT NULL,
	mime_type VARCHAR(50) DEFAULT NULL,
	file_value LONG VARBINARY,
	width INT DEFAULT NULL,
	height INT DEFAULT NULL,
	PRIMARY KEY (id_icon)
);

-- --------------------------------------------------
-- Table structure for table workflow_action_task --
-- --------------------------------------------------
CREATE TABLE workflow_task
(
	id_task INT AUTO_INCREMENT,
	task_type_key VARCHAR(50) DEFAULT NULL, 
	id_action INT DEFAULT 0 NOT NULL,
	display_order INT DEFAULT 0,
	PRIMARY KEY (id_task)
);

CREATE INDEX task_id_action_fk ON workflow_task(id_action);

-- ----------------------------------------------------------
-- Table structure for table workflow_task_comment_config --
-- ----------------------------------------------------------
CREATE TABLE workflow_task_comment_config
(
	id_task INT DEFAULT 0 NOT NULL,
	title VARCHAR(255) DEFAULT NULL, 
	is_mandatory SMALLINT DEFAULT 0,
	is_richtext SMALLINT DEFAULT 0,
	PRIMARY KEY (id_task)
);

-- ---------------------------------------------------------
-- Table structure for table workflow_task_comment_value --
-- ---------------------------------------------------------
CREATE TABLE workflow_task_comment_value
(
	id_history INT DEFAULT 0 NOT NULL,
	id_task INT NOT NULL,
	comment_value LONG VARCHAR DEFAULT NULL,
	PRIMARY KEY (id_history, id_task)
);

CREATE INDEX comment_value_id_history_fk ON workflow_task_comment_value(id_history);
CREATE INDEX comment_value_id_task_fk ON workflow_task_comment_value(id_task);

-- -----------------------------------------------------------
-- Table structure for table workflow_task_notification_cf --
-- -----------------------------------------------------------
CREATE TABLE workflow_task_notification_cf
(
	id_task INT DEFAULT 0 NOT NULL,
	id_mailing_list INT DEFAULT NULL,
	sender_name VARCHAR(255) DEFAULT NULL, 
	subject VARCHAR(255) DEFAULT NULL, 
	message LONG VARCHAR DEFAULT NULL,
	PRIMARY KEY (id_task)
);

-- ---------------------------------------------------------
-- Table structure for table workflow_task_assignment_cf --
-- ---------------------------------------------------------
CREATE TABLE workflow_task_assignment_cf
(
	id_task INT DEFAULT 0 NOT NULL,
	title VARCHAR(255) DEFAULT NULL, 
	is_multiple_owner SMALLINT DEFAULT 0,
	is_notify SMALLINT DEFAULT 0,
	message VARCHAR(255) DEFAULT NULL,
	subject VARCHAR(45) DEFAULT NULL, 
	is_use_user_name SMALLINT DEFAULT 0,
	PRIMARY KEY (id_task)
);

-- ---------------------------------------------------
-- Table structure for table workflow_workgroup_cf --
-- ---------------------------------------------------
CREATE TABLE workflow_workgroup_cf
(
	id_task INT DEFAULT 0 NOT NULL,
	workgroup_key VARCHAR(255) NOT NULL,
	id_mailing_list INT DEFAULT NULL,	
	PRIMARY KEY (id_task, workgroup_key)
);

-- -------------------------------------------------------------
-- Table structure for table workflow_task_assigment_history --
-- -------------------------------------------------------------
CREATE TABLE workflow_assignment_history
(
	id_history INT DEFAULT 0 NOT NULL,
	id_task INT NOT NULL,
	workgroup_key VARCHAR(255) NOT NULL,
	PRIMARY KEY (id_history, id_task, workgroup_key)
);

CREATE INDEX assignment_id_history_fk ON workflow_assignment_history(id_history);
CREATE INDEX assignment_id_task_fk ON workflow_assignment_history(id_task);

-- ---------------------------------------------------------
-- Table structure for table workflow_resource_workgroup --
-- ---------------------------------------------------------
CREATE TABLE workflow_resource_workgroup
(
	id_resource INT DEFAULT 0 NOT NULL,
	resource_type VARCHAR(255) DEFAULT NULL,
	id_workflow INT DEFAULT NULL,
	workgroup_key VARCHAR(255) DEFAULT NULL
);

CREATE INDEX workflow_resource_workgroup_id_resource_fk ON workflow_resource_workgroup(id_resource);
CREATE INDEX workflow_resource_workgroup_resource_type_fk ON workflow_resource_workgroup(resource_type);
CREATE INDEX workflow_resource_workgroup_id_workflow_fk ON workflow_resource_workgroup(id_workflow);

-- ---------------------------------------------------------
-- Table structure for table workflow_action_action 	  --
-- ---------------------------------------------------------
CREATE TABLE workflow_action_action
(
	id_action INT DEFAULT 0 NOT NULL,
	id_linked_action INT DEFAULT 0 NOT NULL,
	PRIMARY KEY (id_action, id_linked_action)
);

CREATE TABLE workflow_prerequisite
(
	id_prerequisite INT AUTO_INCREMENT,
	id_action INT NOT NULL,
	prerequisite_type VARCHAR(255) NOT NULL,
	PRIMARY KEY (id_prerequisite)
);


CREATE TABLE workflow_prerequisite_duration_cf
(
	id_prerequisite INT NOT NULL,
	duration INT NOT NULL,
	PRIMARY KEY (id_prerequisite)
);

CREATE TABLE workflow_task_archive_resource
(
	id_resource INT DEFAULT 0 NOT NULL,
	id_task INT DEFAULT 0 NOT NULL,
	archival_date TIMESTAMP NULL,
	is_archived SMALLINT DEFAULT 0,
	PRIMARY KEY (id_resource,id_task)
);

CREATE TABLE workflow_task_archive_cf
(
	id_task INT DEFAULT 0 NOT NULL,
	next_state INT NOT NULL,
	type_archival VARCHAR(255) DEFAULT NULL, 
	delay_archival INT DEFAULT NULL,
	PRIMARY KEY (id_task)
);

-- ---------------
-- Constraints --
-- ---------------
ALTER TABLE workflow_state ADD CONSTRAINT fk_state_id_workflow FOREIGN KEY (id_workflow)
	REFERENCES workflow_workflow(id_workflow) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE workflow_action ADD CONSTRAINT fk_action_id_workflow FOREIGN KEY (id_workflow)
	REFERENCES workflow_workflow(id_workflow) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE workflow_action ADD CONSTRAINT fk_action_id_state_before FOREIGN KEY (id_state_before)
	REFERENCES workflow_state(id_state) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE workflow_action ADD CONSTRAINT fk_action_id_state_after FOREIGN KEY (id_state_after)
	REFERENCES workflow_state(id_state) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE workflow_action ADD CONSTRAINT fk_action_id_icon FOREIGN KEY (id_icon)
	REFERENCES workflow_icon(id_icon) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE workflow_resource_workflow ADD CONSTRAINT fk_document_id_workflow FOREIGN KEY (id_workflow)
	REFERENCES workflow_workflow(id_workflow) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE workflow_resource_workflow ADD CONSTRAINT fk_document_id_state FOREIGN KEY (id_state)
	REFERENCES workflow_state(id_state) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE workflow_resource_history ADD CONSTRAINT fk_history_id_workflow FOREIGN KEY (id_workflow)
	REFERENCES workflow_workflow(id_workflow) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE workflow_resource_user_history ADD CONSTRAINT fk_user_id_history FOREIGN KEY (id_history)
	REFERENCES workflow_resource_history(id_history) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE workflow_resource_history ADD CONSTRAINT fk_history_id_action FOREIGN KEY (id_action)
	REFERENCES workflow_action(id_action) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE workflow_task ADD CONSTRAINT fk_task_id_action FOREIGN KEY (id_action)
	REFERENCES workflow_action(id_action) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE workflow_task_comment_value ADD CONSTRAINT fk_comment_value_id_history FOREIGN KEY (id_history)
	REFERENCES workflow_resource_history(id_history) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE workflow_task_comment_value ADD CONSTRAINT fk_comment_value_id_task FOREIGN KEY (id_task)
	REFERENCES workflow_task(id_task) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE workflow_assignment_history ADD CONSTRAINT fk_assignment_id_history FOREIGN KEY (id_history)
	REFERENCES workflow_resource_history(id_history) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE workflow_assignment_history ADD CONSTRAINT fk_assignment_id_task FOREIGN KEY (id_task)
	REFERENCES workflow_task(id_task) ON DELETE RESTRICT ON UPDATE RESTRICT;
