-- liquibase formatted sql
-- changeset workflow:update_db_workflow-6.0.10-6.0.11.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- Removes what tasks left behind once removed, and what executions left once their history was removed. Only rows whose task, history or prerequisite no longer exists are deleted.
DELETE FROM workflow_task_archive_resource WHERE NOT EXISTS ( SELECT 1 FROM workflow_task t WHERE t.id_task = workflow_task_archive_resource.id_task );
DELETE FROM workflow_task_archive_cf WHERE NOT EXISTS ( SELECT 1 FROM workflow_task t WHERE t.id_task = workflow_task_archive_cf.id_task );
DELETE FROM workflow_task_comment_config WHERE NOT EXISTS ( SELECT 1 FROM workflow_task t WHERE t.id_task = workflow_task_comment_config.id_task );
DELETE FROM workflow_task_notification_cf WHERE NOT EXISTS ( SELECT 1 FROM workflow_task t WHERE t.id_task = workflow_task_notification_cf.id_task );
DELETE FROM workflow_task_assignment_cf WHERE NOT EXISTS ( SELECT 1 FROM workflow_task t WHERE t.id_task = workflow_task_assignment_cf.id_task );
DELETE FROM workflow_workgroup_cf WHERE NOT EXISTS ( SELECT 1 FROM workflow_task t WHERE t.id_task = workflow_workgroup_cf.id_task );
DELETE FROM workflow_task_choose_state_config WHERE NOT EXISTS ( SELECT 1 FROM workflow_task t WHERE t.id_task = workflow_task_choose_state_config.id_task );
DELETE FROM workflow_task_choose_state_information WHERE NOT EXISTS ( SELECT 1 FROM workflow_task t WHERE t.id_task = workflow_task_choose_state_information.id_task );
DELETE FROM workflow_task_change_state_config WHERE NOT EXISTS ( SELECT 1 FROM workflow_task t WHERE t.id_task = workflow_task_change_state_config.id_task );
DELETE FROM workflow_task_change_state_information WHERE NOT EXISTS ( SELECT 1 FROM workflow_task t WHERE t.id_task = workflow_task_change_state_information.id_task );
DELETE FROM workflow_task_confirm_action_config WHERE NOT EXISTS ( SELECT 1 FROM workflow_task t WHERE t.id_task = workflow_task_confirm_action_config.id_task );
DELETE FROM workflow_task_choice_config WHERE NOT EXISTS ( SELECT 1 FROM workflow_task t WHERE t.id_task = workflow_task_choice_config.id_task );
DELETE FROM workflow_task_choose_state_information WHERE NOT EXISTS ( SELECT 1 FROM workflow_resource_history h WHERE h.id_history = workflow_task_choose_state_information.id_history );
DELETE FROM workflow_task_change_state_information WHERE NOT EXISTS ( SELECT 1 FROM workflow_resource_history h WHERE h.id_history = workflow_task_change_state_information.id_history );
DELETE FROM workflow_prerequisite_duration_cf WHERE NOT EXISTS ( SELECT 1 FROM workflow_prerequisite p WHERE p.id_prerequisite = workflow_prerequisite_duration_cf.id_prerequisite );
