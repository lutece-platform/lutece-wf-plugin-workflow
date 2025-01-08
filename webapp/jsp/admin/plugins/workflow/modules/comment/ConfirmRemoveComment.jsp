<%@ page errorPage="../../ErrorPage.jsp" %>

${ pageContext.response.sendRedirect( commentJspBean.getConfirmRemoveComment( pageContext.request )) }
