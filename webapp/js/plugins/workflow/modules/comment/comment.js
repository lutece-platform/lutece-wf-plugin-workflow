function removeComment(idHistory, idTask) {
  window.location = "jsp/admin/plugins/workflow/modules/comment/ConfirmRemoveComment.jsp?" + "id_history=" + idHistory + "&id_task=" + idTask + "&return_url=" + encodeURIComponent(window.location.href);
}