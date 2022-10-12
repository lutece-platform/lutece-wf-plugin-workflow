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
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import fr.paris.lutece.plugins.workflow.service.prerequisite.PrerequisiteManagementService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.business.prerequisite.Prerequisite;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.service.action.ActionService;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.prerequisite.IPrerequisiteManagementService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
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

        List<State> stateList = (List<State>) _workflowService.getAllStateByWorkflow( idWorkflow );
        jsonData.setStateList( stateList );

        List<Action> actionList = new ArrayList<>( );

        ActionFilter filter = new ActionFilter( );
        filter.setIdWorkflow( workflow.getId( ) );
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
        Map<Integer, Integer> mapIdStates = new HashMap<>( );

        for ( State state : stateList )
        {
            int oldId = state.getId( );
            state.getWorkflow( ).setId( newIdWf );
            _stateService.create( state );

            int newId = state.getId( );
            mapIdStates.put( oldId, newId );
        }

        updateActionWithStateAndWf( newIdWf, actionList, mapIdStates );
    }

    private void updateActionWithStateAndWf( int newIdWf, List<Action> actionList, Map<Integer, Integer> mapIdStates )
    {
        for ( Action action : actionList )
        {
            action.getWorkflow( ).setId( newIdWf );
            action.getStateAfter( ).setId( mapIdStates.get( action.getStateAfter( ).getId( ) ) );
            for (Integer nStateBefore : action.getListIdStateBefore( ) )
            {
            	nStateBefore = mapIdStates.get( nStateBefore );
            }
        }
    }

    private void importActions( List<Action> actionList, List<ITask> taskList, List<Prerequisite> prerequisiteList )
    {
        Map<Integer, Integer> mapIdActions = new HashMap<>( );

        for ( Action action : actionList )
        {
            int oldId = action.getId( );
            _actionService.create( action );

            int newId = action.getId( );
            mapIdActions.put( oldId, newId );
        }

        // Update Linked Actions
        for ( Action action : actionList )
        {
            Collection<Integer> listOldId = action.getListIdsLinkedAction( );

            if ( CollectionUtils.isNotEmpty( listOldId ) )
            {
                Collection<Integer> listNewId = listOldId.stream( ).map( mapIdActions::get ).collect( Collectors.toList( ) );
                action.setListIdsLinkedAction( listNewId );
                _actionService.update( action );
            }
        }

        updateTaskWithActions( taskList, mapIdActions );
        updatePrerequisiteWithActions( prerequisiteList, mapIdActions );
    }

    private void updateTaskWithActions( List<ITask> taskList, Map<Integer, Integer> mapIdActions )
    {
        for ( ITask task : taskList )
        {
            task.getAction( ).setId( mapIdActions.get( task.getAction( ).getId( ) ) );
        }
    }

    private void updatePrerequisiteWithActions( List<Prerequisite> prerequisiteList, Map<Integer, Integer> mapIdActions )
    {
        for ( Prerequisite prerequisite : prerequisiteList )
        {
            prerequisite.setIdAction( mapIdActions.get( prerequisite.getIdAction( ) ) );
        }
    }

    private void importTasks( List<ITask> taskList )
    {
        for ( ITask task : taskList )
        {
            _taskService.create( task );
        }
    }

    private void importPrerequisites( List<Prerequisite> prerequisiteList )
    {
        for ( Prerequisite prerequisite : prerequisiteList )
        {
            _prerequisiteManagementService.createPrerequisite( prerequisite );

        }
    }
}
