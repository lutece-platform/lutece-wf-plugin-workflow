<%@ page errorPage="../../ErrorPage.jsp" %>

${ pageContext.response.sendRedirect( workflow_commentJspBean.doRemoveComment( pageContext.request )) }
