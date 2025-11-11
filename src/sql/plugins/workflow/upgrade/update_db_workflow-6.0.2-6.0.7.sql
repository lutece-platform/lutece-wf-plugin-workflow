-- liquibase formatted sql
-- changeset workflow:update_db_workflow-6.0.2-6.0.7.sql
-- preconditions onFail:MARK_RAN onError:WARN
INSERT INTO core_admin_role_resource (rbac_id,role_key,resource_type,resource_id,permission) VALUES (924,'workflow_manager','WORKFLOW_APP','*','PERM_WORKFLOW_LIST');
