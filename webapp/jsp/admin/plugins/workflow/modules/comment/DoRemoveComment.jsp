<%@ page errorPage="../../ErrorPage.jsp" %>

${ pageContext.response.sendRedirect( commentJspBean.doRemoveComment( pageContext.request )) }
