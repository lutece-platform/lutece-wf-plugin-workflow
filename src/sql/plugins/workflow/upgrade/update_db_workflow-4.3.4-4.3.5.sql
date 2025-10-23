-- liquibase formatted sql
-- changeset workflow:update_db_workflow-4.3.4-4.3.5.sql
-- preconditions onFail:MARK_RAN onError:WARN
/*
    Note : This script works with Mysql. For PostgreSQL, you have to 
    replace the queries, depending on your PostgreSQL version. You can
    find an example on buid.properties in target/lutece/sql directory after compilation.
*/
/*
    This script can be used if SQL constraints are activated. 
 */

/*
    Set AUTO-INCREMENT for table "workflow_workflow"
*/
LOCK TABLES 
  workflow_action WRITE,
  workflow_resource_workflow WRITE,
  workflow_resource_history WRITE,
  workflow_state WRITE,
  workflow_workflow WRITE;

ALTER TABLE workflow_action
  DROP FOREIGN KEY fk_action_id_workflow,
  MODIFY id_workflow int(11) DEFAULT NULL;
ALTER TABLE workflow_resource_workflow
  DROP FOREIGN KEY fk_document_id_workflow,
  MODIFY id_workflow int(11) NOT NULL DEFAULT '0';
ALTER TABLE workflow_resource_history
  DROP FOREIGN KEY fk_history_id_workflow,
  MODIFY id_workflow int(11) DEFAULT NULL;
ALTER TABLE workflow_state
  DROP FOREIGN KEY fk_state_id_workflow,
  MODIFY id_workflow int(11) DEFAULT NULL;

ALTER TABLE workflow_workflow MODIFY COLUMN id_workflow INT AUTO_INCREMENT;

ALTER TABLE workflow_action
  ADD CONSTRAINT fk_action_id_workflow FOREIGN KEY (id_workflow)
    REFERENCES workflow_workflow (id_workflow);
ALTER TABLE workflow_resource_workflow
  ADD CONSTRAINT fk_document_id_workflow FOREIGN KEY (id_workflow)
    REFERENCES workflow_workflow (id_workflow);
ALTER TABLE workflow_resource_history
  ADD CONSTRAINT fk_history_id_workflow FOREIGN KEY (id_workflow)
    REFERENCES workflow_workflow (id_workflow);
ALTER TABLE workflow_state
  ADD CONSTRAINT fk_state_id_workflow FOREIGN KEY (id_workflow)
    REFERENCES workflow_workflow (id_workflow);

UNLOCK TABLES;

/*
    Set AUTO-INCREMENT for table "workflow_state"
*/
LOCK TABLES 
  workflow_action WRITE,
  workflow_resource_workflow WRITE,
  workflow_state WRITE;

ALTER TABLE workflow_action
  DROP FOREIGN KEY fk_action_id_state_after,
  DROP FOREIGN KEY fk_action_id_state_before,
  MODIFY id_state_after int(11) DEFAULT NULL,
  MODIFY id_state_before int(11) DEFAULT NULL;
ALTER TABLE workflow_resource_workflow
  DROP FOREIGN KEY fk_document_id_state,
  MODIFY id_state int(11) DEFAULT NULL;

ALTER TABLE workflow_state MODIFY COLUMN id_state INT AUTO_INCREMENT;

ALTER TABLE workflow_action
  ADD CONSTRAINT fk_action_id_state_after FOREIGN KEY (id_state_after)
    REFERENCES workflow_state (id_state),
  ADD CONSTRAINT fk_action_id_state_before FOREIGN KEY (id_state_before)
    REFERENCES workflow_state (id_state);
ALTER TABLE workflow_resource_workflow
  ADD CONSTRAINT fk_document_id_state FOREIGN KEY (id_state)
    REFERENCES workflow_state (id_state);

UNLOCK TABLES;

/*
    Set AUTO-INCREMENT for table "workflow_action"
*/
LOCK TABLES 
  workflow_resource_history WRITE,
  workflow_task WRITE,
  workflow_action WRITE;

ALTER TABLE workflow_resource_history
  DROP FOREIGN KEY fk_history_id_action,
  MODIFY id_action int(11) DEFAULT NULL;
ALTER TABLE workflow_task
  DROP FOREIGN KEY fk_task_id_action,
  MODIFY id_action int(11) NOT NULL DEFAULT '0';

ALTER TABLE workflow_action MODIFY COLUMN id_action INT AUTO_INCREMENT;

ALTER TABLE workflow_resource_history
  ADD CONSTRAINT fk_history_id_action FOREIGN KEY (id_action)
    REFERENCES workflow_action (id_action);
ALTER TABLE workflow_task
  ADD CONSTRAINT fk_task_id_action FOREIGN KEY (id_action)
    REFERENCES workflow_action (id_action);

UNLOCK TABLES;

/*
    Set AUTO-INCREMENT for table "workflow_resource_history"
*/
LOCK TABLES 
  workflow_assignment_history WRITE,
  workflow_task_comment_value WRITE,
  workflow_resource_history WRITE;

ALTER TABLE workflow_assignment_history
  DROP FOREIGN KEY fk_assignment_id_history,
  MODIFY id_history int(11) NOT NULL DEFAULT '0';
ALTER TABLE workflow_task_comment_value
  DROP FOREIGN KEY fk_comment_value_id_history,
  MODIFY id_history int(11) NOT NULL DEFAULT '0';

ALTER TABLE workflow_resource_history MODIFY COLUMN id_history INT AUTO_INCREMENT;

ALTER TABLE workflow_assignment_history
  ADD CONSTRAINT fk_assignment_id_history FOREIGN KEY (id_history)
    REFERENCES workflow_resource_history (id_history);
ALTER TABLE workflow_task_comment_value
  ADD CONSTRAINT fk_comment_value_id_history FOREIGN KEY (id_history)
    REFERENCES workflow_resource_history (id_history);

UNLOCK TABLES;

/*
    Set AUTO-INCREMENT for table "workflow_icon"
*/
LOCK TABLES 
  workflow_action WRITE,
  workflow_icon WRITE;

ALTER TABLE workflow_action
  DROP FOREIGN KEY fk_action_id_icon,
  MODIFY id_icon int(11) DEFAULT NULL;

ALTER TABLE workflow_icon MODIFY COLUMN id_icon INT AUTO_INCREMENT;

ALTER TABLE workflow_action
  ADD CONSTRAINT fk_action_id_icon FOREIGN KEY (id_icon)
    REFERENCES workflow_icon (id_icon);

UNLOCK TABLES;

/*
    Set AUTO-INCREMENT for table "workflow_task"
*/
LOCK TABLES 
  workflow_assignment_history WRITE,
  workflow_task_comment_value WRITE,
  workflow_task WRITE;

ALTER TABLE workflow_assignment_history
  DROP FOREIGN KEY fk_assignment_id_task,
  MODIFY id_task int(11) NOT NULL DEFAULT '0';
ALTER TABLE workflow_task_comment_value
  DROP FOREIGN KEY fk_comment_value_id_task,
  MODIFY id_task int(11) NOT NULL DEFAULT '0';

ALTER TABLE workflow_task MODIFY COLUMN id_task INT AUTO_INCREMENT;

ALTER TABLE workflow_assignment_history
  ADD CONSTRAINT fk_assignment_id_task FOREIGN KEY (id_task)
    REFERENCES workflow_task (id_task);
ALTER TABLE workflow_task_comment_value
  ADD CONSTRAINT fk_comment_value_id_task FOREIGN KEY (id_task)
    REFERENCES workflow_task (id_task);

UNLOCK TABLES;
