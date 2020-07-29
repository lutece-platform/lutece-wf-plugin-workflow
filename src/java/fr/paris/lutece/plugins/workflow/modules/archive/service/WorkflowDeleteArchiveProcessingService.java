package fr.paris.lutece.plugins.workflow.modules.archive.service;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import fr.paris.lutece.plugins.workflow.modules.assignment.service.IAssignmentHistoryService;
import fr.paris.lutece.plugins.workflow.modules.comment.service.ICommentValueService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;

/**
 * Service for archival of type delete of plugin-workflow.
 */
public class WorkflowDeleteArchiveProcessingService extends AbstractArchiveProcessingService
{
    public static final String BEAN_NAME = "workflow.workflowDeleteArchiveProcessingService";

    private static final String TASK_TYPE_COMMENT = "taskTypeComment";
    private static final String TASK_TYPE_ASSIGNMENT = "taskTypeAssignment";
    private static final String TASK_TYPE_ARCHIVE = "taskTypeArchive";

    @Inject
    private IArchiveService _archiveService;

    @Inject
    private IAssignmentHistoryService _assignmentHistoryService;

    @Inject
    private ICommentValueService _commentValueService;

    @Inject
    private IResourceWorkflowService _resourceWorkflowService;

    @Override
    public void archiveResource( ResourceWorkflow resourceWorkflow )
    {
        List<ResourceHistory> historyList = getListHistoryByResource( resourceWorkflow );

        archiveTaskComment( historyList );
        archiveTaskAssignment( historyList );
        archiveTaskArchive( historyList );

        archiveResourceAndHistory( resourceWorkflow );
    }

    private void archiveTaskComment( List<ResourceHistory> historyList )
    {
        for ( ResourceHistory history : historyList )
        {
            List<ITask> taskList = findTasksByHistory( history, TASK_TYPE_COMMENT );
            for ( ITask task : taskList )
            {
                _commentValueService.removeByHistory( history.getId( ), task.getId( ), WorkflowUtils.getPlugin( ) );
            }

        }
    }

    private void archiveTaskAssignment( List<ResourceHistory> historyList )
    {
        for ( ResourceHistory history : historyList )
        {
            List<ITask> taskList = findTasksByHistory( history, TASK_TYPE_ASSIGNMENT );
            for ( ITask task : taskList )
            {
                _assignmentHistoryService.removeByHistory( history.getId( ), task.getId( ), WorkflowUtils.getPlugin( ) );
            }
        }
    }

    private void archiveTaskArchive( List<ResourceHistory> historyList )
    {
        for ( ResourceHistory history : historyList )
        {
            List<ITask> taskList = findTasksByHistory( history, TASK_TYPE_ARCHIVE );
            for ( ITask task : taskList )
            {
                _archiveService.removeArchiveResource( history.getIdResource( ), task.getId( ) );
            }
        }
    }

    private void archiveResourceAndHistory( ResourceWorkflow resourceWorkflow )
    {
        _resourceHistoryService.removeByListIdResource( Collections.singletonList( resourceWorkflow.getIdResource( ) ), resourceWorkflow.getResourceType( ),
                resourceWorkflow.getWorkflow( ).getId( ) );
        _resourceWorkflowService.remove( resourceWorkflow );
    }
}
