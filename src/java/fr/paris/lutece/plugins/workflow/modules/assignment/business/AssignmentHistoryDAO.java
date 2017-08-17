/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.assignment.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * class AssignmentHistoryDAO
 *
 */
public class AssignmentHistoryDAO implements IAssignmentHistoryDAO
{
    private static final String SQL_QUERY_SELECT = "SELECT id_history,id_task,workgroup_key "
            + "FROM workflow_assignment_history WHERE id_history=? AND id_task=? ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO  workflow_assignment_history " + "(id_history,id_task,workgroup_key)VALUES(?,?,?)";
    private static final String SQL_QUERY_DELETE_BY_HISTORY = "DELETE FROM workflow_assignment_history  WHERE id_history=? AND id_task=?";
    private static final String SQL_QUERY_DELETE_BY_TASK = "DELETE FROM workflow_assignment_history  WHERE  id_task=?";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( AssignmentHistory history, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        int nPos = 0;

        daoUtil.setInt( ++nPos, history.getIdResourceHistory( ) );
        daoUtil.setInt( ++nPos, history.getIdTask( ) );
        daoUtil.setString( ++nPos, history.getWorkgroup( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AssignmentHistory> selectByHistory( int nIdHistory, int nIdTask, Plugin plugin )
    {
        AssignmentHistory assignmentValue = null;

        List<AssignmentHistory> listAssignmentValue = new ArrayList<AssignmentHistory>( );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        int nPos = 0;
        daoUtil.setInt( ++nPos, nIdHistory );
        daoUtil.setInt( ++nPos, nIdTask );

        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            nPos = 0;
            assignmentValue = new AssignmentHistory( );
            assignmentValue.setIdResourceHistory( daoUtil.getInt( ++nPos ) );
            assignmentValue.setIdTask( daoUtil.getInt( ++nPos ) );
            assignmentValue.setWorkgroup( daoUtil.getString( ++nPos ) );

            listAssignmentValue.add( assignmentValue );
        }

        daoUtil.free( );

        return listAssignmentValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByHistory( int nIdHistory, int nIdTask, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_HISTORY, plugin );
        int nPos = 0;
        daoUtil.setInt( ++nPos, nIdHistory );
        daoUtil.setInt( ++nPos, nIdTask );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByTask( int nIdTask, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_TASK, plugin );
        int nPos = 0;
        daoUtil.setInt( ++nPos, nIdTask );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }
}
