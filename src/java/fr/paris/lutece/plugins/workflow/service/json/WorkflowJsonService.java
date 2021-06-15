package fr.paris.lutece.plugins.workflow.service.json;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import fr.paris.lutece.plugins.workflow.service.prerequisite.PrerequisiteManagementService;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.business.prerequisite.Prerequisite;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.service.action.ActionService;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.prerequisite.IPrerequisiteManagementService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.service.task.TaskService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.IWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.WorkflowService;
import fr.paris.lutece.portal.service.spring.SpringContextService;


public class WorkflowJsonService
{

    public static final WorkflowJsonService INSTANCE = new WorkflowJsonService( );

    private ObjectMapper _objectMapper;
    
    private IWorkflowService _workflowService = SpringContextService.getBean( WorkflowService.BEAN_SERVICE );
    private IActionService _actionService = SpringContextService.getBean( ActionService.BEAN_SERVICE );
    private ITaskService _taskService = SpringContextService.getBean( TaskService.BEAN_SERVICE );
    private IPrerequisiteManagementService _prerequisiteManagementService = SpringContextService.getBean( PrerequisiteManagementService.BEAN_NAME );

    private WorkflowJsonService( )
    {
        SimpleModule timestampModule = new SimpleModule( "TimestampModule" );
        timestampModule.addSerializer( Timestamp.class, new TimestampSerializer( ) );
        timestampModule.addDeserializer( Timestamp.class, new TimestampDeserializer( ) );

        _objectMapper = new ObjectMapper( ).configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false ).registerModule( timestampModule );
    }

    public static WorkflowJsonService getInstance( )
    {
        return INSTANCE;
    }
    
    /**
     * Export the workflow as a Json Object.
     * 
     * @return
     * @throws JsonProcessingException
     */
    public String jsonExportWorkflow( int idWorkflow ) throws JsonProcessingException
    {
        WorkflowJsonData jsonData = new WorkflowJsonData( );
        
        Workflow workflow = _workflowService.findByPrimaryKey( idWorkflow );
        jsonData.setWorkflow( workflow );
        
        List<State> stateList = (List<State>) _workflowService.getAllStateByWorkflow( idWorkflow );
        jsonData.setStateList( stateList );
        
        List<Action> actionList = new ArrayList<>( );
        
        ActionFilter filter = new ActionFilter( );
        filter.setIdWorkflow( workflow.getId( ) );
        
        filter.setAutomaticReflexiveAction( true );
        actionList.addAll( _actionService.getListActionByFilter( filter ) );
        
        filter.setAutomaticReflexiveAction( false );
        actionList.addAll( _actionService.getListActionByFilter( filter ) );
        
        jsonData.setActionList( actionList );
        
        List<ITask> taskList = new ArrayList<>( );
        List<Prerequisite> prerequisiteList = new ArrayList<>( );
        for ( Action action : actionList )
        {
            taskList.addAll( _taskService.getListTaskByIdAction( action.getId( ), Locale.getDefault( ) ) );
            prerequisiteList.addAll( _prerequisiteManagementService.getListPrerequisite( action.getId( ) ) );
            
        }
        jsonData.setTaskList( taskList );
        jsonData.setPrerequisiteList( prerequisiteList );
        return _objectMapper.writeValueAsString( jsonData );
    }
}
