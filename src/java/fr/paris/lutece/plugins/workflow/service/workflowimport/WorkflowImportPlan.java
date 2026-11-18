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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.paris.lutece.plugins.workflow.business.workflowimport.ImportElement;

/**
 * The outcome of the analysis of an import into an existing workflow : what is updated, created, removed and kept, shown before anything is written
 */
public final class WorkflowImportPlan
{
    private final Map<ImportElement, List<ImportLine>> _mapLines = new EnumMap<>( ImportElement.class );
    private final Map<ImportElement, List<ImportItem>> _mapCandidates = new EnumMap<>( ImportElement.class );
    private final Timestamp _lastImportDate;

    WorkflowImportPlan( Timestamp lastImportDate )
    {
        _lastImportDate = lastImportDate;
        for ( ImportElement element : ImportElement.values( ) )
        {
            _mapLines.put( element, new ArrayList<>( ) );
            _mapCandidates.put( element, new ArrayList<>( ) );
        }
    }

    void add( ImportLine line )
    {
        _mapLines.get( line.getElement( ) ).add( line );
    }

    void addCandidate( ImportItem item )
    {
        _mapCandidates.get( item.getElement( ) ).add( item );
    }

    /**
     * @return the lines of one kind of element
     */
    public List<ImportLine> getLines( ImportElement element )
    {
        return Collections.unmodifiableList( _mapLines.get( element ) );
    }

    /**
     * @return every line, states first, then actions, tasks and prerequisites
     */
    public List<ImportLine> getAllLines( )
    {
        return _mapLines.values( ).stream( ).flatMap( List::stream ).collect( Collectors.toList( ) );
    }

    /**
     * @return the elements of the target an element of the file may be paired with : those no UUID matched
     */
    public List<ImportItem> getCandidates( ImportElement element )
    {
        return Collections.unmodifiableList( _mapCandidates.get( element ) );
    }

    /**
     * @return the lines of the correspondence table : the elements of the file no UUID matched, when the target has elements they could be paired with
     */
    public List<ImportLine> getPairingLines( )
    {
        return getAllLines( ).stream( )
                .filter( line -> line.getStatus( ) == ImportLine.Status.PAIRED
                        || ( line.getStatus( ) == ImportLine.Status.NEW && !_mapCandidates.get( line.getElement( ) ).isEmpty( ) ) )
                .collect( Collectors.toList( ) );
    }

    /**
     * @return the number of lines with this status
     */
    public long count( ImportLine.Status status )
    {
        return getAllLines( ).stream( ).filter( line -> line.getStatus( ) == status ).count( );
    }

    /**
     * @return the number of elements created
     */
    public long getCreatedCount( )
    {
        return count( ImportLine.Status.NEW );
    }

    /**
     * @return the number of elements removed
     */
    public long getRemovedCount( )
    {
        return count( ImportLine.Status.ABSENT_UNUSED );
    }

    /**
     * @return the number of elements the file removed but which are kept, being used
     */
    public long getKeptCount( )
    {
        return count( ImportLine.Status.ABSENT_USED );
    }

    /**
     * @return the number of lines the update actually changes, on top of those it creates or removes
     */
    public long countChanged( )
    {
        return getAllLines( ).stream( ).filter( ImportLine::isChanged ).count( );
    }

    /**
     * @return true when no element was matched by UUID : the first import of this file into this workflow
     */
    public boolean isFirstAlignment( )
    {
        return count( ImportLine.Status.MATCHED ) == 0;
    }

    /**
     * @return true when a line asks for attention
     */
    public boolean hasDanger( )
    {
        return getAllLines( ).stream( ).anyMatch( ImportLine::isDanger );
    }

    /**
     * @return the date of the last import into the target, null when it was never updated by an import
     */
    public Timestamp getLastImportDate( )
    {
        return _lastImportDate;
    }
}
