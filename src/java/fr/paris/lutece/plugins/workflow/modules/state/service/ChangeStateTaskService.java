package fr.paris.lutece.plugins.workflow.modules.state.service;

import javax.inject.Inject;
import javax.inject.Named;

import fr.paris.lutece.plugins.workflow.modules.state.business.ChangeStateTaskConfig;
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

}
