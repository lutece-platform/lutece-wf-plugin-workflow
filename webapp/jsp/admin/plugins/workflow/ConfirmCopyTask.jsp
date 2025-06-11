<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:useBean id="workflowWorkflow" scope="session" class="fr.paris.lutece.plugins.workflow.web.WorkflowJspBean" />
<%
	workflowWorkflow.init( request, fr.paris.lutece.plugins.workflow.web.WorkflowJspBean.RIGHT_MANAGE_WORKFLOW); 
	response.sendRedirect( workflowWorkflow.getConfirmCopyTask(request) );
%>
