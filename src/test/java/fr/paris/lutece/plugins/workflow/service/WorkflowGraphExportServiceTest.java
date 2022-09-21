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
package fr.paris.lutece.plugins.workflow.service;

import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.task.TaskType;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;
import fr.paris.lutece.plugins.workflowcore.service.task.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author seboo
 */
public class WorkflowGraphExportServiceTest extends LuteceTestCase
{

    public void testSimpleMd( )
    {

        Workflow wf = new Workflow( );

        wf.setId( 1 );
        wf.setName( "test" );

        State s1 = new State( );
        State s2 = new State( );
        State s3 = new State( );

        s1.setId( 1 );
        s2.setId( 2 );
        s3.setId( 3 );

        s1.setWorkflow( wf );
        s2.setWorkflow( wf );
        s3.setWorkflow( wf );

        s1.setName( "brouillon" );
        s2.setName( "à valider" );
        s3.setName( "validé" );
        
        List<State> listStateBefore1 = new ArrayList<>( );
        List<State> listStateBefore2 = new ArrayList<>( );
        List<State> listStateBefore3 = new ArrayList<>( );
        
        listStateBefore1.add( s1 );
        listStateBefore2.add( s2 );
        listStateBefore3.add( s3 );

        Action a1 = new Action( );
        Action a2 = new Action( );
        Action a3 = new Action( );
        Action a4 = new Action( );

        Task t1 = new MyPrivateTestTask( );
        Task t2 = new MyPrivateTestTask( );
        Task t3 = new MyPrivateTestTask( );

        TaskType notifyTaskType = new TaskType( );
        notifyTaskType.setTitle( "notify" );
        TaskType archiveTaskType = new TaskType( );
        archiveTaskType.setTitle( "archive" );

        t1.setTaskType( notifyTaskType );
        t2.setTaskType( notifyTaskType );
        t3.setTaskType( archiveTaskType );

        a1.setId( 11 );
        a2.setId( 12 );
        a3.setId( 13 );
        a4.setId( 14 );

        a1.setName( "Transmettre" );
        a1.setListStateBefore( listStateBefore1 );
        a1.setStateAfter( s2 );

        a2.setName( "Valider" );
        a2.setListStateBefore( listStateBefore2 );
        a2.setStateAfter( s3 );
        List<ITask> tasksA2 = new ArrayList<>( );
        tasksA2.add( t1 );
        a2.setAllTasks( tasksA2 );

        a3.setName( "Refuser" );
        a3.setListStateBefore( listStateBefore2 );
        a3.setStateAfter( s1 );
        List<ITask> tasksA3 = new ArrayList<>( );
        tasksA3.add( t2 );
        tasksA3.add( t3 );
        a3.setAllTasks( tasksA3 );

        a4.setName( "Enregistrer" );
        a4.setListStateBefore( listStateBefore1 );;
        a4.setStateAfter( s1 );

        List<State> states = new ArrayList<>( );
        states.add( s1 );
        states.add( s2 );
        states.add( s3 );
        wf.setAllStates( states );

        List<Action> actions = new ArrayList<>( );
        actions.add( a1 );
        actions.add( a2 );
        actions.add( a3 );
        actions.add( a4 );
        wf.setAllActions( actions );

        String mdGraph = WorkflowGraphExportService.generate( wf, null );

        assertNotNull( mdGraph );

    }

    /**
     * MyPrivateTestTask
     */
    private class MyPrivateTestTask extends SimpleTask
    {

        @Override
        public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
        {
        }

        @Override
        public String getTitle( Locale locale )
        {
            return "test";
        }
    }
}
