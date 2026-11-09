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
package fr.paris.lutece.plugins.workflow.service.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.paris.lutece.plugins.workflow.util.MockTask;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.prerequisite.Prerequisite;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import junit.framework.TestCase;

public class WorkflowUidRenewerTest extends TestCase
{
    private static final String UUID_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    public void testEveryIdentityIsNewAndEveryReferenceFollows( )
    {
        Workflow workflow = new Workflow( );
        workflow.setUid( "wf" );

        State state1 = state( "s1" );
        State state2 = state( "s2" );

        Action action1 = new Action( );
        action1.setUid( "a1" );
        action1.setStrUidStateAfter( "s2" );
        action1.setStrUidAlternativeStateAfter( "s1" );
        action1.setListUidStateBefore( new ArrayList<>( Arrays.asList( "s1", "s2" ) ) );
        action1.setListUidsLinkedAction( new ArrayList<>( Arrays.asList( "a2", "outside" ) ) );

        Action action2 = new Action( );
        action2.setUid( "a2" );
        action2.setStrUidStateAfter( "s1" );

        ITask task = new MockTask( );
        task.setUid( "t1" );
        task.setActionUid( "a1" );
        Action actionOfTask = new Action( );
        actionOfTask.setUid( "a1" );
        task.setAction( actionOfTask );

        ITask taskWithoutUid = new MockTask( );
        taskWithoutUid.setActionUid( "a2" );

        Prerequisite prerequisite = new Prerequisite( );
        prerequisite.setUidPrerequisite( "p1" );
        prerequisite.setUidAction( "a2" );

        WorkflowJsonData jsonData = new WorkflowJsonData( );
        jsonData.setWorkflow( workflow );
        jsonData.setStateList( Arrays.asList( state1, state2 ) );
        jsonData.setActionList( Arrays.asList( action1, action2 ) );
        jsonData.setTaskList( Arrays.asList( task, taskWithoutUid ) );
        jsonData.setPrerequisiteList( Arrays.asList( prerequisite ) );

        WorkflowUidRenewer.renew( jsonData );

        List<String> listIdentity = Arrays.asList( workflow.getUid( ), state1.getUid( ), state2.getUid( ), action1.getUid( ), action2.getUid( ),
                task.getUid( ), taskWithoutUid.getUid( ), prerequisite.getUidPrerequisite( ) );
        Set<String> setOld = new HashSet<>( Arrays.asList( "wf", "s1", "s2", "a1", "a2", "t1", "p1" ) );

        for ( String strUid : listIdentity )
        {
            assertTrue( strUid + " is not a UUID", strUid.matches( UUID_PATTERN ) );
            assertFalse( strUid + " was kept", setOld.contains( strUid ) );
        }
        assertEquals( "two elements share a UUID", listIdentity.size( ), new HashSet<>( listIdentity ).size( ) );

        assertEquals( state2.getUid( ), action1.getStrUidStateAfter( ) );
        assertEquals( state1.getUid( ), action1.getStrUidAlternativeStateAfter( ) );
        assertEquals( Arrays.asList( state1.getUid( ), state2.getUid( ) ), action1.getListUidStateBefore( ) );
        assertEquals( Arrays.asList( action2.getUid( ), "outside" ), new ArrayList<>( action1.getListUidsLinkedAction( ) ) );
        assertEquals( state1.getUid( ), action2.getStrUidStateAfter( ) );
        assertNull( action2.getStrUidAlternativeStateAfter( ) );

        assertEquals( action1.getUid( ), task.getActionUid( ) );
        assertEquals( action1.getUid( ), task.getAction( ).getUid( ) );
        assertEquals( action2.getUid( ), taskWithoutUid.getActionUid( ) );
        assertEquals( action2.getUid( ), prerequisite.getUidAction( ) );
    }

    private static State state( String strUid )
    {
        State state = new State( );
        state.setUid( strUid );
        return state;
    }
}
