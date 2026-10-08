function removeComment( idHistory, idTask ) {
  const form = document.createElement( 'form' );
  form.method = 'post';
  form.action = 'jsp/admin/plugins/workflow/modules/comment/ConfirmRemoveComment.jsp?id_history=' + idHistory + '&id_task=' + idTask + '&return_url=' + encodeURIComponent( window.location.href );
  document.body.appendChild( form );
  form.submit( );
  form.remove( );
}