package fr.paris.lutece.plugins.workflow.modules.archive.service;

import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;

public interface IArchiveProcessingService
{
    /**
     * Archives the resource.
     * 
     * @param resourceWorkflow
     */
    void archiveResource( ResourceWorkflow resourceWorkflow );
}
