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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import fr.paris.lutece.plugins.workflow.business.workflowimport.IWorkflowImportDAO;
import fr.paris.lutece.plugins.workflow.business.workflowimport.ImportElement;
import fr.paris.lutece.plugins.workflow.business.workflowimport.WorkflowImportDAO;
import fr.paris.lutece.plugins.workflow.service.json.WorkflowJsonData;
import fr.paris.lutece.plugins.workflow.service.json.WorkflowJsonService;
import fr.paris.lutece.plugins.workflow.service.prerequisite.PrerequisiteManagementService;
import fr.paris.lutece.plugins.workflow.service.task.TaskFactory;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.icon.Icon;
import fr.paris.lutece.plugins.workflowcore.business.prerequisite.Prerequisite;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.service.action.ActionService;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.icon.IIconService;
import fr.paris.lutece.plugins.workflowcore.service.icon.IconService;
import fr.paris.lutece.plugins.workflowcore.service.prerequisite.IPrerequisiteManagementService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskFactory;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.service.task.TaskService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.IWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.WorkflowService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sql.TransactionManager;

/**
 * Imports a workflow into an existing one : the elements matched by UUID or paired are updated in place, so that the resources in its states and the
 * history of its actions stay valid.
 */
public final class WorkflowImportService
{
    private static final WorkflowImportService INSTANCE = new WorkflowImportService( );

    private final IWorkflowImportDAO _importDAO = SpringContextService.getBean( WorkflowImportDAO.BEAN_NAME );
    private final IWorkflowService _workflowService = SpringContextService.getBean( WorkflowService.BEAN_SERVICE );
    private final IStateService _stateService = SpringContextService.getBean( StateService.BEAN_SERVICE );
    private final IActionService _actionService = SpringContextService.getBean( ActionService.BEAN_SERVICE );
    private final ITaskService _taskService = SpringContextService.getBean( TaskService.BEAN_SERVICE );
    private final ITaskFactory _taskFactory = SpringContextService.getBean( TaskFactory.BEAN_SERVICE );
    private final IIconService _iconService = SpringContextService.getBean( IconService.BEAN_SERVICE );
    private final IPrerequisiteManagementService _prerequisiteService = SpringContextService.getBean( PrerequisiteManagementService.BEAN_NAME );

    private WorkflowImportService( )
    {
    }

    public static WorkflowImportService getInstance( )
    {
        return INSTANCE;
    }

    /**
     * @param strUid
     *            the UUID of an imported workflow
     * @return the workflows of this instance carrying it : several when copies were made before copies got their own UUIDs
     */
    public List<Workflow> findWorkflowsCarrying( String strUid )
    {
        return ( strUid == null ) ? Collections.emptyList( )
                : _importDAO.selectWorkflowIdsByUid( strUid ).stream( ).map( _workflowService::findByPrimaryKey ).collect( Collectors.toList( ) );
    }

    /**
     * Compares an imported workflow with the workflow it would update, without writing anything
     *
     * @param fileData
     *            the imported workflow
     * @param nIdTarget
     *            the workflow to update
     * @param mapChoice
     *            the choices of the correspondence table
     * @return the plan
     */
    public WorkflowImportPlan analyse( WorkflowJsonData fileData, int nIdTarget, Map<String, Integer> mapChoice )
    {
        return WorkflowImportPlanner.plan( fileData, WorkflowJsonService.getInstance( ).buildExportData( nIdTarget ), readContext( nIdTarget ), mapChoice );
    }

    private ImportContext readContext( int nIdTarget )
    {
        ImportContext context = new ImportContext( ).setResourceByState( _importDAO.selectResourceCountByState( nIdTarget ) )
                .setHistoryByAction( _importDAO.selectHistoryCountByAction( nIdTarget ) )
                .setLastExecutionByAction( _importDAO.selectLastExecutionByAction( nIdTarget ) )
                .setLastImportDate( _importDAO.selectLastImportDate( nIdTarget ) );
        for ( ImportElement element : ImportElement.values( ) )
        {
            context.setStoredHashes( element, _importDAO.selectImportHashes( element, nIdTarget ) );
        }
        return context;
    }

    /**
     * Imports a workflow as a new one
     *
     * @param fileData
     *            the imported workflow
     * @param bNewUids
     *            true to give it new UUIDs, unrelated to the file ; false for a first import, which keeps the UUIDs and records the fingerprints a later
     *            update compares with
     * @param locale
     *            the locale
     * @return the identifier of the new workflow
     */
    public int importAsNew( WorkflowJsonData fileData, boolean bNewUids, Locale locale )
    {
        TransactionManager.beginTransaction( WorkflowUtils.getPlugin( ) );
        try
        {
            int nIdWorkflow = WorkflowJsonService.getInstance( ).importAsNewWorkflow( fileData, bNewUids, locale );
            if ( !bNewUids )
            {
                recordImport( analyse( fileData, nIdWorkflow, Collections.emptyMap( ) ), nIdWorkflow, fileData.getWorkflow( ).getUid( ),
                        Collections.emptyMap( ) );
            }
            TransactionManager.commitTransaction( WorkflowUtils.getPlugin( ) );
            return nIdWorkflow;
        }
        catch( RuntimeException e )
        {
            TransactionManager.rollBack( WorkflowUtils.getPlugin( ) );
            throw e;
        }
    }

    /**
     * Updates an existing workflow with an imported one, in one transaction
     *
     * @param fileData
     *            the imported workflow
     * @param nIdTarget
     *            the workflow to update
     * @param mapChoice
     *            the choices of the correspondence table
     * @param locale
     *            the locale
     * @return what could not be carried, for the administrator : task or prerequisite types this instance does not provide
     */
    public List<String> update( WorkflowJsonData fileData, int nIdTarget, Map<String, Integer> mapChoice, Locale locale )
    {
        // the plan is computed again from the database : what was shown may be stale
        WorkflowImportPlan plan = analyse( fileData, nIdTarget, mapChoice );
        List<String> listSkipped = new ArrayList<>( );

        TransactionManager.beginTransaction( WorkflowUtils.getPlugin( ) );
        try
        {
            Map<String, Integer> mapId = new HashMap<>( );
            applyStates( fileData, nIdTarget, plan, mapId );
            applyActions( fileData, nIdTarget, plan, mapId );
            applyTasks( fileData, plan, mapId, listSkipped, locale );
            applyPrerequisites( fileData, plan, mapId, listSkipped );
            removeAbsent( plan, locale );

            Workflow workflow = _workflowService.findByPrimaryKey( nIdTarget );
            workflow.setName( fileData.getWorkflow( ).getName( ) );
            workflow.setDescription( fileData.getWorkflow( ).getDescription( ) );
            _workflowService.update( workflow );

            recordImport( plan, nIdTarget, fileData.getWorkflow( ).getUid( ), mapId );
            TransactionManager.commitTransaction( WorkflowUtils.getPlugin( ) );
        }
        catch( RuntimeException e )
        {
            TransactionManager.rollBack( WorkflowUtils.getPlugin( ) );
            throw e;
        }
        return listSkipped;
    }

    private void applyStates( WorkflowJsonData fileData, int nIdTarget, WorkflowImportPlan plan, Map<String, Integer> mapId )
    {
        Map<String, State> mapFile = byUid( fileData.getStateList( ), State::getUid );
        Workflow workflow = _workflowService.findByPrimaryKey( nIdTarget );

        for ( ImportLine line : plan.getLines( ImportElement.STATE ) )
        {
            if ( line.getFileItem( ) == null )
            {
                continue;
            }
            State source = mapFile.get( line.getFileItem( ).getUid( ) );
            State state = ( line.getTargetItem( ) == null ) ? new State( ) : _stateService.findByPrimaryKey( line.getTargetItem( ).getId( ) );
            state.setUid( source.getUid( ) );
            state.setName( source.getName( ) );
            state.setDescription( source.getDescription( ) );
            state.setInitialState( source.isInitialState( ) );
            state.setRequiredWorkgroupAssigned( source.isRequiredWorkgroupAssigned( ) );
            state.setOrder( source.getOrder( ) );

            if ( line.getTargetItem( ) == null )
            {
                state.setWorkflow( workflow );
                _stateService.create( state );
            }
            else
            {
                _stateService.update( state );
            }
            mapId.put( source.getUid( ), state.getId( ) );
        }
    }

    private void applyActions( WorkflowJsonData fileData, int nIdTarget, WorkflowImportPlan plan, Map<String, Integer> mapId )
    {
        Map<String, Action> mapFile = byUid( fileData.getActionList( ), Action::getUid );
        Workflow workflow = _workflowService.findByPrimaryKey( nIdTarget );
        List<Action> listWritten = new ArrayList<>( );

        for ( ImportLine line : plan.getLines( ImportElement.ACTION ) )
        {
            if ( line.getFileItem( ) == null )
            {
                continue;
            }
            Action source = mapFile.get( line.getFileItem( ).getUid( ) );
            boolean bNew = line.getTargetItem( ) == null;
            Action action = bNew ? new Action( ) : _actionService.findByPrimaryKey( line.getTargetItem( ).getId( ) );
            action.setUid( source.getUid( ) );
            action.setName( source.getName( ) );
            action.setDescription( source.getDescription( ) );
            action.setAutomaticState( source.isAutomaticState( ) );
            action.setMassAction( source.isMassAction( ) );
            action.setAutomaticReflexiveAction( source.isAutomaticReflexiveAction( ) );
            action.setOrder( source.getOrder( ) );
            action.setStateAfter( _stateService.findByPrimaryKey( mapId.get( source.getStrUidStateAfter( ) ) ) );
            action.setAlternativeStateAfter( ( source.getStrUidAlternativeStateAfter( ) == null ) ? null
                    : _stateService.findByPrimaryKey( mapId.get( source.getStrUidAlternativeStateAfter( ) ) ) );
            action.setListIdStateBefore( ( source.getListUidStateBefore( ) == null ) ? new ArrayList<>( )
                    : source.getListUidStateBefore( ).stream( ).map( mapId::get ).collect( Collectors.toList( ) ) );
            // the links are written once every action exists
            action.setListIdsLinkedAction( new ArrayList<>( ) );

            if ( bNew )
            {
                action.setWorkflow( workflow );
                action.setIcon( iconFor( source.getIcon( ) ) );
                _actionService.create( action );
            }
            else
            {
                _actionService.update( action );
            }
            mapId.put( source.getUid( ), action.getId( ) );
            listWritten.add( action );
        }

        for ( Action action : listWritten )
        {
            Action source = mapFile.get( action.getUid( ) );
            if ( source.getListUidsLinkedAction( ) != null && !source.getListUidsLinkedAction( ).isEmpty( ) )
            {
                action.setListIdsLinkedAction( source.getListUidsLinkedAction( ).stream( ).map( mapId::get ).filter( id -> id != null )
                        .collect( Collectors.toList( ) ) );
                _actionService.update( action );
            }
        }
    }

    /**
     * The icon of a new action : the icon of this instance with the same name, otherwise the icon the file carries, created here
     */
    private Icon iconFor( Icon fileIcon )
    {
        if ( fileIcon == null || fileIcon.getName( ) == null )
        {
            return _iconService.getListIcons( ).stream( ).findFirst( ).orElse( null );
        }
        for ( Icon icon : _iconService.getListIcons( ) )
        {
            if ( ImportNames.same( icon.getName( ), fileIcon.getName( ) ) )
            {
                return icon;
            }
        }
        Icon icon = new Icon( );
        icon.setName( fileIcon.getName( ) );
        icon.setMimeType( fileIcon.getMimeType( ) );
        icon.setValue( fileIcon.getValue( ) );
        icon.setWidth( fileIcon.getWidth( ) );
        icon.setHeight( fileIcon.getHeight( ) );
        _iconService.create( icon );
        return icon;
    }

    private void applyTasks( WorkflowJsonData fileData, WorkflowImportPlan plan, Map<String, Integer> mapId, List<String> listSkipped, Locale locale )
    {
        Map<String, ITask> mapFile = byUid( fileData.getTaskList( ), ITask::getUid );

        for ( ImportLine line : plan.getLines( ImportElement.TASK ) )
        {
            if ( line.getFileItem( ) == null )
            {
                continue;
            }
            ITask source = mapFile.get( line.getFileItem( ).getUid( ) );
            Action action = _actionService.findByPrimaryKey( mapId.get( source.getActionUid( ) ) );

            if ( line.getTargetItem( ) == null )
            {
                ITask task = ( source.getTaskType( ) == null ) ? null : _taskFactory.newTask( source.getTaskType( ).getKey( ), locale );
                if ( task == null )
                {
                    listSkipped.add( line.getFileItem( ).getLabel( ) );
                    continue;
                }
                task.setUid( source.getUid( ) );
                task.setAction( action );
                task.setOrder( source.getOrder( ) );
                // a new task arrives without configuration : the export does not carry it
                _taskService.create( task );
                mapId.put( source.getUid( ), task.getId( ) );
            }
            else
            {
                // the configuration of the task stays the one of the target
                ITask task = _taskService.findByPrimaryKey( line.getTargetItem( ).getId( ), locale );
                task.setUid( source.getUid( ) );
                task.setAction( action );
                task.setOrder( source.getOrder( ) );
                _taskService.update( task );
                mapId.put( source.getUid( ), task.getId( ) );
            }
        }
    }

    private void applyPrerequisites( WorkflowJsonData fileData, WorkflowImportPlan plan, Map<String, Integer> mapId, List<String> listSkipped )
    {
        Map<String, Prerequisite> mapFile = byUid( fileData.getPrerequisiteList( ), Prerequisite::getUidPrerequisite );

        for ( ImportLine line : plan.getLines( ImportElement.PREREQUISITE ) )
        {
            if ( line.getFileItem( ) == null )
            {
                continue;
            }
            Prerequisite source = mapFile.get( line.getFileItem( ).getUid( ) );
            int nIdAction = mapId.get( source.getUidAction( ) );

            if ( line.getTargetItem( ) == null )
            {
                if ( _prerequisiteService.getPrerequisiteService( source.getPrerequisiteType( ) ) == null )
                {
                    listSkipped.add( line.getFileItem( ).getLabel( ) );
                    continue;
                }
                Prerequisite prerequisite = new Prerequisite( );
                prerequisite.setUidPrerequisite( source.getUidPrerequisite( ) );
                prerequisite.setPrerequisiteType( source.getPrerequisiteType( ) );
                prerequisite.setIdAction( nIdAction );
                _prerequisiteService.createPrerequisite( prerequisite );
                mapId.put( source.getUidPrerequisite( ), prerequisite.getIdPrerequisite( ) );
            }
            else
            {
                Prerequisite prerequisite = _prerequisiteService.findPrerequisite( line.getTargetItem( ).getId( ) );
                prerequisite.setIdAction( nIdAction );
                _prerequisiteService.modifyPrerequisite( prerequisite );
                _importDAO.updateUid( ImportElement.PREREQUISITE, prerequisite.getIdPrerequisite( ), source.getUidPrerequisite( ) );
                mapId.put( source.getUidPrerequisite( ), prerequisite.getIdPrerequisite( ) );
            }
        }
    }

    /**
     * Removes what the file removed and nothing uses : tasks and prerequisites first, then actions, then states
     */
    private void removeAbsent( WorkflowImportPlan plan, Locale locale )
    {
        for ( ImportLine line : plan.getLines( ImportElement.TASK ) )
        {
            if ( line.getStatus( ) == ImportLine.Status.ABSENT_UNUSED )
            {
                ITask task = _taskService.findByPrimaryKey( line.getTargetItem( ).getId( ), locale );
                if ( task != null )
                {
                    task.doRemoveConfig( );
                    _taskService.remove( task.getId( ) );
                }
            }
        }
        for ( ImportLine line : plan.getLines( ImportElement.PREREQUISITE ) )
        {
            if ( line.getStatus( ) == ImportLine.Status.ABSENT_UNUSED )
            {
                _prerequisiteService.deletePrerequisite( line.getTargetItem( ).getId( ) );
            }
        }
        for ( ImportLine line : plan.getLines( ImportElement.ACTION ) )
        {
            if ( line.getStatus( ) == ImportLine.Status.ABSENT_UNUSED )
            {
                _actionService.remove( line.getTargetItem( ).getId( ) );
            }
        }
        for ( ImportLine line : plan.getLines( ImportElement.STATE ) )
        {
            if ( line.getStatus( ) == ImportLine.Status.ABSENT_UNUSED )
            {
                _stateService.remove( line.getTargetItem( ).getId( ) );
            }
        }
    }

    /**
     * Writes the fingerprint of every element the file carried, the UUID of the file on the workflow and the date of the import
     */
    private void recordImport( WorkflowImportPlan plan, int nIdTarget, String strWorkflowUid, Map<String, Integer> mapId )
    {
        for ( ImportLine line : plan.getAllLines( ) )
        {
            if ( line.getFileItem( ) == null )
            {
                continue;
            }
            Integer nId = mapId.get( line.getFileItem( ).getUid( ) );
            if ( nId == null && line.getTargetItem( ) != null )
            {
                nId = line.getTargetItem( ).getId( );
            }
            if ( nId != null )
            {
                _importDAO.updateImportHash( line.getElement( ), nId, line.getFileItem( ).getFingerprint( ) );
            }
        }
        _importDAO.updateWorkflowImport( nIdTarget, strWorkflowUid, new Timestamp( System.currentTimeMillis( ) ) );
        AppLogService.info( "Workflow import : workflow {} updated from the export of workflow {}", nIdTarget, strWorkflowUid );
    }

    private static <T> Map<String, T> byUid( List<T> list, Function<T, String> uid )
    {
        Map<String, T> map = new HashMap<>( );
        for ( T element : list )
        {
            map.put( uid.apply( element ), element );
        }
        return map;
    }
}
