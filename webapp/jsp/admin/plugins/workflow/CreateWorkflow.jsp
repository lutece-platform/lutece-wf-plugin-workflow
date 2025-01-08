<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.workflow.web.WorkflowJspBean"%>

${ workflowJspBean.init( pageContext.request, WorkflowJspBean.RIGHT_MANAGE_WORKFLOW ) }
${ workflowJspBean.getCreateWorkflow( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>