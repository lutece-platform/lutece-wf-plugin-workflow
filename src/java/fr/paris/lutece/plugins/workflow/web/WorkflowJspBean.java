/*
 * Copyright (c) 2002-2021, City of Paris
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
package fr.paris.lutece.plugins.workflow.web;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.iterators.EntrySetMapIterator;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.workflow.business.prerequisite.PrerequisiteDTO;
import fr.paris.lutece.plugins.workflow.business.task.TaskRemovalListenerService;
import fr.paris.lutece.plugins.workflow.service.ActionResourceIdService;
import fr.paris.lutece.plugins.workflow.service.WorkflowGraphExportService;
import fr.paris.lutece.plugins.workflow.service.json.WorkflowJsonService;
import fr.paris.lutece.plugins.workflow.service.prerequisite.PrerequisiteManagementService;
import fr.paris.lutece.plugins.workflow.service.task.TaskFactory;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflow.web.task.TaskComponentManager;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfig;
import fr.paris.lutece.plugins.workflowcore.business.icon.Icon;
import fr.paris.lutece.plugins.workflowcore.business.prerequisite.Prerequisite;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.business.task.ITaskType;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.business.workflow.WorkflowFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.ActionService;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.icon.IIconService;
import fr.paris.lutece.plugins.workflowcore.service.icon.IconService;
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
import fr.paris.lutece.plugins.workflowcore.web.task.ITaskComponentManager;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowRemovalListenerService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.file.FileUtil;
import fr.paris.lutece.util.html.AbstractPaginator;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.method.MethodUtil;
import fr.paris.lutece.util.url.UrlItem;

/**
 * class ManageDirectoryJspBean
 */
public class WorkflowJspBean extends PluginAdminPageJspBean
{
    /**
     * Right to manage workflows
     */
    public static final String RIGHT_MANAGE_WORKFLOW = "WORKFLOW_MANAGEMENT";
    private static final long serialVersionUID = -3521312136519134434L;

    // jsp
    private static final String JSP_MODIFY_WORKFLOW = "jsp/admin/plugins/workflow/ModifyWorkflow.jsp";
    private static final String JSP_MODIFY_TASK = "jsp/admin/plugins/workflow/ModifyTask.jsp";
    private static final String JSP_MODIFY_ACTION = "jsp/admin/plugins/workflow/ModifyAction.jsp";
    private static final String JSP_MODIFY_REFLEXIVE_ACTION = "jsp/admin/plugins/workflow/GetModifyReflexiveAction.jsp";
    private static final String JSP_DO_REMOVE_WORKFLOW = "jsp/admin/plugins/workflow/DoRemoveWorkflow.jsp";
    private static final String JSP_DO_REMOVE_STATE = "jsp/admin/plugins/workflow/DoRemoveState.jsp";
    private static final String JSP_DO_REMOVE_ACTION = "jsp/admin/plugins/workflow/DoRemoveAction.jsp";
    private static final String JSP_DO_COPY_WORKFLOW = "jsp/admin/plugins/workflow/DoCopyWorkflow.jsp";
    private static final String JSP_DO_IMPORT_WORKFLOW = "jsp/admin/plugins/workflow/DoImportWorkflow.jsp";
    private static final String JSP_DO_REMOVE_TASK = "jsp/admin/plugins/workflow/DoRemoveTask.jsp";
    private static final String JSP_DO_REMOVE_TASK_FROM_REFLEXIVE_ACTION = "jsp/admin/plugins/workflow/DoRemoveTaskFromReflexiveAction.jsp";
    private static final String JSP_MANAGE_WORKFLOW = "jsp/admin/plugins/workflow/ManageWorkflow.jsp";

    // templates
    private static final String TEMPLATE_MANAGE_WORKFLOW = "admin/plugins/workflow/manage_workflow.html";
    private static final String TEMPLATE_CREATE_WORKFLOW = "admin/plugins/workflow/create_workflow.html";
    private static final String TEMPLATE_MODIFY_WORKFLOW = "admin/plugins/workflow/modify_workflow.html";
    private static final String TEMPLATE_GRAPH_WORKFLOW = "admin/plugins/workflow/graph_workflow.html";
    private static final String TEMPLATE_CREATE_STATE = "admin/plugins/workflow/create_state.html";
    private static final String TEMPLATE_MODIFY_STATE = "admin/plugins/workflow/modify_state.html";
    private static final String TEMPLATE_CREATE_ACTION = "admin/plugins/workflow/create_action.html";
    private static final String TEMPLATE_MODIFY_ACTION = "admin/plugins/workflow/modify_action.html";
    private static final String TEMPLATE_MODIFY_TASK = "admin/plugins/workflow/modify_task.html";
    private static final String TEMPLATE_MANAGE_ADVANCED_PARAMETERS = "admin/plugins/workflow/manage_advanced_parameters.html";
    private static final String TEMPLATE_MODIFY_REFLEXIVE_ACTION = "admin/plugins/workflow/modify_reflexive_action.html";

    // parameters
    private static final String PARAMETER_IS_ENABLED = "is_enabled";
    private static final String PARAMETER_IS_INITIAL_STATE = "is_initial_state";
    private static final String PARAMETER_IS_REQUIRED_WORKGROUP_ASSIGNED = "is_required_workgroup_assigned";
    private static final String PARAMETER_WORKGROUP = "workgroup";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_NAME = "name";
    private static final String PARAMETER_DESCRIPTION = "description";
    private static final String PARAMETER_CANCEL = "cancel";
    private static final String PARAMETER_ID_WORKFLOW = "id_workflow";
    private static final String PARAMETER_ID_ACTION = "id_action";
    private static final String PARAMETER_ID_STATE = "id_state";
    private static final String PARAMETER_ID_TASK = "id_task";
    private static final String PARAMETER_ID_ICON = "id_icon";
    private static final String PARAMETER_ID_AUTOMATIC = "automatic";
    private static final String PARAMETER_IS_MASS_ACTION = "is_mass_action";
    private static final String PARAMETER_ID_STATE_BEFORE = "id_state_before";
    private static final String PARAMETER_ID_STATE_AFTER = "id_state_after";
    private static final String PARAMETER_ORDER_ID = "order_id";
    private static final String PARAMETER_ORDER_ACTION_ID = "order_action_id";
    private static final String PARAMETER_ORDER_TASK_ID = "order_task_id";
    private static final String PARAMETER_APPLY = "apply";
    private static final String PARAMETER_PAGE_INDEX_STATE = "page_index_state";
    private static final String PARAMETER_PAGE_INDEX_ACTION = "page_index_action";
    private static final String PARAMETER_ITEMS_PER_PAGE_STATE = "items_per_page_state";
    private static final String PARAMETER_ITEMS_PER_PAGE_ACTION = "items_per_page_action";
    private static final String PARAMETER_TASK_TYPE_KEY = "task_type_key";
    private static final String PARAMETER_SELECT_LINKED_ACTIONS = "select_linked_actions";
    private static final String PARAMETER_UNSELECT_LINKED_ACTIONS = "unselect_linked_actions";
    private static final String PARAMETER_PANE = "pane";
    private static final String PARAMETER_JSON_FILE = "json_file";
    private static final String PARAMETER_SHOW_TASKS = "show_tasks";

    // properties
    private static final String PROPERTY_MANAGE_WORKFLOW_PAGE_TITLE = "workflow.manage_workflow.page_title";
    private static final String PROPERTY_CREATE_WORKFLOW_PAGE_TITLE = "workflow.create_workflow.page_title";
    private static final String PROPERTY_MODIFY_WORKFLOW_PAGE_TITLE = "workflow.modify_workflow.page_title";
    private static final String PROPERTY_GRAPH_WORKFLOW_PAGE_TITLE = "workflow.graph_workflow.page_title";
    private static final String PROPERTY_CREATE_STATE_PAGE_TITLE = "workflow.create_state.page_title";
    private static final String PROPERTY_MODIFY_STATE_PAGE_TITLE = "workflow.modify_state.page_title";
    private static final String PROPERTY_CREATE_ACTION_PAGE_TITLE = "workflow.create_action.page_title";
    private static final String PROPERTY_MODIFY_ACTION_PAGE_TITLE = "workflow.modify_action.page_title";
    private static final String PROPERTY_MODIFY_TASK_PAGE_TITLE = "workflow.modify_task.page_title";
    private static final String PROPERTY_ITEM_PER_PAGE = "workflow.itemsPerPage";
    private static final String PROPERTY_COPY_OF_STATE = "workflow.manage_workflow.copy_of_state";
    private static final String PROPERTY_COPY_OF_ACTION = "workflow.manage_workflow.copy_of_action";
    private static final String PROPERTY_ALL = "workflow.manage_workflow.select.all";
    private static final String PROPERTY_YES = "workflow.manage_workflow.select.yes";
    private static final String PROPERTY_NO = "workflow.manage_workflow.select.no";
    private static final String FIELD_WORKFLOW_NAME = "workflow.create_workflow.label_name";
    private static final String FIELD_ACTION_NAME = "workflow.create_action.label_name";
    private static final String FIELD_STATE_NAME = "workflow.create_state.label_name";
    private static final String FIELD_WORKFLOW_DESCRIPTION = "workflow.create_workflow.label_description";
    private static final String FIELD_ACTION_DESCRIPTION = "workflow.create_action.label_description";
    private static final String FIELD_STATE_DESCRIPTION = "workflow.create_state.label_description";
    private static final String FIELD_STATE_BEFORE = "workflow.create_action.label_state_before";
    private static final String FIELD_STATE_AFTER = "workflow.create_action.label_state_after";
    private static final String FIELD_ICON = "workflow.create_action.label_icon";

    // mark
    private static final String MARK_PLUGIN = "plugin";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_PAGINATOR_ACTION = "paginator_action";
    private static final String MARK_PAGINATOR_STATE = "paginator_state";
    private static final String MARK_USER_WORKGROUP_REF_LIST = "user_workgroup_list";
    private static final String MARK_USER_WORKGROUP_SELECTED = "user_workgroup_selected";
    private static final String MARK_ACTIVE_REF_LIST = "active_list";
    private static final String MARK_ACTIVE_SELECTED = "active_selected";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_NB_ITEMS_PER_PAGE_ACTION = "nb_items_per_page_action";
    private static final String MARK_NB_ITEMS_PER_PAGE_STATE = "nb_items_per_page_state";
    private static final String MARK_WORKFLOW_LIST = "workflow_list";
    private static final String MARK_STATE_LIST = "state_list";
    private static final String MARK_ACTION_LIST = "action_list";
    private static final String MARK_ICON_LIST = "icon_list";
    private static final String MARK_TASK_TYPE_LIST = "task_type_list";
    private static final String MARK_TASK_LIST = "task_list";
    private static final String MARK_TASK_CONFIG = "task_config";
    private static final String MARK_TASK = "task";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_WORKFLOW = "workflow";
    private static final String MARK_STATE = "state";
    private static final String MARK_NUMBER_STATE = "number_state";
    private static final String MARK_NUMBER_ACTION = "number_action";
    private static final String MARK_NUMBER_TASK = "number_task";
    private static final String MARK_ACTION = "action";
    private static final String MARK_INITIAL_STATE = "initial_state";
    private static final String MARK_PERMISSION_MANAGE_ADVANCED_PARAMETERS = "permission_manage_advanced_parameters";
    private static final String MARK_DEFAULT_VALUE_WORKGROUP_KEY = "workgroup_key_default_value";
    private static final String MARK_AVAILABLE_LINKED_ACTIONS = "available_linked_actions";
    private static final String MARK_SELECTED_LINKED_ACTIONS = "selected_linked_actions";
    private static final String MARK_DISPLAY_TASKS_FORM = "display_tasks_form";
    private static final String MARK_PANE = "pane";
    private static final String MARK_PANE_ACTIONS = "pane-actions";
    private static final String MARK_PANE_STATE = "pane-states";
    private static final String MARK_LIST_PREREQUISITE_TYPE = "list_prerequisite_type";
    private static final String MARK_LIST_PREREQUISITE = "listPrerequisite";
    private static final String MARK_MDGRAPH = "mdgraph";
    private static final String MARK_SHOW_TASKS = "showTasks";

    // MESSAGES
    private static final String MESSAGE_MANDATORY_FIELD = "workflow.message.mandatory.field";
    private static final String MESSAGE_ERROR_AUTOMATIC_FIELD = "workflow.message.error.automatic.field";
    private static final String MESSAGE_CONFIRM_REMOVE_WORKFLOW = "workflow.message.confirm_remove_workflow";
    private static final String MESSAGE_CONFIRM_REMOVE_STATE = "workflow.message.confirm_remove_state";
    private static final String MESSAGE_CONFIRM_REMOVE_ACTION = "workflow.message.confirm_remove_action";
    private static final String MESSAGE_CONFIRM_REMOVE_TASK = "workflow.message.confirm_remove_task";
    private static final String MESSAGE_CONFIRM_COPY_WORKFLOW = "workflow.message.confirm_copy_workflow";
    private static final String MESSAGE_INITIAL_STATE_ALREADY_EXIST = "workflow.message.initial_state_already_exist";
    private static final String MESSAGE_CAN_NOT_REMOVE_STATE_ACTIONS_ARE_ASSOCIATE = "workflow.message.can_not_remove_state_actions_are_associate";
    private static final String MESSAGE_CAN_NOT_REMOVE_STATE_TASKS_ARE_ASSOCIATE = "workflow.message.can_not_remove_state_tasks_are_associate";
    private static final String MESSAGE_CAN_NOT_REMOVE_WORKFLOW = "workflow.message.can_not_remove_workflow";
    private static final String MESSAGE_CAN_NOT_REMOVE_TASK = "workflow.message.can_not_remove_task";
    private static final String MESSAGE_CAN_NOT_DISABLE_WORKFLOW = "workflow.message.can_not_disable_workflow";
    private static final String MESSAGE_TASK_IS_NOT_AUTOMATIC = "workflow.message.task_not_automatic";
    private static final String MESSAGE_MASS_ACTION_CANNOT_BE_AUTOMATIC = "workflow.message.mass_action_cannot_be_automatic";
    private static final String MESSAGE_REFLEXIVE_ACTION_NAME = "workflow.reflexive_action.defaultTitle";
    private static final String PANE_STATES = "pane-states";
    private static final String PANE_ACTIONS = "pane-actions";
    private static final String PANE_DEFAULT = PANE_STATES;

    private static final String LOG_ACTION_NOT_FOUND = "Action not found for ID ";
    private static final String LOG_WORKFLOW_NOT_FOUND = "Workflow not found for ID ";

    // session fields
    private int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 50 );
    private String _strCurrentPageIndexWorkflow;
    private int _nItemsPerPageWorkflow;
    private String _strCurrentPageIndexState;
    private int _nItemsPerPageState;
    private String _strCurrentPageIndexAction;
    private int _nItemsPerPageAction;
    private int _nIsEnabled = -1;
    private String _strWorkGroup = AdminWorkgroupService.ALL_GROUPS;
    private IWorkflowService _workflowService = SpringContextService.getBean( WorkflowService.BEAN_SERVICE );
    private IStateService _stateService = SpringContextService.getBean( StateService.BEAN_SERVICE );
    private IActionService _actionService = SpringContextService.getBean( ActionService.BEAN_SERVICE );
    private IIconService _iconService = SpringContextService.getBean( IconService.BEAN_SERVICE );
    private ITaskService _taskService = SpringContextService.getBean( TaskService.BEAN_SERVICE );
    private ITaskFactory _taskFactory = SpringContextService.getBean( TaskFactory.BEAN_SERVICE );
    private ITaskComponentManager _taskComponentManager = SpringContextService.getBean( TaskComponentManager.BEAN_MANAGER );
    private IPrerequisiteManagementService _prerequisiteManagementService = SpringContextService.getBean( PrerequisiteManagementService.BEAN_NAME );
    private FileItem _importWorkflowFile;

    /*-------------------------------MANAGEMENT  WORKFLOW-----------------------------*/

    /**
     * Return management page of plugin workflow
     * 
     * @param request
     *            The Http request
     * @return Html management page of plugin workflow
     */
    public String getManageWorkflow( HttpServletRequest request )
    {
        setPageTitleProperty( WorkflowUtils.EMPTY_STRING );

        String strWorkGroup = request.getParameter( PARAMETER_WORKGROUP );
        String strIsEnabled = request.getParameter( PARAMETER_IS_ENABLED );
        _strCurrentPageIndexWorkflow = AbstractPaginator.getPageIndex( request, AbstractPaginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndexWorkflow );
        _nItemsPerPageWorkflow = AbstractPaginator.getItemsPerPage( request, AbstractPaginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPageWorkflow,
                _nDefaultItemsPerPage );

        if ( ( strIsEnabled != null ) && !strIsEnabled.equals( WorkflowUtils.EMPTY_STRING ) )
        {
            _nIsEnabled = WorkflowUtils.convertStringToInt( strIsEnabled );
        }

        if ( ( strWorkGroup != null ) && !strWorkGroup.equals( WorkflowUtils.EMPTY_STRING ) )
        {
            _strWorkGroup = strWorkGroup;
        }

        // build Filter
        WorkflowFilter filter = new WorkflowFilter( );
        filter.setIsEnabled( _nIsEnabled );
        filter.setWorkGroup( _strWorkGroup );

        List<Workflow> listWorkflow = _workflowService.getListWorkflowsByFilter( filter );
        listWorkflow = (List<Workflow>) AdminWorkgroupService.getAuthorizedCollection( listWorkflow, (User) getUser( ) );
        Collections.sort( listWorkflow, Comparator.comparing( Workflow::getName ) );

        LocalizedPaginator<Workflow> paginator = new LocalizedPaginator<>( listWorkflow, _nItemsPerPageWorkflow, getJspManageWorkflow( request ),
                PARAMETER_PAGE_INDEX, _strCurrentPageIndexWorkflow, getLocale( ) );

        boolean bPermissionAdvancedParameter = RBACService.isAuthorized( Action.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                ActionResourceIdService.PERMISSION_MANAGE_ADVANCED_PARAMETERS, (User) getUser( ) );

        Map<String, Object> model = new HashMap<>( );

        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, WorkflowUtils.EMPTY_STRING + _nItemsPerPageWorkflow );
        model.put( MARK_USER_WORKGROUP_REF_LIST, AdminWorkgroupService.getUserWorkgroups( getUser( ), getLocale( ) ) );
        model.put( MARK_USER_WORKGROUP_SELECTED, _strWorkGroup );
        model.put( MARK_ACTIVE_REF_LIST, getRefListActive( getLocale( ) ) );
        model.put( MARK_ACTIVE_SELECTED, _nIsEnabled );
        model.put( MARK_WORKFLOW_LIST, paginator.getPageItems( ) );
        model.put( MARK_PERMISSION_MANAGE_ADVANCED_PARAMETERS, bPermissionAdvancedParameter );

        setPageTitleProperty( PROPERTY_MANAGE_WORKFLOW_PAGE_TITLE );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_WORKFLOW, getLocale( ), model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Gets the workflow creation page
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The workflow creation page
     */
    public String getCreateWorkflow( HttpServletRequest request ) throws AccessDeniedException
    {
        AdminUser adminUser = getUser( );
        Locale locale = getLocale( );

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_USER_WORKGROUP_REF_LIST, AdminWorkgroupService.getUserWorkgroups( adminUser, locale ) );
        model.put( MARK_DEFAULT_VALUE_WORKGROUP_KEY, AdminWorkgroupService.ALL_GROUPS );
        setPageTitleProperty( PROPERTY_CREATE_WORKFLOW_PAGE_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_WORKFLOW, locale, model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the workflow creation
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doCreateWorkflow( HttpServletRequest request ) throws AccessDeniedException
    {
        if ( ( request.getParameter( PARAMETER_CANCEL ) == null ) )
        {
            Workflow workflow = new Workflow( );
            String strError = getWorkflowData( request, workflow );

            if ( strError != null )
            {
                return strError;
            }

            workflow.setCreationDate( WorkflowUtils.getCurrentTimestamp( ) );
            _workflowService.create( workflow );
        }

        return getJspManageWorkflow( request );
    }

    /**
     * Gets the workflow creation page
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The workflow creation page
     */
    public String getModifyWorkflow( HttpServletRequest request ) throws AccessDeniedException
    {
        AdminUser adminUser = getUser( );
        String strIdWorkflow = request.getParameter( PARAMETER_ID_WORKFLOW );
        String strPane = request.getParameter( PARAMETER_PANE );
        String strShowTasks = request.getParameter( PARAMETER_SHOW_TASKS );
        int nIdWorkflow = WorkflowUtils.convertStringToInt( strIdWorkflow );
        Workflow workflow = null;

        if ( nIdWorkflow != WorkflowUtils.CONSTANT_ID_NULL )
        {
            workflow = _workflowService.findByPrimaryKey( nIdWorkflow );
        }

        if ( workflow == null )
        {
            throw new AccessDeniedException( LOG_WORKFLOW_NOT_FOUND + nIdWorkflow );
        }

        if ( strPane == null )
        {
            strPane = PANE_DEFAULT;
        }

        StateFilter stateFilter = new StateFilter( );
        stateFilter.setIdWorkflow( nIdWorkflow );

        List<State> listState = _stateService.getListStateByFilter( stateFilter );

        _strCurrentPageIndexState = AbstractPaginator.getPageIndex( request, PARAMETER_PAGE_INDEX_STATE, _strCurrentPageIndexState );
        _nItemsPerPageState = AbstractPaginator.getItemsPerPage( request, PARAMETER_ITEMS_PER_PAGE_STATE, _nItemsPerPageState, _nDefaultItemsPerPage );

        LocalizedPaginator<State> paginatorState = new LocalizedPaginator<>( listState, _nItemsPerPageState,
                getJspModifyWorkflow( request, nIdWorkflow, MARK_PANE_STATE ), PARAMETER_PAGE_INDEX_STATE, _strCurrentPageIndexState,
                PARAMETER_ITEMS_PER_PAGE_STATE, getLocale( ) );

        ActionFilter actionFilter = new ActionFilter( );
        actionFilter.setAutomaticReflexiveAction( false );
        actionFilter.setIdWorkflow( nIdWorkflow );

        List<Action> listAction = _actionService.getListActionByFilter( actionFilter );

        for ( Action action : listAction )
        {
            action.setStateBefore( _stateService.findByPrimaryKey( action.getStateBefore( ).getId( ) ) );
            action.setStateAfter( _stateService.findByPrimaryKey( action.getStateAfter( ).getId( ) ) );
            if ( strShowTasks != null )
            {
                List<ITask> listTasks = _taskService.getListTaskByIdAction( action.getId( ), getLocale( ) );
                action.setAllTasks( listTasks );
            }
        }

        _strCurrentPageIndexAction = AbstractPaginator.getPageIndex( request, PARAMETER_PAGE_INDEX_ACTION, _strCurrentPageIndexAction );

        int nOldItemsPerPageAction = _nItemsPerPageAction;
        _nItemsPerPageAction = AbstractPaginator.getItemsPerPage( request, PARAMETER_ITEMS_PER_PAGE_ACTION, _nItemsPerPageAction, _nDefaultItemsPerPage );

        // Boolean that indicates if the action table or the state table should
        // be displayed
        if ( ( nOldItemsPerPageAction != _nItemsPerPageAction ) && ( nOldItemsPerPageAction > 0 ) )
        {
            strPane = PANE_ACTIONS;
        }

        LocalizedPaginator<Action> paginatorAction = new LocalizedPaginator<>( listAction, _nItemsPerPageAction,
                getJspModifyWorkflow( request, nIdWorkflow, MARK_PANE_ACTIONS ), PARAMETER_PAGE_INDEX_ACTION, _strCurrentPageIndexAction,
                PARAMETER_ITEMS_PER_PAGE_ACTION, getLocale( ) );

        workflow.setAllActions( paginatorAction.getPageItems( ) );
        workflow.setAllStates( paginatorState.getPageItems( ) );

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_USER_WORKGROUP_REF_LIST, AdminWorkgroupService.getUserWorkgroups( adminUser, getLocale( ) ) );
        model.put( MARK_WORKFLOW, workflow );
        model.put( MARK_PAGINATOR_STATE, paginatorState );
        model.put( MARK_PAGINATOR_ACTION, paginatorAction );
        model.put( MARK_STATE_LIST, paginatorState.getPageItems( ) );
        model.put( MARK_ACTION_LIST, paginatorAction.getPageItems( ) );
        model.put( MARK_NUMBER_STATE, listState.size( ) );
        model.put( MARK_NUMBER_ACTION, listAction.size( ) );
        model.put( MARK_NB_ITEMS_PER_PAGE_STATE, WorkflowUtils.EMPTY_STRING + _nItemsPerPageState );
        model.put( MARK_NB_ITEMS_PER_PAGE_ACTION, WorkflowUtils.EMPTY_STRING + _nItemsPerPageAction );
        model.put( MARK_PANE, strPane );
        
        if ( strShowTasks != null )
        {
            model.put( MARK_SHOW_TASKS, "true" );
        }
        model.put( MARK_MDGRAPH, WorkflowGraphExportService.generate( workflow, AppPathService.getBaseUrl( request ) ) );

        setPageTitleProperty( PROPERTY_MODIFY_WORKFLOW_PAGE_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_WORKFLOW, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Gets the workflow graph page
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The workflow creation page
     */
    public String getWorkflowGraph( HttpServletRequest request ) throws AccessDeniedException
    {
        AdminUser adminUser = getUser( );
        String strIdWorkflow = request.getParameter( PARAMETER_ID_WORKFLOW );
        String strShowTasks = request.getParameter( PARAMETER_SHOW_TASKS );
        String strPane = request.getParameter( PARAMETER_PANE );
        int nIdWorkflow = WorkflowUtils.convertStringToInt( strIdWorkflow );
        Workflow workflow = null;

        if ( nIdWorkflow != WorkflowUtils.CONSTANT_ID_NULL )
        {
            workflow = _workflowService.findByPrimaryKey( nIdWorkflow );
        }

        if ( workflow == null )
        {
            throw new AccessDeniedException( LOG_WORKFLOW_NOT_FOUND + nIdWorkflow );
        }

        if ( strPane == null )
        {
            strPane = PANE_DEFAULT;
        }

        StateFilter stateFilter = new StateFilter( );
        stateFilter.setIdWorkflow( nIdWorkflow );

        List<State> listState = _stateService.getListStateByFilter( stateFilter );

        ActionFilter actionFilter = new ActionFilter( );
        actionFilter.setIdWorkflow( nIdWorkflow );

        List<Action> listAction = _actionService.getListActionByFilter( actionFilter );

        for ( Action action : listAction )
        {
            action.setStateBefore( _stateService.findByPrimaryKey( action.getStateBefore( ).getId( ) ) );
            action.setStateAfter( _stateService.findByPrimaryKey( action.getStateAfter( ).getId( ) ) );
            
            if ( strShowTasks != null )
            {
                List<ITask> listTasks = _taskService.getListTaskByIdAction( action.getId( ), getLocale( ) );
                action.setAllTasks( listTasks );
            }
        }

        workflow.setAllActions( listAction );
        workflow.setAllStates( listState );

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_USER_WORKGROUP_REF_LIST, AdminWorkgroupService.getUserWorkgroups( adminUser, getLocale( ) ) );
        model.put( MARK_WORKFLOW, workflow );
        model.put( MARK_PANE, strPane );
        if ( strShowTasks != null )
        {
            model.put( MARK_SHOW_TASKS, "true" );
        }
        model.put( MARK_MDGRAPH, WorkflowGraphExportService.generate( workflow, AppPathService.getBaseUrl( request ) ) );

        setPageTitleProperty( PROPERTY_GRAPH_WORKFLOW_PAGE_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_GRAPH_WORKFLOW, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }
    
    /**
     * Perform the workflow modification
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doModifyWorkflow( HttpServletRequest request ) throws AccessDeniedException
    {
        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            String strIdWorkflow = request.getParameter( PARAMETER_ID_WORKFLOW );
            int nIdWorkflow = WorkflowUtils.convertStringToInt( strIdWorkflow );
            Workflow workflow = null;

            if ( nIdWorkflow != WorkflowUtils.CONSTANT_ID_NULL )
            {
                workflow = _workflowService.findByPrimaryKey( nIdWorkflow );

                if ( ( workflow == null ) )
                {
                    throw new AccessDeniedException( LOG_WORKFLOW_NOT_FOUND + nIdWorkflow );
                }

                String strError = getWorkflowData( request, workflow );

                if ( strError != null )
                {
                    return strError;
                }

                _workflowService.update( workflow );

                if ( request.getParameter( PARAMETER_APPLY ) != null )
                {
                    return getJspModifyWorkflow( request, nIdWorkflow );
                }
            }
        }

        return getJspManageWorkflow( request );
    }

    /**
     * Gets the confirmation page of remove all Directory Record
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return the confirmation page of delete all Directory Record
     */
    public String getConfirmRemoveWorkflow( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdWorkflow = request.getParameter( PARAMETER_ID_WORKFLOW );

        UrlItem url = new UrlItem( JSP_DO_REMOVE_WORKFLOW );
        url.addParameter( PARAMETER_ID_WORKFLOW, strIdWorkflow );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_WORKFLOW, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Remove all workflow record of the workflow
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doRemoveWorkflow( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdWorkflow = request.getParameter( PARAMETER_ID_WORKFLOW );

        int nIdWorkflow = WorkflowUtils.convertStringToInt( strIdWorkflow );

        ArrayList<String> listErrors = new ArrayList<>( );

        if ( !WorkflowRemovalListenerService.getService( ).checkForRemoval( strIdWorkflow, listErrors, getLocale( ) ) )
        {
            String strCause = AdminMessageService.getFormattedList( listErrors, getLocale( ) );
            Object [ ] args = {
                    strCause
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_CAN_NOT_REMOVE_WORKFLOW, args, AdminMessage.TYPE_STOP );
        }

        _workflowService.remove( nIdWorkflow );

        return getJspManageWorkflow( request );
    }

    /**
     * Remove all workflow record of the workflow
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doEnableWorkflow( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdWorkflow = request.getParameter( PARAMETER_ID_WORKFLOW );
        int nIdWorkflow = WorkflowUtils.convertStringToInt( strIdWorkflow );
        Workflow workflow = _workflowService.findByPrimaryKey( nIdWorkflow );

        if ( workflow == null )
        {
            throw new AccessDeniedException( LOG_WORKFLOW_NOT_FOUND + nIdWorkflow );
        }

        workflow.setEnabled( true );
        _workflowService.update( workflow );

        return getJspManageWorkflow( request );
    }

    /**
     * Remove all workflow record of the workflow
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doDisableWorkflow( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdWorkflow = request.getParameter( PARAMETER_ID_WORKFLOW );
        int nIdWorkflow = WorkflowUtils.convertStringToInt( strIdWorkflow );
        Workflow workflow = _workflowService.findByPrimaryKey( nIdWorkflow );
        ArrayList<String> listErrors = new ArrayList<>( );

        if ( workflow == null )
        {
            throw new AccessDeniedException( LOG_WORKFLOW_NOT_FOUND + nIdWorkflow );
        }

        if ( !WorkflowRemovalListenerService.getService( ).checkForRemoval( strIdWorkflow, listErrors, getLocale( ) ) )
        {
            String strCause = AdminMessageService.getFormattedList( listErrors, getLocale( ) );
            Object [ ] args = {
                    strCause
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_CAN_NOT_DISABLE_WORKFLOW, args, AdminMessage.TYPE_STOP );
        }

        workflow.setEnabled( false );
        _workflowService.update( workflow );

        return getJspManageWorkflow( request );
    }

    /**
     * set the data of the workflow in the workflow object
     * 
     * @param request
     *            The HTTP request
     * @param workflow
     *            the workflow object
     * @param locale
     *            the locale
     * @return null if no error appear
     */
    private String getWorkflowData( HttpServletRequest request, Workflow workflow )
    {
        String strName = request.getParameter( PARAMETER_NAME );
        String strDescription = request.getParameter( PARAMETER_DESCRIPTION );

        String strWorkgroup = request.getParameter( PARAMETER_WORKGROUP );
        String strFieldError = WorkflowUtils.EMPTY_STRING;

        if ( ( strName == null ) || strName.trim( ).equals( WorkflowUtils.EMPTY_STRING ) )
        {
            strFieldError = FIELD_WORKFLOW_NAME;
        }
        else
            if ( ( strDescription == null ) || strDescription.trim( ).equals( WorkflowUtils.EMPTY_STRING ) )
            {
                strFieldError = FIELD_WORKFLOW_DESCRIPTION;
            }

        if ( !strFieldError.equals( WorkflowUtils.EMPTY_STRING ) )
        {
            Object [ ] tabRequiredFields = {
                    I18nService.getLocalizedString( strFieldError, getLocale( ) )
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        workflow.setName( strName );
        workflow.setDescription( strDescription );
        workflow.setWorkgroup( strWorkgroup );

        return null;
    }

    /**
     * Gets the workflow creation page
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The workflow creation page
     */
    public String getCreateState( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdWorkflow = request.getParameter( PARAMETER_ID_WORKFLOW );
        int nIdWorkflow = WorkflowUtils.convertStringToInt( strIdWorkflow );
        Workflow workflow = null;

        if ( nIdWorkflow != WorkflowUtils.CONSTANT_ID_NULL )
        {
            workflow = _workflowService.findByPrimaryKey( nIdWorkflow );
        }

        if ( workflow == null )
        {
            throw new AccessDeniedException( LOG_WORKFLOW_NOT_FOUND + nIdWorkflow );
        }

        List<Icon> listIcon = _iconService.getListIcons( );

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_WORKFLOW, workflow );
        model.put( MARK_INITIAL_STATE, _stateService.getInitialState( nIdWorkflow ) != null );
        model.put( MARK_ICON_LIST, listIcon );
        setPageTitleProperty( PROPERTY_CREATE_STATE_PAGE_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_STATE, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the workflow creation
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doCreateState( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdWorkflow = request.getParameter( PARAMETER_ID_WORKFLOW );
        int nIdWorkflow = WorkflowUtils.convertStringToInt( strIdWorkflow );

        if ( ( request.getParameter( PARAMETER_CANCEL ) == null ) )
        {
            Workflow workflow = null;

            if ( nIdWorkflow != WorkflowUtils.CONSTANT_ID_NULL )
            {
                workflow = _workflowService.findByPrimaryKey( nIdWorkflow );
            }

            if ( workflow == null )
            {
                throw new AccessDeniedException( LOG_WORKFLOW_NOT_FOUND + nIdWorkflow );
            }

            State state = new State( );
            state.setWorkflow( workflow );

            String strError = getStateData( request, state );

            if ( strError != null )
            {
                return strError;
            }

            if ( Boolean.TRUE.equals( state.isInitialState( ) ) )
            {
                // test if initial test already exist
                State stateInitial = _stateService.getInitialState( nIdWorkflow );

                if ( stateInitial != null )
                {
                    Object [ ] tabInitialState = {
                            stateInitial.getName( )
                    };

                    return AdminMessageService.getMessageUrl( request, MESSAGE_INITIAL_STATE_ALREADY_EXIST, tabInitialState, AdminMessage.TYPE_STOP );
                }
            }

            // get the maximum order number in this workflow and set max+1
            int nMaximumOrder = _stateService.findMaximumOrderByWorkflowId( nIdWorkflow );
            state.setOrder( nMaximumOrder + 1 );

            _stateService.create( state );
        }

        return getJspModifyWorkflow( request, nIdWorkflow );
    }

    /**
     * Gets the workflow creation page
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The workflow creation page
     */
    public String getModifyState( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdState = request.getParameter( PARAMETER_ID_STATE );
        int nIdState = WorkflowUtils.convertStringToInt( strIdState );
        State state = null;

        if ( nIdState != WorkflowUtils.CONSTANT_ID_NULL )
        {
            state = _stateService.findByPrimaryKey( nIdState );
        }

        if ( state == null )
        {
            throw new AccessDeniedException( "State not found for ID " + nIdState );
        }

        List<Icon> listIcon = _iconService.getListIcons( );

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_STATE, state );
        model.put( MARK_ICON_LIST, listIcon );
        setPageTitleProperty( PROPERTY_MODIFY_STATE_PAGE_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_STATE, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the workflow modification
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doModifyState( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdState = request.getParameter( PARAMETER_ID_STATE );
        int nIdState = WorkflowUtils.convertStringToInt( strIdState );
        State state = null;

        if ( nIdState != WorkflowUtils.CONSTANT_ID_NULL )
        {
            state = _stateService.findByPrimaryKey( nIdState );
        }

        if ( state == null )
        {
            throw new AccessDeniedException( "State not found for ID " + nIdState );
        }

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            boolean isInitialTestStore = state.isInitialState( );
            String strError = getStateData( request, state );

            if ( strError != null )
            {
                return strError;
            }

            if ( !isInitialTestStore && Boolean.TRUE.equals( state.isInitialState( ) ) )
            {
                // test if initial test already exist
                StateFilter filter = new StateFilter( );
                filter.setIdWorkflow( state.getWorkflow( ).getId( ) );
                filter.setIsInitialState( StateFilter.FILTER_TRUE );

                List<State> listState = _stateService.getListStateByFilter( filter );

                if ( CollectionUtils.isNotEmpty( listState ) )
                {
                    Object [ ] tabInitialState = {
                            listState.get( 0 ).getName( )
                    };

                    return AdminMessageService.getMessageUrl( request, MESSAGE_INITIAL_STATE_ALREADY_EXIST, tabInitialState, AdminMessage.TYPE_STOP );
                }
            }

            _stateService.update( state );
        }

        return getJspModifyWorkflow( request, state.getWorkflow( ).getId( ) );
    }

    /**
     * Gets the confirmation page of remove all Directory Record
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return the confirmation page of delete all Directory Record
     */
    public String getConfirmRemoveState( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdState = request.getParameter( PARAMETER_ID_STATE );
        int nIdState = WorkflowUtils.convertStringToInt( strIdState );

        ActionFilter filter = new ActionFilter( );
        filter.setIdStateBefore( nIdState );
        filter.setAutomaticReflexiveAction( false );

        List<Action> listActionStateBefore = _actionService.getListActionByFilter( filter );
        filter.setIdStateBefore( ActionFilter.ALL_INT );
        filter.setIdStateAfter( nIdState );

        List<Action> listActionStateAfter = _actionService.getListActionByFilter( filter );

        if ( CollectionUtils.isNotEmpty( listActionStateBefore ) || CollectionUtils.isNotEmpty( listActionStateAfter ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_CAN_NOT_REMOVE_STATE_ACTIONS_ARE_ASSOCIATE, AdminMessage.TYPE_STOP );
        }

        UrlItem url = new UrlItem( JSP_DO_REMOVE_STATE );
        url.addParameter( PARAMETER_ID_STATE, strIdState );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_STATE, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Remove all workflow record of the workflow
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doRemoveState( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdState = request.getParameter( PARAMETER_ID_STATE );
        int nIdState = WorkflowUtils.convertStringToInt( strIdState );

        ActionFilter filter = new ActionFilter( );
        filter.setAutomaticReflexiveAction( false );
        filter.setIdStateBefore( nIdState );

        List<Action> listActionStateBefore = _actionService.getListActionByFilter( filter );
        filter.setIdStateBefore( ActionFilter.ALL_INT );
        filter.setIdStateAfter( nIdState );

        List<Action> listActionStateAfter = _actionService.getListActionByFilter( filter );

        if ( CollectionUtils.isNotEmpty( listActionStateBefore ) || CollectionUtils.isNotEmpty( listActionStateAfter ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_CAN_NOT_REMOVE_STATE_ACTIONS_ARE_ASSOCIATE, AdminMessage.TYPE_STOP );
        }

        State state = _stateService.findByPrimaryKey( nIdState );

        if ( state != null )
        {
            List<Action> listActions = getAutomaticReflexiveActionsFromState( nIdState );

            if ( CollectionUtils.isNotEmpty( listActions ) )
            {
                for ( Action action : listActions )
                {
                    List<ITask> listTasksFound = _taskService.getListTaskByIdAction( action.getId( ), getLocale( ) );

                    if ( CollectionUtils.isNotEmpty( listTasksFound ) )
                    {
                        return AdminMessageService.getMessageUrl( request, MESSAGE_CAN_NOT_REMOVE_STATE_TASKS_ARE_ASSOCIATE, AdminMessage.TYPE_STOP );
                    }
                }
            }

            _stateService.remove( nIdState );
            _stateService.decrementOrderByOne( state.getOrder( ), state.getWorkflow( ).getId( ) );

            return getJspModifyWorkflow( request, state.getWorkflow( ).getId( ) );
        }

        return getJspManageWorkflow( request );
    }

    /**
     * set the data of the state in the state object
     * 
     * @param request
     *            The HTTP request
     * @param state
     *            the state object
     * @param locale
     *            the locale
     * @return null if no error appear
     */
    private String getStateData( HttpServletRequest request, State state )
    {
        String strName = request.getParameter( PARAMETER_NAME );
        String strDescription = request.getParameter( PARAMETER_DESCRIPTION );
        String strIsInitialState = request.getParameter( PARAMETER_IS_INITIAL_STATE );
        String strIsRequiredWorkgroupAssigned = request.getParameter( PARAMETER_IS_REQUIRED_WORKGROUP_ASSIGNED );
        String strIdIcon = request.getParameter( PARAMETER_ID_ICON );

        String strFieldError = WorkflowUtils.EMPTY_STRING;

        if ( ( strName == null ) || strName.trim( ).equals( WorkflowUtils.EMPTY_STRING ) )
        {
            strFieldError = FIELD_STATE_NAME;
        }
        else
            if ( ( strDescription == null ) || strDescription.trim( ).equals( WorkflowUtils.EMPTY_STRING ) )
            {
                strFieldError = FIELD_STATE_DESCRIPTION;
            }

        if ( !strFieldError.equals( WorkflowUtils.EMPTY_STRING ) )
        {
            Object [ ] tabRequiredFields = {
                    I18nService.getLocalizedString( strFieldError, getLocale( ) )
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        int nIdIcon = WorkflowUtils.convertStringToInt( strIdIcon );

        state.setName( strName );
        state.setDescription( strDescription );
        state.setInitialState( strIsInitialState != null );
        state.setRequiredWorkgroupAssigned( strIsRequiredWorkgroupAssigned != null );

        Icon icon = new Icon( );
        icon.setId( nIdIcon );
        state.setIcon( icon );

        return null;
    }

    /**
     * Gets the workflow creation page
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The workflow creation page
     */
    public String getCreateAction( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdWorkflow = request.getParameter( PARAMETER_ID_WORKFLOW );
        int nIdWorkflow = WorkflowUtils.convertStringToInt( strIdWorkflow );
        Workflow workflow = null;

        if ( nIdWorkflow != WorkflowUtils.CONSTANT_ID_NULL )
        {
            workflow = _workflowService.findByPrimaryKey( nIdWorkflow );
        }

        if ( workflow == null )
        {
            throw new AccessDeniedException( LOG_WORKFLOW_NOT_FOUND + nIdWorkflow );
        }

        Map<String, Object> model = new HashMap<>( );

        StateFilter filter = new StateFilter( );
        filter.setIdWorkflow( nIdWorkflow );

        List<State> listState = _stateService.getListStateByFilter( filter );
        List<Icon> listIcon = _iconService.getListIcons( );

        model.put( MARK_WORKFLOW, workflow );
        model.put( MARK_STATE_LIST, WorkflowUtils.getRefList( listState, false, getLocale( ) ) );
        model.put( MARK_ICON_LIST, listIcon );
        model.put( MARK_AVAILABLE_LINKED_ACTIONS, getAvailableActionsToLink( WorkflowUtils.CONSTANT_ID_NULL, nIdWorkflow ) );

        setPageTitleProperty( PROPERTY_CREATE_ACTION_PAGE_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_ACTION, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the workflow creation
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doCreateAction( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdWorkflow = request.getParameter( PARAMETER_ID_WORKFLOW );
        int nIdWorkflow = WorkflowUtils.convertStringToInt( strIdWorkflow );

        if ( ( request.getParameter( PARAMETER_CANCEL ) == null ) )
        {
            Workflow workflow = null;

            if ( nIdWorkflow != WorkflowUtils.CONSTANT_ID_NULL )
            {
                workflow = _workflowService.findByPrimaryKey( nIdWorkflow );
            }

            if ( workflow == null )
            {
                throw new AccessDeniedException( LOG_WORKFLOW_NOT_FOUND + nIdWorkflow );
            }

            Action action = new Action( );
            action.setWorkflow( workflow );

            String strError = getActionData( request, action );

            if ( strError != null )
            {
                return strError;
            }

            // get the maximum order number in this workflow and set max+1
            int nMaximumOrder = _actionService.findMaximumOrderByWorkflowId( nIdWorkflow );
            action.setOrder( nMaximumOrder + 1 );

            _actionService.create( action );

            if ( request.getParameter( PARAMETER_APPLY ) != null )
            {
                return getJspModifyAction( request, action.getId( ) );
            }
        }

        return getJspModifyWorkflow( request, nIdWorkflow, PANE_ACTIONS );
    }

    /**
     * set the data of the action in the action object
     * 
     * @param request
     *            The HTTP request
     * @param action
     *            the action object
     * @param locale
     *            the locale
     * @return null if no error appear
     */
    private String getActionData( HttpServletRequest request, Action action )
    {
        String strName = request.getParameter( PARAMETER_NAME );
        String strDescription = request.getParameter( PARAMETER_DESCRIPTION );
        String strIdStateBefore = request.getParameter( PARAMETER_ID_STATE_BEFORE );
        String strIdStateAfter = request.getParameter( PARAMETER_ID_STATE_AFTER );
        String strIdIcon = request.getParameter( PARAMETER_ID_ICON );
        String strAutomatic = request.getParameter( PARAMETER_ID_AUTOMATIC );
        String strIsMassAction = request.getParameter( PARAMETER_IS_MASS_ACTION );

        int nIdStateBefore = WorkflowUtils.convertStringToInt( strIdStateBefore );
        int nIdStateAfter = WorkflowUtils.convertStringToInt( strIdStateAfter );
        int nIdIcon = WorkflowUtils.convertStringToInt( strIdIcon );
        boolean bIsAutomatic = strAutomatic != null;
        boolean bIsMassAction = strIsMassAction != null;

        String strFieldError = StringUtils.EMPTY;

        if ( StringUtils.isBlank( strName ) )
        {
            strFieldError = FIELD_ACTION_NAME;
        }
        else
            if ( StringUtils.isBlank( strDescription ) )
            {
                strFieldError = FIELD_ACTION_DESCRIPTION;
            }
            else
                if ( nIdStateBefore == WorkflowUtils.CONSTANT_ID_NULL )
                {
                    strFieldError = FIELD_STATE_BEFORE;
                }
                else
                    if ( nIdStateAfter == WorkflowUtils.CONSTANT_ID_NULL )
                    {
                        strFieldError = FIELD_STATE_AFTER;
                    }
                    else
                        if ( nIdIcon == WorkflowUtils.CONSTANT_ID_NULL )
                        {
                            strFieldError = FIELD_ICON;
                        }
                        else
                            if ( bIsAutomatic && ( nIdStateBefore == nIdStateAfter ) )
                            {
                                Object [ ] tabRequiredFields = {
                                        I18nService.getLocalizedString( FIELD_STATE_BEFORE, getLocale( ) ),
                                        I18nService.getLocalizedString( FIELD_STATE_AFTER, getLocale( ) ),
                                };

                                return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_AUTOMATIC_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
                            }
                            else
                                if ( bIsAutomatic && bIsMassAction )
                                {
                                    return AdminMessageService.getMessageUrl( request, MESSAGE_MASS_ACTION_CANNOT_BE_AUTOMATIC, AdminMessage.TYPE_STOP );
                                }

        if ( StringUtils.isNotBlank( strFieldError ) )
        {
            Object [ ] tabRequiredFields = {
                    I18nService.getLocalizedString( strFieldError, getLocale( ) )
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        if ( bIsAutomatic )
        {
            for ( ITask task : _taskService.getListTaskByIdAction( action.getId( ), getLocale( ) ) )
            {
                if ( !task.getTaskType( ).isTaskForAutomaticAction( ) )
                {
                    return AdminMessageService.getMessageUrl( request, MESSAGE_TASK_IS_NOT_AUTOMATIC, AdminMessage.TYPE_STOP );
                }
            }
        }

        action.setName( strName );
        action.setDescription( strDescription );

        State stateBefore = new State( );
        stateBefore.setId( nIdStateBefore );
        action.setStateBefore( stateBefore );

        State stateAfter = new State( );
        stateAfter.setId( nIdStateAfter );
        action.setStateAfter( stateAfter );

        Icon icon = new Icon( );
        icon.setId( nIdIcon );
        action.setIcon( icon );

        action.setAutomaticState( bIsAutomatic );
        action.setMassAction( bIsMassAction );

        action.setListIdsLinkedAction( getSelectedLinkedActions( action, request ) );

        return null;
    }

    /**
     * Gets the workflow creation page
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The workflow creation page
     */
    public String getModifyAction( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdAction = request.getParameter( PARAMETER_ID_ACTION );
        int nIdAction = WorkflowUtils.convertStringToInt( strIdAction );
        Action action = null;

        if ( nIdAction != WorkflowUtils.CONSTANT_ID_NULL )
        {
            action = _actionService.findByPrimaryKey( nIdAction );
        }

        if ( action == null )
        {
            throw new AccessDeniedException( LOG_ACTION_NOT_FOUND + nIdAction );
        }

        StateFilter filter = new StateFilter( );
        filter.setIdWorkflow( action.getWorkflow( ).getId( ) );

        List<State> listState = _stateService.getListStateByFilter( filter );
        List<Icon> listIcon = _iconService.getListIcons( );

        ReferenceList refListTaskType = new ReferenceList( );
        Collection<ITaskType> taskTypeList = _taskFactory.getAllTaskTypes( );

        List<PrerequisiteDTO> listPrerequisiteDTO = null;
        List<Prerequisite> listPrerequisite = _prerequisiteManagementService.getListPrerequisite( action.getId( ) );
        if ( CollectionUtils.isNotEmpty( listPrerequisite ) )
        {
            listPrerequisiteDTO = new ArrayList<>( listPrerequisite.size( ) );

            for ( Prerequisite prerequisite : listPrerequisite )
            {
                IAutomaticActionPrerequisiteService prerequisiteService = _prerequisiteManagementService
                        .getPrerequisiteService( prerequisite.getPrerequisiteType( ) );
                PrerequisiteDTO dto = new PrerequisiteDTO( prerequisite, prerequisiteService.getTitleI18nKey( ), prerequisiteService.hasConfiguration( ) );
                listPrerequisiteDTO.add( dto );
            }
        }

        if ( action.isAutomaticState( ) )
        {
            for ( ITaskType taskType : taskTypeList )
            {
                if ( _taskFactory.newTask( taskType.getKey( ), getLocale( ) ).getTaskType( ).isTaskForAutomaticAction( ) )
                {
                    refListTaskType.addItem( taskType.getKey( ), I18nService.getLocalizedString( taskType.getTitleI18nKey( ), getLocale( ) ) );
                }
            }
        }
        else
        {
            refListTaskType = ReferenceList.convert( _workflowService.getMapTaskTypes( getLocale( ) ) );
        }

        setPageTitleProperty( PROPERTY_MODIFY_ACTION_PAGE_TITLE );

        List<ITask> taskList = _taskService.getListTaskByIdAction( nIdAction, getLocale( ) );

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_ACTION, action );
        model.put( MARK_STATE_LIST, WorkflowUtils.getRefList( listState, false, getLocale( ) ) );
        model.put( MARK_TASK_TYPE_LIST, refListTaskType );
        model.put( MARK_TASK_LIST, taskList );
        model.put( MARK_NUMBER_TASK, taskList.size( ) );
        model.put( MARK_ICON_LIST, listIcon );
        model.put( MARK_PLUGIN, getPlugin( ) );
        model.put( MARK_LOCALE, getLocale( ) );
        model.put( MARK_LIST_PREREQUISITE, listPrerequisiteDTO );
        model.put( MARK_LIST_PREREQUISITE_TYPE, getPrerequisiteServiceRefList( getLocale( ) ) );

        boolean bDisplayTasksForm = _workflowService.isDisplayTasksForm( nIdAction, getLocale( ) );
        model.put( MARK_DISPLAY_TASKS_FORM, bDisplayTasksForm );

        // The action can be linked only it has no task that requires a form
        if ( !bDisplayTasksForm )
        {
            model.put( MARK_AVAILABLE_LINKED_ACTIONS, getAvailableActionsToLink( nIdAction, action.getWorkflow( ).getId( ) ) );
            model.put( MARK_SELECTED_LINKED_ACTIONS, getLinkedActions( nIdAction ) );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_ACTION, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Get the list of prerequisites services
     * 
     * @param locale
     *            The locale
     * @return The list of prerequisite services
     */
    private ReferenceList getPrerequisiteServiceRefList( Locale locale )
    {
        ReferenceList refList = new ReferenceList( );

        for ( IAutomaticActionPrerequisiteService service : _prerequisiteManagementService.getPrerequisiteServiceList( ) )
        {
            refList.addItem( service.getPrerequisiteType( ), I18nService.getLocalizedString( service.getTitleI18nKey( ), locale ) );
        }

        return refList;
    }

    /**
     * Perform the workflow modification
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doModifyAction( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdAction = request.getParameter( PARAMETER_ID_ACTION );
        int nIdAction = WorkflowUtils.convertStringToInt( strIdAction );
        Action action = null;

        if ( nIdAction != WorkflowUtils.CONSTANT_ID_NULL )
        {
            action = _actionService.findByPrimaryKey( nIdAction );
        }

        if ( action == null )
        {
            throw new AccessDeniedException( LOG_ACTION_NOT_FOUND + nIdAction );
        }

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            String strError = getActionData( request, action );

            if ( strError != null )
            {
                return strError;
            }

            _actionService.update( action );
        }

        if ( request.getParameter( PARAMETER_APPLY ) != null )
        {
            return getJspModifyAction( request, nIdAction );
        }

        return getJspModifyWorkflow( request, action.getWorkflow( ).getId( ), PANE_ACTIONS );
    }

    /**
     * Gets the confirmation page of remove all Directory Record
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return the confirmation page of delete all Directory Record
     */
    public String getConfirmRemoveAction( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdAction = request.getParameter( PARAMETER_ID_ACTION );

        UrlItem url = new UrlItem( JSP_DO_REMOVE_ACTION );
        url.addParameter( PARAMETER_ID_ACTION, strIdAction );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_ACTION, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Remove all workflow record of the workflow
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doRemoveAction( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdAction = request.getParameter( PARAMETER_ID_ACTION );
        int nIdAction = WorkflowUtils.convertStringToInt( strIdAction );
        Action action = _actionService.findByPrimaryKey( nIdAction );

        if ( action != null )
        {
            _prerequisiteManagementService.deletePrerequisiteByAction( nIdAction );
            _actionService.remove( nIdAction );
            _actionService.decrementOrderByOne( action.getOrder( ), action.getWorkflow( ).getId( ) );

            return getJspModifyWorkflow( request, action.getWorkflow( ).getId( ), PANE_ACTIONS );
        }

        return getJspManageWorkflow( request );
    }

    /**
     * Remove all workflow record of the workflow
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doInsertTask( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdAction = request.getParameter( PARAMETER_ID_ACTION );
        int nIdAction = WorkflowUtils.convertStringToInt( strIdAction );
        String strTaskTypeKey = request.getParameter( PARAMETER_TASK_TYPE_KEY );
        Action action = _actionService.findByPrimaryKey( nIdAction );
        ITask task = _taskFactory.newTask( strTaskTypeKey, getLocale( ) );

        if ( ( action != null ) && ( task != null ) )
        {
            task.setAction( action );

            // get the maximum order number in this workflow and set max+1
            int nMaximumOrder = _taskService.findMaximumOrderByActionId( action.getId( ) );
            task.setOrder( nMaximumOrder + 1 );

            _taskService.create( task );

            // If the task requires a form, then remove any links to the action
            if ( task.getTaskType( ).isFormTaskRequired( ) )
            {
                _actionService.removeLinkedActions( nIdAction );
            }
        }

        return getJspModifyAction( request, nIdAction );
    }

    /**
     * Gets the workflow creation page
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The workflow creation page
     */
    public String getModifyTask( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdTask = request.getParameter( PARAMETER_ID_TASK );
        int nIdTask = WorkflowUtils.convertStringToInt( strIdTask );
        ITask task = _taskService.findByPrimaryKey( nIdTask, getLocale( ) );

        if ( task == null )
        {
            throw new AccessDeniedException( "Task not found for ID " + nIdTask );
        }

        Map<String, Object> model = new HashMap<>( );

        model.put( MARK_TASK, task );
        model.put( MARK_TASK_CONFIG, _taskComponentManager.getDisplayConfigForm( request, getLocale( ), task ) );

        setPageTitleProperty( PROPERTY_MODIFY_TASK_PAGE_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_TASK, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Modify task
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doModifyTask( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdTask = request.getParameter( PARAMETER_ID_TASK );
        int nIdTask = WorkflowUtils.convertStringToInt( strIdTask );
        ITask task = _taskService.findByPrimaryKey( nIdTask, getLocale( ) );

        if ( task != null )
        {
            Action action = _actionService.findByPrimaryKey( task.getAction( ).getId( ) );

            if ( request.getParameter( PARAMETER_CANCEL ) == null )
            {
                String strError = _taskComponentManager.doSaveConfig( request, getLocale( ), task );

                if ( strError != null )
                {
                    return strError;
                }

                if ( request.getParameter( PARAMETER_APPLY ) != null )
                {
                    return getJspModifyTask( request, nIdTask );
                }
            }

            if ( action.isAutomaticReflexiveAction( ) )
            {
                return getJspModifyReflexiveAction( request, action.getStateBefore( ).getId( ) );
            }

            return getJspModifyAction( request, task.getAction( ).getId( ) );
        }

        return getJspManageWorkflow( request );
    }

    /**
     * Gets the confirmation page of remove task
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return the confirmation page of delete Task
     */
    public String getConfirmRemoveTask( HttpServletRequest request ) throws AccessDeniedException
    {
        String strId = request.getParameter( PARAMETER_ID_TASK );

        if ( StringUtils.isEmpty( strId ) || !StringUtils.isNumeric( strId ) )
        {
            return getJspManageWorkflow( request );
        }

        int nIdTask = Integer.parseInt( strId );
        ITask task = _taskService.findByPrimaryKey( nIdTask, getLocale( ) );
        Action action = _actionService.findByPrimaryKey( task.getAction( ).getId( ) );
        UrlItem url;

        if ( action.isAutomaticReflexiveAction( ) )
        {
            url = new UrlItem( JSP_DO_REMOVE_TASK_FROM_REFLEXIVE_ACTION );
            url.addParameter( PARAMETER_ID_TASK, strId );
            url.addParameter( PARAMETER_ID_STATE, action.getStateBefore( ).getId( ) );
        }
        else
        {
            url = new UrlItem( JSP_DO_REMOVE_TASK );
            url.addParameter( PARAMETER_ID_TASK, strId );
        }

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_TASK, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Remove all workflow record of the workflow
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doRemoveTask( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdTask = request.getParameter( PARAMETER_ID_TASK );
        int nIdTask = WorkflowUtils.convertStringToInt( strIdTask );
        ITask task = _taskService.findByPrimaryKey( nIdTask, getLocale( ) );

        if ( ( task != null ) && ( task.getTaskType( ) != null ) )
        {
            List<String> listErrors = new ArrayList<>( );

            if ( !TaskRemovalListenerService.getService( ).checkForRemoval( strIdTask, listErrors, getLocale( ) ) )
            {
                String strCause = AdminMessageService.getFormattedList( listErrors, getLocale( ) );
                Object [ ] arguments = {
                        strCause
                };

                return AdminMessageService.getMessageUrl( request, MESSAGE_CAN_NOT_REMOVE_TASK, arguments, AdminMessage.TYPE_STOP );
            }

            if ( task.getTaskType( ).isConfigRequired( ) )
            {
                task.doRemoveConfig( );
            }

            _taskService.remove( nIdTask );
            _taskService.decrementOrderByOne( task.getOrder( ), task.getAction( ).getId( ) );

            Action action = _actionService.findByPrimaryKey( task.getAction( ).getId( ) );

            if ( action != null )
            {
                UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MODIFY_ACTION );
                url.addParameter( PARAMETER_ID_ACTION, task.getAction( ).getId( ) );

                return url.getUrl( );
            }
        }

        return getJspManageWorkflow( request );
    }

    /**
     * copy the task whose key is specified in the Http request
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     * @throws InvocationTargetException
     *             the {@link InvocationTargetException}
     * @throws IllegalAccessException
     *             the {@link IllegalAccessException}
     * @throws NoSuchMethodException
     *             the {@link NoSuchMethodException}
     */
    public String doCopyTask( HttpServletRequest request )
            throws AccessDeniedException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        String strIdTask = request.getParameter( PARAMETER_ID_TASK );
        ITask taskToCopy;
        int nIdTaskToCopy = WorkflowUtils.convertStringToInt( strIdTask );
        taskToCopy = _taskService.findByPrimaryKey( nIdTaskToCopy, request.getLocale( ) );

        doCopyTaskWithModifiedParam( taskToCopy, null );

        Action action = _actionService.findByPrimaryKey( taskToCopy.getAction( ).getId( ) );

        if ( action.isAutomaticReflexiveAction( ) )
        {
            return getJspModifyReflexiveAction( request, action.getStateBefore( ).getId( ) );
        }

        return getJspModifyAction( request, taskToCopy.getAction( ).getId( ) );
    }

    /**
     * Copy the task whose key is specified in the Http request and update param if exists
     * 
     * @param taskToCopy
     *            the task to copy
     * @param mapParamToChange
     *            the map of (Param, Value) to change
     * @throws NoSuchMethodException
     *             NoSuchMethodException the {@link NoSuchMethodException}
     * @throws IllegalAccessException
     *             IllegalAccessException the {@link IllegalAccessException}
     * @throws InvocationTargetException
     *             InvocationTargetException the {@link InvocationTargetException}
     */
    public void doCopyTaskWithModifiedParam( ITask taskToCopy, Map<String, String> mapParamToChange )
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        // Save nIdTaskToCopy
        Integer nIdTaskToCopy = taskToCopy.getId( );

        // get the maximum order number in this workflow and set max+1
        int nMaximumOrder = _taskService.findMaximumOrderByActionId( taskToCopy.getAction( ).getId( ) );
        taskToCopy.setOrder( nMaximumOrder + 1 );

        // Create the new task (taskToCopy id will be update with the new
        // idTask)
        _taskService.create( taskToCopy );

        // get all taskConfigService
        List<ITaskConfigService> listTaskConfigService = SpringContextService.getBeansOfType( ITaskConfigService.class );

        // For each taskConfigService, update parameter if exists
        for ( ITaskConfigService taskConfigService : listTaskConfigService )
        {
            ITaskConfig taskConfig = taskConfigService.findByPrimaryKey( nIdTaskToCopy );

            if ( taskConfig != null )
            {
                taskConfig.setIdTask( taskToCopy.getId( ) );

                if ( mapParamToChange != null )
                {
                    EntrySetMapIterator it = new EntrySetMapIterator( mapParamToChange );

                    while ( it.hasNext( ) )
                    {
                        String key = (String) it.next( );
                        String value = (String) it.getValue( );
                        MethodUtil.set( taskConfig, key, value );
                    }
                }

                taskConfigService.create( taskConfig );
            }
        }
    }

    /**
     * Returns advanced parameters form
     * 
     * @param request
     *            The Http request
     * @return Html form
     */
    public String getManageAdvancedParameters( HttpServletRequest request )
    {
        if ( !RBACService.isAuthorized( Action.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, ActionResourceIdService.PERMISSION_MANAGE_ADVANCED_PARAMETERS,
                (User) getUser( ) ) )
        {
            return getManageWorkflow( request );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_ADVANCED_PARAMETERS, getLocale( ) );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * return a reference list wich contains the different state of a workflow
     * 
     * @param locale
     *            the locale
     * @return reference list of workflow state
     */
    private ReferenceList getRefListActive( Locale locale )
    {
        ReferenceList refListState = new ReferenceList( );
        String strAll = I18nService.getLocalizedString( PROPERTY_ALL, locale );
        String strYes = I18nService.getLocalizedString( PROPERTY_YES, locale );
        String strNo = I18nService.getLocalizedString( PROPERTY_NO, locale );

        refListState.addItem( -1, strAll );
        refListState.addItem( 1, strYes );
        refListState.addItem( 0, strNo );

        return refListState;
    }

    /**
     * return url of the jsp modify workflow
     * 
     * @param request
     *            The HTTP request
     * @param nIdWorkflow
     *            the key of workflow to modify
     * @return return url of the jsp modify workflows
     */
    private String getJspModifyWorkflow( HttpServletRequest request, int nIdWorkflow )
    {
        return getJspModifyWorkflow( request, nIdWorkflow, null );
    }

    /**
     * return url of the jsp modify workflow
     * 
     * @param request
     *            The HTTP request
     * @param nIdWorkflow
     *            the key of workflow to modify
     * @param strPane
     *            The pane to display
     * @return return url of the jsp modify workflows
     */
    private String getJspModifyWorkflow( HttpServletRequest request, int nIdWorkflow, String strPane )
    {
        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MODIFY_WORKFLOW );
        url.addParameter( PARAMETER_ID_WORKFLOW, nIdWorkflow );

        if ( strPane != null )
        {
            url.addParameter( PARAMETER_PANE, strPane );
        }

        return url.getUrl( );
    }

    /**
     * Return url of the jsp modify action
     * 
     * @param request
     *            The HTTP request
     * @param nIdTask
     *            the id task
     * @return return url of the jsp modify action
     */
    private String getJspModifyTask( HttpServletRequest request, int nIdTask )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MODIFY_TASK + "?" + PARAMETER_ID_TASK + "=" + nIdTask;
    }

    /**
     * Return url of the jsp modify action
     * 
     * @param request
     *            The HTTP request
     * @param nIdAction
     *            The key of action to modify
     * @return return url of the jsp modify action
     */
    private String getJspModifyAction( HttpServletRequest request, int nIdAction )
    {
        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MODIFY_ACTION );
        url.addParameter( PARAMETER_ID_ACTION, nIdAction );

        return url.getUrl( );
    }

    /**
     * Get the absolute URL of the JSP to modify the automatic reflexive action of a state
     * 
     * @param request
     *            The request
     * @param nIdState
     *            The id of the state
     * @return The absolute URL to the JSP
     */
    private String getJspModifyReflexiveAction( HttpServletRequest request, int nIdState )
    {
        UrlItem urlItem = new UrlItem( JSP_MODIFY_REFLEXIVE_ACTION );
        urlItem.addParameter( PARAMETER_ID_STATE, nIdState );

        return AppPathService.getBaseUrl( request ) + urlItem.getUrl( );
    }

    /**
     * Changes the order of a given state (the way it will be displayed in the list)
     * 
     * @param request
     *            The HTTP request
     * @return return url of the jsp change order state
     */
    public String doChangeOrderState( HttpServletRequest request )
    {
        // gets the state which needs to be changed (order)
        String strStateId = request.getParameter( PARAMETER_ID_STATE );
        String strWorkflowId = request.getParameter( PARAMETER_ID_WORKFLOW );
        String strOrderToSet = request.getParameter( PARAMETER_ORDER_ID );
        int nStateId = 0;
        int nWorkflowId = 0;
        int nOrderToSet = 0;

        if ( StringUtils.isNotBlank( strStateId ) )
        {
            nStateId = WorkflowUtils.convertStringToInt( strStateId );
        }

        if ( StringUtils.isNotBlank( strWorkflowId ) )
        {
            nWorkflowId = WorkflowUtils.convertStringToInt( strWorkflowId );
        }

        if ( StringUtils.isNotBlank( strOrderToSet ) )
        {
            nOrderToSet = WorkflowUtils.convertStringToInt( strOrderToSet );
        }

        State stateToChangeOrder = _stateService.findByPrimaryKey( nStateId );

        // order goes up
        if ( nOrderToSet < stateToChangeOrder.getOrder( ) )
        {
            List<State> listWithOrderAfterChosen = _stateService.findStatesAfterOrder( nOrderToSet, nWorkflowId );

            for ( State state : listWithOrderAfterChosen )
            {
                if ( state.getOrder( ) != stateToChangeOrder.getOrder( ) )
                {
                    if ( state.getOrder( ) < stateToChangeOrder.getOrder( ) )
                    {
                        state.setOrder( state.getOrder( ) + 1 );
                    }

                    _stateService.update( state );
                }
            }
        }

        // order goes down
        else
        {
            // get all the states with the order lower that the chosen state
            List<State> listWithOrderBetweenChosen = _stateService.findStatesBetweenOrders( stateToChangeOrder.getOrder( ), nOrderToSet, nWorkflowId );

            // for all those states, we decrement the order
            for ( State state : listWithOrderBetweenChosen )
            {
                if ( state.getOrder( ) != stateToChangeOrder.getOrder( ) )
                {
                    state.setOrder( state.getOrder( ) - 1 );
                    _stateService.update( state );
                }
            }
        }

        // for the chosen one, we change its order too
        stateToChangeOrder.setOrder( nOrderToSet );
        _stateService.update( stateToChangeOrder );

        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MODIFY_WORKFLOW );
        url.addParameter( PARAMETER_ID_WORKFLOW, nWorkflowId );

        return url.getUrl( );
    }

    /**
     * Changes the order of a given action (the way it will be displayed in the list)
     * 
     * @param request
     *            The HTTP request
     * @return return url of the jsp change order action
     */
    public String doChangeOrderAction( HttpServletRequest request )
    {
        // gets the action which needs to be changed (order)
        String strActionId = request.getParameter( PARAMETER_ID_ACTION );
        String strWorkflowId = request.getParameter( PARAMETER_ID_WORKFLOW );
        String strOrderToSet = request.getParameter( PARAMETER_ORDER_ACTION_ID );
        int nActionId = 0;
        int nWorkflowId = 0;
        int nOrderToSet = 0;

        if ( StringUtils.isNotBlank( strActionId ) )
        {
            nActionId = WorkflowUtils.convertStringToInt( strActionId );
        }

        if ( StringUtils.isNotBlank( strWorkflowId ) )
        {
            nWorkflowId = WorkflowUtils.convertStringToInt( strWorkflowId );
        }

        if ( StringUtils.isNotBlank( strOrderToSet ) )
        {
            nOrderToSet = WorkflowUtils.convertStringToInt( strOrderToSet );
        }

        Action actionToChangeOrder = _actionService.findByPrimaryKey( nActionId );

        // order goes up
        if ( nOrderToSet < actionToChangeOrder.getOrder( ) )
        {
            List<Action> listWithOrderAfterChosen = _actionService.findStatesAfterOrder( nOrderToSet, nWorkflowId );

            for ( Action action : listWithOrderAfterChosen )
            {
                if ( action.getOrder( ) != actionToChangeOrder.getOrder( ) )
                {
                    if ( action.getOrder( ) < actionToChangeOrder.getOrder( ) )
                    {
                        action.setOrder( action.getOrder( ) + 1 );
                    }

                    _actionService.update( action );
                }
            }
        }

        // order goes down
        else
        {
            // get all the actions with the order lower that the chosen action
            List<Action> listWithOrderBetweenChosen = _actionService.findStatesBetweenOrders( actionToChangeOrder.getOrder( ), nOrderToSet, nWorkflowId );

            // for all those action, we decrement the order
            for ( Action action : listWithOrderBetweenChosen )
            {
                if ( action.getOrder( ) != actionToChangeOrder.getOrder( ) )
                {
                    action.setOrder( action.getOrder( ) - 1 );
                    _actionService.update( action );
                }
            }
        }

        // for the chosen one, we change its order too
        actionToChangeOrder.setOrder( nOrderToSet );
        _actionService.update( actionToChangeOrder );

        return getJspModifyWorkflow( request, nWorkflowId, PANE_ACTIONS );
    }

    /**
     * Changes the order of a given task (the way it will be displayed in the list)
     * 
     * @param request
     *            The HTTP request
     * @return return url of the jsp change order task
     */
    public String doChangeOrderTask( HttpServletRequest request )
    {
        // gets the task which needs to be changed (order)
        String strTaskId = request.getParameter( PARAMETER_ID_TASK );
        String strOrderToSet = request.getParameter( PARAMETER_ORDER_TASK_ID );
        int nTaskId = 0;
        int nOrderToSet = 0;

        if ( StringUtils.isNotBlank( strTaskId ) )
        {
            nTaskId = WorkflowUtils.convertStringToInt( strTaskId );
        }

        if ( StringUtils.isNotBlank( strOrderToSet ) )
        {
            nOrderToSet = WorkflowUtils.convertStringToInt( strOrderToSet );
        }

        ITask taskToChangeOrder = _taskService.findByPrimaryKey( nTaskId, this.getLocale( ) );

        // order goes up
        if ( nOrderToSet < taskToChangeOrder.getOrder( ) )
        {
            List<ITask> listWithOrderAfterChosen = _taskService.findTasksAfterOrder( nOrderToSet, taskToChangeOrder.getAction( ).getId( ), this.getLocale( ) );

            for ( ITask task : listWithOrderAfterChosen )
            {
                if ( task.getOrder( ) != taskToChangeOrder.getOrder( ) )
                {
                    if ( task.getOrder( ) < taskToChangeOrder.getOrder( ) )
                    {
                        task.setOrder( task.getOrder( ) + 1 );
                    }

                    _taskService.update( task );
                }
            }
        }

        // order goes down
        else
        {
            // get all the actions with the order lower that the chosen action
            List<ITask> listWithOrderBetweenChosen = _taskService.findTasksBetweenOrders( taskToChangeOrder.getOrder( ), nOrderToSet,
                    taskToChangeOrder.getAction( ).getId( ), this.getLocale( ) );

            // for all those action, we decrement the order
            for ( ITask task : listWithOrderBetweenChosen )
            {
                if ( task.getOrder( ) != taskToChangeOrder.getOrder( ) )
                {
                    task.setOrder( task.getOrder( ) - 1 );
                    _taskService.update( task );
                }
            }
        }

        // for the chosen one, we change its order too
        taskToChangeOrder.setOrder( nOrderToSet );
        _taskService.update( taskToChangeOrder );

        Action action = _actionService.findByPrimaryKey( taskToChangeOrder.getAction( ).getId( ) );

        if ( action.isAutomaticReflexiveAction( ) )
        {
            return getJspModifyReflexiveAction( request, action.getStateBefore( ).getId( ) );
        }

        return getJspModifyAction( request, action.getId( ) );
    }

    /**
     * return url of the jsp manage workflow
     * 
     * @param request
     *            The HTTP request
     * @return url of the jsp manage workflow
     */
    private String getJspManageWorkflow( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_WORKFLOW;
    }

    /**
     * Get the available linked actions for a given action
     * 
     * @param nIdAction
     *            the ID action
     * @param nIdWorkflow
     *            the id workflow
     * @return a {@link ReferenceList}
     */
    private ReferenceList getAvailableActionsToLink( int nIdAction, int nIdWorkflow )
    {
        ReferenceList listLinkedActions = new ReferenceList( );
        Collection<Integer> listIdsLinkedAction = _actionService.getListIdsLinkedAction( nIdAction );
        ActionFilter filter = new ActionFilter( );
        filter.setAutomaticReflexiveAction( false );
        filter.setIdWorkflow( nIdWorkflow );

        for ( Action actionToLink : _actionService.getListActionByFilter( filter ) )
        {
            /**
             * The action can be linked only if the following conditions are met : - it should not already be in the list of linked action - it should not be
             * the action to link itself - it should not have any task that requires a form
             */
            if ( !listIdsLinkedAction.contains( actionToLink.getId( ) ) && ( actionToLink.getId( ) != nIdAction )
                    && !_workflowService.isDisplayTasksForm( actionToLink.getId( ), null ) )
            {
                listLinkedActions.addItem( actionToLink.getId( ), actionToLink.getName( ) );
            }
        }

        return listLinkedActions;
    }

    /**
     * Get the linked actions of a given id action
     * 
     * @param nIdAction
     *            the id action
     * @return a {@link ReferenceList}
     */
    private ReferenceList getLinkedActions( int nIdAction )
    {
        ReferenceList listLinkedActions = new ReferenceList( );

        for ( int nIdLinkedAction : _actionService.getListIdsLinkedAction( nIdAction ) )
        {
            Action linkedAction = _actionService.findByPrimaryKey( nIdLinkedAction );

            if ( ( linkedAction != null ) && ( linkedAction.getId( ) != nIdAction ) )
            {
                listLinkedActions.addItem( linkedAction.getId( ), linkedAction.getName( ) );
            }
        }

        return listLinkedActions;
    }

    /**
     * Get the selected linked actions
     * 
     * @param action
     *            the action
     * @param request
     *            the HTTP request
     * @return a collection of IDs action
     */
    private Collection<Integer> getSelectedLinkedActions( Action action, HttpServletRequest request )
    {
        Collection<Integer> listIdsLinkedAction = action.getListIdsLinkedAction( );

        if ( listIdsLinkedAction == null )
        {
            listIdsLinkedAction = new LinkedHashSet<>( );
        }

        // Remove unselected id linked action from the list
        String [ ] strUnselectedLinkedActions = request.getParameterValues( PARAMETER_UNSELECT_LINKED_ACTIONS );

        if ( ArrayUtils.isNotEmpty( strUnselectedLinkedActions ) && CollectionUtils.isNotEmpty( listIdsLinkedAction ) )
        {
            Integer [ ] listUnselectedLinkedActions = WorkflowUtils.convertStringToInt( strUnselectedLinkedActions );
            listIdsLinkedAction.removeAll( Arrays.asList( listUnselectedLinkedActions ) );
        }

        // Add selected linked actions
        String [ ] strSelectedLinkedActions = request.getParameterValues( PARAMETER_SELECT_LINKED_ACTIONS );

        if ( ( strSelectedLinkedActions != null ) && ( strSelectedLinkedActions.length > 0 ) )
        {
            Integer [ ] listSelectedLinkedActions = WorkflowUtils.convertStringToInt( strSelectedLinkedActions );
            listIdsLinkedAction.addAll( Arrays.asList( listSelectedLinkedActions ) );
        }

        return listIdsLinkedAction;
    }

    /**
     * duplicate a state
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    public String doCopyState( HttpServletRequest request ) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        String strIdState = request.getParameter( PARAMETER_ID_STATE );
        int nIdState = WorkflowUtils.convertStringToInt( strIdState );
        State stateInit = _stateService.findByPrimaryKey( nIdState );

        State stateToCopy = copyStateMethod( request, stateInit );

        // get all the actions of the workflow to copy
        ActionFilter automaticReflexiveActionFilter = new ActionFilter( );
        automaticReflexiveActionFilter.setIdWorkflow( stateToCopy.getWorkflow( ).getId( ) );
        automaticReflexiveActionFilter.setAutomaticReflexiveAction( true );
        automaticReflexiveActionFilter.setIdStateAfter( nIdState );
        automaticReflexiveActionFilter.setIdStateBefore( nIdState );

        List<Action> listAutomaticReflexiveActionsOfWorkflow = _actionService.getListActionByFilter( automaticReflexiveActionFilter );

        for ( Action action : listAutomaticReflexiveActionsOfWorkflow )
        {
            // get the maximum order number in this workflow and set max+1
            int nMaximumOrder = _actionService.findMaximumOrderByWorkflowId( action.getWorkflow( ).getId( ) );
            action.setOrder( nMaximumOrder + 1 );

            // get the linked tasks and duplicate them
            List<ITask> listLinkedTasks = _taskService.getListTaskByIdAction( action.getId( ), this.getLocale( ) );

            action.setStateAfter( stateToCopy );
            action.setStateBefore( stateToCopy );

            _actionService.create( action );

            for ( ITask task : listLinkedTasks )
            {
                // for each we change the linked action
                task.setAction( action );

                // and then we create the new task duplicated
                this.doCopyTaskWithModifiedParam( task, null );
            }
        }

        return getJspModifyWorkflow( request, stateToCopy.getWorkflow( ).getId( ) );
    }

    /**
     * Duplicate an action
     * 
     * @param request
     *            the request
     * @return the URL of the "modify workflow" page
     * @throws NoSuchMethodException
     *             If a method was not found
     * @throws IllegalAccessException
     *             If an illegal access is performed
     * @throws InvocationTargetException
     *             If an error occurs
     */
    public String doCopyAction( HttpServletRequest request ) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        String strIdAction = request.getParameter( PARAMETER_ID_ACTION );
        int nIdAction = WorkflowUtils.convertStringToInt( strIdAction );

        Action actionInit = _actionService.findByPrimaryKey( nIdAction );

        Action actionToCopy = copyActionMethod( request, actionInit );

        return getJspModifyWorkflow( request, actionToCopy.getWorkflow( ).getId( ), PANE_ACTIONS );
    }

    /**
     * Duplicate a workflow
     * 
     * @param request
     *            the request
     * @return the URL of the workflow management page
     * @throws InvocationTargetException
     *             If a method was not found
     * @throws IllegalAccessException
     *             If an illegal access is performed
     * @throws NoSuchMethodException
     *             If an error occurs
     */
    public String doCopyWorkflow( HttpServletRequest request ) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        int nId = WorkflowUtils.convertStringToInt( request.getParameter( PARAMETER_ID_WORKFLOW ) );

        if ( nId == -1 )
        {
            return getJspManageWorkflow( request );
        }

        try
        {
            String json = WorkflowJsonService.getInstance( ).jsonExportWorkflow( nId );
            WorkflowJsonService.getInstance( ).jsonImportWorkflow( json, getLocale( ) );
        }
        catch( JsonProcessingException e )
        {
            AppLogService.debug( e.getMessage( ) );
        }

        return getJspManageWorkflow( request );
    }

    public String getConfirmCopyWorkflow( HttpServletRequest request )
    {
        UrlItem url = new UrlItem( JSP_DO_COPY_WORKFLOW );
        url.addParameter( PARAMETER_ID_WORKFLOW, request.getParameter( PARAMETER_ID_WORKFLOW ) );
        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_COPY_WORKFLOW, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Copy an action
     * 
     * @param request
     *            the request
     * @param actionToCopy
     *            the action to copy
     * @return action the action
     * @throws NoSuchMethodException
     *             If the method was not found
     * @throws IllegalAccessException
     *             The an illegal access occurs
     * @throws InvocationTargetException
     *             The an error occurs
     */
    private Action copyActionMethod( HttpServletRequest request, Action actionToCopy )
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        // get the action
        String newNameForCopy = I18nService.getLocalizedString( PROPERTY_COPY_OF_ACTION, request.getLocale( ) ) + actionToCopy.getName( );
        actionToCopy.setName( newNameForCopy );

        // get the maximum order number in this workflow and set max+1
        int nMaximumOrder = _actionService.findMaximumOrderByWorkflowId( actionToCopy.getWorkflow( ).getId( ) );
        actionToCopy.setOrder( nMaximumOrder + 1 );

        // get the linked tasks and duplicate them
        List<ITask> listLinkedTasks = _taskService.getListTaskByIdAction( actionToCopy.getId( ), this.getLocale( ) );
        int idActionOld = actionToCopy.getId( );

        _actionService.create( actionToCopy );
        int idActionNew = actionToCopy.getId( );

        for ( ITask task : listLinkedTasks )
        {
            // for each we change the linked action
            task.setAction( actionToCopy );

            // and then we create the new task duplicated
            this.doCopyTaskWithModifiedParam( task, null );
        }

        _prerequisiteManagementService.copyPrerequisite( idActionOld, idActionNew );
        return actionToCopy;
    }

    /**
     * Method for copying a state
     * 
     * @param request
     *            the request
     * @param stateToCopy
     *            the state to copy
     * @return state the copy of stateToCopy
     */
    private State copyStateMethod( HttpServletRequest request, State stateToCopy )
    {
        String newNameForCopy = I18nService.getLocalizedString( PROPERTY_COPY_OF_STATE, request.getLocale( ) ) + stateToCopy.getName( );
        stateToCopy.setName( newNameForCopy );
        // get the maximum order number in this workflow and set max+1
        int nMaximumOrder = _stateService.findMaximumOrderByWorkflowId( stateToCopy.getWorkflow( ).getId( ) );
        stateToCopy.setOrder( nMaximumOrder + 1 );
        _stateService.create( stateToCopy );
        return stateToCopy;
    }

    /**
     * Updates the order of states
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The workflow creation page
     */
    public String doUpdateStateOrder( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdWorkflow = request.getParameter( PARAMETER_ID_WORKFLOW );
        int nIdWorkflow = WorkflowUtils.convertStringToInt( strIdWorkflow );
        Workflow workflow = null;

        if ( nIdWorkflow != WorkflowUtils.CONSTANT_ID_NULL )
        {
            workflow = _workflowService.findByPrimaryKey( nIdWorkflow );
        }

        if ( workflow == null )
        {
            throw new AccessDeniedException( LOG_WORKFLOW_NOT_FOUND + nIdWorkflow );
        }

        _stateService.initializeStateOrder( nIdWorkflow );

        return getJspModifyWorkflow( request, nIdWorkflow, PANE_STATES );
    }

    /**
     * Updates the order of actions
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The workflow creation page
     */
    public String doUpdateActionOrder( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdWorkflow = request.getParameter( PARAMETER_ID_WORKFLOW );
        int nIdWorkflow = WorkflowUtils.convertStringToInt( strIdWorkflow );
        Workflow workflow = null;

        if ( nIdWorkflow != WorkflowUtils.CONSTANT_ID_NULL )
        {
            workflow = _workflowService.findByPrimaryKey( nIdWorkflow );
        }

        if ( workflow == null )
        {
            throw new AccessDeniedException( LOG_WORKFLOW_NOT_FOUND + nIdWorkflow );
        }

        _actionService.initializeActionOrder( nIdWorkflow );

        return getJspModifyWorkflow( request, nIdWorkflow, PANE_ACTIONS );
    }

    /**
     * Updates the order of tasks
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The workflow creation page
     */
    public String doUpdateTaskOrder( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdAction = request.getParameter( PARAMETER_ID_ACTION );
        int nIdAction = WorkflowUtils.convertStringToInt( strIdAction );
        Action action = null;

        if ( nIdAction != WorkflowUtils.CONSTANT_ID_NULL )
        {
            action = _actionService.findByPrimaryKey( nIdAction );
        }

        if ( action == null )
        {
            throw new AccessDeniedException( LOG_ACTION_NOT_FOUND + nIdAction );
        }

        _taskService.initializeTaskOrder( nIdAction, this.getLocale( ) );

        if ( action.isAutomaticReflexiveAction( ) )
        {
            return getJspModifyReflexiveAction( request, action.getStateBefore( ).getId( ) );
        }

        return getJspModifyAction( request, nIdAction );
    }

    /**
     * Get the modify reflexive action page
     * 
     * @param request
     *            The request
     * @return The modify reflexive action page, or the manage workflow page if no valid state id is specified
     */
    public String getModifyReflexiveAction( HttpServletRequest request )
    {
        String strIdState = request.getParameter( PARAMETER_ID_STATE );

        if ( StringUtils.isEmpty( strIdState ) || !StringUtils.isNumeric( strIdState ) )
        {
            return getManageWorkflow( request );
        }

        Locale locale = AdminUserService.getLocale( request );
        int nIdState = Integer.parseInt( strIdState );
        State state = _stateService.findByPrimaryKey( nIdState );
        Map<String, Object> model = new HashMap<>( );

        List<Action> listActions = getAutomaticReflexiveActionsFromState( nIdState );
        List<ITask> listTasks = new ArrayList<>( );

        if ( CollectionUtils.isNotEmpty( listActions ) )
        {
            for ( Action action : listActions )
            {
                List<ITask> listTasksFound = _taskService.getListTaskByIdAction( action.getId( ), locale );

                if ( CollectionUtils.isNotEmpty( listTasksFound ) )
                {
                    listTasks.addAll( listTasksFound );
                }
            }

            model.put( MARK_ACTION, listActions.get( 0 ) );
        }

        ReferenceList refListTaskType = new ReferenceList( );
        Collection<ITaskType> taskTypeList = _taskFactory.getAllTaskTypes( );

        for ( ITaskType taskType : taskTypeList )
        {
            if ( _taskFactory.newTask( taskType.getKey( ), getLocale( ) ).getTaskType( ).isTaskForAutomaticAction( ) )
            {
                refListTaskType.addItem( taskType.getKey( ), I18nService.getLocalizedString( taskType.getTitleI18nKey( ), getLocale( ) ) );
            }
        }

        model.put( MARK_TASK_LIST, listTasks );
        model.put( MARK_TASK_TYPE_LIST, refListTaskType );
        model.put( MARK_NUMBER_TASK, listTasks.size( ) );
        model.put( MARK_STATE, state );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_REFLEXIVE_ACTION, locale, model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Do create a task and add it to the reflexive action of a state
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     */
    public String doAddTaskToReflexiveAction( HttpServletRequest request )
    {
        String strIdState = request.getParameter( PARAMETER_ID_STATE );

        if ( StringUtils.isEmpty( strIdState ) || !StringUtils.isNumeric( strIdState ) )
        {
            return getJspManageWorkflow( request );
        }

        int nIdState = Integer.parseInt( strIdState );
        Locale locale = AdminUserService.getLocale( request );
        List<Action> listActions = getAutomaticReflexiveActionsFromState( nIdState );
        Action action;

        if ( CollectionUtils.isNotEmpty( listActions ) )
        {
            action = listActions.get( 0 );
        }
        else
        {
            List<Icon> listIcons = _iconService.getListIcons( );
            action = new Action( );

            State state = _stateService.findByPrimaryKey( nIdState );
            action.setStateBefore( state );
            action.setStateAfter( state );
            action.setAutomaticReflexiveAction( true );
            action.setAutomaticState( false );
            action.setDescription( StringUtils.EMPTY );
            action.setIcon( listIcons.get( 0 ) );
            action.setName( I18nService.getLocalizedString( MESSAGE_REFLEXIVE_ACTION_NAME, locale ) + " " + state.getName( ) );
            action.setMassAction( false );
            action.setOrder( 0 );
            action.setWorkflow( state.getWorkflow( ) );
            _actionService.create( action );
        }

        String strTaskTypeKey = request.getParameter( PARAMETER_TASK_TYPE_KEY );
        ITask task = _taskFactory.newTask( strTaskTypeKey, locale );

        if ( ( task != null ) && task.getTaskType( ).isTaskForAutomaticAction( ) )
        {
            task.setAction( action );

            // get the maximum order number in this workflow and set max+1
            int nMaximumOrder = _taskService.findMaximumOrderByActionId( action.getId( ) );
            task.setOrder( nMaximumOrder + 1 );

            _taskService.create( task );
        }

        return getJspModifyReflexiveAction( request, nIdState );
    }

    /**
     * Do delete a task and remove it from the reflexive action of a state. If the action has no more task, then it is removed
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     * @throws AccessDeniedException
     *             If the user is not allowed to access this feature
     */
    public String doRemoveTaskFromReflexiveAction( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdState = request.getParameter( PARAMETER_ID_STATE );
        String strIdTask = request.getParameter( PARAMETER_ID_TASK );

        if ( StringUtils.isEmpty( strIdState ) || !StringUtils.isNumeric( strIdState ) || StringUtils.isEmpty( strIdTask )
                || !StringUtils.isNumeric( strIdTask ) )
        {
            return getManageWorkflow( request );
        }

        Locale locale = AdminUserService.getLocale( request );
        int nIdState = Integer.parseInt( strIdState );

        doRemoveTask( request );

        List<Action> listActions = getAutomaticReflexiveActionsFromState( nIdState );

        if ( CollectionUtils.isNotEmpty( listActions ) )
        {
            for ( Action action : listActions )
            {
                List<ITask> listTasks = _taskService.getListTaskByIdAction( action.getId( ), locale );

                if ( ( listTasks == null ) || listTasks.isEmpty( ) )
                {
                    _actionService.remove( action.getId( ) );
                }
            }
        }

        return getJspModifyReflexiveAction( request, nIdState );
    }

    /**
     * Get the list of automatic reflexive actions associated with a given state
     * 
     * @param nIdState
     *            The id of the state
     * @return The list of actions associated with the given state.
     */
    private List<Action> getAutomaticReflexiveActionsFromState( int nIdState )
    {
        ActionFilter filter = new ActionFilter( );
        filter.setAutomaticReflexiveAction( true );
        filter.setIdStateBefore( nIdState );

        return _actionService.getListActionByFilter( filter );
    }

    /**
     * Export Workflow as Json
     * 
     * @param request
     */
    public void doExportWorkflow( HttpServletRequest request, HttpServletResponse response )
    {
        int nId = WorkflowUtils.convertStringToInt( request.getParameter( PARAMETER_ID_WORKFLOW ) );

        if ( nId == -1 )
        {
            return;
        }

        String content;
        try ( OutputStream os = response.getOutputStream( ) )
        {
            content = WorkflowJsonService.getInstance( ).jsonExportWorkflow( nId );
            Workflow workflow = _workflowService.findByPrimaryKey( nId );

            MVCUtils.addDownloadHeaderToResponse( response, FileUtil.normalizeFileName( workflow.getName( ) ) + ".json", "application/json" );
            os.write( content.getBytes( ) );
        }
        catch( IOException e )
        {
            AppLogService.error( e.getMessage( ), e );
        }
    }

    public String getConfirmImportWorkflow( HttpServletRequest request )
    {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        _importWorkflowFile = multipartRequest.getFile( PARAMETER_JSON_FILE );
        UrlItem url = new UrlItem( JSP_DO_IMPORT_WORKFLOW );
        return AdminMessageService.getMessageUrl( (MultipartHttpServletRequest) request, MESSAGE_CONFIRM_COPY_WORKFLOW, url.getUrl( ),
                AdminMessage.TYPE_CONFIRMATION );
    }

    public String doImportWorkflow( HttpServletRequest request )
    {
        try
        {
            if ( _importWorkflowFile != null )
            {
                WorkflowJsonService.getInstance( ).jsonImportWorkflow( new String( _importWorkflowFile.get( ) ), getLocale( ) );
            }
        }
        catch( JsonProcessingException e )
        {
            AppLogService.error( e.getMessage( ) );
        }
        finally
        {
            _importWorkflowFile = null;
        }
        return getJspManageWorkflow( request );
    }
}
