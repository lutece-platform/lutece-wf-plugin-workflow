package fr.paris.lutece.plugins.workflow.modules.state.service;

import fr.paris.lutece.plugins.workflow.modules.state.business.ChangeStateTaskConfig;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.util.ReferenceList;

public interface IChangeStateTaskService
{

    /**
     * Get the list of states
     * 
     * @param nIdAction
     *            the id action
     * @return a ReferenceList
     */
    ReferenceList getListStates( int nIdAction );
    
    /**
     * Load config of task.
     * 
     * @param task
     * @return
     */
    ChangeStateTaskConfig loadConfig( ITask task );
}
