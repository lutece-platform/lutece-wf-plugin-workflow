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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import fr.paris.lutece.plugins.workflow.business.workflowimport.ImportElement;
import fr.paris.lutece.plugins.workflow.service.json.WorkflowJsonData;

/**
 * Compares an imported workflow with the workflow it may update, without writing anything
 */
public final class WorkflowImportPlanner
{
    /** a choice of the correspondence table meaning : create the element rather than pair it */
    public static final int CHOICE_NEW = -1;

    private static final List<ImportElement> ORDER = Arrays.asList( ImportElement.STATE, ImportElement.ACTION, ImportElement.TASK,
            ImportElement.PREREQUISITE );
    private static final List<String> STATE_REFERENCES = Arrays.asList( "stateAfter", "alternativeStateAfter", "statesBefore" );

    private WorkflowImportPlanner( )
    {
    }

    /**
     * @param fileData
     *            the imported workflow
     * @param targetData
     *            the export of the workflow it may update, built from the database
     * @param context
     *            the fingerprints and the usage of the target
     * @param mapChoice
     *            the choices of the correspondence table : for the UUID of an element of the file, the identifier of the element of the target it updates,
     *            or {@link #CHOICE_NEW}
     * @return the plan
     */
    public static WorkflowImportPlan plan( WorkflowJsonData fileData, WorkflowJsonData targetData, ImportContext context, Map<String, Integer> mapChoice )
    {
        Map<ImportElement, List<ImportItem>> mapFile = ImportItems.of( fileData, false );
        Map<ImportElement, List<ImportItem>> mapTarget = ImportItems.of( targetData, true );

        WorkflowImportPlan plan = new WorkflowImportPlan( context.getLastImportDate( ) );
        // the UUID of the file each paired element of the target corresponds to, and the reverse
        Map<String, String> mapTargetToFileUid = new HashMap<>( );
        Map<String, String> mapFileToTargetUid = new HashMap<>( );
        List<ImportLine> listAbsent = new ArrayList<>( );

        for ( ImportElement element : ORDER )
        {
            List<ImportItem> listRemaining = new ArrayList<>( mapTarget.get( element ) );
            List<ImportLine> listLine = new ArrayList<>( );
            List<ImportItem> listWaiting = new ArrayList<>( );

            for ( ImportItem fileItem : mapFile.get( element ) )
            {
                Integer nChoice = mapChoice.get( fileItem.getUid( ) );
                ImportItem chosen = ( nChoice == null ) ? null : take( listRemaining, item -> item.getId( ) == nChoice );
                ImportItem sameUid = ( fileItem.getUid( ) == null ) ? null : take( listRemaining, item -> fileItem.getUid( ).equals( item.getUid( ) ) );

                if ( nChoice != null && nChoice == CHOICE_NEW && sameUid == null )
                {
                    listLine.add( new ImportLine( element, fileItem, null, ImportLine.Status.NEW, true ) );
                }
                else if ( sameUid != null )
                {
                    // a UUID match is never undone by a choice : the table only offers the elements no UUID matched
                    if ( chosen != null )
                    {
                        listRemaining.add( chosen );
                    }
                    listLine.add( new ImportLine( element, fileItem, sameUid, ImportLine.Status.MATCHED, false ) );
                }
                else if ( chosen != null )
                {
                    listLine.add( new ImportLine( element, fileItem, chosen, ImportLine.Status.PAIRED, true ) );
                }
                else
                {
                    listWaiting.add( fileItem );
                    listLine.add( null );
                }
            }

            // the elements of the target the table can offer : those no UUID matched
            for ( ImportItem item : mapTarget.get( element ) )
            {
                if ( listLine.stream( ).noneMatch( line -> line != null && line.getStatus( ) == ImportLine.Status.MATCHED && line.getTargetItem( ) == item ) )
                {
                    plan.addCandidate( item );
                }
            }

            for ( ImportItem fileItem : listWaiting )
            {
                ImportItem proposed = propose( fileItem, listRemaining, mapFileToTargetUid );
                ImportLine line = ( proposed == null ) ? new ImportLine( element, fileItem, null, ImportLine.Status.NEW, false )
                        : new ImportLine( element, fileItem, proposed, ImportLine.Status.PAIRED, false );
                listLine.set( listLine.indexOf( null ), line );
            }

            for ( ImportLine line : listLine )
            {
                if ( line.getTargetItem( ) != null )
                {
                    mapTargetToFileUid.put( line.getTargetItem( ).getUid( ), line.getFileItem( ).getUid( ) );
                    mapFileToTargetUid.put( line.getFileItem( ).getUid( ), line.getTargetItem( ).getUid( ) );
                }
                plan.add( line );
            }

            for ( ImportItem item : listRemaining )
            {
                ImportLine line = new ImportLine( element, null, item, isUsed( element, item, context ) ? ImportLine.Status.ABSENT_USED
                        : ImportLine.Status.ABSENT_UNUSED, false );
                listAbsent.add( line );
            }
        }

        for ( ImportLine line : keepWhatKeptActionsNeed( listAbsent ) )
        {
            plan.add( line );
        }

        for ( ImportLine line : plan.getAllLines( ) )
        {
            describe( line, context, mapTargetToFileUid );
            line.setWithoutCounterpart( line.getStatus( ) == ImportLine.Status.NEW && !line.isPairedByHand( )
                    && !plan.getCandidates( line.getElement( ) ).isEmpty( ) );
        }

        return plan;
    }

    /**
     * The element of the target an element of the file is proposed to update
     */
    private static ImportItem propose( ImportItem fileItem, List<ImportItem> listRemaining, Map<String, String> mapFileToTargetUid )
    {
        if ( fileItem.getParentUid( ) == null )
        {
            return take( listRemaining, item -> ImportNames.same( item.getLabel( ), fileItem.getLabel( ) ) );
        }

        // a task or a prerequisite is proposed within the element of the target its action is paired with, of the same type, preferably at the same rank
        String strTargetParent = mapFileToTargetUid.get( fileItem.getParentUid( ) );
        if ( strTargetParent == null )
        {
            return null;
        }
        String strType = fileItem.getFields( ).get( "type" );
        String strOrder = fileItem.getFields( ).get( "order" );
        ImportItem sameRank = take( listRemaining, item -> strTargetParent.equals( item.getParentUid( ) ) && Objects.equals( strType, item.getFields( ).get( "type" ) )
                && strOrder != null && strOrder.equals( item.getFields( ).get( "order" ) ) );
        return ( sameRank != null ) ? sameRank
                : take( listRemaining, item -> strTargetParent.equals( item.getParentUid( ) ) && Objects.equals( strType, item.getFields( ).get( "type" ) ) );
    }

    private static boolean isUsed( ImportElement element, ImportItem item, ImportContext context )
    {
        switch( element )
        {
            case STATE:
                return context.getResourceByState( ).getOrDefault( item.getId( ), 0 ) > 0;
            case ACTION:
                return context.getHistoryByAction( ).getOrDefault( item.getId( ), 0 ) > 0;
            default:
                return false;
        }
    }

    /**
     * An action the file removed but the history keeps stays with what it needs : its tasks, its prerequisites and the states it refers to
     */
    private static List<ImportLine> keepWhatKeptActionsNeed( List<ImportLine> listAbsent )
    {
        Set<String> setKeptAction = new HashSet<>( );
        Set<String> setNeededState = new HashSet<>( );
        for ( ImportLine line : listAbsent )
        {
            if ( line.getElement( ) == ImportElement.ACTION && line.getStatus( ) == ImportLine.Status.ABSENT_USED )
            {
                setKeptAction.add( line.getTargetItem( ).getUid( ) );
                for ( String strField : STATE_REFERENCES )
                {
                    setNeededState.addAll( Arrays.asList( line.getTargetItem( ).getFields( ).getOrDefault( strField, "" ).split( "," ) ) );
                }
            }
        }

        List<ImportLine> listResult = new ArrayList<>( );
        for ( ImportLine line : listAbsent )
        {
            ImportItem item = line.getTargetItem( );
            boolean bNeeded = ( line.getElement( ) == ImportElement.STATE && setNeededState.contains( item.getUid( ) ) )
                    || ( item.getParentUid( ) != null && setKeptAction.contains( item.getParentUid( ) ) );
            listResult.add( ( bNeeded && line.getStatus( ) == ImportLine.Status.ABSENT_UNUSED )
                    ? new ImportLine( line.getElement( ), null, item, ImportLine.Status.ABSENT_USED, false )
                    : line );
        }
        return listResult;
    }

    private static void describe( ImportLine line, ImportContext context, Map<String, String> mapTargetToFileUid )
    {
        ImportItem target = line.getTargetItem( );
        if ( target == null )
        {
            return;
        }

        if ( line.getFileItem( ) != null )
        {
            line.setChanged( !line.getFileItem( ).getFingerprint( ).equals( target.translated( mapTargetToFileUid ).getFingerprint( ) ) );
        }

        String strStored = context.getStoredHashes( line.getElement( ) ).get( target.getId( ) );
        line.setModifiedOnTarget( strStored != null && !strStored.equals( target.getFingerprint( ) ) );

        if ( line.getElement( ) == ImportElement.STATE )
        {
            line.setUsage( context.getResourceByState( ).getOrDefault( target.getId( ), 0 ) );
        }
        else if ( line.getElement( ) == ImportElement.ACTION )
        {
            line.setUsage( context.getHistoryByAction( ).getOrDefault( target.getId( ), 0 ) );
            line.setLastExecution( context.getLastExecutionByAction( ).get( target.getId( ) ) );
        }
    }

    /**
     * Removes and returns the first element matching
     */
    private static ImportItem take( List<ImportItem> list, Predicate<ImportItem> predicate )
    {
        for ( int i = 0; i < list.size( ); i++ )
        {
            if ( predicate.test( list.get( i ) ) )
            {
                return list.remove( i );
            }
        }
        return null;
    }
}
