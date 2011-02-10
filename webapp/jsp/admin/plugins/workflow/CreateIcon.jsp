<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />
<jsp:useBean id="workflowIcon" scope="session" class="fr.paris.lutece.plugins.workflow.web.IconJspBean" />
<% workflowIcon.init( request, fr.paris.lutece.plugins.workflow.web.ManagePluginWorkflowJspBean.RIGHT_MANAGE_WORKFLOW); %>
<%= workflowIcon.getCreateIcon( request ) %>
<%@ include file="../../AdminFooter.jsp" %>