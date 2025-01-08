<%@ page errorPage="../../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.workflow.web.prerequisite.PrerequisiteJspBean"%>
<%@page import="fr.paris.lutece.plugins.workflow.web.WorkflowJspBean"%>

${ prerequisiteJspBean.init( pageContext.request, WorkflowJspBean.RIGHT_MANAGE_WORKFLOW ) }
${ pageContext.response.sendRedirect( prerequisiteJspBean.doModifyPrerequisite( pageContext.request )) }
