/*
 * Copyright (c) 2002-2025, City of Paris
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
package fr.paris.lutece.plugins.workflow.service.workflowimport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import fr.paris.lutece.plugins.workflow.service.json.UnavailableTask;
import fr.paris.lutece.plugins.workflow.service.json.WorkflowJsonData;
import fr.paris.lutece.plugins.workflow.service.json.WorkflowJsonService;
import fr.paris.lutece.plugins.workflow.service.prerequisite.PrerequisiteManagementService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.icon.Icon;
import fr.paris.lutece.plugins.workflowcore.business.prerequisite.Prerequisite;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.service.action.ActionService;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.icon.IIconService;
import fr.paris.lutece.plugins.workflowcore.service.icon.IconService;
import fr.paris.lutece.plugins.workflowcore.service.prerequisite.IPrerequisiteManagementService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.resource.ResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.ResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.IWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.WorkflowService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Updates a workflow of the database from an export of it, through the service the import screen calls
 */
public class WorkflowImportServiceTest extends LuteceTestCase
{
    private static final String RESOURCE_TYPE = "IMPORT_TEST";

    private IWorkflowService _workflowService;
    private IStateService _stateService;
    private IActionService _actionService;
    private IPrerequisiteManagementService _prerequisiteService;

    private Workflow _workflow;
    private State _stateFirst;
    private State _stateSecond;
    private Action _actionKept;
    private Action _actionUpdated;
    private Prerequisite _prerequisite;

    @Override
    protected void setUp( ) throws Exception
    {
        super.setUp( );
        _workflowService = SpringContextService.getBean( WorkflowService.BEAN_SERVICE );
        _stateService = SpringContextService.getBean( StateService.BEAN_SERVICE );
        _actionService = SpringContextService.getBean( ActionService.BEAN_SERVICE );
        _prerequisiteService = SpringContextService.getBean( PrerequisiteManagementService.BEAN_NAME );
        IIconService iconService = SpringContextService.getBean( IconService.BEAN_SERVICE );

        _workflow = new Workflow( );
        _workflow.setUid( UUID.randomUUID( ).toString( ) );
        _workflow.setName( "import test" );
        _workflow.setDescription( "description" );
        _workflow.setCreationDate( WorkflowUtils.getCurrentTimestamp( ) );
        _workflow.setEnabled( false );
        _workflowService.create( _workflow );

        _stateFirst = state( "First", true );
        _stateSecond = state( "Second", false );

        Icon icon = new Icon( );
        icon.setName( "import test icon" );
        iconService.create( icon );

        _actionUpdated = action( "Updated", icon, _stateFirst, _stateSecond );
        _actionKept = action( "Kept", icon, _stateSecond, _stateFirst );
        _actionUpdated.setListIdsLinkedAction( new ArrayList<>( Arrays.asList( _actionKept.getId( ) ) ) );
        _actionService.update( _actionUpdated );

        _prerequisite = new Prerequisite( );
        _prerequisite.setUidPrerequisite( UUID.randomUUID( ).toString( ) );
        _prerequisite.setIdAction( _actionUpdated.getId( ) );
        _prerequisite.setPrerequisiteType( "import.test.typeNoModuleProvides" );
        _prerequisiteService.createPrerequisite( _prerequisite );

        // a resource stays in the first state and the kept action was executed : both are kept by the import
        ResourceWorkflow resource = new ResourceWorkflow( );
        resource.setIdResource( 1 );
        resource.setResourceType( RESOURCE_TYPE );
        resource.setState( _stateFirst );
        resource.setWorkFlow( _workflow );
        resource.setWorkgroups( new ArrayList<>( ) );
        ( (IResourceWorkflowService) SpringContextService.getBean( ResourceWorkflowService.BEAN_SERVICE ) ).create( resource );

        ResourceHistory history = new ResourceHistory( );
        history.setIdResource( 1 );
        history.setResourceType( RESOURCE_TYPE );
        history.setAction( _actionKept );
        history.setWorkFlow( _workflow );
        history.setCreationDate( WorkflowUtils.getCurrentTimestamp( ) );
        history.setUserAccessCode( "test" );
        ( (IResourceHistoryService) SpringContextService.getBean( ResourceHistoryService.BEAN_SERVICE ) ).create( history );
    }

    /**
     * The file drops the first state, the kept action and the prerequisite, and makes the second state initial
     */
    private WorkflowJsonData changedExport( ) throws Exception
    {
        WorkflowJsonService jsonService = WorkflowJsonService.getInstance( );
        WorkflowJsonData file = jsonService.readExportData( jsonService.jsonExportWorkflow( _workflow.getId( ) ) );
        file.getWorkflow( ).setName( "import test renamed" );
        file.setStateList( file.getStateList( ).stream( ).filter( s -> !s.getUid( ).equals( _stateFirst.getUid( ) ) ).collect( Collectors.toList( ) ) );
        file.getStateList( ).get( 0 ).setInitialState( true );
        file.setActionList( file.getActionList( ).stream( ).filter( a -> !a.getUid( ).equals( _actionKept.getUid( ) ) ).collect( Collectors.toList( ) ) );
        Action updated = file.getActionList( ).get( 0 );
        updated.setListUidStateBefore( new ArrayList<>( Arrays.asList( _stateSecond.getUid( ) ) ) );
        updated.setListUidsLinkedAction( new ArrayList<>( ) );
        file.setPrerequisiteList( new ArrayList<>( ) );
        return file;
    }

    public void testUpdate( ) throws Exception
    {
        WorkflowJsonData file = changedExport( );
        WorkflowImportService service = WorkflowImportService.getInstance( );
        String strSignature = service.analyse( file, _workflow.getId( ), Collections.emptyMap( ) ).getSignature( );

        service.update( file, _workflow.getId( ), Collections.emptyMap( ), strSignature, Locale.FRENCH );

        assertEquals( "import test renamed", _workflowService.findByPrimaryKey( _workflow.getId( ) ).getName( ) );
        State first = _stateService.findByPrimaryKey( _stateFirst.getId( ) );
        assertNotNull( "a state holding a resource is kept", first );
        assertFalse( "the kept state is no longer initial : the file has its own", first.isInitialState( ) );
        assertTrue( _stateService.findByPrimaryKey( _stateSecond.getId( ) ).isInitialState( ) );

        assertNotNull( "an executed action is kept", _actionService.findByPrimaryKey( _actionKept.getId( ) ) );
        assertTrue( "the updated action keeps its link to the kept action",
                _actionService.findByPrimaryKey( _actionUpdated.getId( ) ).getListIdsLinkedAction( ).contains( _actionKept.getId( ) ) );

        assertNull( "a prerequisite whose type no module provides is removed all the same", _prerequisiteService.findPrerequisite( _prerequisite.getIdPrerequisite( ) ) );
    }

    public void testAPlanNobodyReviewedIsRefused( ) throws Exception
    {
        WorkflowJsonData file = changedExport( );
        try
        {
            WorkflowImportService.getInstance( ).update( file, _workflow.getId( ), Collections.emptyMap( ), "reviewed elsewhere", Locale.FRENCH );
            fail( "the update applied a plan that was not reviewed" );
        }
        catch( WorkflowImportService.PlanChangedException e )
        {
            assertEquals( "nothing was written", "import test", _workflowService.findByPrimaryKey( _workflow.getId( ) ).getName( ) );
            assertNotNull( _prerequisiteService.findPrerequisite( _prerequisite.getIdPrerequisite( ) ) );
        }
    }

    public void testATaskTypeNoModuleProvidesIsReadNotRejected( ) throws Exception
    {
        WorkflowJsonService jsonService = WorkflowJsonService.getInstance( );
        String strJson = jsonService.jsonExportWorkflow( _workflow.getId( ) ).replaceFirst( "\"taskList\"\\s*:\\s*\\[\\s*\\]",
                "\"taskList\":[{\"taskType\":\"import.test.typeNoModuleProvides\",\"order\":1,\"uid_action\":\"" + _actionUpdated.getUid( )
                        + "\",\"uid\":\"task-uid\"}]" );
        WorkflowJsonData file = jsonService.readExportData( strJson );

        assertEquals( 1, file.getTaskList( ).size( ) );
        assertTrue( file.getTaskList( ).get( 0 ) instanceof UnavailableTask );
        assertEquals( "import.test.typeNoModuleProvides", file.getTaskList( ).get( 0 ).getTaskType( ).getKey( ) );
    }

    public void testDuplicateUidsAreFound( ) throws Exception
    {
        WorkflowJsonService jsonService = WorkflowJsonService.getInstance( );
        WorkflowJsonData file = jsonService.readExportData( jsonService.jsonExportWorkflow( _workflow.getId( ) ) );
        assertNull( WorkflowImportService.findDuplicateUid( file ) );

        file.getActionList( ).get( 0 ).setUid( file.getStateList( ).get( 0 ).getUid( ) );
        assertEquals( file.getStateList( ).get( 0 ).getUid( ), WorkflowImportService.findDuplicateUid( file ) );
    }

    private State state( String strName, boolean bInitial )
    {
        State state = new State( );
        state.setUid( UUID.randomUUID( ).toString( ) );
        state.setName( strName );
        state.setDescription( strName );
        state.setWorkflow( _workflow );
        state.setInitialState( bInitial );
        state.setRequiredWorkgroupAssigned( false );
        _stateService.create( state );
        return _stateService.findByPrimaryKey( state.getId( ) );
    }

    private Action action( String strName, Icon icon, State stateBefore, State stateAfter )
    {
        Action action = new Action( );
        action.setUid( UUID.randomUUID( ).toString( ) );
        action.setName( strName );
        action.setDescription( strName );
        action.setWorkflow( _workflow );
        action.setIcon( icon );
        action.setStateAfter( stateAfter );
        action.setListIdStateBefore( new ArrayList<>( Arrays.asList( stateBefore.getId( ) ) ) );
        action.setListIdsLinkedAction( new ArrayList<>( ) );
        _actionService.create( action );
        return _actionService.findByPrimaryKey( action.getId( ) );
    }
}
