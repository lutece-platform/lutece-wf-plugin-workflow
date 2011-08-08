/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.workflow.business.ActionFilter;
import fr.paris.lutece.plugins.workflow.business.ActionHome;
import fr.paris.lutece.plugins.workflow.business.ResourceHistory;
import fr.paris.lutece.plugins.workflow.business.ResourceHistoryHome;
import fr.paris.lutece.plugins.workflow.business.ResourceWorkflow;
import fr.paris.lutece.plugins.workflow.business.ResourceWorkflowFilter;
import fr.paris.lutece.plugins.workflow.business.ResourceWorkflowHome;
import fr.paris.lutece.plugins.workflow.business.StateFilter;
import fr.paris.lutece.plugins.workflow.business.StateHome;
import fr.paris.lutece.plugins.workflow.business.WorkflowFilter;
import fr.paris.lutece.plugins.workflow.business.WorkflowHome;
import fr.paris.lutece.plugins.workflow.business.task.ITask;
import fr.paris.lutece.plugins.workflow.business.task.ITaskFactory;
import fr.paris.lutece.plugins.workflow.business.task.ITaskType;
import fr.paris.lutece.plugins.workflow.business.task.TaskHome;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.business.workflow.Action;
import fr.paris.lutece.portal.business.workflow.State;
import fr.paris.lutece.portal.business.workflow.Workflow;
import fr.paris.lutece.portal.business.workgroup.AdminWorkgroupHome;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.workflow.IWorkflowService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.xml.XmlUtil;

/**
 * WorkflowService
 */
public class WorkflowService implements IWorkflowService
{
    private static final String BEAN_TASK_FACTORY = "workflowTaskFactory";

    //MARK
    private static final String MARK_RESOURCE_HISTORY = "resource_history";
    private static final String MARK_TASK_INFORMATION_LIST = "task_information_list";
    private static final String MARK_ADMIN_USER_HISTORY = "admin_user_history";
    private static final String MARK_HISTORY_INFORMATION_LIST = "history_information_list";
    private static final String MARK_TASK_FORM_ENTRY_LIST = "task_form_entry_list";

    //TEMPLATES
    private static final String TEMPLATE_RESOURCE_HISTORY = "admin/plugins/workflow/resource_history.html";
    private static final String TEMPLATE_TASKS_FORM = "admin/plugins/workflow/tasks_form.html";
    private static WorkflowService _singleton;

    // Xml Tags
    private static final String TAG_HISTORY = "history";
    private static final String TAG_LIST_RESOURCE_HISTORY = "list-resource-history";
    private static final String TAG_RESOURCE_HISTORY = "resource-history";
    private static final String TAG_CREATION_DATE = "creation-date";
    private static final String TAG_USER = "user";
    private static final String TAG_FIRST_NAME = "first-name";
    private static final String TAG_LAST_NAME = "last-name";
    private static final String TAG_LIST_TASK_INFORMATION = "list-task-information";
    private static final String TAG_TASK_INFORMATION = "task-information";
    private static final String USER_AUTO = "auto";
    private static final Plugin _pluginWorkflow = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
    private ITaskFactory _taskFactory;

    /**
     *
     * WorkflowService
     */
    private WorkflowService(  )
    {
    }

    /**
     *
     * @return a ItaskFactory Object
     */
    private ITaskFactory getTaskFactory(  )
    {
        if ( _taskFactory == null )
        {
            _taskFactory = (ITaskFactory) SpringContextService.getPluginBean( WorkflowPlugin.PLUGIN_NAME,
                    BEAN_TASK_FACTORY );
        }

        return _taskFactory;
    }

    /**
    * Initialize the workflow service
    *
    */
    public void init(  )
    {
        Workflow.init(  );
    }

    /**
     * Returns the instance of the singleton
     *
     * @return The instance of the singleton
     */
    public static WorkflowService getInstance(  )
    {
        if ( _singleton == null )
        {
            _singleton = new WorkflowService(  );
        }

        return _singleton;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.portal.service.workflow.IWorkflowService#getActions(java.util.List, java.lang.String, java.lang.Integer, int, fr.paris.lutece.portal.business.user.AdminUser)
     */
    public HashMap<Integer, List<Action>> getActions( List<Integer> listIdResssource, String strResourceType,
        Integer nIdExternalParentId, int nIdWorkflow, AdminUser user )
    {
        HashMap<Integer, List<Action>> result = new HashMap<Integer, List<Action>>(  );
        State initialState = null;

        //get initial state
        StateFilter filter = new StateFilter(  );
        filter.setIsInitialState( StateFilter.FILTER_TRUE );
        filter.setIdWorkflow( nIdWorkflow );

        List<State> listState = StateHome.getListStateByFilter( filter, _pluginWorkflow );

        if ( listState.size(  ) > 0 )
        {
            initialState = listState.get( 0 );
        }

        Plugin plugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        HashMap<Integer, Integer> ListIdSate = ResourceWorkflowHome.getListIdStateByListId( listIdResssource,
                nIdWorkflow, strResourceType, nIdExternalParentId, _pluginWorkflow );

        listIdResssource.removeAll( ListIdSate.keySet(  ) );

        //get all workflowstate
        StateFilter filterAll = new StateFilter(  );
        filterAll.setIdWorkflow( nIdWorkflow );

        List<State> listAllState = StateHome.getListStateByFilter( filterAll, _pluginWorkflow );

        HashMap<Integer, List<Action>> listActionBySateId = new HashMap<Integer, List<Action>>(  );

        for ( State state : listAllState )
        {
            ActionFilter actionfilter = new ActionFilter(  );
            actionfilter.setIdStateBefore( state.getId(  ) );
            actionfilter.setIdWorkflow( nIdWorkflow );

            List<Action> listAction = ActionHome.getListActionByFilter( actionfilter, plugin );

            listAction = (List<Action>) RBACService.getAuthorizedCollection( listAction,
                    ActionResourceIdService.PERMISSION_VIEW, user );
            listActionBySateId.put( state.getId(  ), listAction );
        }

        Set<Entry<Integer, Integer>> tt = ListIdSate.entrySet(  );

        Iterator<Entry<Integer, Integer>> it = tt.iterator(  );

        while ( it.hasNext(  ) )
        {
            Entry<Integer, Integer> entry = it.next(  );

            if ( listActionBySateId.containsKey( entry.getValue(  ) ) )
            {
                result.put( entry.getKey(  ), listActionBySateId.get( entry.getValue(  ) ) );
            }
        }

        if ( initialState != null )
        {
            ActionFilter actionfilter = new ActionFilter(  );
            actionfilter.setIdStateBefore( initialState.getId(  ) );
            actionfilter.setIdWorkflow( nIdWorkflow );

            List<Action> listAction = ActionHome.getListActionByFilter( actionfilter, plugin );

            for ( Integer nId : listIdResssource )
            {
                result.put( nId, listAction );
            }
        }

        return result;
    }

    /**
    * returns a list of actions possible for a given document based on the status
    * of the document in the workflow and the user role
    * @param nIdResource the document id
    * @param strResourceType the document type
    * @param user the adminUser
    * @param nIdWorkflow the workflow id
    * @return a list of Action
    */
    public Collection<Action> getActions( int nIdResource, String strResourceType, int nIdWorkflow, AdminUser user )
    {
        List<Action> listAction = new ArrayList<Action>(  );
        State resourceState = null;
        Plugin plugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        ResourceWorkflow resourceWorkflow = ResourceWorkflowHome.findByPrimaryKey( nIdResource, strResourceType,
                nIdWorkflow, plugin );

        if ( resourceWorkflow != null )
        {
            resourceState = resourceWorkflow.getState(  );
        }
        else
        {
            //get initial state
            StateFilter filter = new StateFilter(  );
            filter.setIsInitialState( StateFilter.FILTER_TRUE );
            filter.setIdWorkflow( nIdWorkflow );

            List<State> listState = StateHome.getListStateByFilter( filter, plugin );

            if ( listState.size(  ) > 0 )
            {
                resourceState = listState.get( 0 );
            }
        }

        if ( resourceState != null )
        {
            ActionFilter filter = new ActionFilter(  );
            filter.setIdStateBefore( resourceState.getId(  ) );
            filter.setIdWorkflow( nIdWorkflow );
            listAction = ActionHome.getListActionByFilter( filter, plugin );
        }

        return RBACService.getAuthorizedCollection( listAction, ActionResourceIdService.PERMISSION_VIEW, user );
    }

    /**
     * returns the state of a  given document
     * of the document in the workflow and the user role
     * @param nIdResource the document id
     * @param strResourceType the document type
     * @param user the adminUser
     * @param nIdWorkflow the workflow id
     * @param nIdExternalParentId the external parent id
     * @return the state of a given document
     */
    public State getState( int nIdResource, String strResourceType, int nIdWorkflow, Integer nIdExternalParentId,
        AdminUser user )
    {
        State resourceState = null;

        resourceState = StateHome.findByResource( nIdResource, strResourceType, nIdWorkflow, _pluginWorkflow );

        if ( resourceState == null )
        {
            //get initial state
            Workflow workflow = WorkflowHome.findByPrimaryKey( nIdWorkflow, _pluginWorkflow );
            ResourceWorkflow resourceWorkflow = null;

            if ( workflow != null )
            {
                resourceWorkflow = getInitialResourceWorkflow( nIdResource, strResourceType, workflow,
                        nIdExternalParentId, _pluginWorkflow );
            }

            if ( resourceWorkflow != null )
            {
                ResourceWorkflowHome.create( resourceWorkflow, _pluginWorkflow );
                resourceState = resourceWorkflow.getState(  );
            }
        }

        return resourceState;
    }

    /**
     * returns the state of a  given document
     * of the document in the workflow and the user role
     * @param nIdResource the document id
     * @param strResourceType the document type
     * @param user the adminUser
     * @param nIdWorkflow the workflow id
     * @return the state of a given document
     */
    @Deprecated
    public State getState( int nIdResource, String strResourceType, int nIdWorkflow, AdminUser user )
    {
        return this.getState( nIdResource, strResourceType, nIdWorkflow, null, user );
    }

    /**
     * Execute action automatic
     * @param nIdResource the document id
     * @param strResourceType the document type
     * @param nIdWorkflow the workflow id
     * @param nExternalParentId the external parent id
     */
    public void executeActionAutomatic( int nIdResource, String strResourceType, int nIdWorkflow,
        Integer nExternalParentId )
    {
        Plugin plugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );

        //Workflow workflow = WorkflowHome.findByPrimaryKey( nIdWorkflow, plugin );
        ResourceWorkflow resourceWorkflow = ResourceWorkflowHome.findByPrimaryKey( nIdResource, strResourceType,
                nIdWorkflow, plugin );

        if ( resourceWorkflow.getState(  ) != null )
        {
            State state = resourceWorkflow.getState(  );
            state = StateHome.findByPrimaryKey( state.getId(  ), plugin );

            ActionFilter actionFilter = new ActionFilter(  );
            actionFilter.setIdWorkflow( state.getWorkflow(  ).getId(  ) );
            actionFilter.setIdStateBefore( state.getId(  ) );
            actionFilter.setIsAutomaticState( 1 );

            List<Action> listAction = ActionHome.getListActionByFilter( actionFilter, plugin );

            if ( ( listAction != null ) && !listAction.isEmpty(  ) && ( listAction.get( 0 ) != null ) )
            {
                this.doProcessAction( nIdResource, strResourceType, listAction.get( 0 ).getId(  ), nExternalParentId,
                    null, null, true );

                //resourceState = StateHome.findByPrimaryKey( resourceWorkflow.getState(  ).getId(  ), plugin );
            }
        }
    }

    /**
     * Execute action automatic
     * @param nIdResource the document id
     * @param strResourceType the document type
     * @param nIdWorkflow the workflow id
     */
    @Deprecated
    public void executeActionAutomatic( int nIdResource, String strResourceType, int nIdWorkflow )
    {
        this.executeActionAutomatic( nIdResource, strResourceType, nIdWorkflow, null );
    }

    /**
     * returns all state of a  given the workflow
     * @param user the adminUser
     * @param nIdWorkflow the workflow id
     * @return the state of a given document
     */
    public Collection<State> getAllStateByWorkflow( int nIdWorkflow, AdminUser user )
    {
        Plugin plugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        StateFilter stateFilter = new StateFilter(  );
        stateFilter.setIdWorkflow( nIdWorkflow );

        List<State> listState = StateHome.getListStateByFilter( stateFilter, plugin );

        return RBACService.getAuthorizedCollection( listState, StateResourceIdService.PERMISSION_VIEW, user );
    }

    /**
     * return true if a form is associate to the action
     *
     * @param nIdAction the action id
     * @param locale the loacle
     * @return true if a form is associate to the action
     */
    public boolean isDisplayTasksForm( int nIdAction, Locale locale )
    {
        // Workflow workflow = WorkflowHome.findByPrimaryKey( nWorkflowId );
        Plugin plugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        List<ITask> listTasks = TaskHome.getListTaskByIdAction( nIdAction, plugin, locale );

        for ( ITask task : listTasks )
        {
            if ( task.isFormTaskRequire(  ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Proceed action given in parameter
     * @param nIdResource the resource id
     * @param strResourceType the resource type
     * @param request the request
     * @param nIdAction the action id
     * @param nExternalParentId the external parent id
     * @param isAutomatic is automatic action
     * @param locale locale
     */
    public void doProcessAction( int nIdResource, String strResourceType, int nIdAction, Integer nExternalParentId,
        HttpServletRequest request, Locale locale, boolean isAutomatic )
    {
        // Workflow workflow = WorkflowHome.findByPrimaryKey( nWorkflowId );
        boolean bRBACPermissionView = false;
        AdminUser user = null;

        if ( request != null )
        {
            user = AdminUserService.getAdminUser( request );
        }

        Plugin plugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        Action action = ActionHome.findByPrimaryKey( nIdAction, plugin );

        if ( action != null )
        {
            ResourceWorkflow resourceWorkflow = ResourceWorkflowHome.findByPrimaryKey( nIdResource, strResourceType,
                    action.getWorkflow(  ).getId(  ), plugin );

            if ( ( resourceWorkflow == null ) )
            {
                resourceWorkflow = getInitialResourceWorkflow( nIdResource, strResourceType, action.getWorkflow(  ),
                        nExternalParentId, plugin );

                if ( resourceWorkflow != null )
                {
                    //resourceWorkflow.setExternalParentId(nExternalParentId);
                    ResourceWorkflowHome.create( resourceWorkflow, plugin );
                }
            }

            if ( user != null )
            {
                bRBACPermissionView = RBACService.isAuthorized( action, ActionResourceIdService.PERMISSION_VIEW, user );
            }

            try
            {
	            if ( ( resourceWorkflow != null ) &&
	                    ( resourceWorkflow.getState(  ).getId(  ) == action.getStateBefore(  ).getId(  ) ) &&
	                    ( bRBACPermissionView || isAutomatic ) )
	            {
	                //create ResourceHistory
	                ResourceHistory resourceHistory = getNewResourceHistory( nIdResource, strResourceType, action, user,
	                        isAutomatic );
	                ResourceHistoryHome.create( resourceHistory, plugin );
	
	                List<ITask> listActionTasks = TaskHome.getListTaskByIdAction( nIdAction, plugin, locale );
	
	                for ( ITask task : listActionTasks )
	                {
	                    task.processTask( resourceHistory.getId(  ), request, plugin, locale );
	                }
	                
	                //reload the resource workflow in case a task had modified it
	                resourceWorkflow = ResourceWorkflowHome.findByPrimaryKey( nIdResource, strResourceType,
	                        action.getWorkflow(  ).getId(  ), plugin );
	                resourceWorkflow.setState( action.getStateAfter(  ) );
	                resourceWorkflow.setExternalParentId( nExternalParentId );
	                ResourceWorkflowHome.update( resourceWorkflow, plugin );
	            }
	
	            if ( action.getStateAfter(  ) != null )
	            {
	                State state = action.getStateAfter(  );
	                ActionFilter actionFilter = new ActionFilter(  );
	                actionFilter.setIdWorkflow( action.getWorkflow(  ).getId(  ) );
	                actionFilter.setIdStateBefore( state.getId(  ) );
	                actionFilter.setIsAutomaticState( 1 );
	
	                List<Action> listAction = ActionHome.getListActionByFilter( actionFilter, plugin );
	
	                if ( ( listAction != null ) && !listAction.isEmpty(  ) && ( listAction.get( 0 ) != null ) )
	                {
	                    this.doProcessAction( nIdResource, strResourceType, listAction.get( 0 ).getId(  ),
	                        nExternalParentId, request, locale, true );
	                }
	            }
            }
            catch( Exception e )
            {
            	AppLogService.error( "Error during processing tasks : " + e.getMessage(  ) );
            }
        }
    }

    /**
     * Proceed action given in parameter
     * @param nIdResource the resource id
     * @param strResourceType the resource type
     * @param request the request
     * @param nIdAction the action id
     * @param isAutomatic is automatic action
     * @param locale locale
     */
    @Deprecated
    public void doProcessAction( int nIdResource, String strResourceType, int nIdAction, HttpServletRequest request,
        Locale locale, boolean isAutomatic )
    {
        this.doProcessAction( nIdResource, strResourceType, nIdAction, null, request, locale, isAutomatic );
    }

    /**
     * returns the  actions history performed on a resource
     * @param nIdResource the resource id
     * @param strResourceType the resource type
     * @param request the request
     * @param nIdWorkflow the workflow id
     * @param locale the locale
     * @return the history of actions performed on a resource
     */
    public String getDisplayDocumentHistory( int nIdResource, String strResourceType, int nIdWorkflow,
        HttpServletRequest request, Locale locale )
    {
        Plugin plugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        List<ResourceHistory> listResourceHistory = ResourceHistoryHome.getAllHistoryByResource( nIdResource,
                strResourceType, nIdWorkflow, plugin );
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
            listActionTasks = TaskHome.getListTaskByIdAction( resourceHistory.getAction(  ).getId(  ), plugin, locale );

            for ( ITask task : listActionTasks )
            {
                strTaskinformation = task.getDisplayTaskInformation( resourceHistory.getId(  ), request, plugin, locale );

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
     * returns a xml which contains the  actions history performed on a resource
     * @param nIdResource the resource id
     * @param strResourceType the resource type
     * @param request the request
     * @param nIdWorkflow the workflow id
     * @param locale the locale
     * @return a xml which contains  the history of actions performed on a resource
     */
    public String getDocumentHistoryXml( int nIdResource, String strResourceType, int nIdWorkflow,
        HttpServletRequest request, Locale locale )
    {
        Plugin plugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        List<ResourceHistory> listResourceHistory = ResourceHistoryHome.getAllHistoryByResource( nIdResource,
                strResourceType, nIdWorkflow, plugin );
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
            listActionTasks = TaskHome.getListTaskByIdAction( resourceHistory.getAction(  ).getId(  ), plugin, locale );

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

                    strTaskinformation = task.getTaskInformationXml( resourceHistory.getId(  ), request, plugin, locale );

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
     * Perform the information on the various tasks associated with the given action specified in parameter
     * @param nIdResource the resource id
     * @param strResourceType the resource type
     * @param request the request
     * @param nIdAction the action id
     * @param locale the locale
     * @return null if there is no error in the task form
     *                    else return the error message url
     */
    @Deprecated
    public String doSaveTasksForm( int nIdResource, String strResourceType, int nIdAction, HttpServletRequest request,
        Locale locale )
    {
        return this.doSaveTasksForm( nIdResource, strResourceType, nIdAction, null, request, locale );
    }

    /**
     * Perform the information on the various tasks associated with the given action specified in parameter
     * @param nIdResource the resource id
     * @param strResourceType the resource type
     * @param request the request
     * @param nIdAction the action id
     * @param locale the locale
     * @param nExternalParentId the external parent id
     * @return null if there is no error in the task form
     *                    else return the error message url
     */
    public String doSaveTasksForm( int nIdResource, String strResourceType, int nIdAction, Integer nExternalParentId,
        HttpServletRequest request, Locale locale )
    {
        AdminUser user = AdminUserService.getAdminUser( request );
        Plugin plugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        Action action = ActionHome.findByPrimaryKey( nIdAction, plugin );
        String strError = doValidateTasksForm( nIdResource, strResourceType, nIdAction, request, plugin, locale );

        if ( strError != null )
        {
            return strError;
        }

        ResourceWorkflow resourceWorkflow = ResourceWorkflowHome.findByPrimaryKey( nIdResource, strResourceType,
                action.getWorkflow(  ).getId(  ), plugin );

        if ( ( resourceWorkflow == null ) && ( action != null ) )
        {
            resourceWorkflow = getInitialResourceWorkflow( nIdResource, strResourceType, action.getWorkflow(  ),
                    nExternalParentId, plugin );

            if ( resourceWorkflow != null )
            {
                ResourceWorkflowHome.create( resourceWorkflow, plugin );
            }
        }

        if ( ( resourceWorkflow != null ) &&
                ( resourceWorkflow.getState(  ).getId(  ) == action.getStateBefore(  ).getId(  ) ) &&
                RBACService.isAuthorized( action, ActionResourceIdService.PERMISSION_VIEW, user ) )
        {
            // create ResourceHistory
            ResourceHistory resourceHistory = getNewResourceHistory( nIdResource, strResourceType, action, user, false );
            ResourceHistoryHome.create( resourceHistory, plugin );

            List<ITask> listActionTasks = TaskHome.getListTaskByIdAction( nIdAction, plugin, locale );

            for ( ITask task : listActionTasks )
            {
                task.processTask( resourceHistory.getId(  ), request, plugin, locale );
            }

            //reload resource workflow after process task
            resourceWorkflow = ResourceWorkflowHome.findByPrimaryKey( nIdResource, strResourceType,
                    action.getWorkflow(  ).getId(  ), plugin );
            resourceWorkflow.setState( action.getStateAfter(  ) );
            resourceWorkflow.setExternalParentId( nExternalParentId );
            ResourceWorkflowHome.update( resourceWorkflow, plugin );
        }

        if ( ( action != null ) && ( action.getStateAfter(  ) != null ) )
        {
            State state = action.getStateAfter(  );
            ActionFilter actionFilter = new ActionFilter(  );
            actionFilter.setIdWorkflow( action.getWorkflow(  ).getId(  ) );
            actionFilter.setIdStateBefore( state.getId(  ) );
            actionFilter.setIsAutomaticState( 1 );

            List<Action> listAction = ActionHome.getListActionByFilter( actionFilter, plugin );

            if ( ( listAction != null ) && !listAction.isEmpty(  ) && ( listAction.get( 0 ) != null ) )
            {
                doProcessAction( nIdResource, strResourceType, listAction.get( 0 ).getId(  ), nExternalParentId,
                    request, locale, true );
            }
        }

        return null;
    }

    /**
     * Test if the information relating to various tasks associated with action are validated
     * @param nIdResource the resource id
     * @param strResourceType the resource type
     * @param nIdAction the action id
     * @param request the request
     * @param plugin the plugin
     * @param locale the locale
     * @return null if there is no error in the tasks form
     *                    else return the error message
     */
    private String doValidateTasksForm( int nIdResource, String strResourceType, int nIdAction,
        HttpServletRequest request, Plugin plugin, Locale locale )
    {
        List<ITask> listTasks = TaskHome.getListTaskByIdAction( nIdAction, plugin, locale );
        String strError = null;

        for ( ITask task : listTasks )
        {
            strError = task.doValidateTask( nIdResource, strResourceType, request, locale, plugin );

            if ( strError != null )
            {
                return strError;
            }
        }

        return null;
    }

    /**
     * return an instance of Task Object depending on the task type
     * @param strKey the type task key
     * @param locale the locale
     * @return an instance of Task Object
     */
    public ITask getTaskInstance( String strKey, Locale locale )
    {
        ITask task = getTaskFactory(  ).getTask( strKey );

        if ( locale != null )
        {
            task.getTaskType(  )
                .setTitle( I18nService.getLocalizedString( task.getTaskType(  ).getTitleI18nKey(  ), locale ) );
        }

        return task;
    }

    /**
     *
     * @return a list of task type
     */
    public Collection<ITaskType> getTaskTypeList(  )
    {
        return getTaskFactory(  ).getAllTaskType(  );
    }

    /**
     * Return a reference list wich contains the task types
     * @param locale the locale
     * @return a reference list wich contains the task types
     */
    public ReferenceList getRefListTaskType( Locale locale )
    {
        Collection<ITaskType> listTaskType = getTaskTypeList(  );
        ReferenceList refListTaskType = new ReferenceList(  );

        if ( listTaskType != null )
        {
            for ( ITaskType taskType : listTaskType )
            {
                refListTaskType.addItem( taskType.getKey(  ),
                    I18nService.getLocalizedString( taskType.getTitleI18nKey(  ), locale ) );
            }
        }

        return refListTaskType;
    }

    /**
     * Remove in all workflows the resource specified in parameter
     * @param nIdResource the resource id
     * @param strResourceType the resource type
     */
    public void doRemoveWorkFlowResource( int nIdResource, String strResourceType )
    {
        Plugin plugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );

        for ( Workflow workflow : WorkflowHome.getListWorkflowsByFilter( new WorkflowFilter(  ), plugin ) )
        {
            doRemoveWorkFlowResource( nIdResource, strResourceType, workflow.getId(  ) );
        }
    }

    /**
     * Remove in the workflow the resource specified in parameter
     * @param nIdResource the resource id
     * @param strResourceType the resource type
     * @param nIdWorkflow the workflow id
     */
    public void doRemoveWorkFlowResource( int nIdResource, String strResourceType, int nIdWorkflow )
    {
        Plugin plugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        ResourceWorkflow resourceWorkflowToRemove = null;
        List<ResourceHistory> listResourceHistoryToRemove;
        List<ITask> listTask = new ArrayList<ITask>(  );
        List<Action> listWorkflowAction;

        ActionFilter actionFilter = new ActionFilter(  );

        listResourceHistoryToRemove = ResourceHistoryHome.getAllHistoryByResource( nIdResource, strResourceType,
                nIdWorkflow, plugin );

        actionFilter.setIdWorkflow( nIdWorkflow );
        listWorkflowAction = ActionHome.getListActionByFilter( actionFilter, plugin );

        for ( Action action : listWorkflowAction )
        {
            listTask.addAll( TaskHome.getListTaskByIdAction( action.getId(  ), plugin, Locale.FRENCH ) );
        }

        //Remove TaskInformation
        for ( ResourceHistory resourceHistory : listResourceHistoryToRemove )
        {
            for ( ITask task : listTask )
            {
                task.doRemoveTaskInformation( resourceHistory.getId(  ), plugin );
            }

            ResourceHistoryHome.remove( resourceHistory.getId(  ), plugin );
        }

        //Remove resourceWorkflow
        resourceWorkflowToRemove = ResourceWorkflowHome.findByPrimaryKey( nIdResource, strResourceType, nIdWorkflow,
                plugin );

        if ( resourceWorkflowToRemove != null )
        {
            ResourceWorkflowHome.remove( resourceWorkflowToRemove, plugin );
        }
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.portal.service.workflow.IWorkflowService#doRemoveWorkFlowResourceByListId(java.util.List, java.lang.String, java.lang.Integer)
     */
    public void doRemoveWorkFlowResourceByListId( List<Integer> lListIdResource, String strResourceType,
        Integer nIdWorflow )
    {
        Plugin plugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );

        List<Integer> listIdHistory = ResourceHistoryHome.getListHistoryIdByListIdResourceId( lListIdResource,
                strResourceType, nIdWorflow, plugin );

        ActionFilter actionFilter = new ActionFilter(  );
        actionFilter.setIdWorkflow( nIdWorflow );

        List<Action> listWorkflowAction = ActionHome.getListActionByFilter( actionFilter, plugin );
        List<ITask> listTask = new ArrayList<ITask>(  );

        for ( Action action : listWorkflowAction )
        {
            listTask.addAll( TaskHome.getListTaskByIdAction( action.getId(  ), plugin, Locale.FRENCH ) );
        }

        for ( Integer nIdHistory : listIdHistory )
        {
            for ( ITask task : listTask )
            {
                task.doRemoveTaskInformation( nIdHistory, plugin );
            }
        }

        ResourceHistoryHome.removeByListIdResource( lListIdResource, strResourceType, nIdWorflow, plugin );
        ResourceWorkflowHome.removeByListIdResource( lListIdResource, strResourceType, nIdWorflow, plugin );
    }

    /**
     * returns the tasks form
     * @param nIdResource the resource id
     * @param strResourceType the resource type
     * @param request the request
     * @param nIdAction the action id
     * @param locale the locale
     * @return the tasks form associated to the action
     *
     */
    public String getDisplayTasksForm( int nIdResource, String strResourceType, int nIdAction,
        HttpServletRequest request, Locale locale )
    {
        Plugin plugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );

        List<ITask> listTasks = TaskHome.getListTaskByIdAction( nIdAction, plugin, locale );
        List<String> listFormEntry = new ArrayList<String>(  );
        String strFormEntry;

        for ( ITask task : listTasks )
        {
            strFormEntry = task.getDisplayTaskForm( nIdResource, strResourceType, request, plugin, locale );

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
     * return a new ResourceHistory Object
     * @param nIdResource the resource id
     * @param strResourceType the resource type
     * @param action the action
     * @param user the {@link AdminUser}
     * @return ResourceHistory Object
     */
    private ResourceHistory getNewResourceHistory( int nIdResource, String strResourceType, Action action,
        AdminUser user, boolean isAutomatic )
    {
        ResourceHistory resourceHistory = new ResourceHistory(  );
        resourceHistory.setIdResource( nIdResource );
        resourceHistory.setResourceType( strResourceType );
        resourceHistory.setAction( action );
        resourceHistory.setWorkFlow( action.getWorkflow(  ) );
        resourceHistory.setCreationDate( WorkflowUtils.getCurrentTimestamp(  ) );

        if ( isAutomatic )
        {
            resourceHistory.setUserAccessCode( USER_AUTO );
        }
        else
        {
            resourceHistory.setUserAccessCode( user.getAccessCode(  ) );
        }

        return resourceHistory;
    }

    /**
     * Check that a given user is allowed to view a resource depending the state of the resource
     * @param nIdResource the document id
     * @param strResourceType the document type
     * @param  user the AdminUser
     * @param nIdWorkflow the workflow id
     * @return a list of Action
     */
    public boolean isAuthorized( int nIdResource, String strResourceType, int nIdWorkflow, AdminUser user )
    {
        boolean bReturn = false;
        State resourceState = null;
        Plugin plugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        ResourceWorkflow resourceWorkflow = ResourceWorkflowHome.findByPrimaryKey( nIdResource, strResourceType,
                nIdWorkflow, plugin );

        if ( resourceWorkflow != null )
        {
            resourceState = StateHome.findByPrimaryKey( resourceWorkflow.getState(  ).getId(  ), plugin );
        }
        else
        {
            //get initial state
            StateFilter filter = new StateFilter(  );
            filter.setIsInitialState( StateFilter.FILTER_TRUE );
            filter.setIdWorkflow( nIdWorkflow );

            List<State> listState = StateHome.getListStateByFilter( filter, plugin );

            if ( listState.size(  ) > 0 )
            {
                resourceState = listState.get( 0 );
            }
        }

        if ( resourceState != null )
        {
            if ( RBACService.isAuthorized( resourceState, StateResourceIdService.PERMISSION_VIEW, user ) )
            {
                if ( resourceState.isRequiredWorkgroupAssigned(  ) )
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

    /* (non-Javadoc)
     * @see fr.paris.lutece.portal.service.workflow.IWorkflowService#getAuthorizedResourceList(java.lang.String, int, java.util.List, java.lang.Integer, fr.paris.lutece.portal.business.user.AdminUser)
     */
    public List<Integer> getAuthorizedResourceList( String strResourceType, int nIdWorkflow,
        List<Integer> lListIdWorkflowState, Integer nExternalParentId, AdminUser user )
    {
        Plugin plugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        List<Integer> lListAutorizedIdSate = new ArrayList<Integer>(  );

        StateFilter stateFilter = new StateFilter(  );
        stateFilter.setIdWorkflow( nIdWorkflow );

        Collection<State> listState = StateHome.getListStateByFilter( stateFilter, plugin );

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
        resourceWorkflowFilter.setWorkgroupKeyList( AdminWorkgroupService.getUserWorkgroups( user, null ) );

        return ResourceWorkflowHome.getListResourceIdWorkflowByFilter( resourceWorkflowFilter, lListAutorizedIdSate,
            plugin );
    }

    /**
     * @param strResourceType
     * @param nIdWorkflow
     * @param nIdWorkflowState
     * @param nExternalParentId
     * @param user
     * @return
     */
    public List<Integer> getAuthorizedResourceList( String strResourceType, int nIdWorkflow, int nIdWorkflowState,
        Integer nExternalParentId, AdminUser user )
    {
        if ( nIdWorkflowState < 1 )
        {
            return this.getAuthorizedResourceList( strResourceType, nIdWorkflowState, null, nExternalParentId, user );
        }

        Plugin plugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        List<Integer> resourceIdList = new ArrayList<Integer>(  );

        State state = StateHome.findByPrimaryKey( nIdWorkflowState, plugin );

        ResourceWorkflowFilter resourceWorkflowFilter = new ResourceWorkflowFilter(  );

        if ( user != null )
        {
            if ( RBACService.isAuthorized( state, StateResourceIdService.PERMISSION_VIEW, user ) )
            {
                if ( state.isRequiredWorkgroupAssigned(  ) )
                {
                    resourceWorkflowFilter.setWorkgroupKeyList( AdminWorkgroupService.getUserWorkgroups( user, null ) );
                }

                resourceWorkflowFilter.setIdState( state.getId(  ) );
                resourceWorkflowFilter.setIdWorkflow( nIdWorkflow );
                resourceWorkflowFilter.setResourceType( strResourceType );
                resourceWorkflowFilter.setExternalParentId( nExternalParentId );
                resourceIdList = ResourceWorkflowHome.getListResourceIdWorkflowByFilter( resourceWorkflowFilter, plugin );
            }
        }
        else //WARNING : if content "user!=null" because for the batch the user is null, for the other case the user is not null
        {
            resourceWorkflowFilter.setIdState( state.getId(  ) );
            resourceWorkflowFilter.setIdWorkflow( nIdWorkflow );
            resourceWorkflowFilter.setResourceType( strResourceType );
            resourceWorkflowFilter.setExternalParentId( nExternalParentId );
            resourceIdList = ResourceWorkflowHome.getListResourceIdWorkflowByFilter( resourceWorkflowFilter, plugin );
        }

        return resourceIdList;
    }

    /**
     * Get all authorized resource Id
     * @param strResourceType the resource type
     * @param nIdWorkflow the workflow id
     * @param nIdWorkflowState The workflow state id or -1 for all workflow states
     * @param user the AdminUser
     * @return a list resource id
     */
    @Deprecated
    public List<Integer> getAuthorizedResourceList( String strResourceType, int nIdWorkflow, int nIdWorkflowState,
        AdminUser user )
    {
        return this.getAuthorizedResourceList( strResourceType, nIdWorkflow, nIdWorkflowState, null, user );
    }

    /**
     * return a referencelist wich contains a list enabled workflow
     * @param user the AdminUser
     * @param locale the locale
     * @return a referencelist wich contains a list enabled workflow
     */
    public ReferenceList getWorkflowsEnabled( AdminUser user, Locale locale )
    {
        return WorkflowUtils.getRefList( getWorkflowsEnabled( user ), true, locale );
    }

    /**
     * <b> WRONG : need to filter on workflow id</b>
     * return a list witch contains idRessource for a given state
     * @param nIdState the id State
     * @param strResourceType the resource type
     * @param user the AdminUser
     * @return a list witch contains idRessource
     */
    @Deprecated
    public Collection<Integer> getListIdRessourceByState( int nIdState, String strResourceType, AdminUser user )
    {
        /*
        Plugin plugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        
        ResourceWorkflowFilter resourceWorkflowFilter = new ResourceWorkflowFilter(  );
        resourceWorkflowFilter.setIdState( nIdState );
        resourceWorkflowFilter.setResourceType( strResourceType );
        
        List<Integer> listIdResourceWorkflow = ResourceWorkflowHome.getListResourceIdWorkflowByFilter( resourceWorkflowFilter, plugin );
        
        return listIdResourceWorkflow;
        */
        return null;
    }

    /**
     * return a collection witch contains a list enabled workflow
     * @param user the AdminUser
     * @return a collection witch contains a list enabled workflow
     */
    private Collection<Workflow> getWorkflowsEnabled( AdminUser user )
    {
        Plugin plugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        WorkflowFilter filter = new WorkflowFilter(  );
        filter.setIsEnabled( WorkflowFilter.FILTER_TRUE );

        List<Workflow> listWorkflow = WorkflowHome.getListWorkflowsByFilter( filter, plugin );

        return AdminWorkgroupService.getAuthorizedCollection( listWorkflow, user );
    }

    /**
     * create a ResourceWorkflow Object wich contains the association of  resource and the initial state of the workflow
     * @param nIdResource the resource id
     * @param strResourceType the resource type
     * @param workflow the workflow
     * @param plugin the plugin
     * @return  a ResourceWorkflow Object
     */
    private ResourceWorkflow getInitialResourceWorkflow( int nIdResource, String strResourceType, Workflow workflow,
        Integer nExternalParentId, Plugin plugin )
    {
        ResourceWorkflow resourceWorkflow = null;
        StateFilter filter = new StateFilter(  );
        filter.setIdWorkflow( workflow.getId(  ) );
        filter.setIsInitialState( StateFilter.FILTER_TRUE );

        List<State> listInitialState = StateHome.getListStateByFilter( filter, plugin );

        if ( listInitialState.size(  ) > 0 )
        {
            resourceWorkflow = new ResourceWorkflow(  );
            resourceWorkflow.setIdResource( nIdResource );
            resourceWorkflow.setResourceType( strResourceType );
            resourceWorkflow.setState( listInitialState.get( 0 ) );
            resourceWorkflow.setWorkFlow( workflow );
            resourceWorkflow.setExternalParentId( nExternalParentId );
        }

        return resourceWorkflow;
    }

    /**
     * Build the advanced parameters management
     * @param user the current user
     * @return The model for the advanced parameters
     */
    public Map<String, Object> getManageAdvancedParameters( AdminUser user )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        
        return model;
    }
}
