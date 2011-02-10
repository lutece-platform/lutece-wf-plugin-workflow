-- indexes on workflow_resource_workgroup
CREATE INDEX workflow_resource_workgroup_id_resource_fk ON workflow_resource_workgroup(id_resource);
CREATE INDEX workflow_resource_workgroup_resource_type_fk ON workflow_resource_workgroup(resource_type);
CREATE INDEX workflow_resource_workgroup_id_workflow_fk ON workflow_resource_workgroup(id_workflow);

-- indexes on workflow_resource_workflow
CREATE INDEX workflow_resource_workflow_id_resource_fk ON workflow_resource_workflow(id_resource);
CREATE INDEX workflow_resource_workflow_resource_type_fk ON workflow_resource_workflow(resource_type);
CREATE INDEX workflow_resource_workflow_id_workflow_fk ON workflow_resource_workflow(id_workflow);

