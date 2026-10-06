<%@ page errorPage="../../ErrorPage.jsp" %>

${ pageContext.response.sendRedirect( workflow_commentJspBean.getConfirmRemoveComment( pageContext.request )) }
