/*
 * Copyright (c) 2002-2021, City of Paris
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
package fr.paris.lutece.plugins.workflow.business.task;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.task.ITaskDAO;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskFactory;
import fr.paris.lutece.util.sql.DAOUtil;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 *
 * TaskDAO
 *
 */
public class TaskDAO implements ITaskDAO
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT task_type_key,id_task,id_action, display_order" + " FROM workflow_task WHERE id_task=?";
    private static final String SQL_QUERY_SELECT_STATE_BY_ID_ACTION = "SELECT task_type_key,id_task,id_action, display_order "
            + " FROM workflow_task WHERE id_action=? ORDER BY display_order";
    private static final String SQL_QUERY_INSERT = "INSERT INTO  workflow_task " + "(task_type_key,id_action, display_order)VALUES(?,?,?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_task  SET id_task=?,task_type_key=?,id_action=?,display_order=?" + " WHERE id_task=?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_task  WHERE id_task=? ";
    private static final String SQL_QUERY_FIND_MAXIMUM_ORDER_BY_ACTION = "SELECT MAX(display_order) FROM workflow_task WHERE id_action=?";
    private static final String SQL_QUERY_DECREMENT_ORDER = "UPDATE workflow_task SET display_order = display_order - 1 WHERE display_order > ? AND id_action=? ";
    private static final String SQL_QUERY_TASKS_WITH_ORDER_BETWEEN = "SELECT task_type_key,id_task,id_action, display_order FROM workflow_task WHERE (display_order BETWEEN ? AND ?) AND id_action=?";
    private static final String SQL_QUERY_TASKS_AFTER_ORDER = "SELECT task_type_key,id_task,id_action, display_order FROM workflow_task WHERE display_order >=? AND id_action=?";
    private static final String SQL_QUERY_SELECT_TASK_FOR_ORDER_INIT = "SELECT task_type_key,id_task,id_action, display_order "
            + " FROM workflow_task WHERE id_action=? ORDER BY id_task";
    @Inject
    private ITaskFactory _taskFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( ITask task )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;
            daoUtil.setString( ++nPos, task.getTaskType( ).getKey( ) );
            daoUtil.setInt( ++nPos, task.getAction( ).getId( ) );
            daoUtil.setInt( ++nPos, task.getOrder( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                task.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( ITask task )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;

            daoUtil.setInt( ++nPos, task.getId( ) );
            daoUtil.setString( ++nPos, task.getTaskType( ).getKey( ) );
            daoUtil.setInt( ++nPos, task.getAction( ).getId( ) );
            daoUtil.setInt( ++nPos, task.getOrder( ) );
            daoUtil.setInt( ++nPos, task.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITask load( int nIdTask, Locale locale )
    {
        ITask task = null;
        Action action = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdTask );

            daoUtil.executeQuery( );

            int nPos = 0;

            if ( daoUtil.next( ) )
            {
                task = _taskFactory.newTask( daoUtil.getString( ++nPos ), locale );
                task.setId( daoUtil.getInt( ++nPos ) );
                action = new Action( );
                action.setId( daoUtil.getInt( ++nPos ) );
                task.setOrder( daoUtil.getInt( ++nPos ) );
                task.setAction( action );
            }
        }
        return task;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdTask )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdTask );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ITask> selectTaskByIdAction( int nIdAction, Locale locale )
    {
        ITask task = null;
        Action action = null;
        List<ITask> listTask = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_STATE_BY_ID_ACTION, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdAction );

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                int nPos = 0;
                task = _taskFactory.newTask( daoUtil.getString( ++nPos ), locale );
                task.setId( daoUtil.getInt( ++nPos ) );
                action = new Action( );
                action.setId( daoUtil.getInt( ++nPos ) );
                task.setAction( action );
                task.setOrder( daoUtil.getInt( ++nPos ) );

                listTask.add( task );
            }
        }
        return listTask;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findMaximumOrderByActionId( int nIdAction )
    {
        int nMaximumOrder = 0;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_MAXIMUM_ORDER_BY_ACTION, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdAction );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                nMaximumOrder = daoUtil.getInt( 1 );
            }
        }
        return nMaximumOrder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void decrementOrderByOne( int nOrder, int nIdAction )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DECREMENT_ORDER, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nOrder );
            daoUtil.setInt( 2, nIdAction );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ITask> findTasksBetweenOrders( int nOrder1, int nOrder2, int nIdAction, Locale locale )
    {
        List<ITask> listTask = new ArrayList<>( );
        int nPos = 0;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_TASKS_WITH_ORDER_BETWEEN, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nOrder1 );
            daoUtil.setInt( 2, nOrder2 );
            daoUtil.setInt( 3, nIdAction );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                ITask task = null;
                Action action = null;
                nPos = 0;
                task = _taskFactory.newTask( daoUtil.getString( ++nPos ), locale );
                task.setId( daoUtil.getInt( ++nPos ) );
                action = new Action( );
                action.setId( daoUtil.getInt( ++nPos ) );
                task.setAction( action );
                task.setOrder( daoUtil.getInt( ++nPos ) );

                listTask.add( task );
            }
        }
        return listTask;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ITask> findTasksAfterOrder( int nOrder, int nIdAction, Locale locale )
    {
        List<ITask> listTask = new ArrayList<>( );
        int nPos = 0;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_TASKS_AFTER_ORDER, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nOrder );
            daoUtil.setInt( 2, nIdAction );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                ITask task = null;
                Action action = null;
                nPos = 0;
                task = _taskFactory.newTask( daoUtil.getString( ++nPos ), locale );
                task.setId( daoUtil.getInt( ++nPos ) );
                action = new Action( );
                action.setId( daoUtil.getInt( ++nPos ) );
                task.setAction( action );
                task.setOrder( daoUtil.getInt( ++nPos ) );

                listTask.add( task );
            }
        }
        return listTask;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ITask> findTasksForOrderInit( int nIdAction, Locale locale )
    {
        List<ITask> listTask = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_TASK_FOR_ORDER_INIT, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdAction );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                ITask task = null;
                Action action = null;
                int nPos = 0;
                task = _taskFactory.newTask( daoUtil.getString( ++nPos ), locale );
                task.setId( daoUtil.getInt( ++nPos ) );
                action = new Action( );
                action.setId( daoUtil.getInt( ++nPos ) );
                task.setAction( action );
                task.setOrder( daoUtil.getInt( ++nPos ) );

                listTask.add( task );
            }
        }
        return listTask;
    }
}
