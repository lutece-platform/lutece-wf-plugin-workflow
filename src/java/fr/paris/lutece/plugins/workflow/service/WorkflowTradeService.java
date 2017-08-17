package fr.paris.lutece.plugins.workflow.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fr.paris.lutece.plugins.workflow.modules.notification.service.TaskNotification;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfig;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.task.ITaskType;
import fr.paris.lutece.plugins.workflowcore.business.task.TaskType;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.business.workflow.WorkflowFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.ActionService;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.service.task.TaskService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.IWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.WorkflowService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class WorkflowTradeService
{

    // PROPERTIES
    private static final String ACTION = "action";
    private static final String ACTIONS = "actions";
    private static final String CONFIGS = "configs";
    private static final String ID = "id";
    private static final String ID_TASK = "idTask";
    private static final String IMPORT = "Import";
    private static final String KEY = "key";
    private static final String STATES = "states";
    private static final String TASKS = "tasks";
    private static final String TASK_TYPE = "taskType";
    private static final String WORKFLOW = "workflow";

    private static IActionService _actionService = SpringContextService.getBean( ActionService.BEAN_SERVICE );
    private static IWorkflowService _workflowService = SpringContextService.getBean( WorkflowService.BEAN_SERVICE );
    private static IStateService _stateService = SpringContextService.getBean( StateService.BEAN_SERVICE );
    private static ITaskService _taskService = SpringContextService.getBean( TaskService.BEAN_SERVICE );

    private static ObjectMapper _mapper = new ObjectMapper( ).registerModule( new JavaTimeModule( ) ).setSerializationInclusion( Include.NON_NULL );

    public static int importWorkflowFromJson( JSONObject jsonObject )
    {
        int nIdWorkflow = 0;
        try
        {
            Workflow workflow = null;
            Object objectWorkflow = jsonObject.get( WORKFLOW );
            if ( objectWorkflow != null )
            {
                workflow = _mapper.readValue( objectWorkflow.toString( ), Workflow.class );
            }
            if ( workflow != null )
            {
                // To avoid duplicate name
                WorkflowFilter filter = new WorkflowFilter( );
                filter.setName( workflow.getName( ) );
                // Check if a workflow with this name already exists
                if ( CollectionUtils.isNotEmpty( _workflowService.getListWorkflowsByFilter( filter ) ) )
                {
                    // Add import in front of the name
                    workflow.setName( IMPORT + StringUtils.SPACE + workflow.getName( ) );
                }
                _workflowService.create( workflow );
                nIdWorkflow = workflow.getId( );
                HashMap<Integer, State> mapIdState = importStates( jsonObject, workflow );
                HashMap<Integer, Action> mapIdAction = importActions( jsonObject, mapIdState, workflow );
                HashMap<Integer, Integer> mapIdTask = importTasks( jsonObject, mapIdAction );
                importConfigs( jsonObject, mapIdTask );
            }
        }
        catch( IOException | ClassNotFoundException e )
        {
            AppLogService.error( "Erreur lors de l'import du json", e );
        }
        return nIdWorkflow;
    }

    public static JSONObject exportWorkflowToJson( int nIdWorkflow, Locale locale )
    {
        JSONObject jsonObject = new JSONObject( );
        Workflow workflow = _workflowService.findByPrimaryKey( nIdWorkflow );
        try
        {
            if ( workflow != null )
            {
                jsonObject.put( WORKFLOW, _mapper.writeValueAsString( workflow ) );
                exportStates( jsonObject, nIdWorkflow );
                exportActionsTasksAndConfigs( jsonObject, nIdWorkflow, locale );
            }
        }
        catch( IOException e )
        {
            AppLogService.error( "Error during import of the json", e );
        }
        return jsonObject;
    }

    private static HashMap<Integer, State> importStates( JSONObject jsonObject, Workflow workflow ) throws JsonParseException, JsonMappingException,
            IOException
    {
        HashMap<Integer, State> mapIdState = new HashMap<>( );
        List<State> listStates = new ArrayList<>( );
        JSONArray jsArrayStates = null;
        if ( jsonObject.containsKey( STATES ) )
        {
            jsArrayStates = jsonObject.getJSONArray( STATES );
        }
        if ( CollectionUtils.isNotEmpty( jsArrayStates ) )
        {
            listStates = Arrays.asList( _mapper.readValue( jsArrayStates.toString( ), State [ ].class ) );
        }
        int nOldIdState;
        for ( State state : listStates )
        {
            nOldIdState = state.getId( );
            state.setWorkflow( workflow );
            _stateService.create( state );
            mapIdState.put( nOldIdState, state );
        }
        return mapIdState;
    }

    private static HashMap<Integer, Action> importActions( JSONObject jsonObject, HashMap<Integer, State> mapIdState, Workflow workflow )
            throws JsonParseException, JsonMappingException, IOException
    {
        HashMap<Integer, Action> mapIdAction = new HashMap<>( );
        List<Action> listActions = new ArrayList<>( );
        JSONArray jsArrayActions = null;
        if ( jsonObject.containsKey( ACTIONS ) )
        {
            jsArrayActions = jsonObject.getJSONArray( ACTIONS );
        }
        if ( CollectionUtils.isNotEmpty( jsArrayActions ) )
        {
            listActions = Arrays.asList( _mapper.readValue( jsArrayActions.toString( ), Action [ ].class ) );
        }
        int nOldIdAction;
        for ( Action action : listActions )
        {
            nOldIdAction = action.getId( );
            action.setStateBefore( mapIdState.get( action.getStateBefore( ).getId( ) ) );
            action.setStateAfter( mapIdState.get( action.getStateAfter( ).getId( ) ) );
            action.setWorkflow( workflow );
            _actionService.create( action );
            mapIdAction.put( nOldIdAction, action );
        }
        List<Integer> listNewIdsLinkedAction;
        for ( Action action : listActions )
        {
            if ( CollectionUtils.isNotEmpty( action.getListIdsLinkedAction( ) ) )
            {
                listNewIdsLinkedAction = new ArrayList<>( );
                for ( int oldIdLinkedAction : action.getListIdsLinkedAction( ) )
                {
                    listNewIdsLinkedAction.add( mapIdAction.get( oldIdLinkedAction ).getId( ) );
                }
                action.setListIdsLinkedAction( listNewIdsLinkedAction );
                _actionService.update( action );
            }
        }
        return mapIdAction;
    }

    private static HashMap<Integer, Integer> importTasks( JSONObject jsonObject, HashMap<Integer, Action> mapIdAction )
    {
        HashMap<Integer, Integer> mapIdTask = new HashMap<>( );
        JSONArray jsArrayTasks = null;
        if ( jsonObject.containsKey( TASKS ) )
        {
            jsArrayTasks = jsonObject.getJSONArray( TASKS );
        }
        if ( CollectionUtils.isNotEmpty( jsArrayTasks ) )
        {
            JSONObject jsObjTask;
            for ( Object objTask : jsArrayTasks )
            {
                jsObjTask = JSONObject.fromObject( objTask );
                ITask task = new TaskNotification( );
                JSONObject jsObjectAction = jsObjTask.getJSONObject( ACTION );
                Action action = new Action( );
                action.setId( mapIdAction.get( jsObjectAction.getInt( ID ) ).getId( ) );
                JSONObject jsonObjectTaskType = jsObjTask.getJSONObject( TASK_TYPE );
                ITaskType taskType = new TaskType( );
                taskType.setKey( jsonObjectTaskType.getString( KEY ) );
                task.setAction( action );
                int nMaximumOrder = _taskService.findMaximumOrderByActionId( task.getAction( ).getId( ) );
                task.setOrder( nMaximumOrder + 1 );
                task.setTaskType( taskType );
                _taskService.create( task );
                mapIdTask.put( jsObjTask.getInt( ID ), task.getId( ) );
            }
        }
        return mapIdTask;
    }

    private static void importConfigs( JSONObject jsonObject, HashMap<Integer, Integer> mapIdTask ) throws JsonParseException, JsonMappingException,
            IOException, ClassNotFoundException
    {
        JSONArray jsArrayConfigs = null;
        if ( jsonObject.containsKey( CONFIGS ) )
        {
            jsArrayConfigs = jsonObject.getJSONArray( CONFIGS );
        }
        List<ITaskConfigService> listTaskConfigService = SpringContextService.getBeansOfType( ITaskConfigService.class );
        if ( CollectionUtils.isNotEmpty( jsArrayConfigs ) )
        {
            for ( Object object : jsArrayConfigs )
            {
                JSONArray jsArrayObject = (JSONArray) object;
                String className = String.valueOf( jsArrayObject.get( 0 ) );
                Class<?> classImpl = Class.forName( className );
                ITaskConfig taskConfig = (ITaskConfig) _mapper.readValue( jsArrayObject.get( 1 ).toString( ), classImpl );
                JSONObject jsObject = JSONObject.fromObject( jsArrayObject.get( 1 ) );
                taskConfig.setIdTask( mapIdTask.get( jsObject.getInt( ID_TASK ) ) );
                for ( ITaskConfigService configService : listTaskConfigService )
                {
                    try
                    {
                        configService.create( taskConfig );
                        break;
                    }
                    catch( Exception e )
                    {
                        continue;
                    }
                }
            }
        }
    }

    private static void exportStates( JSONObject jsonObject, int nIdWorkflow ) throws JsonProcessingException
    {
        JSONArray jsStates = new JSONArray( );
        List<State> listStates = new ArrayList<>( );
        listStates.addAll( _workflowService.getAllStateByWorkflow( nIdWorkflow ) );
        for ( State state : listStates )
        {
            jsStates.add( _mapper.writeValueAsString( state ) );
        }
        if ( CollectionUtils.isNotEmpty( jsStates ) )
        {
            jsonObject.put( STATES, jsStates );
        }
    }

    private static void exportActionsTasksAndConfigs( JSONObject jsonObject, int nIdWorkflow, Locale locale ) throws JsonProcessingException
    {
        Locale localeToUse = Locale.getDefault( );
        if ( locale != null )
        {
            localeToUse = locale;
        }
        JSONArray jsActions = new JSONArray( );
        JSONArray jsTasks = new JSONArray( );
        JSONArray jsConfig = new JSONArray( );
        ActionFilter actionFilter = new ActionFilter( );
        actionFilter.setIdWorkflow( nIdWorkflow );
        List<Action> listActions = _actionService.getListActionByFilter( actionFilter );
        ITaskConfig taskConfig = null;
        for ( Action action : listActions )
        {
            jsActions.add( _mapper.writeValueAsString( action ) );
            List<ITask> listTasks = _taskService.getListTaskByIdAction( action.getId( ), localeToUse );
            for ( ITask task : listTasks )
            {
                jsTasks.add( _mapper.writeValueAsString( task ) );
                List<ITaskConfigService> listTaskConfigService = SpringContextService.getBeansOfType( ITaskConfigService.class );
                for ( ITaskConfigService taskConfigService : listTaskConfigService )
                {
                    taskConfig = taskConfigService.findByPrimaryKey( task.getId( ) );
                    if ( taskConfig != null )
                    {
                        List<Object> listObjects = new ArrayList<>( );
                        listObjects.add( taskConfig.getClass( ) );
                        listObjects.add( taskConfig );
                        jsConfig.add( listObjects );
                    }
                }
            }
        }
        if ( CollectionUtils.isNotEmpty( jsActions ) )
        {
            jsonObject.put( ACTIONS, jsActions );
        }
        if ( CollectionUtils.isNotEmpty( jsTasks ) )
        {
            jsonObject.put( TASKS, jsTasks );
        }
        if ( CollectionUtils.isNotEmpty( jsConfig ) )
        {
            jsonObject.put( CONFIGS, jsConfig );
        }
    }
}
