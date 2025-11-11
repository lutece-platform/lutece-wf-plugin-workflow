package fr.paris.lutece.plugins.workflow.service;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.business.workflow.WorkflowFilter;
import fr.paris.lutece.plugins.workflowcore.service.workflow.IWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.WorkflowService;
import fr.paris.lutece.portal.service.rbac.Permission;
import fr.paris.lutece.portal.service.rbac.ResourceIdService;
import fr.paris.lutece.portal.service.rbac.ResourceType;
import fr.paris.lutece.portal.service.rbac.ResourceTypeManager;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;
import java.util.Locale;

/**
 *
 * class WorkflowResourceIdService
 *
 */
public class WorkflowResourceIdService extends ResourceIdService
{
    public static final String RESOURCE_TYPE_KEY = "WORKFLOW";
    public static final String PERM_WORKFLOW_READ    = "PERM_WORKFLOW_READ";
    public static final String PERM_WORKFLOW_EDIT    = "PERM_WORKFLOW_EDIT";
    public static final String PERM_WORKFLOW_EDIT_STATES    = "PERM_WORKFLOW_EDIT_STATES";
    public static final String PERM_WORKFLOW_EDIT_ACTIONS    = "PERM_WORKFLOW_EDIT_ACTIONS";
    public static final String PERM_WORKFLOW_DELETE = "PERM_WORKFLOW_DELETE";
    public static final String PERM_WORKFLOW_DELETE_STATES = "PERM_WORKFLOW_DELETE_STATES";
    public static final String PERM_WORKFLOW_DELETE_ACTIONS = "PERM_WORKFLOW_DELETE_ACTIONS";

    private static final String PROPERTY_LABEL_RESOURCE_TYPE = "workflow.permission.label.resource_type_workflow";
    private static final String PROPERTY_LABEL_READ     = "workflow.permission.label.read_workflow";
    private static final String PROPERTY_LABEL_EDIT     = "workflow.permission.label.edit_workflow";
    private static final String PROPERTY_LABEL_EDIT_STATES     = "workflow.permission.label.edit_workflow_states";
    private static final String PROPERTY_LABEL_EDIT_ACTIONS     = "workflow.permission.label.edit_workflow_actions";
    private static final String PROPERTY_LABEL_DELETE  = "workflow.permission.label.delete_workflow";
    private static final String PROPERTY_LABEL_DELETE_STATES  = "workflow.permission.label.delete_workflow_states";
    private static final String PROPERTY_LABEL_DELETE_ACTIONS  = "workflow.permission.label.delete_workflow_actions";

    public WorkflowResourceIdService( )
    {
        setPluginName( WorkflowPlugin.PLUGIN_NAME );
    }

    @Override
    public void register( )
    {
        ResourceType rt = new ResourceType( );
        rt.setResourceIdServiceClass( getClass( ).getName( ) );
        rt.setPluginName( WorkflowPlugin.PLUGIN_NAME );
        rt.setResourceTypeKey( RESOURCE_TYPE_KEY );
        rt.setResourceTypeLabelKey( PROPERTY_LABEL_RESOURCE_TYPE );

        Permission p = new Permission( );
        p.setPermissionKey( PERM_WORKFLOW_READ );
        p.setPermissionTitleKey( PROPERTY_LABEL_READ );
        rt.registerPermission( p );

        p = new Permission( );
        p.setPermissionKey( PERM_WORKFLOW_EDIT );
        p.setPermissionTitleKey( PROPERTY_LABEL_EDIT );
        rt.registerPermission( p );

        p = new Permission( );
        p.setPermissionKey( PERM_WORKFLOW_EDIT_STATES );
        p.setPermissionTitleKey( PROPERTY_LABEL_EDIT_STATES );
        rt.registerPermission( p );

        p = new Permission( );
        p.setPermissionKey( PERM_WORKFLOW_EDIT_ACTIONS );
        p.setPermissionTitleKey( PROPERTY_LABEL_EDIT_ACTIONS );
        rt.registerPermission( p );

        p = new Permission( );
        p.setPermissionKey( PERM_WORKFLOW_DELETE );
        p.setPermissionTitleKey( PROPERTY_LABEL_DELETE );
        rt.registerPermission( p );

        p = new Permission( );
        p.setPermissionKey( PERM_WORKFLOW_DELETE_STATES );
        p.setPermissionTitleKey( PROPERTY_LABEL_DELETE_STATES );
        rt.registerPermission( p );

        p = new Permission( );
        p.setPermissionKey( PERM_WORKFLOW_DELETE_ACTIONS );
        p.setPermissionTitleKey( PROPERTY_LABEL_DELETE_ACTIONS );
        rt.registerPermission( p );

        ResourceTypeManager.registerResourceType( rt );
    }

    @Override
    public ReferenceList getResourceIdList( Locale locale )
    {
        IWorkflowService workflowService = SpringContextService.getBean( WorkflowService.BEAN_SERVICE );
        List<Workflow> listWorkflow = workflowService.getListWorkflowsByFilter( new WorkflowFilter( ) );
        ReferenceList list = new ReferenceList( );
        listWorkflow.forEach( wf -> list.addItem( wf.getId( ), wf.getName( ) ) );
        return list;
    }

    @Override
    public String getTitle( String strId, Locale locale )
    {
        IWorkflowService workflowService = SpringContextService.getBean( WorkflowService.BEAN_SERVICE );
        int id = WorkflowUtils.convertStringToInt( strId );
        Workflow wf = workflowService.findByPrimaryKey( id );
        return wf != null ? wf.getName( ) : null;
    }
}
