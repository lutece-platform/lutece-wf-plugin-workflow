--
-- Update table core_admin_right
--
UPDATE core_admin_right SET admin_url = 'jsp/admin/plugins/workflow/ManageWorkflow.jsp' WHERE id_right = 'WORKFLOW_MANAGEMENT';

--
-- Init  table core_admin_dashboard
--
INSERT INTO core_admin_dashboard(dashboard_name, dashboard_column, dashboard_order) VALUES('workflowAdminDashboardComponent', 1, 1);

--
-- Init  table core_dashboard
--
INSERT INTO core_dashboard(dashboard_name, dashboard_column, dashboard_order) VALUES('WORKFLOW', 3, 1);
