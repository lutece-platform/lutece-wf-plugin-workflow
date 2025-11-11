package fr.paris.lutece.plugins.workflow.service;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.service.rbac.RBACAction;
import fr.paris.lutece.portal.service.rbac.RBACResource;
import fr.paris.lutece.portal.service.rbac.RBACService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WorkflowRBACService
{
    private static RBACResource resource( String type, String id )
    {
        return new RBACResource( )
        {
            @Override public String getResourceTypeCode( ) { return type; }
            @Override public String getResourceId( ) { return id; }
        };
    }

    private static <E extends RBACAction> Collection<E> filterAuthorizedActions(User user, String type, String id, Collection<E> actions )
    {
        return RBACService.getAuthorizedActionsCollection( actions, resource( type, id ), user );
    }

    public static Collection<WorkflowRBACAction> getAuthorizedActionsCollection( User user )
    {
        Collection<WorkflowRBACAction> applicationActions = getScopedActions( user, WorkflowRBACAction.Scope.APP, RBAC.WILDCARD_RESOURCES_ID, WorkflowActionRegistry.getInstance( ).getActionsForScope( WorkflowRBACAction.Scope.APP ) );
        Collection<WorkflowRBACAction> globalWorkflowActions = getScopedActions(user, WorkflowRBACAction.Scope.WORKFLOW, RBAC.WILDCARD_RESOURCES_ID, WorkflowActionRegistry.getInstance( ).getActionsForScope( WorkflowRBACAction.Scope.WORKFLOW ) );

        LinkedHashSet<WorkflowRBACAction> effectiveActions = new LinkedHashSet<>( applicationActions );
        effectiveActions.addAll( globalWorkflowActions );
        return effectiveActions;
    }

    public static Collection<WorkflowRBACAction> getAuthorizedActionsCollection( User user, int workflowId )
    {
        Map<WorkflowRBACAction.Scope, List<WorkflowRBACAction>> actionsByScope = Arrays.stream( WorkflowRBACAction.values( ) ).collect( Collectors.groupingBy( WorkflowRBACAction::getScope ) );

        Collection<WorkflowRBACAction> globalActions = getAuthorizedActionsCollection( user );
        Collection<WorkflowRBACAction> workflowActions = getScopedActions( user, WorkflowRBACAction.Scope.WORKFLOW, String.valueOf( workflowId ), WorkflowActionRegistry.getInstance( ).getActionsForScope( WorkflowRBACAction.Scope.WORKFLOW ) );

        LinkedHashSet<WorkflowRBACAction> effectiveActions = new LinkedHashSet<>( globalActions );
        effectiveActions.addAll( workflowActions );
        return effectiveActions;
    }

    public static Map<String, Collection<WorkflowRBACAction>> getAuthorizedActionsCollection( User user, List<Workflow> workflows )
    {
        Collection<WorkflowRBACAction> globalActions = getAuthorizedActionsCollection( user );

        Map<String, Collection<WorkflowRBACAction>> permissionMap = new HashMap<>( );

        for (Workflow workflow : workflows)
        {
            Collection<WorkflowRBACAction> workflowActions = getScopedActions( user, WorkflowRBACAction.Scope.WORKFLOW, String.valueOf( workflow.getId( ) ), WorkflowActionRegistry.getInstance( ).getActionsForScope( WorkflowRBACAction.Scope.WORKFLOW )  );
            LinkedHashSet<WorkflowRBACAction> effectiveActions = new LinkedHashSet<>( globalActions );
            effectiveActions.addAll( workflowActions );
            permissionMap.put( String.valueOf( workflow.getId( ) ), effectiveActions );
        }

        return permissionMap;
    }

    public static Map<String, Boolean> getAuthorizedActionsMap( User user )
    {
        return toPermissionMap( getAuthorizedActionsCollection( user ) );
    }

    public static Map<String, Boolean> getAuthorizedActionsMap( User user, int workflowId )
    {
        return toPermissionMap( getAuthorizedActionsCollection( user, workflowId ) );
    }

    public static Map<String, Map<String, Boolean>> getAuthorizedActionsMap( User user, List<Workflow> workflows )
    {
        return getPermissionMapByWorkflow( getAuthorizedActionsCollection( user, workflows ) );
    }

    private static Collection<WorkflowRBACAction> getScopedActions( User user, WorkflowRBACAction.Scope scope, String resourceId, List<WorkflowRBACAction> actionsByScope )
    {
        if ( actionsByScope == null || actionsByScope.isEmpty( ) )
        {
            return Collections.emptyList( );
        }

        String resourceType;
        switch ( scope )
        {
            case APP:
                resourceType = WorkflowAppResourceIdService.RESOURCE_TYPE_KEY;
                break;
            case WORKFLOW:
                resourceType = WorkflowResourceIdService.RESOURCE_TYPE_KEY;
                break;
            default:
                return Collections.emptyList( );
        }

        return filterAuthorizedActions(user, resourceType, resourceId, actionsByScope);
    }

    private static Map<String,Boolean> toPermissionMap( Collection<WorkflowRBACAction> allowed )
    {
        return allowed.stream().collect(Collectors.toMap(
                WorkflowRBACAction::getPermission,
                a -> Boolean.TRUE,
                ( a,b ) -> a,
                LinkedHashMap::new
        ));
    }

    private static Map<String, Map<String, Boolean>> getPermissionMapByWorkflow( Map<String, Collection<WorkflowRBACAction>> allowed )
    {
        return allowed.entrySet( ).stream( ).collect(
                Collectors.toMap( Map.Entry::getKey,
                        entry -> entry.getValue( ).stream( )
                                .map( WorkflowRBACAction::getPermission )
                                .collect( Collectors.toMap( Function.identity( ), p -> true ) )
                                )
        );
    }
}
