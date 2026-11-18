/*
 * Copyright (c) 2002-2026, City of Paris
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.paris.lutece.plugins.workflow.modules.duration.business.PrerequisiteDurationConfig;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.business.icon.Icon;
import fr.paris.lutece.plugins.workflowcore.business.prerequisite.Prerequisite;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.business.workflow.WorkflowFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.icon.IIconService;
import fr.paris.lutece.plugins.workflowcore.service.prerequisite.IPrerequisiteManagementService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskFactory;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.IWorkflowService;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;

/**
 * Copy (export then import) of a workflow
 */
public class WorkflowJsonServiceTest extends LuteceTestCase
{
    // not the comment task: its configuration table cannot be written in the HSQL test database (is_richtext column renamed)
    private static final String TASK_TYPE = "taskTypeNotification";

    @Inject
    private WorkflowJsonService _workflowJsonService;
    @Inject
    private IWorkflowService _workflowService;
    @Inject
    private IStateService _stateService;
    @Inject
    private IActionService _actionService;
    @Inject
    private ITaskService _taskService;
    @Inject
    private ITaskFactory _taskFactory;
    @Inject
    private IIconService _iconService;
    @Inject
    private IPrerequisiteManagementService _prerequisiteManagementService;

    @Test
    public void testCopyWorkflow( ) throws JsonProcessingException
    {
        String strName = "wf-json-" + UUID.randomUUID( );
        Workflow workflow = new Workflow( );
        workflow.setName( strName );
        workflow.setDescription( "description" );
        workflow.setCreationDate( WorkflowUtils.getCurrentTimestamp( ) );
        workflow.setEnabled( true );
        _workflowService.create( workflow );

        State s1 = createState( workflow, "S1", true, "uid-state" );
        State s2 = createState( workflow, "S2", false, UUID.randomUUID( ).toString( ) );
        // a state copied by an older version: same uid as S1
        State s3 = createState( workflow, "S3", false, "uid-state" );

        Action actionA = createAction( workflow, "A", s1, s2, "uid-action" );
        // an action copied by an older version: same uid as A
        Action actionB = createAction( workflow, "B", s1, s3, "uid-action" );

        actionA.setListIdsLinkedAction( List.of( actionB.getId( ) ) );
        _actionService.update( actionA );

        ITask task = _taskFactory.newTask( TASK_TYPE, Locale.getDefault( ) );
        task.setAction( actionA );
        task.setOrder( 1 );
        task.setUid( UUID.randomUUID( ).toString( ) );
        _taskService.create( task );

        Prerequisite prerequisite = new Prerequisite( );
        prerequisite.setIdAction( actionB.getId( ) );
        prerequisite.setPrerequisiteType( PrerequisiteDurationConfig.PREREQUISITE_TYPE );
        _prerequisiteManagementService.createPrerequisite( prerequisite );

        _workflowJsonService.jsonImportWorkflow( _workflowJsonService.jsonExportWorkflow( workflow.getId( ) ), Locale.FRENCH );

        Workflow copy = findCopy( strName );
        assertNotNull( copy );

        // WF-01: states and actions keep their own transitions, tasks and prerequisites
        Action copyA = findAction( copy, "A" );
        Action copyB = findAction( copy, "B" );
        assertEquals( "S2", _stateService.findByPrimaryKey( copyA.getStateAfter( ).getId( ) ).getName( ) );
        assertEquals( "S3", _stateService.findByPrimaryKey( copyB.getStateAfter( ).getId( ) ).getName( ) );
        List<ITask> listTasksA = _taskService.getListTaskByIdAction( copyA.getId( ), Locale.getDefault( ) );
        assertEquals( 1, listTasksA.size( ) );
        assertEquals( 0, _taskService.getListTaskByIdAction( copyB.getId( ), Locale.getDefault( ) ).size( ) );
        List<Prerequisite> listPrerequisitesB = _prerequisiteManagementService.getListPrerequisite( copyB.getId( ) );
        assertEquals( 1, listPrerequisitesB.size( ) );
        assertEquals( 0, _prerequisiteManagementService.getListPrerequisite( copyA.getId( ) ).size( ) );

        // WF-02: the link A - B is kept, and no action is linked to itself
        Collection<Integer> listLinkedA = _actionService.getListIdsLinkedAction( copyA.getId( ) );
        assertEquals( List.of( copyB.getId( ) ), new ArrayList<>( listLinkedA ) );
        assertFalse( _actionService.getListIdsLinkedAction( copyB.getId( ) ).contains( copyB.getId( ) ) );

        // WF-17: the duration prerequisite of the copy has a configuration
        assertNotNull( _prerequisiteManagementService.getPrerequisiteConfiguration( listPrerequisitesB.get( 0 ).getIdPrerequisite( ),
                _prerequisiteManagementService.getPrerequisiteService( PrerequisiteDurationConfig.PREREQUISITE_TYPE ) ) );
    }

    @Test
    public void testImportInvalidFile( )
    {
        // WF-23
        assertThrows( JsonProcessingException.class, ( ) -> _workflowJsonService.jsonImportWorkflow( "{\"workflow\":{\"name\":\"A\"}", Locale.FRENCH ) );
        assertThrows( JsonProcessingException.class, ( ) -> _workflowJsonService.jsonImportWorkflow( "{}", Locale.FRENCH ) );
    }

    @Test
    public void testImportUnknownTaskType( )
    {
        // WF-23
        String strJson = "{\"workflow\":{\"name\":\"A\"},\"stateList\":[],\"actionList\":[],\"prerequisiteList\":[],"
                + "\"taskList\":[{\"order\":1,\"uid_action\":\"a\",\"uid\":\"t\",\"taskType\":\"taskTypeDoesNotExist\"}]}";
        UnknownTaskTypeException exception = assertThrows( UnknownTaskTypeException.class,
                ( ) -> _workflowJsonService.jsonImportWorkflow( strJson, Locale.FRENCH ) );
        assertEquals( "taskTypeDoesNotExist", exception.getTaskType( ) );
    }

    private State createState( Workflow workflow, String strName, boolean bInitial, String strUid )
    {
        State state = new State( );
        state.setName( strName );
        state.setDescription( strName );
        state.setWorkflow( workflow );
        state.setInitialState( bInitial );
        state.setRequiredWorkgroupAssigned( false );
        state.setUid( strUid );
        _stateService.create( state );
        return state;
    }

    private Action createAction( Workflow workflow, String strName, State stateBefore, State stateAfter, String strUid )
    {
        Icon icon = new Icon( );
        icon.setName( "icon" );
        _iconService.create( icon );
        Action action = new Action( );
        action.setName( strName );
        action.setDescription( strName );
        action.setWorkflow( workflow );
        action.setIcon( icon );
        action.setStateAfter( stateAfter );
        action.setListIdStateBefore( new ArrayList<>( List.of( stateBefore.getId( ) ) ) );
        action.setListIdsLinkedAction( new ArrayList<>( ) );
        action.setUid( strUid );
        _actionService.create( action );
        return action;
    }

    private Workflow findCopy( String strName )
    {
        return _workflowService.getListWorkflowsByFilter( new WorkflowFilter( ) ).stream( )
                .filter( wf -> wf.getName( ) != null && wf.getName( ).endsWith( strName ) && !wf.getName( ).equals( strName ) ).findFirst( )
                .orElse( null );
    }

    private Action findAction( Workflow workflow, String strName )
    {
        ActionFilter filter = new ActionFilter( );
        filter.setIdWorkflow( workflow.getId( ) );
        return _actionService.getListActionByFilter( filter ).stream( ).filter( a -> strName.equals( a.getName( ) ) ).findFirst( ).orElseThrow( );
    }
}
