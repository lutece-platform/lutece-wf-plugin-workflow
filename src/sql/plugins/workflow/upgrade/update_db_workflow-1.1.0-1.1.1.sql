ALTER TABLE workflow_action ADD COLUMN is_automatic SMALLINT DEFAULT 0;

-- /!\ WARNING THIS UPDATE MAY BREAK COMPATIBILITY
ALTER TABLE workflow_resource_workflow ADD COLUMN id_external_parent INT DEFAULT NULL;