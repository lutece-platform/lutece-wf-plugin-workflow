-- liquibase formatted sql
-- changeset workflow:init_core_workflow.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- ---------------------------------------------------------
--	Init  table core_admin_right							
-- ---------------------------------------------------------
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url, documentation_url) VALUES ('WORKFLOW_MANAGEMENT','workflow.adminFeature.workflow_management.name',2,'jsp/admin/plugins/workflow/ManageWorkflow.jsp','workflow.adminFeature.workflow_management.description',0,'workflow','APPLICATIONS','images/admin/skin/plugins/workflow/workflow.png', 'jsp/admin/documentation/AdminDocumentation.jsp?doc=admin-workflow');

-- ---------------------------------------------------------
--	Init  table  core_admin_role								
-- ---------------------------------------------------------
INSERT INTO core_admin_role (role_key,role_description) VALUES ('workflow_manager','Workflow management');

-- ---------------------------------------------------------
--	Init  table  core_admin_role_resource					
-- ---------------------------------------------------------
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('workflow_manager','WORKFLOW_ACTION_TYPE','*','*');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('workflow_manager','WORKFLOW_STATE_TYPE','*','*');


INSERT INTO core_user_right (id_right,id_user) VALUES ('WORKFLOW_MANAGEMENT',1);
INSERT INTO core_user_right (id_right,id_user) VALUES('WORKFLOW_MANAGEMENT',2);

--
-- Dumping data for table core_user_role
--
INSERT INTO core_user_role (role_key,id_user) VALUES ('workflow_manager',1);
INSERT INTO core_user_role (role_key,id_user) VALUES ('workflow_manager',2);

--
-- Init  table core_admin_dashboard
--
INSERT INTO core_admin_dashboard(dashboard_name, dashboard_column, dashboard_order) VALUES('workflowAdminDashboardComponent', 1, 1);

--
-- Init  table core_dashboard
--
INSERT INTO core_dashboard(dashboard_name, dashboard_column, dashboard_order) VALUES('WORKFLOW', 3, 1);
