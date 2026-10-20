function removeComment( idHistory, idTask ) {
  const form = document.createElement( 'form' );
  form.method = 'post';
  form.action = 'jsp/admin/plugins/workflow/modules/comment/ConfirmRemoveComment.jsp?id_history=' + encodeURIComponent( idHistory ) + '&id_task=' + encodeURIComponent( idTask ) + '&return_url=' + encodeURIComponent( window.location.href );
  document.body.appendChild( form );
  form.submit( );
  form.remove( );
}

// The remove buttons of task_comment_information.html carry data-workflow-remove-comment, data-id-history and
// data-id-task (no inline handler). The template includes this file once per comment: the listener is added once.
if ( !window.workflowRemoveCommentListener ) {
  window.workflowRemoveCommentListener = true;
  document.addEventListener( 'click', function( event ) {
    var button = event.target.closest( '[data-workflow-remove-comment]' );
    if ( button ) {
      event.preventDefault( );
      removeComment( button.dataset.idHistory, button.dataset.idTask );
    }
  } );
}
