package fr.paris.lutece.plugins.workflow.modules.state.service;

import fr.paris.lutece.plugins.workflow.modules.state.business.ChangeStateTaskConfig;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
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
    
    /**
     * Load a resource by it History.
     * 
     * @param nIdHistory
     * @param nIdWorkflow
     * @return
     */
    ResourceWorkflow getResourceByHistory( int nIdHistory, int nIdWorkflow );
    
    /**
     * Change State
     * @param task
     * @param nIdResource
     * @param strResourceType
     * @param nIdWorkflow
     * @param newState
     */
    void doChangeState( ITask task, int nIdResource, String strResourceType, int nIdWorkflow, int newState );
}
