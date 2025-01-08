<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.workflow.web.IconJspBean"%>
<%@page import="fr.paris.lutece.plugins.workflow.web.WorkflowJspBean"%>

${ iconJspBean.init( pageContext.request, WorkflowJspBean.RIGHT_MANAGE_WORKFLOW ) }
${ pageContext.response.sendRedirect( iconJspBean.getConfirmRemoveIcon( pageContext.request )) }
