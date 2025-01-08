<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.workflow.web.WorkflowJspBean"%>

${ workflowJspBean.init( pageContext.request, WorkflowJspBean.RIGHT_MANAGE_WORKFLOW ) }
${ pageContext.response.sendRedirect( workflowJspBean.doModifyState( pageContext.request )) }
