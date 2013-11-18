<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:useBean id="workflowPrerequisite" scope="session" class="fr.paris.lutece.plugins.workflow.web.prerequisite.PrerequisiteJspBean" />
<%
	workflowPrerequisite.init( request, fr.paris.lutece.plugins.workflow.web.WorkflowJspBean.RIGHT_MANAGE_WORKFLOW);
	String strContent = workflowPrerequisite.getCreatePrerequisite( request, response );
	if ( strContent != null )
	{
%>
<jsp:include page="../../../AdminHeader.jsp" />

<%= strContent %>
<%@ include file="../../../AdminFooter.jsp" %>
<%
	}
%>
