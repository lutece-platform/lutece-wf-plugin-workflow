ALTER TABLE workflow_resource_workflow ADD COLUMN is_associated_workgroups SMALLINT DEFAULT 0;

UPDATE workflow_resource_workflow r 
SET r.is_associated_workgroups=1 
WHERE r.id_resource IN ( 
        SELECT  w.id_resource FROM workflow_resource_workgroup w        
        WHERE w.id_resource = r.id_resource
              AND w.id_workflow = r.id_workflow
              AND w.resource_type = r.resource_type );