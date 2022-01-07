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
package fr.paris.lutece.plugins.workflow.business.workflow;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.test.LuteceTestCase;

public class WorkflowDAOTest extends LuteceTestCase
{

    private WorkflowDAO _dao = new WorkflowDAO( );

    public void testCRUD( )
    {
        Workflow wf1 = new Workflow( );
        wf1.setName( "wf1" );
        wf1.setCreationDate( WorkflowUtils.getCurrentTimestamp( ) );
        wf1.setDescription( "strDescription" );
        wf1.setEnabled( true );
        wf1.setWorkgroup( "group" );

        _dao.insert( wf1 );
        Workflow wf2 = _dao.load( wf1.getId( ) );
        assertEquals( wf1.getName( ), wf2.getName( ) );

        wf1.setName( "wf11" );
        _dao.store( wf1 );
        wf2 = _dao.load( wf1.getId( ) );
        assertEquals( wf1.getName( ), wf2.getName( ) );

        _dao.delete( wf1.getId( ) );
        wf2 = _dao.load( wf1.getId( ) );
        assertNull( wf2 );
    }
}
