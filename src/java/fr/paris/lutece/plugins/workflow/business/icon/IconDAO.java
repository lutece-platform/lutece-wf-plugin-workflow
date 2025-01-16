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
package fr.paris.lutece.plugins.workflow.business.icon;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.icon.IIconDAO;
import fr.paris.lutece.plugins.workflowcore.business.icon.Icon;
import fr.paris.lutece.util.sql.DAOUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * IconDAO
 *
 */
@ApplicationScoped
@Named( "workflow.iconDAO" )
public class IconDAO implements IIconDAO
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_icon,name,mime_type,file_value,width,height" + " FROM workflow_icon WHERE id_icon=?";
    private static final String SQL_QUERY_SELECT_ICON = "SELECT id_icon,name,mime_type,width,height" + " FROM workflow_icon ORDER BY name DESC  ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO  workflow_icon " + "(name,mime_type,file_value,width,height)VALUES(?,?,?,?,?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_icon  SET id_icon=?,name=?,mime_type=?,file_value=?,width=?,height=?" + " WHERE id_icon=?";
    private static final String SQL_QUERY_UPDATE_METADATA = "UPDATE workflow_icon  SET id_icon=?,name=?,width=?,height=?" + " WHERE id_icon=?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_icon  WHERE id_icon=? ";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( Icon icon )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;
            daoUtil.setString( ++nPos, icon.getName( ) );
            daoUtil.setString( ++nPos, icon.getMimeType( ) );
            daoUtil.setBytes( ++nPos, icon.getValue( ) );
            daoUtil.setInt( ++nPos, icon.getWidth( ) );
            daoUtil.setInt( ++nPos, icon.getHeight( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                icon.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( Icon icon )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;

            daoUtil.setInt( ++nPos, icon.getId( ) );
            daoUtil.setString( ++nPos, icon.getName( ) );
            daoUtil.setString( ++nPos, icon.getMimeType( ) );
            daoUtil.setBytes( ++nPos, icon.getValue( ) );
            daoUtil.setInt( ++nPos, icon.getWidth( ) );
            daoUtil.setInt( ++nPos, icon.getHeight( ) );

            daoUtil.setInt( ++nPos, icon.getId( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storeMetadata( Icon icon )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_METADATA, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;

            daoUtil.setInt( ++nPos, icon.getId( ) );
            daoUtil.setString( ++nPos, icon.getName( ) );
            daoUtil.setInt( ++nPos, icon.getWidth( ) );
            daoUtil.setInt( ++nPos, icon.getHeight( ) );
            daoUtil.setInt( ++nPos, icon.getId( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Icon load( int nIdIcon )
    {
        Icon icon = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdIcon );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                int nPos = 0;
                icon = new Icon( );
                icon.setId( daoUtil.getInt( ++nPos ) );
                icon.setName( daoUtil.getString( ++nPos ) );
                icon.setMimeType( daoUtil.getString( ++nPos ) );
                icon.setValue( daoUtil.getBytes( ++nPos ) );
                icon.setWidth( daoUtil.getInt( ++nPos ) );
                icon.setHeight( daoUtil.getInt( ++nPos ) );
            }
        }
        return icon;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdIcon )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdIcon );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Icon> selectAll( )
    {
        Icon icon = null;
        List<Icon> listIcon = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ICON, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                int nPos = 0;
                icon = new Icon( );
                icon.setId( daoUtil.getInt( ++nPos ) );
                icon.setName( daoUtil.getString( ++nPos ) );
                icon.setMimeType( daoUtil.getString( ++nPos ) );
                icon.setWidth( daoUtil.getInt( ++nPos ) );
                icon.setHeight( daoUtil.getInt( ++nPos ) );

                listIcon.add( icon );
            }
        }
        return listIcon;
    }
}
