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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.paris.lutece.plugins.workflow.business.workflowimport.ImportElement;
import fr.paris.lutece.plugins.workflow.service.json.WorkflowJsonData;
import fr.paris.lutece.plugins.workflow.util.MockTask;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.prerequisite.Prerequisite;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.task.TaskType;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import junit.framework.TestCase;

public class WorkflowImportPlannerTest extends TestCase
{
    private static final Map<String, Integer> NO_CHOICE = Collections.emptyMap( );

    /**
     * A workflow imported earlier, then changed in the file : a state renamed, an action added, an unused action and a used state removed
     */
    public void testUpdateOfAWorkflowImportedEarlier( )
    {
        WorkflowJsonData target = data( state( 1, "s-new", "New" ), state( 2, "s-done", "Done" ), state( 3, "s-old", "Old" ) );
        addAction( target, action( 10, "a-close", "Close", "s-done", "s-new" ) );
        addAction( target, action( 11, "a-unused", "Unused", "s-done", "s-new" ) );

        WorkflowJsonData file = data( state( -1, "s-new", "Created" ), state( -1, "s-done", "Done" ) );
        addAction( file, action( -1, "a-close", "Close", "s-done", "s-new" ) );
        addAction( file, action( -1, "a-reopen", "Reopen", "s-new", "s-done" ) );

        ImportContext context = new ImportContext( ).setResourceByState( map( 3, 5 ) );
        WorkflowImportPlan plan = WorkflowImportPlanner.plan( file, target, context, NO_CHOICE );

        ImportLine renamed = line( plan, ImportElement.STATE, "s-new" );
        assertEquals( ImportLine.Status.MATCHED, renamed.getStatus( ) );
        assertTrue( "the rename is a change", renamed.isChanged( ) );
        assertFalse( line( plan, ImportElement.STATE, "s-done" ).isChanged( ) );

        ImportLine kept = targetLine( plan, ImportElement.STATE, 3 );
        assertEquals( "a state holding resources is kept", ImportLine.Status.ABSENT_USED, kept.getStatus( ) );
        assertEquals( 5, kept.getUsage( ) );
        assertTrue( kept.isDanger( ) );

        assertEquals( ImportLine.Status.NEW, line( plan, ImportElement.ACTION, "a-reopen" ).getStatus( ) );
        assertEquals( ImportLine.Status.ABSENT_UNUSED, targetLine( plan, ImportElement.ACTION, 11 ).getStatus( ) );
        assertFalse( "every UUID matched : not a first alignment", plan.isFirstAlignment( ) );
        // the target still has an action no UUID matched : the new one is offered for pairing, in danger since nothing was found for it
        assertEquals( 1, plan.getPairingLines( ).size( ) );
        assertTrue( line( plan, ImportElement.ACTION, "a-reopen" ).isWithoutCounterpart( ) );
        assertTrue( line( plan, ImportElement.ACTION, "a-reopen" ).isDanger( ) );
        assertFalse( "no candidate among the states : a matched state is not offered", line( plan, ImportElement.STATE, "s-new" ).isDanger( ) );
    }

    /**
     * An element modified on the target since the last import is flagged, an untouched one is not
     */
    public void testModificationOnTheTargetSinceTheLastImport( )
    {
        WorkflowJsonData file = data( state( -1, "s-a", "A" ), state( -1, "s-b", "B" ) );
        Map<String, String> mapHashAtImport = new HashMap<>( );
        for ( ImportItem item : ImportItems.of( file, false ).get( ImportElement.STATE ) )
        {
            mapHashAtImport.put( item.getUid( ), item.getFingerprint( ) );
        }

        // since that import, someone renamed B on the target
        WorkflowJsonData target = data( state( 1, "s-a", "A" ), state( 2, "s-b", "B renamed on the target" ) );
        Map<Integer, String> mapStored = new HashMap<>( );
        mapStored.put( 1, mapHashAtImport.get( "s-a" ) );
        mapStored.put( 2, mapHashAtImport.get( "s-b" ) );

        WorkflowImportPlan plan = WorkflowImportPlanner.plan( file, target, new ImportContext( ).setStoredHashes( ImportElement.STATE, mapStored ),
                NO_CHOICE );

        assertFalse( line( plan, ImportElement.STATE, "s-a" ).isModifiedOnTarget( ) );
        assertTrue( line( plan, ImportElement.STATE, "s-b" ).isModifiedOnTarget( ) );
        assertTrue( line( plan, ImportElement.STATE, "s-b" ).isDanger( ) );
    }

    /**
     * Two workflows built separately share no UUID : the names propose the pairs, a mismatch waits for a choice, and a choice pairs it
     */
    public void testFirstAlignmentByNameThenByHand( )
    {
        WorkflowJsonData target = data( state( 1, "t-new", "New" ), state( 2, "t-closed", "Closed" ) );
        addAction( target, action( 10, "t-close", "Close", "t-closed", "t-new" ) );
        WorkflowJsonData file = data( state( -1, "f-new", "new " ), state( -1, "f-done", "Done" ) );
        addAction( file, action( -1, "f-close", "Close", "f-done", "f-new" ) );

        WorkflowImportPlan plan = WorkflowImportPlanner.plan( file, target, new ImportContext( ), NO_CHOICE );

        assertTrue( plan.isFirstAlignment( ) );
        ImportLine byName = line( plan, ImportElement.STATE, "f-new" );
        assertEquals( "a name differing only by case and spaces is proposed", ImportLine.Status.PAIRED, byName.getStatus( ) );
        assertEquals( 1, byName.getTargetItem( ).getId( ) );
        assertFalse( byName.isDanger( ) );
        assertEquals( ImportLine.Status.NEW, line( plan, ImportElement.STATE, "f-done" ).getStatus( ) );
        assertEquals( ImportLine.Status.ABSENT_UNUSED, targetLine( plan, ImportElement.STATE, 2 ).getStatus( ) );
        assertEquals( 3, plan.getPairingLines( ).size( ) );

        // the administrator says Done is Closed
        Map<String, Integer> mapChoice = new HashMap<>( );
        mapChoice.put( "f-done", 2 );
        plan = WorkflowImportPlanner.plan( file, target, new ImportContext( ), mapChoice );

        ImportLine byHand = line( plan, ImportElement.STATE, "f-done" );
        assertEquals( ImportLine.Status.PAIRED, byHand.getStatus( ) );
        assertTrue( byHand.isPairedByHand( ) );
        assertTrue( "the names differ : the pair stays visible", byHand.isDanger( ) );
        assertEquals( 0, plan.count( ImportLine.Status.ABSENT_UNUSED ) );
        // the action refers to the paired states : translated, it does not change
        assertFalse( line( plan, ImportElement.ACTION, "f-close" ).isChanged( ) );

        // and a choice can ask for a creation instead of the proposal
        mapChoice.put( "f-new", WorkflowImportPlanner.CHOICE_NEW );
        plan = WorkflowImportPlanner.plan( file, target, new ImportContext( ), mapChoice );
        assertEquals( ImportLine.Status.NEW, line( plan, ImportElement.STATE, "f-new" ).getStatus( ) );
    }

    /**
     * Tasks and prerequisites are paired within the action their action is paired with, by type, preferably at the same rank
     */
    public void testTasksArePairedWithinTheirAction( )
    {
        WorkflowJsonData target = data( state( 1, "t-s", "S" ) );
        addAction( target, action( 10, "t-a", "Send", "t-s", "t-s" ) );
        addTask( target, task( 100, "t-t1", "t-a", "notify", 1 ) );
        addTask( target, task( 101, "t-t2", "t-a", "notify", 2 ) );
        addPrerequisite( target, prerequisite( 200, "t-p", "t-a", "duration" ) );

        WorkflowJsonData file = data( state( -1, "f-s", "S" ) );
        addAction( file, action( -1, "f-a", "Send", "f-s", "f-s" ) );
        addTask( file, task( -1, "f-t2", "f-a", "notify", 2 ) );
        addTask( file, task( -1, "f-t3", "f-a", "comment", 3 ) );
        addPrerequisite( file, prerequisite( -1, "f-p", "f-a", "duration" ) );

        WorkflowImportPlan plan = WorkflowImportPlanner.plan( file, target, new ImportContext( ), NO_CHOICE );

        assertEquals( "same type, same rank", 101, line( plan, ImportElement.TASK, "f-t2" ).getTargetItem( ).getId( ) );
        assertEquals( "no task of that type", ImportLine.Status.NEW, line( plan, ImportElement.TASK, "f-t3" ).getStatus( ) );
        assertEquals( ImportLine.Status.ABSENT_UNUSED, targetLine( plan, ImportElement.TASK, 100 ).getStatus( ) );
        assertEquals( 200, line( plan, ImportElement.PREREQUISITE, "f-p" ).getTargetItem( ).getId( ) );
    }

    /**
     * An action the file removed but the history keeps stays, with its tasks and the states it refers to
     */
    public void testAKeptActionKeepsWhatItNeeds( )
    {
        WorkflowJsonData target = data( state( 1, "s-a", "A" ), state( 2, "s-b", "B" ) );
        addAction( target, action( 10, "a-old", "Old", "s-b", "s-a" ) );
        addTask( target, task( 100, "t-old", "a-old", "notify", 1 ) );
        WorkflowJsonData file = data( state( -1, "s-a", "A" ) );

        ImportContext context = new ImportContext( ).setHistoryByAction( map( 10, 3 ) );
        WorkflowImportPlan plan = WorkflowImportPlanner.plan( file, target, context, NO_CHOICE );

        assertEquals( ImportLine.Status.ABSENT_USED, targetLine( plan, ImportElement.ACTION, 10 ).getStatus( ) );
        assertEquals( 3, targetLine( plan, ImportElement.ACTION, 10 ).getUsage( ) );
        assertEquals( "the state the kept action leads to is kept", ImportLine.Status.ABSENT_USED, targetLine( plan, ImportElement.STATE, 2 ).getStatus( ) );
        assertEquals( "the task of the kept action is kept", ImportLine.Status.ABSENT_USED, targetLine( plan, ImportElement.TASK, 100 ).getStatus( ) );
    }

    // WorkflowJsonData hands out copies of its lists
    private static void addAction( WorkflowJsonData data, Action action )
    {
        List<Action> list = data.getActionList( );
        list.add( action );
        data.setActionList( list );
    }

    private static void addTask( WorkflowJsonData data, ITask task )
    {
        List<ITask> list = data.getTaskList( );
        list.add( task );
        data.setTaskList( list );
    }

    private static void addPrerequisite( WorkflowJsonData data, Prerequisite prerequisite )
    {
        List<Prerequisite> list = data.getPrerequisiteList( );
        list.add( prerequisite );
        data.setPrerequisiteList( list );
    }

    // --- data builders

    private static WorkflowJsonData data( State... states )
    {
        WorkflowJsonData data = new WorkflowJsonData( );
        Workflow workflow = new Workflow( );
        workflow.setUid( "wf" );
        data.setWorkflow( workflow );
        data.setStateList( new ArrayList<>( Arrays.asList( states ) ) );
        data.setActionList( new ArrayList<>( ) );
        data.setTaskList( new ArrayList<>( ) );
        data.setPrerequisiteList( new ArrayList<>( ) );
        return data;
    }

    private static State state( int nId, String strUid, String strName )
    {
        State state = new State( );
        state.setId( nId );
        state.setUid( strUid );
        state.setName( strName );
        state.setInitialState( false );
        state.setRequiredWorkgroupAssigned( false );
        return state;
    }

    private static Action action( int nId, String strUid, String strName, String strStateAfter, String strStateBefore )
    {
        Action action = new Action( );
        action.setId( nId );
        action.setUid( strUid );
        action.setName( strName );
        action.setStrUidStateAfter( strStateAfter );
        action.setListUidStateBefore( new ArrayList<>( Arrays.asList( strStateBefore ) ) );
        return action;
    }

    private static ITask task( int nId, String strUid, String strActionUid, String strType, int nOrder )
    {
        MockTask task = new MockTask( );
        task.setId( nId );
        task.setUid( strUid );
        task.setActionUid( strActionUid );
        TaskType type = new TaskType( );
        type.setKey( strType );
        task.setTaskType( type );
        task.setOrder( nOrder );
        return task;
    }

    private static Prerequisite prerequisite( int nId, String strUid, String strActionUid, String strType )
    {
        Prerequisite prerequisite = new Prerequisite( );
        prerequisite.setIdPrerequisite( nId );
        prerequisite.setUidPrerequisite( strUid );
        prerequisite.setUidAction( strActionUid );
        prerequisite.setPrerequisiteType( strType );
        return prerequisite;
    }

    private static Map<Integer, Integer> map( int nKey, int nValue )
    {
        Map<Integer, Integer> map = new HashMap<>( );
        map.put( nKey, nValue );
        return map;
    }

    private static ImportLine line( WorkflowImportPlan plan, ImportElement element, String strFileUid )
    {
        return plan.getLines( element ).stream( ).filter( l -> l.getFileItem( ) != null && strFileUid.equals( l.getFileItem( ).getUid( ) ) ).findFirst( )
                .orElseThrow( ( ) -> new AssertionError( "no line for " + strFileUid ) );
    }

    private static ImportLine targetLine( WorkflowImportPlan plan, ImportElement element, int nTargetId )
    {
        return plan.getLines( element ).stream( ).filter( l -> l.getFileItem( ) == null && l.getTargetItem( ).getId( ) == nTargetId ).findFirst( )
                .orElseThrow( ( ) -> new AssertionError( "no absent line for " + nTargetId ) );
    }

}
