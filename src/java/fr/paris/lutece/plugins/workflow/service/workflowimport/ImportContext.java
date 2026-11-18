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
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import fr.paris.lutece.plugins.workflow.business.workflowimport.ImportElement;

/**
 * What the analysis reads on the target besides its content : the fingerprints of the last import, and what uses its elements
 */
public final class ImportContext
{
    private final Map<ImportElement, Map<Integer, String>> _mapStoredHash = new EnumMap<>( ImportElement.class );
    private Map<Integer, Integer> _mapResourceByState = new HashMap<>( );
    private Map<Integer, Integer> _mapHistoryByAction = new HashMap<>( );
    private Map<Integer, Timestamp> _mapLastExecutionByAction = new HashMap<>( );
    private Timestamp _lastImportDate;

    public Map<Integer, String> getStoredHashes( ImportElement element )
    {
        return _mapStoredHash.getOrDefault( element, Collections.emptyMap( ) );
    }

    public ImportContext setStoredHashes( ImportElement element, Map<Integer, String> mapHash )
    {
        _mapStoredHash.put( element, mapHash );
        return this;
    }

    public Map<Integer, Integer> getResourceByState( )
    {
        return _mapResourceByState;
    }

    public ImportContext setResourceByState( Map<Integer, Integer> mapResourceByState )
    {
        _mapResourceByState = mapResourceByState;
        return this;
    }

    public Map<Integer, Integer> getHistoryByAction( )
    {
        return _mapHistoryByAction;
    }

    public ImportContext setHistoryByAction( Map<Integer, Integer> mapHistoryByAction )
    {
        _mapHistoryByAction = mapHistoryByAction;
        return this;
    }

    public Map<Integer, Timestamp> getLastExecutionByAction( )
    {
        return _mapLastExecutionByAction;
    }

    public ImportContext setLastExecutionByAction( Map<Integer, Timestamp> mapLastExecutionByAction )
    {
        _mapLastExecutionByAction = mapLastExecutionByAction;
        return this;
    }

    public Timestamp getLastImportDate( )
    {
        return _lastImportDate;
    }

    public ImportContext setLastImportDate( Timestamp lastImportDate )
    {
        _lastImportDate = lastImportDate;
        return this;
    }
}
