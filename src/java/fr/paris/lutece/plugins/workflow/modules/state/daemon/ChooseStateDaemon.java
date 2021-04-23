package fr.paris.lutece.plugins.workflow.modules.state.daemon;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import fr.paris.lutece.plugins.workflow.modules.state.business.ChooseStateTaskConfig;
import fr.paris.lutece.plugins.workflow.modules.state.service.IChooseStateTaskService;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflowFilter;
import fr.paris.lutece.plugins.workflowcore.business.task.ITaskType;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.business.workflow.WorkflowFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.ActionService;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.resource.ResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.service.task.TaskService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.IWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.WorkflowService;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;

public class ChooseStateDaemon extends Daemon
{

    private IChooseStateTaskService _chooseStateTaskService = SpringContextService.getBean( "workflow.chooseStateTaskService" );
    private IWorkflowService _workflowService = SpringContextService.getBean( WorkflowService.BEAN_SERVICE );
    private IResourceWorkflowService _resourceWorkflowService = SpringContextService.getBean( ResourceWorkflowService.BEAN_SERVICE );
    private IActionService _actionService = SpringContextService.getBean( ActionService.BEAN_SERVICE );
    private ITaskService _taskService = SpringContextService.getBean( TaskService.BEAN_SERVICE );

    @Override
    public void run( )
    {
        WorkflowFilter workflowFilter = new WorkflowFilter( );
        workflowFilter.setIsEnabled( 1 );

        List<Workflow> listWorkflows = _workflowService.getListWorkflowsByFilter( workflowFilter );

        for ( Workflow workflow : listWorkflows )
        {
            ActionFilter filter = new ActionFilter( );
            filter.setAutomaticReflexiveAction( true );
            filter.setIdWorkflow( workflow.getId( ) );

            List<Action> listAutomaticActions = _actionService.getListActionByFilter( filter );

            for ( Action action : listAutomaticActions )
            {
                ResourceWorkflowFilter filt = new ResourceWorkflowFilter( );
                filt.setIdState( action.getStateBefore( ).getId( ) );
                filt.setIdWorkflow( workflow.getId( ) );
                
                
                List<ITask> listActionTasks = _taskService.getListTaskByIdAction( action.getId( ), Locale.getDefault( ) );
                ITask task = findTaskToExecute( listActionTasks );
                
                if ( task == null )
                {
                    continue;
                }
                
            	ChooseStateTaskConfig config = _chooseStateTaskService.loadConfig( task );
            	
                List<ResourceWorkflow> listResource = _resourceWorkflowService.getListResourceWorkflowByFilter( filt );

                for ( ResourceWorkflow resource : listResource )
                {
                    try
                    {
                        _chooseStateTaskService.chooseNewState( resource.getIdResource( ), resource.getResourceType( ), task, config, workflow.getId( ), resource
                                .getState( ).getId( ) );
                    }
                    catch( Exception e )
                    {
                        AppLogService.error( "Unexpected Error", e );
                    }
                }
            }
        }
    }
    
    private ITask findTaskToExecute( List<ITask> listActionTasks )
    {
        for ( ITask tsk : listActionTasks )
        {
            String beanName = Optional.ofNullable( tsk ).map( ITask::getTaskType ).map( ITaskType::getBeanName ).orElse( null );
            if ( "workflow.chooseStateTask".equals( beanName ) )
            {
                return tsk;
            }
        }
        return null;
    }
}
