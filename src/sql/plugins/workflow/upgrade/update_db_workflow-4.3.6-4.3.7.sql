-- liquibase formatted sql
-- changeset workflow:update_db_workflow-4.3.6-4.3.7.sql
-- preconditions onFail:MARK_RAN onError:WARN
/*
    Note : AUTO_INCREMENT fields work with Mysql. For PostgreSQL, you have to 
    replace AUTO_INCREMENT, depending on your PostgreSQL version. You can
    find an example on buid.properties in target/lutece/sql directory after compilation.
*/

ALTER TABLE workflow_prerequisite MODIFY COLUMN id_prerequisite INT AUTO_INCREMENT;
