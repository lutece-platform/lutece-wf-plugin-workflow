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
package fr.paris.lutece.plugins.workflow.business.prerequisite;

import fr.paris.lutece.plugins.workflowcore.business.prerequisite.Prerequisite;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO for prerequisite
 */
public class PrerequisiteDAO implements IPrerequisiteDAO
{
    /**
     * The name of the bean of this service
     */
    public static final String BEAN_NAME = "workflow.prerequisiteDAO";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_prerequisite, id_action, prerequisite_type FROM workflow_prerequisite WHERE id_prerequisite = ?";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_prerequisite SET id_action = ?, prerequisite_type = ? WHERE id_prerequisite = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO workflow_prerequisite(id_action,prerequisite_type) VALUES(?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_prerequisite WHERE id_prerequisite = ?";
    private static final String SQL_QUERY_FIND_BY_ID_ACTION = "SELECT id_prerequisite, id_action, prerequisite_type FROM workflow_prerequisite WHERE id_action = ?";

    /**
     * {@inheritDoc}
     */
    @Override
    public Prerequisite findByPrimaryKey( int nIdPrerequisite, Plugin plugin )
    {
        Prerequisite prerequisite = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin ) )
        {
            daoUtil.setInt( 1, nIdPrerequisite );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                prerequisite = new Prerequisite( );
                prerequisite.setIdPrerequisite( daoUtil.getInt( 1 ) );
                prerequisite.setIdAction( daoUtil.getInt( 2 ) );
                prerequisite.setPrerequisiteType( daoUtil.getString( 3 ) );
            }
        }
        return prerequisite;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void create( Prerequisite prerequisite, Plugin plugin )
    {

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            daoUtil.setInt( 1, prerequisite.getIdAction( ) );
            daoUtil.setString( 2, prerequisite.getPrerequisiteType( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                prerequisite.setIdPrerequisite( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update( Prerequisite prerequisite, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            daoUtil.setInt( 1, prerequisite.getIdAction( ) );
            daoUtil.setString( 2, prerequisite.getPrerequisiteType( ) );
            daoUtil.setInt( 3, prerequisite.getIdPrerequisite( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove( int nIdPrerequisite, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdPrerequisite );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Prerequisite> findByIdAction( int nIdAction, Plugin plugin )
    {
        List<Prerequisite> listPrerequisites = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_ID_ACTION, plugin ) )
        {
            daoUtil.setInt( 1, nIdAction );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                Prerequisite prerequisite = new Prerequisite( );
                prerequisite.setIdPrerequisite( daoUtil.getInt( 1 ) );
                prerequisite.setIdAction( daoUtil.getInt( 2 ) );
                prerequisite.setPrerequisiteType( daoUtil.getString( 3 ) );
                listPrerequisites.add( prerequisite );
            }
        }
        return listPrerequisites;
    }
}
