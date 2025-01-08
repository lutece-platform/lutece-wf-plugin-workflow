<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.workflow.web.IconJspBean"%>
<%@page import="fr.paris.lutece.plugins.workflow.web.WorkflowJspBean"%>

${ iconJspBean.init( pageContext.request, WorkflowJspBean.RIGHT_MANAGE_WORKFLOW ) }
${ iconJspBean.getManageIcon( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>