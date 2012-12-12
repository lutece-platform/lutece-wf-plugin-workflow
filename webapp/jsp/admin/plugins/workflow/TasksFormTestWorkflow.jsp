<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />
<jsp:useBean id="workflowTestWorkflow" scope="session" class="fr.paris.lutece.plugins.workflow.web.TestWorkflowJspBean" />
<%
workflowTestWorkflow.init( request, fr.paris.lutece.plugins.workflow.web.WorkflowJspBean.RIGHT_MANAGE_WORKFLOW);
%>
<%= workflowTestWorkflow.getTasksFormTest(request) %>
<%@ include file="../../AdminFooter.jsp" %>