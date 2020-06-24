/*
 * Copyright (c) 2002-2020, City of Paris
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
package fr.paris.lutece.plugins.workflow.modules.archive.business;

import java.sql.Timestamp;

public class ArchiveResource
{
    private int _nIdResource;
    private int _nIdTask;
    private Timestamp _tInitialDate;
    private Timestamp _tArchivalDate;
    private boolean _bIsArchived;

    /**
     * @return the nIdResource
     */
    public int getIdResource( )
    {
        return _nIdResource;
    }

    /**
     * @param nIdResource
     *            the nIdResource to set
     */
    public void setIdResource( int nIdResource )
    {
        _nIdResource = nIdResource;
    }

    /**
     * @return the nIdTask
     */
    public int getIdTask( )
    {
        return _nIdTask;
    }

    /**
     * @param nIdTask
     *            the nIdTask to set
     */
    public void setIdTask( int nIdTask )
    {
        _nIdTask = nIdTask;
    }

    /**
     * @return the tInitialDate
     */
    public Timestamp getInitialDate( )
    {
        if ( _tInitialDate == null )
        {
            return null;
        }
        return new Timestamp( _tInitialDate.getTime( ) );
    }

    /**
     * @param tInitialDate the tInitialDate to set
     */
    public void setInitialDate( Timestamp tInitialDate )
    {
        if ( tInitialDate == null )
        {
            _tInitialDate = null;
        }
        else
        {
            _tInitialDate = new Timestamp( tInitialDate.getTime( ) );
        }
    }

    /**
     * @return the tArchivalDate
     */
    public Timestamp getArchivalDate( )
    {
        if ( _tArchivalDate == null )
        {
            return null;
        }
        return new Timestamp( _tArchivalDate.getTime( ) );
    }

    /**
     * @param tArchivalDate
     *            the tArchivalDate to set
     */
    public void setArchivalDate( Timestamp tArchivalDate )
    {
        if ( tArchivalDate == null )
        {
            _tArchivalDate = null;
        }
        else
        {
            _tArchivalDate = new Timestamp( tArchivalDate.getTime( ) );
        }
    }

    /**
     * @return the bIsArchived
     */
    public boolean isArchived( )
    {
        return _bIsArchived;
    }

    /**
     * @param bIsArchived
     *            the bIsArchived to set
     */
    public void setIsArchived( boolean bIsArchived )
    {
        _bIsArchived = bIsArchived;
    }
}
