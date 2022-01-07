/*
 * Copyright (c) 2002-2022, City of Paris
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
package fr.paris.lutece.plugins.workflow.modules.archive.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import fr.paris.lutece.plugins.workflow.modules.archive.business.ArchiveConfig;
import fr.paris.lutece.plugins.workflow.modules.archive.business.ArchiveResource;
import fr.paris.lutece.plugins.workflow.modules.archive.business.ArchiveResourceDao;
import fr.paris.lutece.plugins.workflow.modules.archive.business.IArchiveResourceDao;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.test.LuteceTestCase;

public class ArchiveServiceTest extends LuteceTestCase
{
    private ArchiveService _service = SpringContextService.getBean( ArchiveService.BEAN_SERVICE );

    private IArchiveResourceDao _archiveResourceDao = SpringContextService.getBean( ArchiveResourceDao.BEAN_NAME );

    public void testIsResourceUpForArchival_Yes( )
    {
        ArchiveResource archiveResource = new ArchiveResource( );
        archiveResource.setIdResource( 1 );
        archiveResource.setIdTask( 2 );
        archiveResource.setInitialDate( Timestamp.valueOf( LocalDateTime.now( ).minusDays( 20 ) ) );
        archiveResource.setIsArchived( false );
        _archiveResourceDao.insert( archiveResource );

        ArchiveConfig config = new ArchiveConfig( );
        config.setIdTask( 2 );
        config.setDelayArchival( 10 );

        ResourceWorkflow resourceWorkflow = new ResourceWorkflow( );
        resourceWorkflow.setIdResource( 1 );

        assertTrue( _service.isResourceUpForArchival( resourceWorkflow, config ) );

        _archiveResourceDao.delete( resourceWorkflow.getIdResource( ), config.getIdTask( ) );
    }

    public void testIsResourceUpForArchival_No( )
    {
        ArchiveResource archiveResource = new ArchiveResource( );
        archiveResource.setIdResource( 1 );
        archiveResource.setIdTask( 2 );
        archiveResource.setInitialDate( Timestamp.valueOf( LocalDateTime.now( ) ) );
        archiveResource.setIsArchived( false );
        _archiveResourceDao.insert( archiveResource );

        ArchiveConfig config = new ArchiveConfig( );
        config.setIdTask( 2 );
        config.setDelayArchival( 2 );

        ResourceWorkflow resourceWorkflow = new ResourceWorkflow( );
        resourceWorkflow.setIdResource( 1 );

        assertFalse( _service.isResourceUpForArchival( resourceWorkflow, config ) );

        _archiveResourceDao.delete( resourceWorkflow.getIdResource( ), config.getIdTask( ) );
    }

    public void testIsResourceUpForArchival_AlreadyArchived( )
    {
        Timestamp yesterday = Timestamp.valueOf( LocalDateTime.now( ).minusDays( 1 ) );
        ArchiveResource archiveResource = new ArchiveResource( );
        archiveResource.setIdResource( 1 );
        archiveResource.setIdTask( 2 );
        archiveResource.setInitialDate( yesterday );
        archiveResource.setIsArchived( true );
        _archiveResourceDao.insert( archiveResource );

        ArchiveConfig config = new ArchiveConfig( );
        config.setIdTask( 2 );
        config.setDelayArchival( 10 );

        ResourceWorkflow resourceWorkflow = new ResourceWorkflow( );
        resourceWorkflow.setIdResource( 1 );

        assertFalse( _service.isResourceUpForArchival( resourceWorkflow, config ) );

        _archiveResourceDao.delete( resourceWorkflow.getIdResource( ), config.getIdTask( ) );
    }

    public void testIsResourceUpForArchival_Immediate( )
    {
        ArchiveResource archiveResource = new ArchiveResource( );
        archiveResource.setIdResource( 1 );
        archiveResource.setIdTask( 2 );
        archiveResource.setInitialDate( Timestamp.valueOf( LocalDateTime.now( ) ) );
        archiveResource.setIsArchived( false );
        _archiveResourceDao.insert( archiveResource );

        ArchiveConfig config = new ArchiveConfig( );
        config.setIdTask( 2 );
        config.setDelayArchival( 0 );

        ResourceWorkflow resourceWorkflow = new ResourceWorkflow( );
        resourceWorkflow.setIdResource( 1 );

        assertTrue( _service.isResourceUpForArchival( resourceWorkflow, config ) );

        _archiveResourceDao.delete( resourceWorkflow.getIdResource( ), config.getIdTask( ) );
    }
}
