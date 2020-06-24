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
import java.time.LocalDateTime;

import fr.paris.lutece.test.LuteceTestCase;

public class ArchiveResourceDaoTest extends LuteceTestCase
{
    private ArchiveResourceDao _dao = new ArchiveResourceDao( );
    private static final int ID_RESOURCE = 1;
    private static final int ID_TASK = 2;
    
    @Override
    protected void tearDown( ) throws Exception
    {
        _dao.delete( ID_RESOURCE, ID_TASK );
        super.tearDown( );
    }

    public void testCRUD( )
    {
        Timestamp now = Timestamp.valueOf( LocalDateTime.now( ) );

        ArchiveResource archiveResource = new ArchiveResource( );
        archiveResource.setIdResource( ID_RESOURCE );
        archiveResource.setIdTask( ID_TASK );
        archiveResource.setIsArchived( true );

        _dao.insert( archiveResource );
        ArchiveResource load = _dao.load( archiveResource.getIdResource( ), archiveResource.getIdTask( ) );
        assertEquals( archiveResource.getIdResource( ), load.getIdResource( ) );
        assertEquals( archiveResource.getIdTask( ), load.getIdTask( ) );
        assertNull( load.getArchivalDate( ) );
        assertNull( load.getInitialDate( ) );
        assertEquals( archiveResource.isArchived( ), load.isArchived( ) );

        archiveResource.setArchivalDate( now );
        archiveResource.setInitialDate( now );
        _dao.store( archiveResource );
        load = _dao.load( archiveResource.getIdResource( ), archiveResource.getIdTask( ) );
        assertNotNull( archiveResource.getArchivalDate( ) );
        assertNotNull( archiveResource.getInitialDate( ) );

        _dao.delete( archiveResource.getIdResource( ), archiveResource.getIdTask( ) );
        load = _dao.load( archiveResource.getIdResource( ), archiveResource.getIdTask( ) );
        assertNull( load );
    }
}
