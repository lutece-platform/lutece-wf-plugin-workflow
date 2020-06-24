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
package fr.paris.lutece.plugins.workflow.modules.archive.business;

import fr.paris.lutece.plugins.workflow.modules.archive.ArchivalType;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.util.sql.DAOUtil;

public class ArchiveConfigDao implements ITaskConfigDAO<ArchiveConfig>
{
    public static final String BEAN_NAME = "workflow.taskArchiveConfigDao";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_task,next_state,type_archival,delay_archival "
            + " FROM workflow_task_archive_cf WHERE id_task=?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO  workflow_task_archive_cf  " + "(id_task,next_state,type_archival,delay_archival) VALUES (?,?,?,?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_task_archive_cf " + "SET id_task=?,next_state=?,type_archival=?,delay_archival=? " + " WHERE id_task=?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_task_archive_cf WHERE id_task=? ";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( ArchiveConfig config )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;

            daoUtil.setInt( ++nPos, config.getIdTask( ) );
            daoUtil.setInt( ++nPos, config.getNextState( ) );
            daoUtil.setString( ++nPos, config.getTypeArchival( ).name( ) );
            daoUtil.setInt( ++nPos, config.getDelayArchival( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( ArchiveConfig config )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;

            daoUtil.setInt( ++nPos, config.getIdTask( ) );
            daoUtil.setInt( ++nPos, config.getNextState( ) );
            daoUtil.setString( ++nPos, config.getTypeArchival( ).name( ) );
            daoUtil.setInt( ++nPos, config.getDelayArchival( ) );
            daoUtil.setInt( ++nPos, config.getIdTask( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArchiveConfig load( int idTask )
    {
        ArchiveConfig config = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, idTask );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                int nPos = 0;
                config = new ArchiveConfig( );
                config.setIdTask( daoUtil.getInt( ++nPos ) );
                config.setNextState( daoUtil.getInt( ++nPos ) );
                config.setTypeArchival( ArchivalType.valueOf( daoUtil.getString( ++nPos ) ) );
                config.setDelayArchival( daoUtil.getInt( ++nPos ) );
            }
        }
        return config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int idTask )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, idTask );
            daoUtil.executeUpdate( );
        }
    }
}
