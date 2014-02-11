/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.business.resource.IResourceHistoryFactory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflowFilter;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.business.workflow.WorkflowFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.IWorkflowService;
import fr.paris.lutece.plugins.workflowcore.web.task.ITaskComponentManager;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.business.workgroup.AdminWorkgroupHome;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.workflow.IWorkflowProvider;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.xml.XmlUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import javax.servlet.http.HttpServletRequest;


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
    private static final String MARK_ADMIN_USER_HISTORY = "admin_user_history";
    private static final String MARK_HISTORY_INFORMATION_LIST = "history_information_list";
    private static final String MARK_TASK_FORM_ENTRY_LIST = "task_form_entry_list";

    // TEMPLATES
    private static final String TEMPLATE_RESOURCE_HISTORY = "admin/plugins/workflow/resource_history.html";
    private static final String TEMPLATE_TASKS_FORM = "admin/plugins/workflow/tasks_form.html";

    // XML TAGS
    private static final String TAG_HISTORY = "history";
    private static final String TAG_LIST_RESOURCE_HISTORY = "list-resource-history";
    private static final String TAG_RESOURCE_HISTORY = "resource-history";
    private static final String TAG_CREATION_DATE = "creation-date";
    private static final String TAG_USER = "user";
    private static final String TAG_FIRST_NAME = "first-name";
    private static final String TAG_LAST_NAME = "last-name";
    private static final String TAG_LIST_TASK_INFORMATION = "list-task-information";
    private static final String TAG_TASK_INFORMATION = "task-information";

    // SERVICES
    @Inject
    private IActionService _actionService;
    @Inject
    private IResourceWorkflowService _resourceWorkflowService;
    @Inject
    private IResourceHistoryService _resourceHistoryService;
    @Inject
    private IResourceHistoryFactory _resourceHistoryFactory;
    @Inject
    private IStateService _stateService;
    @Inject
    private ITaskService _taskService;
    @Inject
    private IWorkflowService _workflowService;
    @Inject
    private ITaskComponentManager _taskComponentManager;

    // GET

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Action> getActions( Collection<Action> listActions, AdminUser user )
    {
        return RBACService.getAuthorizedCollection( listActions, ActionResourceIdService.PERMISSION_VIEW, user );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Integer, List<Action>> getActions( Map<Integer, List<Action>> mapActions, AdminUser user )
    {
        for ( Entry<Integer, List<Action>> entry : mapActions.entrySet(  ) )
        {
            List<Action> listActions = entry.getValue(  );
            listActions = (List<Action>) RBACService.getAuthorizedCollection( listActions,
                    ActionResourceIdService.PERMISSION_VIEW, user );
            mapActions.put( entry.getKey(  ), listActions );
        }

        return mapActions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<State> getAllStateByWorkflow( Collection<State> listStates, AdminUser user )
    {
        return RBACService.getAuthorizedCollection( listStates, StateResourceIdService.PERMISSION_VIEW, user );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getAuthorizedResourceList( String strResourceType, int nIdWorkflow, int nIdWorkflowState,
        Integer nExternalParentId, AdminUser user )
    {
        if ( nIdWorkflowState < 1 )
        {
            return this.getAuthorizedResourceList( strResourceType, nIdWorkflowState, null, nExternalParentId, user );
        }

        List<Integer> resourceIdList = new ArrayList<Integer>(  );

        State state = _stateService.findByPrimaryKey( nIdWorkflowState );

        ResourceWorkflowFilter resourceWorkflowFilter = new ResourceWorkflowFilter(  );

        if ( user != null )
        {
            if ( RBACService.isAuthorized( state, StateResourceIdService.PERMISSION_VIEW, user ) )
            {
                if ( state.isRequiredWorkgroupAssigned(  ) )
                {
                    ReferenceList refWorkgroupKey = AdminWorkgroupService.getUserWorkgroups( user, user.getLocale(  ) );

                    if ( refWorkgroupKey != null )
                    {
                        resourceWorkflowFilter.setWorkgroupKeyList( refWorkgroupKey.toMap(  ) );
                    }
                }

                resourceWorkflowFilter.setIdState( state.getId(  ) );
                resourceWorkflowFilter.setIdWorkflow( nIdWorkflow );
                resourceWorkflowFilter.setResourceType( strResourceType );
                resourceWorkflowFilter.setExternalParentId( nExternalParentId );
                resourceIdList = _resourceWorkflowService.getListResourceIdWorkflowByFilter( resourceWorkflowFilter );
            }
        }
        else //WARNING : if content "user!=null" because for the batch the user is null, for the other case the user is not null
        {
            resourceWorkflowFilter.setIdState( state.getId(  ) );
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
    public List<Integer> getAuthorizedResourceList( String strResourceType, int nIdWorkflow,
        List<Integer> lListIdWorkflowState, Integer nExternalParentId, AdminUser user )
    {
        List<Integer> lListAutorizedIdSate = new ArrayList<Integer>(  );

        StateFilter stateFilter = new StateFilter(  );
        stateFilter.setIdWorkflow( nIdWorkflow );

        Collection<State> listState = _stateService.getListStateByFilter( stateFilter );

        if ( lListIdWorkflowState == null )
        {
            for ( State state : listState )
            {
                if ( user != null )
                {
                    if ( RBACService.isAuthorized( state, StateResourceIdService.PERMISSION_VIEW, user ) )
                    {
                        lListAutorizedIdSate.add( Integer.valueOf( state.getId(  ) ) );
                    }
                }
                else //WARNING : if content "user!=null" because for the batch the user is null, for the other case the user is not null
                {
                    lListAutorizedIdSate.add( Integer.valueOf( state.getId(  ) ) );
                }
            }
        }
        else
        {
            for ( State state : listState )
            {
                Integer nIdState = Integer.valueOf( state.getId(  ) );

                if ( lListIdWorkflowState.contains( nIdState ) )
                {
                    if ( user != null )
                    {
                        if ( RBACService.isAuthorized( state, StateResourceIdService.PERMISSION_VIEW, user ) )
                        {
                            lListAutorizedIdSate.add( nIdState );
                        }
                    }
                    else //WARNING : if content "user!=null" because for the batch the user is null, for the other case the user is not null
                    {
                        lListAutorizedIdSate.add( nIdState );
                    }
                }
            }
        }

        ResourceWorkflowFilter resourceWorkflowFilter = new ResourceWorkflowFilter(  );
        resourceWorkflowFilter.setIdState( ResourceWorkflowFilter.ALL_INT );
        resourceWorkflowFilter.setIdWorkflow( nIdWorkflow );
        resourceWorkflowFilter.setResourceType( strResourceType );
        resourceWorkflowFilter.setExternalParentId( nExternalParentId );

        if ( user != null )
        {
            ReferenceList refWorkgroupKey = AdminWorkgroupService.getUserWorkgroups( user, user.getLocale(  ) );

            if ( refWorkgroupKey != null )
            {
                resourceWorkflowFilter.setWorkgroupKeyList( refWorkgroupKey.toMap(  ) );
            }
        }

        return _resourceWorkflowService.getListResourceIdWorkflowByFilter( resourceWorkflowFilter, lListAutorizedIdSate );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayDocumentHistory( int nIdResource, String strResourceType, int nIdWorkflow,
        HttpServletRequest request, Locale locale )
    {
        List<ResourceHistory> listResourceHistory = _resourceHistoryService.getAllHistoryByResource( nIdResource,
                strResourceType, nIdWorkflow );
        List<ITask> listActionTasks;
        List<String> listTaskInformation;
        Map<String, Object> model = new HashMap<String, Object>(  );
        Map<String, Object> resourceHistoryTaskInformation;
        List<Map<String, Object>> listResourceHistoryTaskInformation = new ArrayList<Map<String, Object>>(  );
        String strTaskinformation;

        for ( ResourceHistory resourceHistory : listResourceHistory )
        {
            resourceHistoryTaskInformation = new HashMap<String, Object>(  );
            resourceHistoryTaskInformation.put( MARK_RESOURCE_HISTORY, resourceHistory );

            if ( resourceHistory.getUserAccessCode(  ) != null )
            {
                resourceHistoryTaskInformation.put( MARK_ADMIN_USER_HISTORY,
                    AdminUserHome.findUserByLogin( resourceHistory.getUserAccessCode(  ) ) );
            }

            listTaskInformation = new ArrayList<String>(  );
            listActionTasks = _taskService.getListTaskByIdAction( resourceHistory.getAction(  ).getId(  ), locale );

            for ( ITask task : listActionTasks )
            {
                strTaskinformation = _taskComponentManager.getDisplayTaskInformation( resourceHistory.getId(  ),
                        request, locale, task );

                if ( strTaskinformation != null )
                {
                    listTaskInformation.add( strTaskinformation );
                }
            }

            resourceHistoryTaskInformation.put( MARK_TASK_INFORMATION_LIST, listTaskInformation );

            listResourceHistoryTaskInformation.add( resourceHistoryTaskInformation );
        }

        model.put( MARK_HISTORY_INFORMATION_LIST, listResourceHistoryTaskInformation );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_RESOURCE_HISTORY, locale, model );

        return templateList.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTasksForm( int nIdResource, String strResourceType, int nIdAction,
        HttpServletRequest request, Locale locale )
    {
        List<ITask> listTasks = _taskService.getListTaskByIdAction( nIdAction, locale );
        List<String> listFormEntry = new ArrayList<String>(  );
        String strFormEntry;

        for ( ITask task : listTasks )
        {
            strFormEntry = _taskComponentManager.getDisplayTaskForm( nIdResource, strResourceType, request, locale, task );

            if ( strFormEntry != null )
            {
                listFormEntry.add( strFormEntry );
            }
        }

        Map<String, Object> model = new HashMap<String, Object>(  );

        model.put( MARK_TASK_FORM_ENTRY_LIST, listFormEntry );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_TASKS_FORM, locale, model );

        return templateList.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDocumentHistoryXml( int nIdResource, String strResourceType, int nIdWorkflow,
        HttpServletRequest request, Locale locale )
    {
        List<ResourceHistory> listResourceHistory = _resourceHistoryService.getAllHistoryByResource( nIdResource,
                strResourceType, nIdWorkflow );
        List<ITask> listActionTasks;
        String strTaskinformation;
        StringBuffer strXml = new StringBuffer(  );
        AdminUser user;

        XmlUtil.beginElement( strXml, TAG_HISTORY );
        XmlUtil.beginElement( strXml, TAG_LIST_RESOURCE_HISTORY );

        for ( ResourceHistory resourceHistory : listResourceHistory )
        {
            user = ( resourceHistory.getUserAccessCode(  ) != null )
                ? AdminUserHome.findUserByLogin( resourceHistory.getUserAccessCode(  ) ) : null;
            listActionTasks = _taskService.getListTaskByIdAction( resourceHistory.getAction(  ).getId(  ), locale );

            XmlUtil.beginElement( strXml, TAG_RESOURCE_HISTORY );
            XmlUtil.addElement( strXml, TAG_CREATION_DATE,
                DateUtil.getDateString( resourceHistory.getCreationDate(  ), locale ) );
            XmlUtil.beginElement( strXml, TAG_USER );

            if ( user != null )
            {
                XmlUtil.addElementHtml( strXml, TAG_FIRST_NAME, user.getFirstName(  ) );
                XmlUtil.addElementHtml( strXml, TAG_LAST_NAME, user.getLastName(  ) );
            }
            else
            {
                XmlUtil.addEmptyElement( strXml, TAG_FIRST_NAME, null );
                XmlUtil.addEmptyElement( strXml, TAG_LAST_NAME, null );

                XmlUtil.endElement( strXml, TAG_USER );

                XmlUtil.beginElement( strXml, TAG_LIST_TASK_INFORMATION );

                for ( ITask task : listActionTasks )
                {
                    XmlUtil.beginElement( strXml, TAG_TASK_INFORMATION );
                    strTaskinformation = _taskComponentManager.getTaskInformationXml( resourceHistory.getId(  ),
                            request, locale, task );

                    if ( strTaskinformation != null )
                    {
                        strXml.append( strTaskinformation );
                    }

                    XmlUtil.endElement( strXml, TAG_TASK_INFORMATION );
                }

                XmlUtil.endElement( strXml, TAG_LIST_TASK_INFORMATION );

                XmlUtil.endElement( strXml, TAG_RESOURCE_HISTORY );
            }
        }

        XmlUtil.endElement( strXml, TAG_LIST_RESOURCE_HISTORY );
        XmlUtil.endElement( strXml, TAG_HISTORY );

        return strXml.toString(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceList getWorkflowsEnabled( AdminUser user, Locale locale )
    {
        return WorkflowUtils.getRefList( getWorkflowsEnabled( user ), true, locale );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAccessCode( HttpServletRequest request )
    {
        AdminUser user = AdminUserService.getAdminUser( request );

        if ( user != null )
        {
            return user.getAccessCode(  );
        }

        return null;
    }

    // CHECK

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canProcessAction( int nIdAction, HttpServletRequest request )
    {
        if ( request != null )
        {
            Action action = _actionService.findByPrimaryKey( nIdAction );
            AdminUser user = AdminUserService.getAdminUser( request );

            if ( user != null )
            {
                return RBACService.isAuthorized( action, ActionResourceIdService.PERMISSION_VIEW, user );
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAuthorized( int nIdResource, String strResourceType, int nIdWorkflow, AdminUser user )
    {
        boolean bReturn = false;
        State resourceState = null;
        ResourceWorkflow resourceWorkflow = _resourceWorkflowService.findByPrimaryKey( nIdResource, strResourceType,
                nIdWorkflow );

        if ( resourceWorkflow != null )
        {
            resourceState = _stateService.findByPrimaryKey( resourceWorkflow.getState(  ).getId(  ) );
        }
        else
        {
            // Get initial state
            StateFilter filter = new StateFilter(  );
            filter.setIsInitialState( StateFilter.FILTER_TRUE );
            filter.setIdWorkflow( nIdWorkflow );

            List<State> listState = _stateService.getListStateByFilter( filter );

            if ( listState.size(  ) > 0 )
            {
                resourceState = listState.get( 0 );
            }
        }

        if ( resourceState != null )
        {
            if ( RBACService.isAuthorized( resourceState, StateResourceIdService.PERMISSION_VIEW, user ) )
            {
                if ( resourceState.isRequiredWorkgroupAssigned(  ) && ( resourceWorkflow != null ) )
                {
                    for ( String strWorkgroup : resourceWorkflow.getWorkgroups(  ) )
                    {
                        if ( AdminWorkgroupHome.isUserInWorkgroup( user, strWorkgroup ) ||
                                RBACService.isAuthorized( resourceState,
                                    StateResourceIdService.PERMISSION_VIEW_ALL_WORKGROUP, user ) )
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
            }
        }

        return bReturn;
    }

    // DO

    /**
     * {@inheritDoc}
     */
    @Override
    public String doValidateTasksForm( int nIdResource, String strResourceType, int nIdAction,
        HttpServletRequest request, Locale locale )
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSaveTasksForm( int nIdResource, String strResourceType, int nIdAction, Integer nExternalParentId,
        HttpServletRequest request, Locale locale, String strUserAccessCode )
    {
        Action action = _actionService.findByPrimaryKey( nIdAction );

        if ( ( action != null ) &&
                WorkflowService.getInstance(  )
                                   .canProcessAction( nIdResource, strResourceType, nIdAction, nExternalParentId,
                    request, false ) )
        {
            ResourceWorkflow resourceWorkflow = _resourceWorkflowService.findByPrimaryKey( nIdResource,
                    strResourceType, action.getWorkflow(  ).getId(  ) );

            if ( resourceWorkflow == null )
            {
                resourceWorkflow = _workflowService.getInitialResourceWorkflow( nIdResource, strResourceType,
                        action.getWorkflow(  ), nExternalParentId );

                if ( resourceWorkflow != null )
                {
                    _resourceWorkflowService.create( resourceWorkflow );
                }
            }

            // Create ResourceHistory
            ResourceHistory resourceHistory = _resourceHistoryFactory.newResourceHistory( nIdResource, strResourceType,
                    action, strUserAccessCode, false );
            _resourceHistoryService.create( resourceHistory );

            List<ITask> listActionTasks = _taskService.getListTaskByIdAction( nIdAction, locale );

            for ( ITask task : listActionTasks )
            {
                task.setAction( action );
                task.processTask( resourceHistory.getId(  ), request, locale );
            }

            // Reload resource workflow after process task
            resourceWorkflow = _resourceWorkflowService.findByPrimaryKey( nIdResource, strResourceType,
                    action.getWorkflow(  ).getId(  ) );
            resourceWorkflow.setState( action.getStateAfter(  ) );
            resourceWorkflow.setExternalParentId( nExternalParentId );
            _resourceWorkflowService.update( resourceWorkflow );

            if ( action.getStateAfter(  ) != null )
            {
                State state = action.getStateAfter(  );
                ActionFilter actionFilter = new ActionFilter(  );
                actionFilter.setIdWorkflow( action.getWorkflow(  ).getId(  ) );
                actionFilter.setIdStateBefore( state.getId(  ) );
                actionFilter.setIsAutomaticState( 1 );

                List<Action> listAction = _actionService.getListActionByFilter( actionFilter );

                if ( ( listAction != null ) && !listAction.isEmpty(  ) && ( listAction.get( 0 ) != null ) )
                {
                    WorkflowService.getInstance(  )
                                   .doProcessAction( nIdResource, strResourceType, listAction.get( 0 ).getId(  ),
                        nExternalParentId, request, locale, true );
                }
            }
        }
    }

    // PRIVATE METHODS

    /**
    * Return a collection witch contains a list enabled workflow
    * @param user the AdminUser
    * @return a collection witch contains a list enabled workflow
    */
    private Collection<Workflow> getWorkflowsEnabled( AdminUser user )
    {
        WorkflowFilter filter = new WorkflowFilter(  );
        filter.setIsEnabled( WorkflowFilter.FILTER_TRUE );

        List<Workflow> listWorkflow = _workflowService.getListWorkflowsByFilter( filter );

        return AdminWorkgroupService.getAuthorizedCollection( listWorkflow, user );
    }
}
