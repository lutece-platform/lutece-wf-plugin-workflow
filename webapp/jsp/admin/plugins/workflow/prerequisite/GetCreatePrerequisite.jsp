<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.workflow.web.prerequisite.PrerequisiteJspBean"%>
<%@page import="fr.paris.lutece.plugins.workflow.web.WorkflowJspBean"%>

${ prerequisiteJspBean.init( pageContext.request, WorkflowJspBean.RIGHT_MANAGE_WORKFLOW ) }
${ prerequisiteJspBean.getCreatePrerequisite( pageContext.request, pageContext.response ) }

<%@ include file="../../../AdminFooter.jsp" %>
