package fr.paris.lutece.plugins.workflow.modules.state.service;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.workflow.modules.state.business.ChooseStateTaskInformationHome;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;
import fr.paris.lutece.portal.service.i18n.I18nService;

public class ChooseStateTask extends SimpleTask
{
    private static final String MESSAGE_MARK_TITLE = "module.workflow.state.task.choose.state.title";

    @Inject
    @Named( "workflow.chooseStateTaskConfigService" )
    private ITaskConfigService _chooseStateTaskConfigService;
    
    @Override
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
       // The real process is executed by the ChooseStateDaemon
    }

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_MARK_TITLE, locale );
    }
    
    @Override
    public void doRemoveTaskInformation( int nIdHistory )
    {
        ChooseStateTaskInformationHome.remove( nIdHistory, getId( ) );
    }
    
    @Override
    public void doRemoveConfig( )
    {
        _chooseStateTaskConfigService.remove( getId( ) );
    }
}
