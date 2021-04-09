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
package fr.paris.lutece.plugins.workflow.modules.archive.business;

import java.sql.Types;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.util.sql.DAOUtil;

public class ArchiveResourceDao implements IArchiveResourceDao
{
    public static final String BEAN_NAME = "workflow.archiveResourceDao";
    private static final String SQL_QUERY_INSERT = "INSERT INTO workflow_task_archive_resource (id_resource, id_task, initial_date, archival_date, is_archived) VALUES (?,?,?,?,?) ";
    private static final String SQL_QUERY_SELECT_BY_ID = "SELECT id_resource, id_task, initial_date, archival_date, is_archived FROM workflow_task_archive_resource WHERE id_resource=? AND id_task=? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_task_archive_resource SET id_resource=?, id_task=?, initial_date=?, archival_date=?, is_archived=? WHERE id_resource=? AND id_task=? ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_task_archive_resource WHERE id_resource=? AND id_task=? ";

    @Override
    public void insert( ArchiveResource archiveResource )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;
            daoUtil.setInt( ++nPos, archiveResource.getIdResource( ) );
            daoUtil.setInt( ++nPos, archiveResource.getIdTask( ) );

            if ( archiveResource.getInitialDate( ) != null )
            {
                daoUtil.setTimestamp( ++nPos, archiveResource.getInitialDate( ) );
            }
            else
            {
                daoUtil.setNull( ++nPos, Types.TIMESTAMP );
            }

            if ( archiveResource.getArchivalDate( ) != null )
            {
                daoUtil.setTimestamp( ++nPos, archiveResource.getArchivalDate( ) );
            }
            else
            {
                daoUtil.setNull( ++nPos, Types.TIMESTAMP );
            }
            daoUtil.setBoolean( ++nPos, archiveResource.isArchived( ) );

            daoUtil.executeUpdate( );
        }
    }

    @Override
    public ArchiveResource load( int nIdResource, int nIdTask )
    {
        ArchiveResource archiveResource = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;
            daoUtil.setInt( ++nPos, nIdResource );
            daoUtil.setInt( ++nPos, nIdTask );

            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                archiveResource = dataToObject( daoUtil );
            }
        }
        return archiveResource;
    }

    @Override
    public void store( ArchiveResource archiveResource )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;
            daoUtil.setInt( ++nPos, archiveResource.getIdResource( ) );
            daoUtil.setInt( ++nPos, archiveResource.getIdTask( ) );

            if ( archiveResource.getInitialDate( ) != null )
            {
                daoUtil.setTimestamp( ++nPos, archiveResource.getInitialDate( ) );
            }
            else
            {
                daoUtil.setNull( ++nPos, Types.TIMESTAMP );
            }

            if ( archiveResource.getArchivalDate( ) != null )
            {
                daoUtil.setTimestamp( ++nPos, archiveResource.getArchivalDate( ) );
            }
            else
            {
                daoUtil.setNull( ++nPos, Types.TIMESTAMP );
            }
            daoUtil.setBoolean( ++nPos, archiveResource.isArchived( ) );

            daoUtil.setInt( ++nPos, archiveResource.getIdResource( ) );
            daoUtil.setInt( ++nPos, archiveResource.getIdTask( ) );

            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void delete( int nIdResource, int nIdTask )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;
            daoUtil.setInt( ++nPos, nIdResource );
            daoUtil.setInt( ++nPos, nIdTask );
            daoUtil.executeUpdate( );
        }
    }

    private ArchiveResource dataToObject( DAOUtil daoUtil )
    {
        int nPos = 0;
        ArchiveResource archiveResource = new ArchiveResource( );
        archiveResource.setIdResource( daoUtil.getInt( ++nPos ) );
        archiveResource.setIdTask( daoUtil.getInt( ++nPos ) );

        if ( daoUtil.getObject( ++nPos ) != null )
        {
            archiveResource.setInitialDate( daoUtil.getTimestamp( nPos ) );
        }

        if ( daoUtil.getObject( ++nPos ) != null )
        {
            archiveResource.setArchivalDate( daoUtil.getTimestamp( nPos ) );
        }

        archiveResource.setIsArchived( daoUtil.getBoolean( ++nPos ) );
        return archiveResource;
    }
}
