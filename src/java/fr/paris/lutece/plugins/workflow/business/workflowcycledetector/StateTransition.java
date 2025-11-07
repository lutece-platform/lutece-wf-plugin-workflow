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
package fr.paris.lutece.plugins.workflow.business.workflowcycledetector;

import fr.paris.lutece.plugins.workflowcore.business.action.Action;

import java.util.List;

public class StateTransition {

    private final int _stateBefore;
    private final int _stateAfter;

    private Action _action = null;


    private StateTransition(int stateBefore, int stateAfter )
    {
        _stateBefore = stateBefore;
        _stateAfter = stateAfter;
    }

    /**
     * Adds state transitions (StateTransition) to the given on the provided Action.
     *
     * @param list    The list of existing StateTransition objects.
     * @param action  The Action object containing state transition details.
     */
    public static void addTransitionFromAction(List<StateTransition> list, Action action )
    {
        for ( int stateBefore : action.getListIdStateBefore() )
        {
            if( action.getStateAfter() != null && action.getStateAfter().getId() != -1 )
            {
                StateTransition stateTransition = new StateTransition( stateBefore, action.getStateAfter().getId() );
                stateTransition.setAction( action );
                if( !transitionAlreadyExist( list, stateTransition) )
                {
                    list.add(stateTransition);
                }
            }

            if( action.getAlternativeStateAfter() != null && action.getAlternativeStateAfter().getId() != -1 )
            {
                StateTransition stateTransition = new StateTransition( stateBefore, action.getAlternativeStateAfter().getId() );
                stateTransition.setAction( action );
                list.add(stateTransition);
                if( !transitionAlreadyExist( list, stateTransition) )
                {
                    list.add(stateTransition);
                }
            }
        }
    }

    /**
     * Checks whether a given state transition already exists in the list.
     *
     * @param list list of StateTransition
     * @param stateTransition The StateTransition to check for duplication.
     * @return true if a transition with the same source and destination states exists; false otherwise.
     */
    private static boolean transitionAlreadyExist(List<StateTransition> list, StateTransition stateTransition)
    {
        return list.stream().anyMatch( s -> s.getStateBefore() == stateTransition.getStateBefore()
                && s.getStateAfter() == stateTransition.getStateAfter() );
    }

    /**
     * Builds and returns a string representation of the path name.
     *
     * @return A string representation of the path name.
     */

    public String getPathName()
    {
        if( _action != null )
        {
            return _action.getName();
        }
        return "";
    }

    /**
     * Returns the identifier of the previous state.
     *
     * @return The ID of the state before the current transition.
     */
    public int getStateBefore() {
        return _stateBefore;
    }

    /**
     * Returns the identifier of the next state.
     *
     * @return The ID of the state after the current transition.
     */
    public int getStateAfter() {
        return _stateAfter;
    }

    /**
     * Sets the action associated with the current transition.
     *
     * @param action The Action object to assign.
     */
    private void setAction(Action action) {
        this._action = action;
    }

}
