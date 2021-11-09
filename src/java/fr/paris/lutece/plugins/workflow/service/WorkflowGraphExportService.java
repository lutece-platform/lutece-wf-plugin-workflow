/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.workflow.service;


import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;

/**
 *
 * @author seboo
 */
public class WorkflowGraphExportService {

    private static final String KEY_GRAPH = "graph TD";
    private static final String KEY_ASSIGN_LABEL_START = "[\"";
    private static final String KEY_ASSIGN_LABEL_END = "\"]";
    private static final String KEY_ASSIGN_ACTIONS_START = " --> |\"";
    private static final String KEY_ASSIGN_ACTIONS_END = "\"| ";
    private static final String KEY_NEWLINE = "<br /> ";
    private static final String KEY_CLICK = "click";
    private static final String STATE_URL_PART = "/jsp/admin/plugins/workflow/ModifyState.jsp?id_state=" ;
    private static final String NEWLINE = "\n";
    
    /**
     * Export workflow as Mermaid MD graph
     * 
     * @param wf
     * @param locale
     * @return the markdown definition of the workflow
     */
    public static String generate( Workflow wf, String strBaseUrl )
    {
        StringBuilder sb = new StringBuilder( KEY_GRAPH ).append( NEWLINE );
        
        // list states 
        for (State state : wf.getAllStates( ) )
        {
            sb.append( state.getId( ) )
                    .append( KEY_ASSIGN_LABEL_START )
                    .append( state.getName( ).replaceAll("\"", "'") )
                    .append( KEY_ASSIGN_LABEL_END )
                    .append( NEWLINE );
        }
        
        // list actions with tasks
        for (Action action : wf.getAllActions( ) )
        {
            sb.append( action.getStateBefore( ).getId( ) )
                    .append( KEY_ASSIGN_ACTIONS_START )
                    .append( getTransitionLabel( action ) )
                    .append( KEY_ASSIGN_ACTIONS_END  )
                    .append( action.getStateAfter( ).getId( ) )
                    .append( NEWLINE );
        }        
        
        if ( strBaseUrl != null )
        {
            // add links
            for (State state : wf.getAllStates( ) )
            {
                sb.append( KEY_CLICK )
                        .append( " " )
                        .append( state.getId( ) )
                        .append( " \"" )
                        .append( strBaseUrl )
                        .append( STATE_URL_PART )
                        .append( state.getId( ) )
                        .append( "\"" )
                        .append( NEWLINE );
            }            
        }
        
        return sb.toString( );
    }
    
    
    /**
     * get labels for actions and tasks
     * 
     * @param action
     * @return the label 
     */
    private static String getTransitionLabel( Action action )
    {
        StringBuilder sb = new StringBuilder( action.getName( ) );
        
        if ( action.getAllTasks( ) == null) return sb.toString( );
        
        for (ITask task : action.getAllTasks( ) )
        {
            sb.append( KEY_NEWLINE ).append ( " * " ).append( task.getTaskType( ).getTitle( ) ) ;
        }
        
        return sb.toString( ).replaceAll("\"", "'");
    }
   
}
