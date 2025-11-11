package fr.paris.lutece.plugins.workflow.service;

import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.rbac.Permission;
import fr.paris.lutece.portal.service.rbac.ResourceIdService;
import fr.paris.lutece.portal.service.rbac.ResourceType;
import fr.paris.lutece.portal.service.rbac.ResourceTypeManager;
import fr.paris.lutece.util.ReferenceList;

import java.util.Locale;

/**
 *
 * class WorkflowResourceIdService
 *
 */
public class WorkflowAppResourceIdService extends ResourceIdService
{
    public static final String RESOURCE_TYPE_KEY = "WORKFLOW_APP";
    public static final String PERM_WORKFLOW_LIST    = "PERM_WORKFLOW_LIST";
    public static final String PERM_WORKFLOW_CREATE    = "PERM_WORKFLOW_CREATE";
    public static final String PERM_WORKFLOW_IMPORT    = "PERM_WORKFLOW_IMPORT";

    private static final String PROPERTY_LABEL_RESOURCE_TYPE = "workflow.permission.label.resource_type_workflow_app";
    private static final String PROPERTY_LABEL_LIST     = "workflow.permission.label.list_workflow";
    private static final String PROPERTY_LABEL_CREATE     = "workflow.permission.label.create_workflow";
    private static final String PROPERTY_LABEL_IMPORT     = "workflow.permission.label.import_workflow";
    private static final String PROPERTY_LABEL_TITLE     = "workflow.permission.label.title_workflow_app";

    public WorkflowAppResourceIdService( )
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
        p.setPermissionKey( PERM_WORKFLOW_CREATE );
        p.setPermissionTitleKey( PROPERTY_LABEL_CREATE );
        rt.registerPermission( p );

        p = new Permission( );
        p.setPermissionKey( PERM_WORKFLOW_LIST );
        p.setPermissionTitleKey( PROPERTY_LABEL_LIST );
        rt.registerPermission( p );

        p = new Permission( );
        p.setPermissionKey( PERM_WORKFLOW_IMPORT );
        p.setPermissionTitleKey( PROPERTY_LABEL_IMPORT );
        rt.registerPermission( p );

        ResourceTypeManager.registerResourceType( rt );
    }

    @Override
    public ReferenceList getResourceIdList( Locale locale )
    {
        ReferenceList rl = new ReferenceList( );
        rl.addItem( RBAC.WILDCARD_RESOURCES_ID, I18nService.getLocalizedString( PROPERTY_LABEL_TITLE, locale ) );
        return rl;
    }

    @Override
    public String getTitle( String strId, Locale locale )
    {
        return I18nService.getLocalizedString( PROPERTY_LABEL_TITLE, locale );
    }
}
