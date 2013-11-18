<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:useBean id="workflowPrerequisite" scope="session" class="fr.paris.lutece.plugins.workflow.web.prerequisite.PrerequisiteJspBean" />
<%
	workflowPrerequisite.init( request, fr.paris.lutece.plugins.workflow.web.WorkflowJspBean.RIGHT_MANAGE_WORKFLOW); 
	response.sendRedirect( workflowPrerequisite.doCreatePrerequisite( request ) );
%>
