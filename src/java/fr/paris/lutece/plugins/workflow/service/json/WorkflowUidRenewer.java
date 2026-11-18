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
package fr.paris.lutece.plugins.workflow.service.json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.prerequisite.Prerequisite;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;

/**
 * Gives an exported workflow new UUIDs, so that importing it creates a copy rather than a second holder of the same identities.
 * <p>
 * Every reference inside the export (the states of an action, its linked actions, the action of a task or of a prerequisite) follows the element it
 * designates.
 */
final class WorkflowUidRenewer
{
    private final Map<String, String> _mapNewUid = new HashMap<>( );

    private WorkflowUidRenewer( )
    {
    }

    /**
     * Replaces every UUID of the export by a new one
     *
     * @param jsonData
     *            the exported workflow
     */
    static void renew( WorkflowJsonData jsonData )
    {
        WorkflowUidRenewer renewer = new WorkflowUidRenewer( );

        jsonData.getWorkflow( ).setUid( renewer.identity( jsonData.getWorkflow( ).getUid( ) ) );

        for ( State state : jsonData.getStateList( ) )
        {
            state.setUid( renewer.identity( state.getUid( ) ) );
        }

        for ( Action action : jsonData.getActionList( ) )
        {
            action.setUid( renewer.identity( action.getUid( ) ) );
        }

        for ( ITask task : jsonData.getTaskList( ) )
        {
            task.setUid( renewer.identity( task.getUid( ) ) );
        }

        for ( Prerequisite prerequisite : jsonData.getPrerequisiteList( ) )
        {
            prerequisite.setUidPrerequisite( renewer.identity( prerequisite.getUidPrerequisite( ) ) );
        }

        // the references are rewritten once every identity has its new value
        for ( Action action : jsonData.getActionList( ) )
        {
            action.setStrUidStateAfter( renewer.reference( action.getStrUidStateAfter( ) ) );
            action.setStrUidAlternativeStateAfter( renewer.reference( action.getStrUidAlternativeStateAfter( ) ) );

            if ( action.getListUidStateBefore( ) != null )
            {
                action.setListUidStateBefore( renewer.references( action.getListUidStateBefore( ) ) );
            }

            if ( action.getListUidsLinkedAction( ) != null )
            {
                action.setListUidsLinkedAction( renewer.references( action.getListUidsLinkedAction( ).stream( ).collect( Collectors.toList( ) ) ) );
            }
        }

        for ( ITask task : jsonData.getTaskList( ) )
        {
            task.setActionUid( renewer.reference( task.getActionUid( ) ) );

            if ( task.getAction( ) != null )
            {
                task.getAction( ).setUid( renewer.reference( task.getAction( ).getUid( ) ) );
            }
        }

        for ( Prerequisite prerequisite : jsonData.getPrerequisiteList( ) )
        {
            prerequisite.setUidAction( renewer.reference( prerequisite.getUidAction( ) ) );
        }
    }

    /**
     * The new UUID of an element, a fresh one when the element had none
     */
    private String identity( String strUid )
    {
        String strNewUid = UUID.randomUUID( ).toString( );

        if ( strUid != null && !strUid.isEmpty( ) )
        {
            _mapNewUid.put( strUid, strNewUid );
        }

        return strNewUid;
    }

    /**
     * The new UUID of the element a reference designates, the reference itself when it designates nothing of the export
     */
    private String reference( String strUid )
    {
        return ( strUid == null ) ? null : _mapNewUid.getOrDefault( strUid, strUid );
    }

    private List<String> references( List<String> listUid )
    {
        return listUid.stream( ).map( this::reference ).collect( Collectors.toList( ) );
    }
}
