function removeComment(idHistory, idTask) {
  var form = $('<form>', 
    { 'action': 'jsp/admin/plugins/workflow/modules/comment/ConfirmRemoveComment.jsp?' + 'id_history=' + idHistory + '&id_task=' + idTask + '&return_url=' + encodeURIComponent(window.location.href),
      'method':'post'}
    );
    form.appendTo('body');
    form.submit().remove();
}