<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:useBean id="workflowTestWorkflow" scope="session" class="fr.paris.lutece.plugins.workflow.web.TestWorkflowJspBean" />
<% 
	workflowTestWorkflow.init( request, fr.paris.lutece.plugins.workflow.web.WorkflowJspBean.RIGHT_MANAGE_WORKFLOW); 
	response.sendRedirect( workflowTestWorkflow.doSaveTasksForm(request) );
%>
