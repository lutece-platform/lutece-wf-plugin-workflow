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
package fr.paris.lutece.plugins.workflow.service.workflowimport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.paris.lutece.plugins.workflow.business.workflowimport.ImportElement;
import fr.paris.lutece.plugins.workflow.service.json.WorkflowJsonData;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.prerequisite.Prerequisite;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;

/**
 * Builds the comparable view of every element of an exported workflow
 */
final class ImportItems
{
    private ImportItems( )
    {
    }

    /**
     * @param jsonData
     *            the export of a workflow, read from a file or built from the database
     * @param bWithIds
     *            true when the objects come from the database and carry their identifiers
     * @return the elements, by kind, in the order of the export
     */
    static Map<ImportElement, List<ImportItem>> of( WorkflowJsonData jsonData, boolean bWithIds )
    {
        Map<ImportElement, List<ImportItem>> mapItems = new EnumMap<>( ImportElement.class );
        Map<String, String> mapActionName = new HashMap<>( );

        List<ImportItem> listState = new ArrayList<>( );
        for ( State state : jsonData.getStateList( ) )
        {
            listState.add( new ImportItem( ImportElement.STATE, state.getUid( ), bWithIds ? state.getId( ) : -1, state.getName( ), null )
                    .field( "name", state.getName( ) ).field( "description", state.getDescription( ) ).field( "initial", state.isInitialState( ) )
                    .field( "requiredWorkgroupAssigned", state.isRequiredWorkgroupAssigned( ) ).field( "order", state.getOrder( ) ) );
        }
        mapItems.put( ImportElement.STATE, listState );

        List<ImportItem> listAction = new ArrayList<>( );
        for ( Action action : jsonData.getActionList( ) )
        {
            mapActionName.put( action.getUid( ), action.getName( ) );
            listAction.add( new ImportItem( ImportElement.ACTION, action.getUid( ), bWithIds ? action.getId( ) : -1, action.getName( ), null )
                    .field( "name", action.getName( ) ).field( "description", action.getDescription( ) ).field( "automatic", action.isAutomaticState( ) )
                    .field( "massAction", action.isMassAction( ) ).field( "automaticReflexive", action.isAutomaticReflexiveAction( ) )
                    .field( "order", action.getOrder( ) ).field( "stateAfter", action.getStrUidStateAfter( ) )
                    .field( "alternativeStateAfter", action.getStrUidAlternativeStateAfter( ) )
                    .field( "statesBefore", sorted( action.getListUidStateBefore( ) ) )
                    .field( "linkedActions", sorted( action.getListUidsLinkedAction( ) ) ) );
        }
        mapItems.put( ImportElement.ACTION, listAction );

        List<ImportItem> listTask = new ArrayList<>( );
        for ( ITask task : jsonData.getTaskList( ) )
        {
            String strType = ( task.getTaskType( ) == null ) ? null : task.getTaskType( ).getKey( );
            listTask.add( new ImportItem( ImportElement.TASK, task.getUid( ), bWithIds ? task.getId( ) : -1,
                    mapActionName.get( task.getActionUid( ) ) + " · " + strType + " #" + task.getOrder( ), task.getActionUid( ) ).field( "type", strType )
                    .field( "order", task.getOrder( ) ).field( "action", task.getActionUid( ) ) );
        }
        mapItems.put( ImportElement.TASK, listTask );

        List<ImportItem> listPrerequisite = new ArrayList<>( );
        for ( Prerequisite prerequisite : jsonData.getPrerequisiteList( ) )
        {
            listPrerequisite.add( new ImportItem( ImportElement.PREREQUISITE, prerequisite.getUidPrerequisite( ),
                    bWithIds ? prerequisite.getIdPrerequisite( ) : -1, mapActionName.get( prerequisite.getUidAction( ) ) + " · "
                            + prerequisite.getPrerequisiteType( ), prerequisite.getUidAction( ) )
                    .field( "type", prerequisite.getPrerequisiteType( ) ).field( "action", prerequisite.getUidAction( ) ) );
        }
        mapItems.put( ImportElement.PREREQUISITE, listPrerequisite );

        return mapItems;
    }

    private static String sorted( Collection<String> listUid )
    {
        return ( listUid == null ) ? "" : listUid.stream( ).sorted( ).collect( Collectors.joining( "," ) );
    }
}
