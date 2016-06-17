<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:useBean id="manageComment" scope="session" class="fr.paris.lutece.plugins.workflow.modules.comment.web.CommentJspBean" />
<% 
    response.sendRedirect( manageComment.getConfirmRemoveComment(request) );
%>