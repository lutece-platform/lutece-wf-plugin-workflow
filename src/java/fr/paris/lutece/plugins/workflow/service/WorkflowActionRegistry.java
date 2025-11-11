package fr.paris.lutece.plugins.workflow.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WorkflowActionRegistry
{
    private final Map<WorkflowRBACAction.Scope, List<WorkflowRBACAction>> actionsByScope;

    public WorkflowActionRegistry( )
    {
        this.actionsByScope = Collections.unmodifiableMap( Arrays.stream( WorkflowRBACAction.values( ) ).collect( Collectors.groupingBy( WorkflowRBACAction::getScope ) ) );
    }

    public List<WorkflowRBACAction> getActionsForScope( WorkflowRBACAction.Scope scope )
    {
        return actionsByScope.getOrDefault( scope, Collections.emptyList( ) );
    }

    public Map<WorkflowRBACAction.Scope, List<WorkflowRBACAction>> getAllActionsByScope( )
    {
        return actionsByScope;
    }

    private static class Holder
    {
        private static final WorkflowActionRegistry INSTANCE = new WorkflowActionRegistry( );
    }

    public static WorkflowActionRegistry getInstance( )
    {
        return Holder.INSTANCE;
    }
}
