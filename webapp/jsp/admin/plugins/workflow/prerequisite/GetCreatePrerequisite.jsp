<%@page import="fr.paris.lutece.plugins.workflow.web.prerequisite.PrerequisiteJspBean"%>
<%@page import="fr.paris.lutece.plugins.workflow.web.WorkflowJspBean"%>

${ prerequisiteJspBean.init( pageContext.request, WorkflowJspBean.RIGHT_MANAGE_WORKFLOW ) }
${ pageContext.setAttribute( 'strHtml', prerequisiteJspBean.getCreatePrerequisite( pageContext.request, pageContext.response ) ) }

<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

${ pageContext.getAttribute( 'strHtml' ) }

<%@ include file="../../../AdminFooter.jsp" %>