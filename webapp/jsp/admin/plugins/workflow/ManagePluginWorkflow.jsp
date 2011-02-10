<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />
<jsp:useBean id="workflowWorkflowPlugin" scope="session" class="fr.paris.lutece.plugins.workflow.web.ManagePluginWorkflowJspBean" />
<%
workflowWorkflowPlugin.init( request, fr.paris.lutece.plugins.workflow.web.ManagePluginWorkflowJspBean.RIGHT_MANAGE_WORKFLOW);
%>
<%=workflowWorkflowPlugin.getManagePluginWorkflow(request)%>
<%@ include file="../../AdminFooter.jsp" %>