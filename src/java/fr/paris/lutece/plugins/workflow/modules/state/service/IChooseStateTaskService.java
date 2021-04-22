package fr.paris.lutece.plugins.workflow.modules.state.service;

import java.util.List;

import fr.paris.lutece.plugins.workflow.modules.state.business.ChooseStateTaskConfig;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

/**
 * Service for IChooseStateTask
 */
public interface IChooseStateTaskService
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
     * Get the list of implemented {@link IResourceController}
     * 
     * @return
     */
    default List<IResourceController> getControllerList( )
    {
        return SpringContextService.getBeansOfType( IResourceController.class );
    }

    /**
     * Load config of task.
     * 
     * @param task
     * @return
     */
    ChooseStateTaskConfig loadConfig( ITask task );

    /**
     * Change the state of the given resource if the control is satisfied.
     * @param nIdResource
     * @param strResourceType
     * @param task
     * @param config
     * @param nIdWorkflow
     * @param oldState
     */
    void chooseNewState( int nIdResource, String strResourceType, ITask task, ChooseStateTaskConfig config, int nIdWorkflow, int oldState );
    
    /**
     * Load a resource by it History.
     * @param nIdHistory
     * @param nIdWorkflow
     * @return
     */
    ResourceWorkflow getResourceByHistory( int nIdHistory, int nIdWorkflow );
}
