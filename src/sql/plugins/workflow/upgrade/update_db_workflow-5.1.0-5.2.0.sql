DROP TABLE IF EXISTS workflow_task_change_state_config;
CREATE TABLE workflow_task_change_state_config (
	 id_task INT DEFAULT 0 NOT NULL ,
	 id_next_state INT NOT NULL,
	 id_state_ko INT NOT NULL,
);
