package fr.paris.lutece.plugins.workflow.modules.state.service;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.workflow.modules.state.business.ChooseStateTaskConfig;
import fr.paris.lutece.plugins.workflow.modules.state.business.ChooseStateTaskInformation;
import fr.paris.lutece.plugins.workflow.modules.state.business.ChooseStateTaskInformationHome;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.util.ReferenceList;

/**
 * Implements IChooseStateTaskService
 */
public class ChooseStateTaskService implements IChooseStateTaskService
{

    private static final String USER_AUTO = "auto";

    @Inject
    private IResourceHistoryService _resourceHistoryService;

    @Inject
    private IResourceWorkflowService _resourceWorkflowService;

    @Inject
    private IActionService _actionService;

    @Inject
    private IStateService _stateService;

    @Inject
    @Named( "workflow.chooseStateTaskConfigService" )
    private ITaskConfigService _taskConfigService;

    @Override
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

    @Override
    public ChooseStateTaskConfig loadConfig( ITask task )
    {
        ChooseStateTaskConfig config = _taskConfigService.findByPrimaryKey( task.getId( ) );
        if ( config == null )
        {
            config = new ChooseStateTaskConfig( );
            config.setIdTask( task.getId( ) );
            _taskConfigService.create( config );
        }
        return config;
    }

    private IChooseStateController getController( ChooseStateTaskConfig config )
    {
        for ( IChooseStateController controller : getControllerList( ) )
        {
            if ( controller.getName( ).equals( config.getControllerName( ) ) )
            {
                return controller;
            }
        }
        return null;
    }
    
    @Override
    public void chooseNewState( int nIdResource, String strResourceType, ITask task, ChooseStateTaskConfig config, int nIdWorkflow, int oldState )
    {
        IChooseStateController controller = getController( config );
        if ( controller == null )
        {
            return;
        }

        int newState = -1;
        if ( controller.control( nIdResource, strResourceType ) )
        {
            newState = config.getIdStateOK( );
        }
        else
        {
            newState = config.getIdStateKO( );
        }

        if ( newState != -1 && newState != oldState )
        {
            doChangeState( task, nIdResource, strResourceType, nIdWorkflow, newState );
        }
    }

    private void doChangeState( ITask task, int nIdResource, String strResourceType, int nIdWorkflow, int newState )
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

    private void saveTaskInformation( int nIdResourceHistory, ITask task, State state )
    {
        ChooseStateTaskInformation taskInformation = new ChooseStateTaskInformation( );
        taskInformation.setIdHistory( nIdResourceHistory );
        taskInformation.setIdTask( task.getId( ) );
        taskInformation.setNewState( state.getName( ) );

        ChooseStateTaskInformationHome.create( taskInformation );
    }
    
    @Override
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
}
