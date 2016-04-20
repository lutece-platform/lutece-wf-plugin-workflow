<div class="row">
	<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
		<div class="box box-primary">
			<h3 class="box-title">#i18n{workflow.import_workflow.page_title}</h3>
		</div>
		
		<div class="box-body">
			<form class="form-horizontal"
				enctype="multipart/form-data"
				action="jsp/admin/plugins/workflow/DoImportWorkflow.jsp?plugin_name=workflow" method="post" name="" id="">
				
				<fieldset>
					<div class="form-group">
						<label class="control-label col-xs-12 col-sm-3 col-md-3 col-lg-3"
							for="name">#i18n{workflow.create_workflow.label_name} * </label>
						<div class="col-xs-12 col-sm-8 col-md-6 col-lg-6">
							<input type="file" name="import_file" id="name" class="file"> <span
								class="help-block"></span>
						</div>
					</div>
					<div class="form-group">
						<div
							class="col-xs-12 col-sm-offset-3 col-md-offset-3 col-lg-offset-3">
							<button class="btn btn-primary btn-sm btn-flat" type="submit">
								<i class="glyphicon glyphicon-ok"></i>&nbsp;#i18n{workflow.create_workflow.button_save}
							</button>
						</div>
					</div>
				</fieldset>
		</div>
	</div>
</div>
