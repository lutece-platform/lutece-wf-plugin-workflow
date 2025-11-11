package fr.paris.lutece.plugins.workflow.service;

import fr.paris.lutece.portal.service.rbac.RBACAction;

import static fr.paris.lutece.plugins.workflow.service.WorkflowAppResourceIdService.*;
import static fr.paris.lutece.plugins.workflow.service.WorkflowResourceIdService.*;

public enum WorkflowRBACAction implements RBACAction
{
    VIEW_LIST("workflow.permission.label.list_workflow",           PERM_WORKFLOW_LIST, Scope.APP ),
    IMPORT_WORKFLOW("workflow.permission.label.import_workflow",     PERM_WORKFLOW_IMPORT, Scope.APP ),
    CREATE_WORKFLOW("workflow.permission.label.create_workflow",     PERM_WORKFLOW_CREATE, Scope.APP ),
    VIEW_CONFIG("workflow.permission.label.read_workflow",           PERM_WORKFLOW_READ, Scope.WORKFLOW ),
    EDIT_WORKFLOW("workflow.permission.label.edit_workflow",         PERM_WORKFLOW_EDIT, Scope.WORKFLOW ),
    EDIT_STATES("workflow.permission.label.edit_workflow_states",    PERM_WORKFLOW_EDIT_STATES, Scope.WORKFLOW ),
    EDIT_ACTIONS("workflow.permission.label.edit_workflow_actions",  PERM_WORKFLOW_EDIT_ACTIONS, Scope.WORKFLOW ),
    DELETE_WORKFLOW("workflow.permission.label.delete_workflow",     PERM_WORKFLOW_DELETE, Scope.WORKFLOW ),
    DELETE_STATES("workflow.permission.label.delete_workflow_states",PERM_WORKFLOW_DELETE_STATES, Scope.WORKFLOW ),
    DELETE_ACTIONS("workflow.permission.label.delete_workflow_actions", PERM_WORKFLOW_DELETE_ACTIONS, Scope.WORKFLOW );

    public enum Scope { APP, WORKFLOW }

    private final String i18nKey;
    private final String permission;
    private final Scope scope;

    WorkflowRBACAction( String i18nKey, String permission, Scope scope )
    {
        this.i18nKey = i18nKey;
        this.permission = permission;
        this.scope = scope;
    }

    @Override public String getPermission( )
    {
        return permission;
    }
    public String getI18nKey( )
    {
        return i18nKey;
    }
    public Scope  getScope( )
    {
        return scope;
    }
}
