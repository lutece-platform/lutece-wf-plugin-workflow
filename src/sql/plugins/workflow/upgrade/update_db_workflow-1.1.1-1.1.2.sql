-- liquibase formatted sql
-- changeset workflow:update_db_workflow-1.1.1-1.1.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- Add state icon
ALTER TABLE workflow_state ADD COLUMN id_icon int DEFAULT NULL;

ALTER TABLE workflow_state ADD CONSTRAINT fk_state_id_icon FOREIGN KEY (id_icon)
      REFERENCES workflow_icon(id_icon) ON DELETE RESTRICT ON UPDATE RESTRICT;
