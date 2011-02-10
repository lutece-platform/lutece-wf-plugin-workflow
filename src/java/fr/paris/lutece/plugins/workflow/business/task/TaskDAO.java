/*
 * Copyright (c) 2002-2009, Mairie de Paris
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

import fr.paris.lutece.plugins.workflow.service.WorkflowService;
import fr.paris.lutece.portal.business.workflow.Action;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 *
 *class  TaskDAO
 *
 */
public class TaskDAO implements ITaskDAO
{
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_task ) FROM workflow_task";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT task_type_key,id_task,id_action" +
        " FROM workflow_task WHERE id_task=?";
    private static final String SQL_QUERY_SELECT_STATE_BY_ID_ACTION = "SELECT task_type_key,id_task,id_action" +
        " FROM workflow_task WHERE id_action=? ORDER BY id_task";
    private static final String SQL_QUERY_INSERT = "INSERT INTO  workflow_task " +
        "(id_task,task_type_key,id_action)VALUES(?,?,?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_task  SET id_task=?,task_type_key=?,id_action=?" +
        " WHERE id_task=?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_task  WHERE id_task=? ";

    /**
         * Generates a new primary key
         *
         * @param plugin the plugin
         * @return The new primary key
         */
    private int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery(  );

        int nKey;

        if ( !daoUtil.next(  ) )
        {
            // if the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;
        daoUtil.free(  );

        return nKey;
    }

    /* (non-Javadoc)
         * @see fr.paris.lutece.plugins.workflow.business.task.ITaskDAO#insert(fr.paris.lutece.plugins.workflow.business.task.ITask, fr.paris.lutece.portal.service.plugin.Plugin)
         */
    public synchronized void insert( ITask task, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        int nPos = 0;
        task.setId( newPrimaryKey( plugin ) );

        daoUtil.setInt( ++nPos, task.getId(  ) );
        daoUtil.setString( ++nPos, task.getTaskType(  ).getKey(  ) );
        daoUtil.setInt( ++nPos, task.getAction(  ).getId(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /* (non-Javadoc)
         * @see fr.paris.lutece.plugins.workflow.business.task.ITaskDAO#store(fr.paris.lutece.plugins.workflow.business.task.ITask, fr.paris.lutece.portal.service.plugin.Plugin)
         */
    public void store( ITask task, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        int nPos = 0;

        daoUtil.setInt( ++nPos, task.getId(  ) );
        daoUtil.setString( ++nPos, task.getTaskType(  ).getKey(  ) );
        daoUtil.setInt( ++nPos, task.getAction(  ).getId(  ) );
        daoUtil.setInt( ++nPos, task.getId(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITaskDAO#load(int, fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public ITask load( int nIdTask, Plugin plugin, Locale locale )
    {
        ITask task = null;
        Action action = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );

        daoUtil.setInt( 1, nIdTask );

        daoUtil.executeQuery(  );

        int nPos = 0;

        if ( daoUtil.next(  ) )
        {
            task = WorkflowService.getInstance(  ).getTaskInstance( daoUtil.getString( ++nPos ), locale );
            task.setId( daoUtil.getInt( ++nPos ) );
            action = new Action(  );
            action.setId( daoUtil.getInt( ++nPos ) );
            task.setAction( action );
        }

        daoUtil.free(  );

        return task;
    }

    /* (non-Javadoc)
         * @see fr.paris.lutece.plugins.workflow.business.task.ITaskDAO#delete(int, fr.paris.lutece.portal.service.plugin.Plugin)
         */
    public void delete( int nIdTask, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );

        daoUtil.setInt( 1, nIdTask );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITaskDAO#selectTaskByIdAction(int, fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public List<ITask> selectTaskByIdAction( int nIdAction, Plugin plugin, Locale locale )
    {
        ITask task = null;
        Action action = null;
        List<ITask> listTask = new ArrayList<ITask>(  );

        int nPos = 0;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_STATE_BY_ID_ACTION, plugin );

        daoUtil.setInt( 1, nIdAction );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            nPos = 0;
            task = WorkflowService.getInstance(  ).getTaskInstance( daoUtil.getString( ++nPos ), locale );
            task.setId( daoUtil.getInt( ++nPos ) );
            action = new Action(  );
            action.setId( daoUtil.getInt( ++nPos ) );
            task.setAction( action );

            listTask.add( task );
        }

        daoUtil.free(  );

        return listTask;
    }
}
