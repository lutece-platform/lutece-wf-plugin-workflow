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
package fr.paris.lutece.plugins.workflow.business.workflow;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.workflow.IWorkflowDAO;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.business.workflow.WorkflowFilter;
import fr.paris.lutece.util.sql.DAOUtil;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * WorkflowDAO
 *
 */
public class WorkflowDAO implements IWorkflowDAO
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_workflow,name,description,creation_date,is_enabled,workgroup_key"
            + " FROM workflow_workflow WHERE id_workflow=?";
    private static final String SQL_QUERY_SELECT_BY_FILTER = "SELECT id_workflow,name,description,creation_date,is_enabled,workgroup_key"
            + " FROM workflow_workflow ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO  workflow_workflow "
            + "(name,description,creation_date,is_enabled,workgroup_key)VALUES(?,?,?,?,?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_workflow  SET id_workflow=?,name=?,description=?,is_enabled=?,workgroup_key=?"
            + " WHERE id_workflow=?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_workflow  WHERE id_workflow=? ";
    private static final String SQL_FILTER_IS_ENABLED = " is_enabled = ? ";
    private static final String SQL_FILTER_WORKGROUP = " workgroup_key = ? ";
    private static final String SQL_FILTRE_NAME = " name = ? ";
    private static final String SQL_ORDER_BY_DATE_CREATION = " ORDER BY creation_date DESC ";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( Workflow workflow )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, WorkflowUtils.getPlugin( ) );
        try
        {
            int nPos = 0;
            daoUtil.setString( ++nPos, workflow.getName( ) );
            daoUtil.setString( ++nPos, workflow.getDescription( ) );
            daoUtil.setTimestamp( ++nPos, workflow.getCreationDate( ) );
            daoUtil.setBoolean( ++nPos, workflow.isEnabled( ) );
            daoUtil.setString( ++nPos, workflow.getWorkgroup( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                workflow.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
        finally
        {
            daoUtil.free( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( Workflow workflow )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, WorkflowUtils.getPlugin( ) );

        int nPos = 0;

        daoUtil.setInt( ++nPos, workflow.getId( ) );
        daoUtil.setString( ++nPos, workflow.getName( ) );
        daoUtil.setString( ++nPos, workflow.getDescription( ) );
        daoUtil.setBoolean( ++nPos, workflow.isEnabled( ) );
        daoUtil.setString( ++nPos, workflow.getWorkgroup( ) );

        daoUtil.setInt( ++nPos, workflow.getId( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Workflow load( int nIdWorkflow )
    {
        Workflow workflow = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, WorkflowUtils.getPlugin( ) );

        daoUtil.setInt( 1, nIdWorkflow );

        daoUtil.executeQuery( );

        int nPos = 0;

        if ( daoUtil.next( ) )
        {
            workflow = new Workflow( );
            workflow.setId( daoUtil.getInt( ++nPos ) );
            workflow.setName( daoUtil.getString( ++nPos ) );
            workflow.setDescription( daoUtil.getString( ++nPos ) );
            workflow.setCreationDate( daoUtil.getTimestamp( ++nPos ) );
            workflow.setEnabled( daoUtil.getBoolean( ++nPos ) );
            workflow.setWorkgroup( daoUtil.getString( ++nPos ) );
        }

        daoUtil.free( );

        return workflow;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdWorkflow )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowUtils.getPlugin( ) );

        daoUtil.setInt( 1, nIdWorkflow );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Workflow> selectWorkflowByFilter( WorkflowFilter filter )
    {
        List<Workflow> listWorkflow = new ArrayList<Workflow>( );
        Workflow workflow = null;
        List<String> listStrFilter = new ArrayList<String>( );

        if ( filter.containsIsEnabled( ) )
        {
            listStrFilter.add( SQL_FILTER_IS_ENABLED );
        }

        if ( filter.containsWorkgroupCriteria( ) )
        {
            listStrFilter.add( SQL_FILTER_WORKGROUP );
        }

        if ( filter.containsName( ) )
        {
            listStrFilter.add( SQL_FILTRE_NAME );
        }

        int nPos = 0;
        String strSQL = WorkflowUtils.buildRequestWithFilter( SQL_QUERY_SELECT_BY_FILTER, listStrFilter, SQL_ORDER_BY_DATE_CREATION );
        DAOUtil daoUtil = new DAOUtil( strSQL, WorkflowUtils.getPlugin( ) );

        if ( filter.containsIsEnabled( ) )
        {
            daoUtil.setInt( ++nPos, filter.getIsEnabled( ) );
        }

        if ( filter.containsWorkgroupCriteria( ) )
        {
            daoUtil.setString( ++nPos, filter.getWorkgroup( ) );
        }

        if ( filter.containsName( ) )
        {
            daoUtil.setString( ++nPos, filter.getName( ) );
        }

        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            nPos = 0;
            workflow = new Workflow( );
            workflow.setId( daoUtil.getInt( ++nPos ) );
            workflow.setName( daoUtil.getString( ++nPos ) );
            workflow.setDescription( daoUtil.getString( ++nPos ) );
            workflow.setCreationDate( daoUtil.getTimestamp( ++nPos ) );
            workflow.setEnabled( daoUtil.getBoolean( ++nPos ) );
            workflow.setWorkgroup( daoUtil.getString( ++nPos ) );

            listWorkflow.add( workflow );
        }

        daoUtil.free( );

        return listWorkflow;
    }
}
