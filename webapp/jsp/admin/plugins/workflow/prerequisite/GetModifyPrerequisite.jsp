<%@ page errorPage="../../../ErrorPage.jsp" %>
<%@page import="fr.paris.lutece.plugins.workflow.web.prerequisite.PrerequisiteJspBean"%>
<%@page import="fr.paris.lutece.plugins.workflow.web.WorkflowJspBean"%>

${ prerequisiteJspBean.init( pageContext.request, WorkflowJspBean.RIGHT_MANAGE_WORKFLOW ) }
${ pageContext.setAttribute( 'strContent', prerequisiteJspBean.getModifyPrerequisite( pageContext.request, pageContext.response ) ) }

<jsp:include page="../../../AdminHeader.jsp" />

${ pageContext.getAttribute( 'strContent' ) }

<%@ include file="../../../AdminFooter.jsp" %>
