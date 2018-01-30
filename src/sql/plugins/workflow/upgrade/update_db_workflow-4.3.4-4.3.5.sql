/*
    Note : AUTO_INCREMENT fields work with Mysql. For PostgreSQL, you have to 
    replace AUTO_INCREMENT, depending on your PostgreSQL version. You can
    find an example on buid.properties in target/lutece/sql directory after compilation.
*/

ALTER TABLE workflow_workflow MODIFY COLUMN id_workflow INT AUTO_INCREMENT;
ALTER TABLE workflow_state MODIFY COLUMN id_state INT AUTO_INCREMENT;
ALTER TABLE workflow_action MODIFY COLUMN id_action INT AUTO_INCREMENT;
ALTER TABLE workflow_resource_history MODIFY COLUMN id_history INT AUTO_INCREMENT;
ALTER TABLE workflow_icon MODIFY COLUMN id_icon INT AUTO_INCREMENT;
ALTER TABLE workflow_task MODIFY COLUMN id_task INT AUTO_INCREMENT;
