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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import fr.paris.lutece.plugins.workflow.business.workflowimport.ImportElement;
import fr.paris.lutece.portal.service.util.AppException;

/**
 * One element of a workflow, as the import compares it : its UUID, what it refers to by UUID, and the fields an update carries.
 * <p>
 * The same view is built from the imported file and from the workflow it may update, so that both give the same fingerprint for the same content.
 */
public final class ImportItem
{
    /** The fields holding UUIDs of other elements */
    private static final Set<String> REFERENCE_FIELDS = Collections.unmodifiableSet(
            new HashSet<>( Arrays.asList( "stateAfter", "alternativeStateAfter", "statesBefore", "linkedActions", "action" ) ) );

    private final ImportElement _element;
    private final String _strUid;
    private final int _nId;
    private final String _strLabel;
    private final String _strParentUid;
    private final Map<String, String> _mapField = new TreeMap<>( );

    ImportItem( ImportElement element, String strUid, int nId, String strLabel, String strParentUid )
    {
        _element = element;
        _strUid = strUid;
        _nId = nId;
        _strLabel = strLabel;
        _strParentUid = strParentUid;
    }

    ImportItem field( String strName, Object value )
    {
        _mapField.put( strName, ( value == null ) ? "" : String.valueOf( value ) );
        return this;
    }

    public ImportElement getElement( )
    {
        return _element;
    }

    public String getUid( )
    {
        return _strUid;
    }

    /**
     * @return the database identifier on the target, -1 for an element of the imported file
     */
    public int getId( )
    {
        return _nId;
    }

    public String getLabel( )
    {
        return _strLabel;
    }

    /**
     * @return the UUID of the action a task or a prerequisite belongs to, null for a state or an action
     */
    public String getParentUid( )
    {
        return _strParentUid;
    }

    /**
     * @return the fields an update carries, by name, in a stable order
     */
    public Map<String, String> getFields( )
    {
        return _mapField;
    }

    /**
     * The same element with its references written in other UUIDs : an element of the target paired with an element of the file refers to target UUIDs,
     * which the update replaces by those of the file
     *
     * @param mapUid
     *            the UUID each target UUID becomes
     * @return the translated element
     */
    ImportItem translated( Map<String, String> mapUid )
    {
        ImportItem item = new ImportItem( _element, _strUid, _nId, _strLabel, translate( _strParentUid, mapUid ) );
        for ( Map.Entry<String, String> entry : _mapField.entrySet( ) )
        {
            String strValue = entry.getValue( );
            if ( REFERENCE_FIELDS.contains( entry.getKey( ) ) )
            {
                strValue = Arrays.stream( strValue.split( "," ) ).filter( s -> !s.isEmpty( ) ).map( s -> translate( s, mapUid ) ).sorted( )
                        .collect( Collectors.joining( "," ) );
            }
            item._mapField.put( entry.getKey( ), strValue );
        }
        return item;
    }

    private static String translate( String strUid, Map<String, String> mapUid )
    {
        return ( strUid == null ) ? null : mapUid.getOrDefault( strUid, strUid );
    }

    /**
     * The fingerprint of the content, written at each import : a later difference means the element was modified since
     *
     * @return the SHA-256 of the kind of element and of its fields
     */
    public String getFingerprint( )
    {
        StringBuilder sbContent = new StringBuilder( _element.name( ) );
        for ( Map.Entry<String, String> entry : _mapField.entrySet( ) )
        {
            // the length prefix keeps two different contents from writing the same string
            sbContent.append( '\n' ).append( entry.getKey( ) ).append( '=' ).append( entry.getValue( ).length( ) ).append( ':' ).append( entry.getValue( ) );
        }

        try
        {
            byte [ ] hash = MessageDigest.getInstance( "SHA-256" ).digest( sbContent.toString( ).getBytes( StandardCharsets.UTF_8 ) );
            StringBuilder sbHex = new StringBuilder( );
            for ( byte b : hash )
            {
                sbHex.append( String.format( "%02x", b ) );
            }
            return sbHex.toString( );
        }
        catch( NoSuchAlgorithmException e )
        {
            throw new AppException( "SHA-256 is not available", e );
        }
    }
}
