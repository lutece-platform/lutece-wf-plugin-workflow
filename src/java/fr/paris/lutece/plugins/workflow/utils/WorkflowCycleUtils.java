/*
 * Copyright (c) 2002-2025, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.utils;

import fr.paris.lutece.plugins.workflow.business.workflowcycledetector.StateTransition;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.util.mvc.utils.MVCMessage;
import fr.paris.lutece.util.ErrorMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WorkflowCycleUtils {

    private static final String MESSAGE_WARNING_LOOP = "workflow.message.warning.action.auto.loop";

    private static final String MESSAGE_SEPARATOR_STATE_TRANSITION = " - ";
    private static final String MESSAGE_SEPARATOR_BETWEEN_STATE = " -> ";

    /**
     * WorkflowCycleUtils
     *
     */
    private WorkflowCycleUtils( )
    {
    }

    /**
     * Check if we have a loop of automatique action that lead to the same State
     * @param listState The State list of the workflow
     * @param listAction The action list of the workflow
     * @return ErrorMessage
     */
    public static ErrorMessage detectCycle(List<State> listState, List<Action> listAction, Locale locale)
    {

        List<StateTransition> listStateTransition = new ArrayList<>();
        
        for( Action action : listAction )
        {
            if( action.isAutomaticState() )
            {
                StateTransition.addTransitionFromAction(listStateTransition, action );
            }
        }

        return hasCycle( listState, listStateTransition, locale);
    }

    /**
     * Check if we found a cycle with the list of StateTransition
     * @param listState list of state
     * @param listStateTransition list of StateTransition
     * @param locale the locale
     * @return ErrorMessage
     */
    private static ErrorMessage hasCycle(List<State> listState, List<StateTransition> listStateTransition, Locale locale ) {

        Map<Integer, List<Integer>> graph = new HashMap<>();

        for ( StateTransition path : listStateTransition)
        {
            graph.computeIfAbsent(path.getStateBefore(), k -> new ArrayList<>()).add(path.getStateAfter());
        }

        List<Integer> visited = new ArrayList<>();
        List<Integer> recStack = new ArrayList<>();
        boolean cycleFound = false;

        for ( Integer node : graph.keySet() ) {
            if ( depthFirstSearch(node, visited, recStack, graph) ) {
                cycleFound = true;
                break;
            }
        }

        if( cycleFound )
        {
            return printCycleStack( listState, listStateTransition, reduceStack( recStack ), locale );
        }

        return null;
    }

    /**
     * reduce de stack to keep only have the loop
     * @param fullStack the full stack of the Depth-First Search
     * @return reduced stack
     */
    private static List<Integer> reduceStack( List<Integer> fullStack )
    {
        Integer lastIdState = fullStack.get(fullStack.size()-1);
        boolean firstStateFound = false;
        List<Integer> stackFromFirstState = new ArrayList<>();
        for( Integer idState : fullStack )
        {
            if ( !firstStateFound && idState.equals(lastIdState))
            {
                firstStateFound = true;
                stackFromFirstState.add( idState );
            }
            else
            {
                if (firstStateFound)
                {
                    stackFromFirstState.add( idState );
                }
            }
        }
        return stackFromFirstState;
    }


    /**
     * Print the path use to found the loop
     * @param listState the listState List
     * @param listStateTransition the listStateTransition List
     * @param recStack the the stack to print
     * @return ErrorMessage
     */
    private static ErrorMessage printCycleStack(List<State> listState, List<StateTransition> listStateTransition, List<Integer> recStack, Locale locale )
    {
        List<StateTransition> cyclePath = new ArrayList<>();

        Iterator<Integer> cycleIterator = recStack.iterator();
        Integer stateBefore = cycleIterator.next();

        while ( cycleIterator.hasNext() ) {
            Integer stateAfter = cycleIterator.next();
            Integer finalStateBefore = stateBefore;
            cyclePath.add(listStateTransition.stream().filter(p -> p.getStateBefore() == finalStateBefore
                    && p.getStateAfter() == stateAfter).findFirst().orElse(null));
            stateBefore = stateAfter;
        }

        StringBuilder str = new StringBuilder();
        for (StateTransition path : cyclePath) {
            str.append( printState(listState, path.getStateBefore()));
            str.append( MESSAGE_SEPARATOR_STATE_TRANSITION ).append( path.getPathName() );
            str.append( MESSAGE_SEPARATOR_BETWEEN_STATE );
        }
        StateTransition finalPath = cyclePath.get(cyclePath.size()-1);
        str.append( printState(listState, finalPath.getStateAfter()) );

        return new MVCMessage(I18nService.getLocalizedString( MESSAGE_WARNING_LOOP, locale )
                + str.toString());
    }

    /**
     * Print the name of a state from a list based on its identifier.
     *
     * @param listState A list State
     * @param idState The ID of the state whose name is to be retrieved.
     * @return The name of the matching state if found; otherwise, an empty string.
     */
    private static String printState( List<State> listState, Integer idState )
    {
        State state = listState.stream().filter(s -> s.getId() == idState).findFirst().orElse( null );
        if ( state != null )
        {
            return state.getName();
        }
        return "";
    }

    /**
     * Performs a Depth-First Search to detect cycles in a directed graph.
     *
     * @param node The current node being explored.
     * @param visited A list of nodes that have already been visited.
     * @param recStack A recursion stack tracking the current path of exploration.
     * @param graph A map representing the graph, where each node maps to a list of its neighbors.
     * @return true if a cycle is detected, false otherwise.
     */
    private static boolean depthFirstSearch(Integer node, List<Integer> visited, List<Integer> recStack, Map<Integer, List<Integer>> graph)
    {
        if (recStack.contains(node))
        {
            recStack.add(node);
            return true;
        }
        if (visited.contains(node)) return false;

        visited.add(node);
        recStack.add(node);

        for (Integer neighbor : graph.getOrDefault(node, Collections.emptyList())) {
            if ( depthFirstSearch(neighbor, visited, recStack, graph)) {
                return true;
            }
        }

        recStack.remove(node);
        return false;
    }

}
