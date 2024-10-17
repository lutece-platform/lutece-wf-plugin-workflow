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
package fr.paris.lutece.plugins.workflow.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.workflow.service.prerequisite.IManualActionPrerequisiteService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.prerequisite.IPrerequisiteConfig;
import fr.paris.lutece.plugins.workflowcore.business.prerequisite.Prerequisite;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflowFilter;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.business.workflow.WorkflowFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.prerequisite.IAutomaticActionPrerequisiteService;
import fr.paris.lutece.plugins.workflowcore.service.prerequisite.IPrerequisiteManagementService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.IWorkflowService;
import fr.paris.lutece.plugins.workflowcore.web.task.ITaskComponentManager;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.workflow.IWorkflowProvider;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 *
 * WorkflowProvider
 *
 */
public class WorkflowProvider implements IWorkflowProvider
{
    // MARKS
    private static final String MARK_RESOURCE_HISTORY = "resource_history";
    private static final String MARK_TASK_INFORMATION_LIST = "task_information_list";
    private static final String MARK_USER_HISTORY = "user_history";
    private static final String MARK_HISTORY_INFORMATION_LIST = "history_information_list";
    private static final String MARK_TASK_FORM_ENTRY_LIST = "task_form_entry_list";
    private static final String MARK_ADMIN_AVATAR = "adminAvatar";
    private static final String MARK_WORKFLOW_ACTION = "workflow_action";

    // TEMPLATES
    private static final String TEMPLATE_RESOURCE_HISTORY = "admin/plugins/workflow/resource_history.html";
    private static final String TEMPLATE_TASKS_FORM = "admin/plugins/workflow/tasks_form.html";
    private static final String TEMPLATE_PROCESS_ACTION_CONFIRMATION = "admin/plugins/workflow/process_action_confirmation.html";

    // SERVICES
    @Inject
    private IActionService _actionService;
    @Inject
    private IResourceWorkflowService _resourceWorkflowService;
    @Inject
    private IResourceHistoryService _resourceHistoryService;
    @Inject
    private IStateService _stateService;
    @Inject
    private ITaskService _taskService;
    @Inject
    private IWorkflowService _workflowService;
    @Inject
    private ITaskComponentManager _taskComponentManager;
    @Inject
    private IPrerequisiteManagementService _prerequisiteManagementService;

    /**
     * {@inheritDoc}
     */
    // @Override don't declare as Override to be compatible with older Lutece Core version
    public Collection<Action> getActions( int nIdResource, String strResourceType, Collection<Action> listActions, User user )
    {
        listActions = listActions.stream( ).filter( a -> canActionBeProcessed( user, nIdResource, strResourceType, a.getId( ) ) )
                .collect( Collectors.toList( ) );
        return RBACService.getAuthorizedCollection( listActions, ActionResourceIdService.PERMISSION_VIEW, user );
    }

    /**
     * {@inheritDoc}
     */
    // @Override don't declare as Override to be compatible with older Lutece Core version
    public Collection<Action> getAuthorizedActions( Collection<Action> listActions, User user )
    {
        return RBACService.getAuthorizedCollection( listActions, ActionResourceIdService.PERMISSION_VIEW, user );
    }

    /**
     * {@inheritDoc}
     */
    // @Override don't declare as Override to be compatible with older Lutece Core version
    public Map<Integer, List<Action>> getActions( String strResourceType, Map<Integer, List<Action>> mapActions, User user )
    {
        for ( Entry<Integer, List<Action>> entry : mapActions.entrySet( ) )
        {
            List<Action> listActions = entry.getValue( );
            listActions = listActions.stream( ).filter( a -> canActionBeProcessed( user, entry.getKey( ), strResourceType, a.getId( ) ) )
                    .collect( Collectors.toList( ) );
            listActions = (List<Action>) RBACService.getAuthorizedCollection( listActions, ActionResourceIdService.PERMISSION_VIEW, user );
            mapActions.put( entry.getKey( ), listActions );
        }

        return mapActions;
    }

    private boolean canActionBeProcessed( User user, int nIdResource, String strResourceType, int nIdAction )
    {
        for ( Prerequisite prerequisite : _prerequisiteManagementService.getListPrerequisite( nIdAction ) )
        {
            IAutomaticActionPrerequisiteService prerequisiteService = _prerequisiteManagementService
                    .getPrerequisiteService( prerequisite.getPrerequisiteType( ) );

            IPrerequisiteConfig config = _prerequisiteManagementService.getPrerequisiteConfiguration( prerequisite.getIdPrerequisite( ), prerequisiteService );
            boolean canBePerformed = false;
            if ( prerequisiteService instanceof IManualActionPrerequisiteService )
            {
                canBePerformed = ( (IManualActionPrerequisiteService) prerequisiteService ).canManualActionBePerformed( user, nIdResource, strResourceType,
                        config, nIdAction );
            }
            else
            {
                canBePerformed = prerequisiteService.canActionBePerformed( nIdResource, strResourceType, config, nIdAction );
            }

            if ( !canBePerformed )
            {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<State> getAllStateByWorkflow( Collection<State> listStates, User user )
    {
        return RBACService.getAuthorizedCollection( listStates, StateResourceIdService.PERMISSION_VIEW, user );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getAuthorizedResourceList( String strResourceType, int nIdWorkflow, int nIdWorkflowState, Integer nExternalParentId, User user )
    {
        if ( nIdWorkflowState < 1 )
        {
            return this.getAuthorizedResourceList( strResourceType, nIdWorkflow, null, nExternalParentId, user );
        }

        List<Integer> resourceIdList = new ArrayList<>( );

        State state = _stateService.findByPrimaryKey( nIdWorkflowState );

        ResourceWorkflowFilter resourceWorkflowFilter = new ResourceWorkflowFilter( );

        if ( user != null )
        {
            if ( RBACService.isAuthorized( state, StateResourceIdService.PERMISSION_VIEW, user ) )
            {
                if ( Boolean.TRUE.equals( state.isRequiredWorkgroupAssigned( ) ) )
                {

                    ReferenceList refWorkgroupKey = getUserWorkgroups( user );
                    resourceWorkflowFilter.setWorkgroupKeyList( refWorkgroupKey.toMap( ) );
                }

                resourceWorkflowFilter.setIdState( state.getId( ) );
                resourceWorkflowFilter.setIdWorkflow( nIdWorkflow );
                resourceWorkflowFilter.setResourceType( strResourceType );
                resourceWorkflowFilter.setExternalParentId( nExternalParentId );
                resourceIdList = _resourceWorkflowService.getListResourceIdWorkflowByFilter( resourceWorkflowFilter );
            }
        }
        else
        // WARNING : if content "user!=null" because for the batch the user is null, for the other case the user is not null
        {
            resourceWorkflowFilter.setIdState( state.getId( ) );
            resourceWorkflowFilter.setIdWorkflow( nIdWorkflow );
            resourceWorkflowFilter.setResourceType( strResourceType );
            resourceWorkflowFilter.setExternalParentId( nExternalParentId );
            resourceIdList = _resourceWorkflowService.getListResourceIdWorkflowByFilter( resourceWorkflowFilter );
        }

        return resourceIdList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getAuthorizedResourceList( String strResourceType, int nIdWorkflow, List<Integer> lListIdWorkflowState, Integer nExternalParentId,
            User user )
    {
        List<Integer> lListAutorizedIdSate = new ArrayList<>( );

        StateFilter stateFilter = new StateFilter( );
        stateFilter.setIdWorkflow( nIdWorkflow );

        Collection<State> listState = _stateService.getListStateByFilter( stateFilter );

        for ( State state : listState )
        {
            Integer nIdState = state.getId( );
            if ( lListIdWorkflowState == null || lListIdWorkflowState.contains( nIdState ) )
            {
                if ( user != null )
                {
                    if ( RBACService.isAuthorized( state, StateResourceIdService.PERMISSION_VIEW, user ) )
                    {
                        lListAutorizedIdSate.add( nIdState );
                    }
                }
                else
                // WARNING : if content "user!=null" because for the batch the user is null, for the other case the user is not null
                {
                    lListAutorizedIdSate.add( nIdState );
                }
            }
        }

        ResourceWorkflowFilter resourceWorkflowFilter = new ResourceWorkflowFilter( );
        resourceWorkflowFilter.setIdState( ResourceWorkflowFilter.ALL_INT );
        resourceWorkflowFilter.setIdWorkflow( nIdWorkflow );
        resourceWorkflowFilter.setResourceType( strResourceType );
        resourceWorkflowFilter.setExternalParentId( nExternalParentId );

        if ( user != null )
        {
            ReferenceList refWorkgroupKey = getUserWorkgroups( user );
            resourceWorkflowFilter.setWorkgroupKeyList( refWorkgroupKey.toMap( ) );
        }

        return _resourceWorkflowService.getListResourceIdWorkflowByFilter( resourceWorkflowFilter, lListAutorizedIdSate );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayDocumentHistory( int nIdResource, String strResourceType, int nIdWorkflow, HttpServletRequest request, Locale locale, User user )
    {
        return getDisplayDocumentHistory( nIdResource, strResourceType, nIdWorkflow, request, locale, null, TEMPLATE_RESOURCE_HISTORY, user );
    }

    /**
     * Implements IWorkflowProvider of Lutece Core version 5.1
     * 
     * @param nIdResource
     *            The resource
     * @param strResourceType
     *            The resource type
     * @param nIdWorkflow
     *            the workflow id
     * @param request
     *            The request
     * @param locale
     *            The locale
     * @param model
     *            The model to add to the default model
     * @param strTemplate
     *            The template
     * @return The HTML code to display
     */

    // @Override don't declare as Override to be compatible with older Lutece Core version
    public String getDisplayDocumentHistory( int nIdResource, String strResourceType, int nIdWorkflow, HttpServletRequest request, Locale locale,
            Map<String, Object> model, String strTemplate, User user )
    {
        Map<String, Object> defaultModel = getDefaultModelDocumentHistory( nIdResource, strResourceType, nIdWorkflow, request, locale );

        if ( model != null )
        {
            defaultModel.putAll( model );
        }

        HtmlTemplate templateList = AppTemplateService.getTemplate( strTemplate, locale, defaultModel );

        return templateList.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTasksForm( int nIdResource, String strResourceType, int nIdAction, HttpServletRequest request, Locale locale, User user )
    {
        List<ITask> listTasks = _taskService.getListTaskByIdAction( nIdAction, locale );
        List<String> listFormEntry = new ArrayList<>( );
        String strFormEntry;

        for ( ITask task : listTasks )
        {
            strFormEntry = _taskComponentManager.getDisplayTaskForm( nIdResource, strResourceType, request, locale, task );

            if ( strFormEntry != null )
            {
                listFormEntry.add( strFormEntry );
            }
        }

        Map<String, Object> model = new HashMap<>( );

        model.put( MARK_TASK_FORM_ENTRY_LIST, listFormEntry );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_TASKS_FORM, locale, model );

        return templateList.getHtml( );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayProcessActionConfirmation( int nIdAction, Locale locale, List<Integer> actionHistoryResourceIdList )
    {
        if ( CollectionUtils.isNotEmpty( actionHistoryResourceIdList ) )
		{
            Action action = _actionService.findByPrimaryKey( nIdAction );
        	
            Map<String, Object> model = new HashMap<>( );
            model.put( MARK_WORKFLOW_ACTION, action );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_PROCESS_ACTION_CONFIRMATION, locale, model );

            return template.getHtml( );
		}

        return StringUtils.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceList getWorkflowsEnabled( User user, Locale locale )
    {
        return WorkflowUtils.getRefList( getWorkflowsEnabled( user ), true, locale );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAccessCode( HttpServletRequest request, User user )
    {
        String strAccessCode = null;
        if ( user == null )
        { /// get user in the httpservletRequest
            user = getUserInRequest( request );
        }

        if ( user != null )
        {
            strAccessCode = user.getAccessCode( );
        }
        return strAccessCode;
    }

    // CHECK

    /**
     * {@inheritDoc}
     */
    // @Override don't declare as Override to be compatible with older Lutece Core version
    public boolean canProcessAction( int nIdResource, String strResourceType, int nIdAction, HttpServletRequest request, User user )
    {
        if ( user == null )
        { // get user in the httpservletRequest
            user = getUserInRequest( request );
        }

        if ( user != null )
        {
            Action action = _actionService.findByPrimaryKey( nIdAction );
            return canActionBeProcessed( user, nIdResource, strResourceType, nIdAction )
                    && RBACService.isAuthorized( action, ActionResourceIdService.PERMISSION_VIEW, user );
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAuthorized( int nIdResource, String strResourceType, int nIdWorkflow, User user )
    {
        boolean bReturn = false;
        State resourceState = null;
        ResourceWorkflow resourceWorkflow = _resourceWorkflowService.findByPrimaryKey( nIdResource, strResourceType, nIdWorkflow );

        if ( resourceWorkflow != null )
        {
            resourceState = _stateService.findByPrimaryKey( resourceWorkflow.getState( ).getId( ) );
        }
        else
        {
            // Get initial state
            StateFilter filter = new StateFilter( );
            filter.setIsInitialState( StateFilter.FILTER_TRUE );
            filter.setIdWorkflow( nIdWorkflow );

            List<State> listState = _stateService.getListStateByFilter( filter );

            if ( CollectionUtils.isNotEmpty( listState ) )
            {
                resourceState = listState.get( 0 );
            }
        }

        if ( resourceState == null || !RBACService.isAuthorized( resourceState, StateResourceIdService.PERMISSION_VIEW, user ) )
        {
            return bReturn;
        }

        if ( Boolean.TRUE.equals( resourceState.isRequiredWorkgroupAssigned( ) ) && ( resourceWorkflow != null ) )
        {

            for ( String strWorkgroup : resourceWorkflow.getWorkgroups( ) )
            {
                if ( isUserInWorkgroup( user, strWorkgroup )
                        || RBACService.isAuthorized( resourceState, StateResourceIdService.PERMISSION_VIEW_ALL_WORKGROUP, user ) )
                {
                    bReturn = true;

                    break;
                }
            }
        }
        else
        {
            bReturn = true;
        }

        return bReturn;
    }

    // DO

    /**
     * {@inheritDoc}
     */
    @Override
    public String doValidateTasksForm( int nIdResource, String strResourceType, int nIdAction, HttpServletRequest request, Locale locale, User user )
    {
        List<ITask> listTasks = _taskService.getListTaskByIdAction( nIdAction, locale );
        String strError = null;

        for ( ITask task : listTasks )
        {
            strError = _taskComponentManager.doValidateTask( nIdResource, strResourceType, request, locale, task );

            if ( strError != null )
            {
                return strError;
            }
        }

        return null;
    }

    // PRIVATE METHODS

    /**
     * Return a collection witch contains a list enabled workflow
     * 
     * @param user
     *            the User
     * @return a collection witch contains a list enabled workflow
     */
    private Collection<Workflow> getWorkflowsEnabled( User user )
    {
        WorkflowFilter filter = new WorkflowFilter( );
        filter.setIsEnabled( WorkflowFilter.FILTER_TRUE );

        List<Workflow> listWorkflow = _workflowService.getListWorkflowsByFilter( filter );

        return AdminWorkgroupService.getAuthorizedCollection( listWorkflow, user );
    }

    /**
     * returns the default model to build history performed on a resource.
     *
     * @param nIdResource
     *            the resource id
     * @param strResourceType
     *            the resource type
     * @param nIdWorkflow
     *            the workflow id
     * @param request
     *            the request
     * @param locale
     *            the locale
     * @return the default model
     */
    private Map<String, Object> getDefaultModelDocumentHistory( int nIdResource, String strResourceType, int nIdWorkflow, HttpServletRequest request,
            Locale locale )
    {
        List<ResourceHistory> listResourceHistory = _resourceHistoryService.getAllHistoryByResource( nIdResource, strResourceType, nIdWorkflow );
        List<ITask> listActionTasks;
        List<String> listTaskInformation;
        Map<String, Object> model = new HashMap<>( );
        Map<String, Object> resourceHistoryTaskInformation;
        List<Map<String, Object>> listResourceHistoryTaskInformation = new ArrayList<>( );
        String strTaskinformation;

        for ( ResourceHistory resourceHistory : listResourceHistory )
        {
            resourceHistoryTaskInformation = new HashMap<>( );
            resourceHistoryTaskInformation.put( MARK_RESOURCE_HISTORY, resourceHistory );

            if ( resourceHistory.getUserAccessCode( ) != null )
            {
                if ( resourceHistory.getResourceUserHistory( ) != null )
                {
                    resourceHistoryTaskInformation.put( MARK_USER_HISTORY, resourceHistory.getResourceUserHistory( ) );
                }
                else
                {
                    resourceHistoryTaskInformation.put( MARK_USER_HISTORY, getUserByAccessCode( resourceHistory.getUserAccessCode( ) ) );
                }
            }

            listTaskInformation = new ArrayList<>( );
            listActionTasks = _taskService.getListTaskByIdAction( resourceHistory.getAction( ).getId( ), locale );

            for ( ITask task : listActionTasks )
            {
                strTaskinformation = _taskComponentManager.getDisplayTaskInformation( resourceHistory.getId( ), request, locale, task );

                if ( strTaskinformation != null )
                {
                    listTaskInformation.add( strTaskinformation );
                }
            }

            resourceHistoryTaskInformation.put( MARK_TASK_INFORMATION_LIST, listTaskInformation );

            listResourceHistoryTaskInformation.add( resourceHistoryTaskInformation );
        }

        model.put( MARK_HISTORY_INFORMATION_LIST, listResourceHistoryTaskInformation );
        model.put( MARK_ADMIN_AVATAR, PluginService.isPluginEnable( "adminavatar" ) );

        return model;
    }

    /**
     * Method used when the user is not provided.
     * 
     * @param request
     *            the httpServletRequest
     * @return the user in the request
     */
    private User getUserInRequest( HttpServletRequest request )
    {
        return request != null ? AdminUserService.getAdminUser( request ) : null;
    }

    /**
     * Return a ReferenceList witch contains the user workgoups
     * 
     * @param user
     *            the user
     * @return a ReferenceList witch contains the user workgoups
     */

    private ReferenceList getUserWorkgroups( User user )
    {

        ReferenceList refListWorkgroup = new ReferenceList( );
        if ( user.getUserWorkgroups( ) != null )
        {
            user.getUserWorkgroups( ).forEach( x -> refListWorkgroup.addItem( x, x ) );
        }
        return refListWorkgroup;

    }

    /**
     * Return true if the user is in the workgoup
     * 
     * @param user
     *            the user
     * @param strWorkgroup
     *            the workgroup
     * @return true if the user is in the workgroup
     */
    private boolean isUserInWorkgroup( User user, String strWorkgroup )
    {
        if ( user.getUserWorkgroups( ) != null )
        {
            return user.getUserWorkgroups( ).stream( ).anyMatch( x -> x.equals( strWorkgroup ) );
        }
        return false;
    }

    // TODO provide UserInfo depending the User type who made the action
    /**
     * get a User by Access Code
     * 
     * @param strAccessCode
     *            the strAccessCode
     * @return a user by access code
     */
    private User getUserByAccessCode( String strAccessCode )
    {
        return AdminUserHome.findUserByLogin( strAccessCode );
    }

}
