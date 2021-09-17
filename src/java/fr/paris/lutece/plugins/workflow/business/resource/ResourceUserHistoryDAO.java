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
package fr.paris.lutece.plugins.workflow.business.resource;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.resource.IResourceUserHistoryDAO;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceUserHistory;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for ResourceUserHistory objects
 */
public final class ResourceUserHistoryDAO implements IResourceUserHistoryDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT  id_history, user_access_code, email, first_name, last_name, realm FROM workflow_resource_user_history WHERE id_history = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO workflow_resource_user_history ( id_history, user_access_code, email, first_name, last_name, realm ) VALUES ( ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_resource_user_history WHERE id_history = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_resource_user_history SET user_access_code = ?, email = ?, first_name = ?, last_name = ?, realm = ? WHERE id_history = ?";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( ResourceUserHistory resourceUserHistory )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, WorkflowUtils.getPlugin( ) ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, resourceUserHistory.getIdHistory( ) );
            daoUtil.setString( nIndex++, resourceUserHistory.getUserAccessCode( ) );
            daoUtil.setString( nIndex++, resourceUserHistory.getEmail( ) );
            daoUtil.setString( nIndex++, resourceUserHistory.getFirstName( ) );
            daoUtil.setString( nIndex++, resourceUserHistory.getLastName( ) );
            daoUtil.setString( nIndex++, resourceUserHistory.getRealm( ) );

            daoUtil.executeUpdate( );
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ResourceUserHistory load( int nKey )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );
            ResourceUserHistory resourceUserHistory = null;

            if ( daoUtil.next( ) )
            {
                resourceUserHistory = new ResourceUserHistory( );
                int nIndex = 1;
                resourceUserHistory.setIdHistory( daoUtil.getInt( nIndex++ ) );
                resourceUserHistory.setUserAccessCode( daoUtil.getString( nIndex++ ) );
                resourceUserHistory.setEmail( daoUtil.getString( nIndex++ ) );
                resourceUserHistory.setFirstName( daoUtil.getString( nIndex++ ) );
                resourceUserHistory.setLastName( daoUtil.getString( nIndex++ ) );
                resourceUserHistory.setRealm( daoUtil.getString( nIndex ) );
            }

            daoUtil.free( );
            return resourceUserHistory;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeUpdate( );
            daoUtil.free( );
        }
    }

    @Override
    public void store( ResourceUserHistory resourceUserHistory )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;

            daoUtil.setString( ++nPos, resourceUserHistory.getUserAccessCode( ) );
            daoUtil.setString( ++nPos, resourceUserHistory.getEmail( ) );
            daoUtil.setString( ++nPos, resourceUserHistory.getFirstName( ) );
            daoUtil.setString( ++nPos, resourceUserHistory.getLastName( ) );
            daoUtil.setString( ++nPos, resourceUserHistory.getRealm( ) );

            daoUtil.setInt( ++nPos, resourceUserHistory.getIdHistory( ) );

            daoUtil.executeUpdate( );
        }
    }
}
