/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.resource.IResourceHistoryDAO;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * ResourceHistoryDAO
 *
 */
public class ResourceHistoryDAO implements IResourceHistoryDAO
{
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_history ) FROM workflow_resource_history";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_history,id_resource,resource_type," +
        "id_workflow,id_action,creation_date,user_access_code " + "FROM workflow_resource_history WHERE id_history=?";
    private static final String SQL_QUERY_SELECT_BY_RESSOURCE = "SELECT id_history,id_resource,resource_type," +
        "id_workflow,id_action,creation_date,user_access_code " +
        "FROM workflow_resource_history WHERE id_resource=? AND resource_type=? AND id_workflow=?";
    private static final String SQL_QUERY_SELECT_BY_ACTION = "SELECT id_history,id_resource,resource_type," +
        "id_workflow,id_action,creation_date,user_access_code " + "FROM workflow_resource_history WHERE id_action=?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO  workflow_resource_history " +
        "(id_history,id_resource,resource_type,id_workflow,id_action,creation_date,user_access_code )VALUES(?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_resource_history  WHERE id_history=?";
    private static final String SQL_QUERY_DELETE_BY_LIST_ID_RESOURCE = "DELETE FROM workflow_resource_history WHERE id_workflow = ? AND resource_type = ? AND id_resource IN (?";
    private static final String SQL_QUERY_SELECT_LIST_ID_HISTORY_BY_LIST_ID_RESOURCE = "SELECT id_history FROM workflow_resource_history WHERE id_workflow = ? AND resource_type = ? AND id_resource IN (?";
    private static final String SQL_CLOSE_PARENTHESIS = " ) ";
    private static final String SQL_ADITIONAL_PARAMETER = ",?";
    private static final String SQL_ORDER_BY_CREATION_DATE_DESC = " ORDER BY creation_date DESC";

    /**
     * Generates a new primary key
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

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( ResourceHistory resourceHistory )
    {
        resourceHistory.setId( newPrimaryKey( WorkflowUtils.getPlugin(  ) ) );

        int nPos = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, WorkflowUtils.getPlugin(  ) );

        daoUtil.setInt( ++nPos, resourceHistory.getId(  ) );
        daoUtil.setInt( ++nPos, resourceHistory.getIdResource(  ) );
        daoUtil.setString( ++nPos, resourceHistory.getResourceType(  ) );
        daoUtil.setInt( ++nPos, resourceHistory.getWorkflow(  ).getId(  ) );
        daoUtil.setInt( ++nPos, resourceHistory.getAction(  ).getId(  ) );
        daoUtil.setTimestamp( ++nPos, resourceHistory.getCreationDate(  ) );
        daoUtil.setString( ++nPos, resourceHistory.getUserAccessCode(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceHistory load( int nIdHistory )
    {
        ResourceHistory resourceHistory = null;
        Action action;
        Workflow workflow;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, WorkflowUtils.getPlugin(  ) );

        daoUtil.setInt( 1, nIdHistory );

        int nPos = 0;

        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            resourceHistory = new ResourceHistory(  );
            resourceHistory.setId( daoUtil.getInt( ++nPos ) );
            resourceHistory.setIdResource( daoUtil.getInt( ++nPos ) );
            resourceHistory.setResourceType( daoUtil.getString( ++nPos ) );

            workflow = new Workflow(  );
            workflow.setId( daoUtil.getInt( ++nPos ) );
            resourceHistory.setWorkFlow( workflow );

            action = new Action(  );
            action.setId( daoUtil.getInt( ++nPos ) );
            resourceHistory.setAction( action );

            resourceHistory.setCreationDate( daoUtil.getTimestamp( ++nPos ) );
            resourceHistory.setUserAccessCode( daoUtil.getString( ++nPos ) );
        }

        daoUtil.free(  );

        return resourceHistory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceHistory> selectByResource( int nIdResource, String strResourceType, int nIdWorkflow )
    {
        List<ResourceHistory> listResourceHostory = new ArrayList<ResourceHistory>(  );
        ResourceHistory resourceHistory = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_RESSOURCE + SQL_ORDER_BY_CREATION_DATE_DESC,
                WorkflowUtils.getPlugin(  ) );
        int nPos = 0;
        daoUtil.setInt( ++nPos, nIdResource );
        daoUtil.setString( ++nPos, strResourceType );
        daoUtil.setInt( ++nPos, nIdWorkflow );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            nPos = 0;
            resourceHistory = new ResourceHistory(  );
            resourceHistory.setId( daoUtil.getInt( ++nPos ) );
            resourceHistory.setIdResource( daoUtil.getInt( ++nPos ) );
            resourceHistory.setResourceType( daoUtil.getString( ++nPos ) );

            Workflow workflow = new Workflow(  );
            workflow.setId( daoUtil.getInt( ++nPos ) );
            resourceHistory.setWorkFlow( workflow );

            Action action = new Action(  );
            action.setId( daoUtil.getInt( ++nPos ) );
            resourceHistory.setAction( action );
            resourceHistory.setCreationDate( daoUtil.getTimestamp( ++nPos ) );
            resourceHistory.setUserAccessCode( daoUtil.getString( ++nPos ) );

            listResourceHostory.add( resourceHistory );
        }

        daoUtil.free(  );

        return listResourceHostory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceHistory> selectByAction( int nIdAction )
    {
        List<ResourceHistory> listResourceHostory = new ArrayList<ResourceHistory>(  );
        ResourceHistory resourceHistory = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ACTION + SQL_ORDER_BY_CREATION_DATE_DESC,
                WorkflowUtils.getPlugin(  ) );
        int nPos = 0;
        daoUtil.setInt( ++nPos, nIdAction );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            nPos = 0;
            resourceHistory = new ResourceHistory(  );
            resourceHistory.setId( daoUtil.getInt( ++nPos ) );
            resourceHistory.setIdResource( daoUtil.getInt( ++nPos ) );
            resourceHistory.setResourceType( daoUtil.getString( ++nPos ) );

            Workflow workflow = new Workflow(  );
            workflow.setId( daoUtil.getInt( ++nPos ) );
            resourceHistory.setWorkFlow( workflow );

            Action action = new Action(  );
            action.setId( daoUtil.getInt( ++nPos ) );
            resourceHistory.setAction( action );
            resourceHistory.setCreationDate( daoUtil.getTimestamp( ++nPos ) );
            resourceHistory.setUserAccessCode( daoUtil.getString( ++nPos ) );

            listResourceHostory.add( resourceHistory );
        }

        daoUtil.free(  );

        return listResourceHostory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdHistory )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowUtils.getPlugin(  ) );
        daoUtil.setInt( 1, nIdHistory );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByListIdResource( List<Integer> listIdResource, String strResourceType, Integer nIdWorflow )
    {
        int nlistIdResourceSize = listIdResource.size(  );

        if ( nlistIdResourceSize > 0 )
        {
            StringBuffer sbSQL = new StringBuffer( SQL_QUERY_DELETE_BY_LIST_ID_RESOURCE );

            for ( int i = 1; i < nlistIdResourceSize; i++ )
            {
                sbSQL.append( SQL_ADITIONAL_PARAMETER );
            }

            sbSQL.append( SQL_CLOSE_PARENTHESIS );

            DAOUtil daoUtil = new DAOUtil( sbSQL.toString(  ), WorkflowUtils.getPlugin(  ) );
            daoUtil.setInt( 1, nIdWorflow );
            daoUtil.setString( 2, strResourceType );

            for ( int i = 0; i < nlistIdResourceSize; i++ )
            {
                daoUtil.setInt( i + 3, listIdResource.get( i ) );
            }

            daoUtil.executeUpdate(  );
            daoUtil.free(  );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getListHistoryIdByListIdResourceId( List<Integer> listIdResource, String strResourceType,
        Integer nIdWorflow )
    {
        List<Integer> lListResult = new ArrayList<Integer>(  );

        int nlistIdResourceSize = listIdResource.size(  );

        if ( nlistIdResourceSize > 0 )
        {
            StringBuffer sbSQL = new StringBuffer( SQL_QUERY_SELECT_LIST_ID_HISTORY_BY_LIST_ID_RESOURCE );

            for ( int i = 1; i < nlistIdResourceSize; i++ )
            {
                sbSQL.append( SQL_ADITIONAL_PARAMETER );
            }

            sbSQL.append( SQL_CLOSE_PARENTHESIS );

            DAOUtil daoUtil = new DAOUtil( sbSQL.toString(  ), WorkflowUtils.getPlugin(  ) );
            daoUtil.setInt( 1, nIdWorflow );
            daoUtil.setString( 2, strResourceType );

            for ( int i = 0; i < nlistIdResourceSize; i++ )
            {
                daoUtil.setInt( i + 3, listIdResource.get( i ) );
            }

            daoUtil.executeQuery(  );

            while ( daoUtil.next(  ) )
            {
                lListResult.add( daoUtil.getInt( 1 ) );
            }

            daoUtil.free(  );
        }

        return lListResult;
    }
}
