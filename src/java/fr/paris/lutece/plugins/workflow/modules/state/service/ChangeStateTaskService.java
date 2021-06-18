package fr.paris.lutece.plugins.workflow.modules.state.service;

import javax.inject.Inject;
import javax.inject.Named;

import fr.paris.lutece.plugins.workflow.modules.state.business.ChangeStateTaskConfig;
import fr.paris.lutece.plugins.workflow.modules.state.business.ChangeStateTaskInformation;
import fr.paris.lutece.plugins.workflow.modules.state.business.ChangeStateTaskInformationHome;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;

public class ChangeStateTaskService extends AbstractStateTaskService implements IChangeStateTaskService
{
    @Inject
    @Named( "workflow.changeStateTaskConfigService" )
    private ITaskConfigService _taskConfigService;
    
    
    @Override
    public ChangeStateTaskConfig loadConfig( ITask task )
    {
        ChangeStateTaskConfig config = _taskConfigService.findByPrimaryKey( task.getId( ) );
        if ( config == null )
        {
            config = new ChangeStateTaskConfig( );
            config.setIdTask( task.getId( ) );
            _taskConfigService.create( config );
        }
        return config;
    }

    @Override
    protected void saveTaskInformation( int nIdResourceHistory, ITask task, State state )
    {
        ChangeStateTaskInformation taskInformation = new ChangeStateTaskInformation( );
        taskInformation.setIdHistory( nIdResourceHistory );
        taskInformation.setIdTask( task.getId( ) );
        taskInformation.setNewState( state.getName( ) );

        ChangeStateTaskInformationHome.create( taskInformation );
    }
}
