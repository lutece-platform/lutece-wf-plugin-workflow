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
package fr.paris.lutece.plugins.workflow.modules.archive.web;

import java.util.Locale;

import org.springframework.mock.web.MockHttpServletRequest;

import fr.paris.lutece.plugins.workflow.modules.archive.business.ArchiveConfig;
import fr.paris.lutece.plugins.workflow.modules.archive.business.ArchiveConfigDao;
import fr.paris.lutece.plugins.workflow.util.MockTask;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.test.LuteceTestCase;

public class ArchiveTaskComponentTest extends LuteceTestCase
{
    private ArchiveTaskComponent _taskComponent;

    private ITaskConfigDAO<ArchiveConfig> _taskConfigDAO = new ArchiveConfigDao( );
    private Locale _locale = Locale.getDefault( );

    @Override
    protected void setUp( ) throws Exception
    {
        super.setUp( );
        _taskComponent = SpringContextService.getBean( "workflow.archiveTaskComponent" );
    }
    
    public void testDoSaveConfig( )
    {
        ITask task = new MockTask( );

        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( "archival_type", "test" );
        request.addParameter( "archival_delay", "5" );
        request.addParameter( "next_state", "2" );
        try
        {
            String res = _taskComponent.doSaveConfig( request, _locale, task );
            assertNull( res );

        }
        catch( Exception e )
        {
            e.printStackTrace( );
        }

        ArchiveConfig config = _taskConfigDAO.load( task.getId( ) );
        assertEquals( "test", config.getTypeArchival( ) );
        assertEquals( 5, config.getDelayArchival( ) );
        assertEquals( 2, config.getNextState( ) );

        _taskConfigDAO.delete( task.getId( ) );
    }
}
