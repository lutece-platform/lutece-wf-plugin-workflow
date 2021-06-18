package fr.paris.lutece.plugins.workflow.modules.state.service;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.util.ReferenceList;

public abstract class AbstractStateTaskService
{

    private static final String USER_AUTO = "auto";
    
    @Inject
    protected IActionService _actionService;

    @Inject
    protected IStateService _stateService;
    
    @Inject
    protected IResourceHistoryService _resourceHistoryService;

    @Inject
    protected IResourceWorkflowService _resourceWorkflowService;
    
    public ReferenceList getListStates( int nIdAction )
    {
        ReferenceList referenceListStates = new ReferenceList( );
        Action action = _actionService.findByPrimaryKey( nIdAction );

        if ( ( action != null ) && ( action.getWorkflow( ) != null ) )
        {
            StateFilter stateFilter = new StateFilter( );
            stateFilter.setIdWorkflow( action.getWorkflow( ).getId( ) );

            List<State> listStates = _stateService.getListStateByFilter( stateFilter );

            referenceListStates.addItem( -1, StringUtils.EMPTY );
            referenceListStates.addAll( ReferenceList.convert( listStates, "id", "name", true ) );
        }

        return referenceListStates;
    }
    
    public ResourceWorkflow getResourceByHistory( int nIdHistory, int nIdWorkflow )
    {
        ResourceWorkflow resourceWorkflow = null;
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdHistory );
        if ( resourceHistory != null )
        {
            resourceWorkflow = _resourceWorkflowService.findByPrimaryKey( resourceHistory.getIdResource( ), resourceHistory.getResourceType( ), nIdWorkflow );
        }
        return resourceWorkflow;
    }
    
    public void doChangeState( ITask task, int nIdResource, String strResourceType, int nIdWorkflow, int newState )
    {
        Locale locale = I18nService.getDefaultLocale( );
        State state = _stateService.findByPrimaryKey( newState );
        Action action = _actionService.findByPrimaryKey( task.getAction( ).getId( ) );

        if ( state != null && action != null )
        {

            // Create Resource History
            ResourceHistory resourceHistory = new ResourceHistory( );
            resourceHistory.setIdResource( nIdResource );
            resourceHistory.setResourceType( strResourceType );
            resourceHistory.setAction( action );
            resourceHistory.setWorkFlow( action.getWorkflow( ) );
            resourceHistory.setCreationDate( WorkflowUtils.getCurrentTimestamp( ) );
            resourceHistory.setUserAccessCode( USER_AUTO );
            _resourceHistoryService.create( resourceHistory );

            // Update Resource
            ResourceWorkflow resourceWorkflow = _resourceWorkflowService.findByPrimaryKey( nIdResource, strResourceType, nIdWorkflow );
            resourceWorkflow.setState( state );
            _resourceWorkflowService.update( resourceWorkflow );
            saveTaskInformation( resourceHistory.getId( ), task, state );
            // Execute the relative tasks of the state in the workflow
            // We use AutomaticReflexiveActions because we don't want to change the state of the resource by executing actions.
            WorkflowService.getInstance( ).doProcessAutomaticReflexiveActions( nIdResource, strResourceType, state.getId( ), null, locale, null );
        }
    }
    
    protected abstract void saveTaskInformation( int nIdResourceHistory, ITask task, State state );
}
