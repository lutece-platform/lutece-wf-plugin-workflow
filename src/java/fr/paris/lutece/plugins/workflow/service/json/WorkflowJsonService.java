/*
 * Copyright (c) 2002-2022, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.service.json;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import fr.paris.lutece.plugins.workflow.service.prerequisite.PrerequisiteManagementService;
import fr.paris.lutece.plugins.workflow.service.task.TaskFactory;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.business.prerequisite.Prerequisite;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.service.action.ActionService;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.prerequisite.IAutomaticActionPrerequisiteService;
import fr.paris.lutece.plugins.workflowcore.service.prerequisite.IPrerequisiteManagementService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskFactory;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.service.task.TaskService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.IWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.WorkflowService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class WorkflowJsonService
{
    private static final String PROPERTY_COPY_WF_TITLE = "workflow.manage_workflow.copy_of_workflow";

    public static final WorkflowJsonService INSTANCE = new WorkflowJsonService( );

    private ObjectMapper _objectMapper;

    private IWorkflowService _workflowService = SpringContextService.getBean( WorkflowService.BEAN_SERVICE );
    private IStateService _stateService = SpringContextService.getBean( StateService.BEAN_SERVICE );
    private IActionService _actionService = SpringContextService.getBean( ActionService.BEAN_SERVICE );
    private ITaskService _taskService = SpringContextService.getBean( TaskService.BEAN_SERVICE );
    private ITaskFactory _taskFactory = SpringContextService.getBean( TaskFactory.BEAN_SERVICE );
    private IPrerequisiteManagementService _prerequisiteManagementService = SpringContextService.getBean( PrerequisiteManagementService.BEAN_NAME );

    private WorkflowJsonService( )
    {
        SimpleModule timestampModule = new SimpleModule( "TimestampModule" );
        timestampModule.addSerializer( Timestamp.class, new TimestampSerializer( ) );
        timestampModule.addDeserializer( Timestamp.class, new TimestampDeserializer( ) );

        SimpleModule taskModule = new SimpleModule( "TaskModule" );
        taskModule.addSerializer( ITask.class, new TaskSerialiser( ) );
        taskModule.addDeserializer( ITask.class, new TaskDeserializer( ) );

        _objectMapper = new ObjectMapper( ).configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false ).registerModule( timestampModule )
                .registerModule( taskModule );
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

        List<State> stateList = (List<State>) _workflowService.getAllStateByWorkflow( workflow.getId( ) );
        jsonData.setStateList( stateList );

        List<Action> actionList = new ArrayList<>( );

        ActionFilter filter = new ActionFilter( );
        filter.setIdWorkflow( workflow.getId( ) );
        actionList.addAll( _actionService.getListActionByFilter( filter ) );
       
        for( Action action : actionList ) 
        {       	
        	State actionStateAfter = _stateService.findByPrimaryKey( action.getStateAfter( ).getId( ) );
    		action.setStrUidStateAfter( actionStateAfter.getUid( ) );
    		
    		if(action.getAlternativeStateAfter().getId() != -1) {
            	State actionAlternativeStateAfter = _stateService.findByPrimaryKey( action.getAlternativeStateAfter( ).getId( ) );
        		action.setStrUidAlternativeStateAfter( actionAlternativeStateAfter.getUid( ) );
    		}
    		
    		List<String> listUidStateBefore = new ArrayList<>( );
    		action.getListIdStateBefore( ).forEach( stateId -> 
    		{
    			listUidStateBefore.add( _stateService.findByPrimaryKey( stateId ).getUid( ) );
    		});
    		action.setListUidStateBefore( listUidStateBefore );
        }
        
        jsonData.setActionList( actionList );

        List<ITask> taskList = new ArrayList<>( );
        List<Prerequisite> prerequisiteList = new ArrayList<>( );
        for ( Action action : actionList )
        {
            taskList.addAll( _taskService.getListTaskByIdAction( action.getId( ), Locale.getDefault( ) ) );
            prerequisiteList.addAll( _prerequisiteManagementService.getListPrerequisite( action.getId( ) ) );
        }
        
        for ( ITask task : taskList ) 
        {
        	task.setActionUid( _actionService.findByPrimaryKey( task.getAction( ).getId( ) ).getUid( ) );
        }
        
        for( Prerequisite prerequisite : prerequisiteList ) 
        {
        	prerequisite.setUidAction( _actionService.findByPrimaryKey( prerequisite.getIdAction() ).getUid( ) );
        }

        jsonData.setTaskList( taskList );
        jsonData.setPrerequisiteList( prerequisiteList );
        return _objectMapper.writeValueAsString( jsonData );
    }

    @Transactional
    public void jsonImportWorkflow( String json, Locale locale ) throws JsonProcessingException
    {
        WorkflowJsonData jsonData = _objectMapper.readValue( json, WorkflowJsonData.class );

        int newIdWf = importWorkflow( jsonData.getWorkflow( ), locale );

        List<State> stateList = jsonData.getStateList( );
        List<Action> actionList = jsonData.getActionList( );
        List<ITask> taskList = jsonData.getTaskList( );
        List<Prerequisite> prerequisiteList = jsonData.getPrerequisiteList( );

        importStates( newIdWf, stateList, actionList );
        importActions( actionList, taskList, prerequisiteList );
        importTasks( taskList );
        importPrerequisites( prerequisiteList );
    }

    public int importWorkflow( Workflow workflow, Locale locale )
    {
        String strNameCopyWf = I18nService.getLocalizedString( PROPERTY_COPY_WF_TITLE, locale );
        if ( strNameCopyWf != null )
        {
            workflow.setName( strNameCopyWf + workflow.getName( ) );
        }
        workflow.setEnabled( false );
        workflow.setCreationDate( WorkflowUtils.getCurrentTimestamp( ) );

        _workflowService.create( workflow );

        return workflow.getId( );
    }

    private void importStates( int newIdWf, List<State> stateList, List<Action> actionList )
    {
        Map<String, Integer> mapIdStates = new HashMap<>( );
        Workflow workflow = _workflowService.findByPrimaryKey( newIdWf );

        for ( State state : stateList )
        {
            String uid = state.getUid( );
            state.setWorkflow( workflow );
            _stateService.create( state );

            int newId = state.getId( );
            mapIdStates.put( uid, newId );
        }
        updateActionWithStateAndWf( newIdWf, actionList, mapIdStates );
    }

    private void updateActionWithStateAndWf( int newIdWf, List<Action> actionList, Map<String, Integer> mapIdStates )
    {
    	Workflow workflow = _workflowService.findByPrimaryKey( newIdWf );
        for ( Action action : actionList )
        {
            action.setWorkflow( workflow );
            action.setStateAfter( _stateService.findByPrimaryKey( mapIdStates.get( action.getStrUidStateAfter( ) ) ) );
            
            if(action.getStrUidAlternativeStateAfter( ) != null) {
            	action.setAlternativeStateAfter( _stateService.findByPrimaryKey( mapIdStates.get( action.getStrUidAlternativeStateAfter( ) ) ) );
            }
            updateStateBefore(action, mapIdStates);
        }
    }
    
    private void updateStateBefore(Action action, Map<String, Integer> mapIdStates) {
    	if(action.getListUidStateBefore( ) != null) {
        	List<Integer> StateBefore = new ArrayList<Integer>();
            for (String strStateBefore : action.getListUidStateBefore( ) )
            {
            	StateBefore.add(mapIdStates.get( strStateBefore ));
            }
            action.setListIdStateBefore( StateBefore );
    	}
    }

    private void importActions( List<Action> actionList, List<ITask> taskList, List<Prerequisite> prerequisiteList )
    {
        Map<String, Integer> mapIdActions = new HashMap<>( );

        for ( Action action : actionList )
        {
            String uid = action.getUid( );
            if(uid == null || uid.isEmpty()) 
            {
                action.setListIdStateBefore( new ArrayList<Integer>( ) );
                action.setListIdsLinkedAction( new ArrayList<Integer>( ) );
            }
            _actionService.create( action );


            int newId = action.getId( );
            mapIdActions.put( uid, newId );
        }

        // Update Linked Actions
        for ( Action action : actionList )
        {
            Collection<String> listUid = action.getListUidsLinkedAction( );

            if ( CollectionUtils.isNotEmpty( listUid ) )
            {
                Collection<Integer> listNewId = listUid.stream( ).map( mapIdActions::get ).collect( Collectors.toList( ) );
                action.setListIdsLinkedAction( listNewId );
                _actionService.update( action );
            }
        }

        updateTaskWithActions( taskList, mapIdActions );
        updatePrerequisiteWithActions( prerequisiteList, mapIdActions );
    }

    private void updateTaskWithActions( List<ITask> taskList, Map<String, Integer> mapIdActions )
    {
        for ( ITask task : taskList )
        {
        	task.setAction( _actionService.findByPrimaryKey( mapIdActions.get( task.getAction( ).getUid() ) ) );
        }
    }

    private void updatePrerequisiteWithActions( List<Prerequisite> prerequisiteList, Map<String, Integer> mapIdActions )
    {
        for ( Prerequisite prerequisite : prerequisiteList )
        {
            prerequisite.setIdAction( mapIdActions.get( prerequisite.getUidAction( ) ) );
        }
    }

    private void importTasks( List<ITask> taskList )
    {
        for ( ITask task : taskList )
        {
        	if( _taskFactory.getAllTaskTypes( ).stream( ).anyMatch( type -> type.getTitle( ) == task.getTaskType( ).getTitle( ) ) ) {
            	task.setTaskType( _taskFactory.getAllTaskTypes( ).stream( )
            			.filter( type -> type.getTitle( ) == task.getTaskType( ).getTitle( ) )
            			.findFirst( ).orElseThrow( ) );
                _taskService.create( task );
        	}
        }
    }

    private void importPrerequisites( List<Prerequisite> prerequisiteList )
    {
    	List<String> PrerequisiteTypeList = new ArrayList<String>();
    	
        for ( IAutomaticActionPrerequisiteService service : _prerequisiteManagementService.getPrerequisiteServiceList( ) )
        {
        	PrerequisiteTypeList.add( service.getPrerequisiteType( ) );
        }
    	
        for ( Prerequisite prerequisite : prerequisiteList )
        {
        	if( PrerequisiteTypeList.contains( prerequisite.getPrerequisiteType( ) ) )
        	{
                _prerequisiteManagementService.createPrerequisite( prerequisite );
        	}
        }
    }
}
