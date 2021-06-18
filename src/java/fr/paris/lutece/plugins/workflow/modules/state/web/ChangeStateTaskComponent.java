package fr.paris.lutece.plugins.workflow.modules.state.web;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.workflow.modules.state.business.ChangeStateTaskConfig;
import fr.paris.lutece.plugins.workflow.modules.state.business.ChangeStateTaskInformation;
import fr.paris.lutece.plugins.workflow.modules.state.business.ChangeStateTaskInformationHome;
import fr.paris.lutece.plugins.workflow.modules.state.service.IChangeStateTaskService;
import fr.paris.lutece.plugins.workflow.web.task.NoFormTaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

public class ChangeStateTaskComponent extends NoFormTaskComponent
{
    private static final String TEMPLATE_TASK_CHANGE_STATE_CONFIG = "admin/plugins/workflow/modules/state/task_change_state_config_form.html";
    private static final String TEMPLATE_TASK_CHANGE_STATE_INFORMATION = "admin/plugins/workflow/modules/state/task_choose_state_information.html";
    
    // Marks
    private static final String MARK_LIST_STATES = "list_states";
    private static final String MARK_NEW_STATE = "new_state";
    private static final String MARK_CONFIG = "config";
    
    @Inject
    private IChangeStateTaskService _changeStateTaskService;
    
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        ChangeStateTaskConfig config = _changeStateTaskService.loadConfig( task );
        
        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_CONFIG, config );
        model.put( MARK_LIST_STATES, _changeStateTaskService.getListStates( task.getAction( ).getId( ) ) );
        

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_CHANGE_STATE_CONFIG, locale, model );

        return template.getHtml( );
    }
    
    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<>( );
        ChangeStateTaskInformation taskInformation = ChangeStateTaskInformationHome.find( nIdHistory, task.getId( ) );
        if ( taskInformation == null )
        {
            return null;
        }

        model.put( MARK_NEW_STATE, taskInformation.getState( ) );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_CHANGE_STATE_INFORMATION, locale, model );

        return template.getHtml( );
    }
    
    @Override
    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }
}
