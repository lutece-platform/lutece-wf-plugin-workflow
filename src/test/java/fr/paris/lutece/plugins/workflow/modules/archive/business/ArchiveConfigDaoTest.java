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
package fr.paris.lutece.plugins.workflow.modules.archive.business;

import fr.paris.lutece.plugins.workflow.modules.archive.ArchivalType;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.test.LuteceTestCase;

public class ArchiveConfigDaoTest extends LuteceTestCase
{
    ITaskConfigDAO<ArchiveConfig> _dao = new ArchiveConfigDao( );

    public void testCRUD( )
    {
        ArchiveConfig config = new ArchiveConfig( );
        config.setIdTask( 1 );
        config.setNextState( 2 );
        config.setTypeArchival( ArchivalType.DELETE );
        config.setDelayArchival( 10 );

        _dao.insert( config );
        ArchiveConfig load = _dao.load( 1 );
        assertEquals( config.getNextState( ), load.getNextState( ) );
        assertEquals( config.getTypeArchival( ), load.getTypeArchival( ) );
        assertEquals( config.getDelayArchival( ), load.getDelayArchival( ) );

        config.setDelayArchival( 22 );
        _dao.store( config );
        load = _dao.load( 1 );
        assertEquals( config.getDelayArchival( ), load.getDelayArchival( ) );

        _dao.delete( 1 );
        load = _dao.load( 1 );
        assertNull( load );
    }
}
