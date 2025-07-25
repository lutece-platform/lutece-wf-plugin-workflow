--liquibase formatted sql
--changeset workflow:update_db_workflow-1.0.0-1.0.1.sql
--preconditions onFail:MARK_RAN onError:WARN
DROP TABLE IF EXISTS workflow_task_notification_cf;
DROP TABLE IF EXISTS workflow_task_assignment_cf;
DROP TABLE IF EXISTS workflow_workgroup_cf;
DROP TABLE IF EXISTS workflow_assignment_history;
DROP TABLE IF EXISTS workflow_resource_workgroup;




-- ---------------------------------------------------------
-- Table structure for table workflow_task_notification_cf 	
-- ---------------------------------------------------------
CREATE TABLE workflow_task_notification_cf (
  id_task INT NOT NULL DEFAULT 0,
  id_mailing_list INT DEFAULT NULL,
  sender_name VARCHAR(255) DEFAULT NULL, 
  subject VARCHAR(255) DEFAULT NULL, 
  message LONG VARCHAR DEFAULT NULL,
  PRIMARY KEY  (id_task)
  );


-- ---------------------------------------------------------
-- alter  table workflow_state			
-- ---------------------------------------------------------
ALTER TABLE workflow_state ADD COLUMN is_required_workgroup_assigned SMALLINT DEFAULT 0;


-- ---------------------------------------------------------
-- alter  table workflow_resource_history		
-- ---------------------------------------------------------
ALTER TABLE workflow_resource_history ADD COLUMN user_access_code VARCHAR(255) DEFAULT NULL;

-- ---------------------------------------------------------
-- Table structure for table workflow_task_assignment_cf		
-- ---------------------------------------------------------
CREATE TABLE workflow_task_assignment_cf (
  id_task INT NOT NULL DEFAULT 0,
  title VARCHAR(255) DEFAULT NULL, 
  is_multiple_owner SMALLINT DEFAULT 0,
  PRIMARY KEY  (id_task)
  );

-- ---------------------------------------------------------
-- Table structure for table workflow_workgroup_cf 			
-- ---------------------------------------------------------
CREATE TABLE workflow_workgroup_cf (
  id_task INT NOT NULL DEFAULT 0,
  workgroup_key VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY  (id_task,workgroup_key)
  );




-- ---------------------------------------------------------
-- Table structure for table workflow_task_assigment_history
-- ---------------------------------------------------------
CREATE TABLE workflow_assignment_history(
  id_history INT NOT NULL DEFAULT 0,
  id_task INT DEFAULT NULL,
  workgroup_key VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY  ( id_history,id_task,workgroup_key)
  );

CREATE INDEX  assignment_id_history_fk ON workflow_assignment_history
(
   id_history
);
CREATE INDEX  assignment_id_task_fk ON workflow_assignment_history
(
   id_task
);


-- ---------------------------------------------------------
-- Table structure for table workflow_resource_workgroup		
-- ---------------------------------------------------------
CREATE TABLE workflow_resource_workgroup (
  
  id_resource INT NOT NULL DEFAULT 0,
  resource_type VARCHAR(255) DEFAULT NULL,
  id_workflow INT DEFAULT NULL,
  workgroup_key VARCHAR(255) DEFAULT NULL
  );
 

ALTER TABLE workflow_assignment_history ADD CONSTRAINT fk_assignment_id_history FOREIGN KEY (id_history)
      REFERENCES workflow_resource_history(id_history)  ON DELETE RESTRICT ON UPDATE RESTRICT ;

ALTER TABLE workflow_assignment_history ADD CONSTRAINT fk_assignment_id_task FOREIGN KEY (id_task)
      REFERENCES workflow_task(id_task)  ON DELETE RESTRICT ON UPDATE RESTRICT ;               

