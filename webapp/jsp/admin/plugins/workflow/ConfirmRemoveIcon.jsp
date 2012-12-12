<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:useBean id="workflowIcon" scope="session" class="fr.paris.lutece.plugins.workflow.web.IconJspBean" />
<% 
	workflowIcon.init( request, fr.paris.lutece.plugins.workflow.web.WorkflowJspBean.RIGHT_MANAGE_WORKFLOW); 
	response.sendRedirect(workflowIcon.getConfirmRemoveIcon(request) );
%>
