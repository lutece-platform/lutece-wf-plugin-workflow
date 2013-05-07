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
package fr.paris.lutece.plugins.workflow.web;

import fr.paris.lutece.plugins.workflow.business.testresource.TestResource;
import fr.paris.lutece.plugins.workflow.service.testresource.ITestResourceService;
import fr.paris.lutece.plugins.workflow.service.testresource.TestResourceService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * class TestWorkflowJspBean
 */
public class TestWorkflowJspBean extends PluginAdminPageJspBean
{
    // jsp
    private static final String JSP_MANAGE_TEST_WORKFLOW = "jsp/admin/plugins/workflow/ManageTestWorkflow.jsp";
    private static final String JSP_TASKS_FORM_TEST_WORKFLOW = "jsp/admin/plugins/workflow/TasksFormTestWorkflow.jsp";
    private static final String JSP_DO_REMOVE_RESOURCE_TEST_WORKFLOW = "jsp/admin/plugins/workflow/DoRemoveResourceTestWorkflow.jsp";

    // templates
    private static final String TEMPLATE_MANAGE_TEST_WORKFLOW = "admin/plugins/workflow/manage_test_workflow.html";
    private static final String TEMPLATE_RESOURCE_HISTORY_TEST_WORKFLOW = "admin/plugins/workflow/resource_history_test_workflow.html";
    private static final String TEMPLATE_TASKS_FORM_TEST_WORKFLOW = "admin/plugins/workflow/tasks_form_test_workflow.html";

    // parameters
    private static final String PARAMETER_ID_WORKFLOW = "id_workflow";
    private static final String PARAMETER_ID_ACTION = "id_action";
    private static final String PARAMETER_CANCEL = "cancel";
    private static final String PARAMETER_ID_TEST_RESOURCE = "id_test_resource";
    private static final String PARAMETER_TITLE_TEST_RESOURCE = "title_test_resource";

    // properties
    private static final String PROPERTY_MANAGE_WORKFLOW_TEST_PAGE_TITLE = "workflow.manage_test_workflow.page_title";
    private static final String PROPERTY_TASKS_FORM_TEST_WORKFLOW_PAGE_TITLE = "workflow.tasks_form_test_workflow.page_title";
    private static final String PROPERTY_RESOURCE_HISTORY_TEST_WORKFLOW_PAGE_TITLE = "workflow.resource_history_test_workflow.page_title";

    // mark
    private static final String MARK_WORKFLOW_ACTION_LIST = "workflow_action_list";
    private static final String MARK_WORKFLOW_STATE = "workflow_state";
    private static final String MARK_RESOURCE = "resource";
    private static final String MARK_ID_ACTION = "id_action";
    private static final String MARK_ID_TEST_RESOURCE = "id_test_resource";
    private static final String MARK_TEST_RESOURCE_ACTIONS_LIST = "test_resource_actions_list";
    private static final String MARK_WORKFLOW_LIST = "workflow_list";
    private static final String MARK_WORKFLOW_SELECTED = "workflow_selected";
    private static final String MARK_TASKS_FORM = "tasks_form";
    private static final String MARK_RESOURCE_HISTORY = "resource_history";

    // mark
    private static final String MESSAGE_TEST_RESOURCE_TITLE_IS_MANDATORY = "workflow.message.test_resource_title_is_mandatory";
    private static final String MESSAGE_CONFIRM_REMOVE_TEST_RESOURCE = "workflow.message.confirm_remove_test_resource";
    private int _nIdWorkflow = WorkflowUtils.CONSTANT_ID_NULL;
    private ITestResourceService _testResourceService = SpringContextService.getBean( TestResourceService.BEAN_SERVICE );

    /*-------------------------------MANAGEMENT  WORKFLOW-----------------------------*/

    /**
     * Return management page of test workflow
     *
     * @param request
     *            The Http request
     * @return Html management page of test workflow
     */
    public String getManageTestWorkflow( HttpServletRequest request )
    {
        String strIdWorkflow = request.getParameter( PARAMETER_ID_WORKFLOW );

        if ( ( strIdWorkflow != null ) && !strIdWorkflow.equals( WorkflowUtils.EMPTY_STRING ) )
        {
            _nIdWorkflow = WorkflowUtils.convertStringToInt( strIdWorkflow );
        }

        Map<String, Object> model = new HashMap<String, Object>(  );
        List<Map<String, Object>> listResourceActions = new ArrayList<Map<String, Object>>(  );

        if ( _nIdWorkflow != WorkflowUtils.CONSTANT_ID_NULL )
        {
            List<TestResource> listTestResource = _testResourceService.getList( getPlugin(  ) );

            for ( TestResource testResource : listTestResource )
            {
                if ( WorkflowService.getInstance(  )
                                        .isAuthorized( testResource.getIdResource(  ), TestResource.RESOURCE_TYPE,
                            _nIdWorkflow, AdminUserService.getAdminUser( request ) ) )
                {
                    //get workflow actions associated to the resource
                    Collection<Action> listAction = WorkflowService.getInstance(  )
                                                                   .getActions( testResource.getIdResource(  ),
                            TestResource.RESOURCE_TYPE, _nIdWorkflow, AdminUserService.getAdminUser( request ) );
                    State state = WorkflowService.getInstance(  )
                                                 .getState( testResource.getIdResource(  ), TestResource.RESOURCE_TYPE,
                            _nIdWorkflow, null );
                    Map<String, Object> resourceActions = new HashMap<String, Object>(  );
                    resourceActions.put( MARK_RESOURCE, testResource );
                    resourceActions.put( MARK_WORKFLOW_ACTION_LIST, listAction );
                    resourceActions.put( MARK_WORKFLOW_STATE, state );
                    listResourceActions.add( resourceActions );
                }
            }
        }

        model.put( MARK_TEST_RESOURCE_ACTIONS_LIST, listResourceActions );
        model.put( MARK_WORKFLOW_LIST,
            WorkflowService.getInstance(  ).getWorkflowsEnabled( AdminUserService.getAdminUser( request ), getLocale(  ) ) );
        model.put( MARK_WORKFLOW_SELECTED, _nIdWorkflow );

        setPageTitleProperty( PROPERTY_MANAGE_WORKFLOW_TEST_PAGE_TITLE );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_TEST_WORKFLOW, getLocale(  ), model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Perform the resource creation
     *
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doCreateTestResource( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strTestResourceTitle = request.getParameter( PARAMETER_TITLE_TEST_RESOURCE );

        if ( StringUtils.isBlank( strTestResourceTitle ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_TEST_RESOURCE_TITLE_IS_MANDATORY,
                AdminMessage.TYPE_STOP );
        }

        TestResource testResource = new TestResource(  );
        testResource.setTitle( strTestResourceTitle );
        _testResourceService.create( testResource, getPlugin(  ) );

        return getJspManageTestWorkflow( request );
    }

    /**
     * Gets the confirmation page of remove resource
     *
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return the confirmation page of remove resource
     */
    public String getConfirmRemoveTestResource( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdTestResource = request.getParameter( PARAMETER_ID_TEST_RESOURCE );

        UrlItem url = new UrlItem( JSP_DO_REMOVE_RESOURCE_TEST_WORKFLOW );
        url.addParameter( PARAMETER_ID_TEST_RESOURCE, strIdTestResource );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_TEST_RESOURCE, url.getUrl(  ),
            AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Remove  resource
     *
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doRemoveTestResource( HttpServletRequest request )
        throws AccessDeniedException
    {
        String strIdTestResource = request.getParameter( PARAMETER_ID_TEST_RESOURCE );
        int nIdTestResource = WorkflowUtils.convertStringToInt( strIdTestResource );
        _testResourceService.remove( nIdTestResource, getPlugin(  ) );
        //Remove workflow resource associated
        WorkflowService.getInstance(  ).doRemoveWorkFlowResource( nIdTestResource, TestResource.RESOURCE_TYPE );

        return getJspManageTestWorkflow( request );
    }

    /**
     * returns the resource history
     * @param request request  The HTTP request
     * @return the resource history
     */
    public String getResourceHistoryTest( HttpServletRequest request )
    {
        String strIdTestResource = request.getParameter( PARAMETER_ID_TEST_RESOURCE );
        int nIdTestResource = WorkflowUtils.convertStringToInt( strIdTestResource );

        Map<String, Object> model = new HashMap<String, Object>(  );

        model.put( MARK_RESOURCE_HISTORY,
            WorkflowService.getInstance(  )
                           .getDisplayDocumentHistory( nIdTestResource, TestResource.RESOURCE_TYPE, _nIdWorkflow,
                request, getLocale(  ) ) );

        setPageTitleProperty( PROPERTY_RESOURCE_HISTORY_TEST_WORKFLOW_PAGE_TITLE );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_RESOURCE_HISTORY_TEST_WORKFLOW,
                getLocale(  ), model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * returns the tasks form associated with the given action
     * @param request request  The HTTP request
     * @return the tasks form associated with the given action
     */
    public String getTasksFormTest( HttpServletRequest request )
    {
        String strIdTestResource = request.getParameter( PARAMETER_ID_TEST_RESOURCE );
        String strIdAction = request.getParameter( PARAMETER_ID_ACTION );
        int nIdTestResource = WorkflowUtils.convertStringToInt( strIdTestResource );
        int nIdAction = WorkflowUtils.convertStringToInt( strIdAction );

        Map<String, Object> model = new HashMap<String, Object>(  );

        model.put( MARK_TASKS_FORM,
            WorkflowService.getInstance(  )
                           .getDisplayTasksForm( nIdTestResource, TestResource.RESOURCE_TYPE, nIdAction, request,
                getLocale(  ) ) );
        model.put( MARK_ID_ACTION, nIdAction );
        model.put( MARK_ID_TEST_RESOURCE, nIdTestResource );

        setPageTitleProperty( PROPERTY_TASKS_FORM_TEST_WORKFLOW_PAGE_TITLE );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_TASKS_FORM_TEST_WORKFLOW, getLocale(  ),
                model );

        return getAdminPage( templateList.getHtml(  ) );
    }

    /**
     * Perform the information on the various tasks associated with the given action
     * @param request  The HTTP request
     * @return The URL to go after performing the action
     */
    public String doSaveTasksForm( HttpServletRequest request )
    {
        String strIdTestResource = request.getParameter( PARAMETER_ID_TEST_RESOURCE );
        String strIdAction = request.getParameter( PARAMETER_ID_ACTION );

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            int nIdTestResource = WorkflowUtils.convertStringToInt( strIdTestResource );
            int nIdAction = WorkflowUtils.convertStringToInt( strIdAction );
            String strError = WorkflowService.getInstance(  )
                                             .doSaveTasksForm( nIdTestResource, TestResource.RESOURCE_TYPE, nIdAction,
                    null, request, getLocale(  ) );

            if ( strError != null )
            {
                return strError;
            }
        }

        return getJspManageTestWorkflow( request );
    }

    /**
     * Proceed action
     * @param request The HTTP request
     * @return The URL to go after performing the action
     */
    public String doProcessAction( HttpServletRequest request )
    {
        String strIdTestResource = request.getParameter( PARAMETER_ID_TEST_RESOURCE );
        String strIdAction = request.getParameter( PARAMETER_ID_ACTION );
        int nIdTestResource = WorkflowUtils.convertStringToInt( strIdTestResource );
        int nIdAction = WorkflowUtils.convertStringToInt( strIdAction );

        if ( WorkflowService.getInstance(  ).isDisplayTasksForm( nIdAction, getLocale(  ) ) )
        {
            return getJspTasksFormTest( request, nIdTestResource, nIdAction );
        }

        WorkflowService.getInstance(  )
                       .doProcessAction( nIdTestResource, TestResource.RESOURCE_TYPE, nIdAction, null, request,
            getLocale(  ), false );

        return getJspManageTestWorkflow( request );
    }

    /**
     * return url of the jsp manage workflow
     * @param request The HTTP request
     * @return url of the jsp manage workflow
     */
    private String getJspManageTestWorkflow( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_TEST_WORKFLOW;
    }

    /**
     * return url of the jsp tasks form test
     * @param request The HTTP request
     * @param nIdTestResource the resource test id
     * @param nIdAction the action id
     * @return url of the jsp tasks form test
     */
    private String getJspTasksFormTest( HttpServletRequest request, int nIdTestResource, int nIdAction )
    {
        return AppPathService.getBaseUrl( request ) + JSP_TASKS_FORM_TEST_WORKFLOW + "?" + PARAMETER_ID_TEST_RESOURCE +
        "=" + nIdTestResource + "&" + PARAMETER_ID_ACTION + "=" + nIdAction;
    }
}
