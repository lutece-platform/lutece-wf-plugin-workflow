<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />
<jsp:useBean id="workflowWorkflow" scope="session" class="fr.paris.lutece.plugins.workflow.web.WorkflowJspBean" />
<% workflowWorkflow.init( request, fr.paris.lutece.plugins.workflow.web.ManagePluginWorkflowJspBean.RIGHT_MANAGE_WORKFLOW); %>
<%= workflowWorkflow.getCreateWorkflow( request ) %>
<%@ include file="../../AdminFooter.jsp" %>