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
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.resource.IResourceHistoryDAO;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistoryFilter;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceUserHistory;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.util.sql.DAOUtil;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * ResourceHistoryDAO
 *
 */
public class ResourceHistoryDAO implements IResourceHistoryDAO
{
    private static final String SQL_QUERY_SELECT_ALL = "SELECT wh.id_history,wh.id_resource,wh.resource_type,"
            + "wh.id_workflow,wh.id_action,wh.creation_date,wh.user_access_code FROM workflow_resource_history wh ";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = SQL_QUERY_SELECT_ALL + " WHERE wh.id_history=?";
    private static final String SQL_QUERY_SELECT_BY_RESSOURCE = SQL_QUERY_SELECT_ALL + " WHERE wh.id_resource=? AND wh.resource_type=? AND wh.id_workflow=?";
    private static final String SQL_QUERY_SELECT_BY_ACTION = SQL_QUERY_SELECT_ALL + " WHERE wh.id_action=?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO  workflow_resource_history "
            + "(id_resource,resource_type,id_workflow,id_action,creation_date,user_access_code )VALUES(?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_resource_history  WHERE id_history=?";
    private static final String SQL_QUERY_DELETE_BY_LIST_ID_RESOURCE = "DELETE FROM workflow_resource_history WHERE id_workflow = ? AND resource_type = ? AND id_resource IN (?";
    private static final String SQL_QUERY_SELECT_LIST_ID_HISTORY_BY_LIST_ID_RESOURCE_AND_TYPE = "SELECT id_history FROM workflow_resource_history WHERE id_workflow = ? AND resource_type = ? AND id_resource IN (?";
    private static final String SQL_QUERY_SELECT_BY_FILTER = "SELECT wh.id_history, wh.id_resource,wh. resource_type, wh.id_workflow, wh.id_action, wh.creation_date, wh.user_access_code, a.name, a.description, a.is_automatic, a.is_mass_action, a.display_order, a.is_automatic_reflexive_action, user.email, user.first_name, user.last_name, user.realm  FROM workflow_resource_history wh left join workflow_action a on ( wh.id_action=a.id_action) left join workflow_resource_user_history user on (wh.id_history=user.id_history) ";
    private static final String SQL_QUERY_SELECT_ID_HISTORY_BY_FILTER = "SELECT wh.id_history FROM workflow_resource_history wh";
    private static final String SQL_QUERY_SELECT_BY_LIST_HISTORY = SQL_QUERY_SELECT_BY_FILTER + " WHERE wh.id_history IN (?";
    private static final String SQL_CLOSE_PARENTHESIS = " ) ";
    private static final String SQL_ADITIONAL_PARAMETER = ",?";
    private static final String SQL_ORDER_BY_CREATION_DATE_DESC = " ORDER BY creation_date DESC";
    private static final String SQL_FILTER_ID_WORKFLOW = " wh.id_workflow = ? ";
    private static final String SQL_FILTER_ID_ACTION = " wh.id_action = ? ";
    private static final String SQL_FILTER_RESOURCE_TYPE = " wh.resource_type = ? ";
    private static final String SQL_FILTER_USER_ACCESS_CODE = " wh.user_access_code = ? ";
    private static final String SQL_FILTER_LIST_RESOURCES = " wh.id_resource IN ( ";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( ResourceHistory resourceHistory )
    {

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;
            daoUtil.setInt( ++nPos, resourceHistory.getIdResource( ) );
            daoUtil.setString( ++nPos, resourceHistory.getResourceType( ) );
            daoUtil.setInt( ++nPos, resourceHistory.getWorkflow( ).getId( ) );
            daoUtil.setInt( ++nPos, resourceHistory.getAction( ).getId( ) );
            daoUtil.setTimestamp( ++nPos, resourceHistory.getCreationDate( ) );
            daoUtil.setString( ++nPos, resourceHistory.getUserAccessCode( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                resourceHistory.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceHistory load( int nIdHistory )
    {
        ResourceHistory resourceHistory = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdHistory );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                resourceHistory = dataToObject( daoUtil );
            }
        }
        return resourceHistory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceHistory> selectByResource( int nIdResource, String strResourceType, int nIdWorkflow )
    {
        List<ResourceHistory> listResourceHostory = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_RESSOURCE + SQL_ORDER_BY_CREATION_DATE_DESC, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;
            daoUtil.setInt( ++nPos, nIdResource );
            daoUtil.setString( ++nPos, strResourceType );
            daoUtil.setInt( ++nPos, nIdWorkflow );

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                ResourceHistory resourceHistory = dataToObject( daoUtil );
                listResourceHostory.add( resourceHistory );
            }
        }
        return listResourceHostory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceHistory> selectByAction( int nIdAction )
    {
        List<ResourceHistory> listResourceHostory = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ACTION + SQL_ORDER_BY_CREATION_DATE_DESC, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;
            daoUtil.setInt( ++nPos, nIdAction );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                ResourceHistory resourceHistory = dataToObject( daoUtil );
                listResourceHostory.add( resourceHistory );
            }
        }
        return listResourceHostory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdHistory )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdHistory );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByListIdResource( List<Integer> listIdResource, String strResourceType, Integer nIdWorflow )
    {
        int nlistIdResourceSize = listIdResource.size( );

        if ( nlistIdResourceSize > 0 )
        {
            StringBuilder sbSQL = new StringBuilder( SQL_QUERY_DELETE_BY_LIST_ID_RESOURCE );

            for ( int i = 1; i < nlistIdResourceSize; i++ )
            {
                sbSQL.append( SQL_ADITIONAL_PARAMETER );
            }

            sbSQL.append( SQL_CLOSE_PARENTHESIS );

            try ( DAOUtil daoUtil = new DAOUtil( sbSQL.toString( ), WorkflowUtils.getPlugin( ) ) )
            {
                daoUtil.setInt( 1, nIdWorflow );
                daoUtil.setString( 2, strResourceType );
                for ( int i = 0; i < nlistIdResourceSize; i++ )
                {
                    daoUtil.setInt( i + 3, listIdResource.get( i ) );
                }
                daoUtil.executeUpdate( );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getListHistoryIdByListIdResourceId( List<Integer> listIdResource, String strResourceType, Integer nIdWorflow )
    {
        List<Integer> lListResult = new ArrayList<>( );

        int nlistIdResourceSize = listIdResource.size( );

        if ( nlistIdResourceSize > 0 )
        {
            StringBuilder sbSQL = new StringBuilder( SQL_QUERY_SELECT_LIST_ID_HISTORY_BY_LIST_ID_RESOURCE_AND_TYPE );

            for ( int i = 1; i < nlistIdResourceSize; i++ )
            {
                sbSQL.append( SQL_ADITIONAL_PARAMETER );
            }

            sbSQL.append( SQL_CLOSE_PARENTHESIS );

            try ( DAOUtil daoUtil = new DAOUtil( sbSQL.toString( ), WorkflowUtils.getPlugin( ) ) )
            {
                daoUtil.setInt( 1, nIdWorflow );
                daoUtil.setString( 2, strResourceType );

                for ( int i = 0; i < nlistIdResourceSize; i++ )
                {
                    daoUtil.setInt( i + 3, listIdResource.get( i ) );
                }

                daoUtil.executeQuery( );

                while ( daoUtil.next( ) )
                {
                    lListResult.add( daoUtil.getInt( 1 ) );
                }
            }
        }
        return lListResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> selectListHistoryIdByFilter( ResourceHistoryFilter filter )
    {
        List<Integer> lListResult = new ArrayList<>( );

        String strSQL = buildFilterQuerydHeader( filter, SQL_QUERY_SELECT_ID_HISTORY_BY_FILTER );
        try ( DAOUtil daoUtil = new DAOUtil( strSQL, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;
            buildFilterQuerydFooter( filter, daoUtil, nPos );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                lListResult.add( daoUtil.getInt( 1 ) );
            }
        }

        return lListResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceHistory> selectByFilter( ResourceHistoryFilter filter )
    {
        List<ResourceHistory> lListResult = new ArrayList<>( );

        String strSQL = buildFilterQuerydHeader( filter, SQL_QUERY_SELECT_BY_FILTER );
        try ( DAOUtil daoUtil = new DAOUtil( strSQL, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;
            buildFilterQuerydFooter( filter, daoUtil, nPos );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                lListResult.add( dataToResourceHistoryObject( daoUtil ) );
            }
        }

        return lListResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceHistory> selectByPrimaryKeyList( List<Integer> listIdHistory )
    {
        List<ResourceHistory> lListResult = new ArrayList<>( );
        int nlistIdResourceSize = listIdHistory.size( );

        if ( nlistIdResourceSize > 0 )
        {
            StringBuilder sbSQL = new StringBuilder( SQL_QUERY_SELECT_BY_LIST_HISTORY );

            for ( int i = 1; i < nlistIdResourceSize; i++ )
            {
                sbSQL.append( SQL_ADITIONAL_PARAMETER );
            }

            sbSQL.append( SQL_CLOSE_PARENTHESIS );

            try ( DAOUtil daoUtil = new DAOUtil( sbSQL.toString( ), WorkflowUtils.getPlugin( ) ) )
            {

                for ( int i = 0; i < nlistIdResourceSize; i++ )
                {
                    daoUtil.setInt( i + 1, listIdHistory.get( i ) );
                }

                daoUtil.executeQuery( );

                while ( daoUtil.next( ) )
                {
                    lListResult.add( dataToResourceHistoryObject( daoUtil ) );
                }
            }
        }
        return lListResult;
    }

    private ResourceHistory dataToObject( DAOUtil daoUtil )
    {
        int nPos = 0;
        ResourceHistory resourceHistory = new ResourceHistory( );
        resourceHistory.setId( daoUtil.getInt( ++nPos ) );
        resourceHistory.setIdResource( daoUtil.getInt( ++nPos ) );
        resourceHistory.setResourceType( daoUtil.getString( ++nPos ) );

        Workflow workflow = new Workflow( );
        workflow.setId( daoUtil.getInt( ++nPos ) );
        resourceHistory.setWorkFlow( workflow );

        Action action = new Action( );
        action.setId( daoUtil.getInt( ++nPos ) );
        resourceHistory.setAction( action );

        resourceHistory.setCreationDate( daoUtil.getTimestamp( ++nPos ) );
        resourceHistory.setUserAccessCode( daoUtil.getString( ++nPos ) );

        return resourceHistory;
    }

    private ResourceHistory dataToResourceHistoryObject( DAOUtil daoUtil )
    {
        int nPos = 0;
        ResourceHistory resourceHistory = new ResourceHistory( );
        resourceHistory.setId( daoUtil.getInt( ++nPos ) );
        resourceHistory.setIdResource( daoUtil.getInt( ++nPos ) );
        resourceHistory.setResourceType( daoUtil.getString( ++nPos ) );

        Workflow workflow = new Workflow( );
        workflow.setId( daoUtil.getInt( ++nPos ) );
        resourceHistory.setWorkFlow( workflow );

        Action action = new Action( );
        action.setId( daoUtil.getInt( ++nPos ) );

        resourceHistory.setCreationDate( daoUtil.getTimestamp( ++nPos ) );
        resourceHistory.setUserAccessCode( daoUtil.getString( ++nPos ) );
        resourceHistory.setAction( dataToActionObject( daoUtil, action, nPos ) );

        if ( StringUtils.isNotEmpty( resourceHistory.getUserAccessCode( ) ) )
        {

            ResourceUserHistory resourceUserHistory = new ResourceUserHistory( );
            resourceUserHistory.setUserAccessCode( resourceHistory.getUserAccessCode( ) );
            resourceUserHistory.setIdHistory( ( resourceHistory.getId( ) ) );
            resourceHistory.setResourceUserHistory( dataToUserObject( daoUtil, resourceUserHistory, nPos ) );
        }
        return resourceHistory;
    }

    private Action dataToActionObject( DAOUtil daoUtil, Action action, int nPos )
    {
        action.setName( daoUtil.getString( ++nPos ) );
        action.setDescription( daoUtil.getString( ++nPos ) );
        action.setAutomaticState( daoUtil.getBoolean( ++nPos ) );
        action.setMassAction( daoUtil.getBoolean( ++nPos ) );
        action.setOrder( daoUtil.getInt( ++nPos ) );
        action.setAutomaticReflexiveAction( daoUtil.getBoolean( ++nPos ) );

        return action;
    }

    private ResourceUserHistory dataToUserObject( DAOUtil daoUtil, ResourceUserHistory resourceUserHistory, int nPos )
    {
        resourceUserHistory.setEmail( daoUtil.getString( ++nPos ) );
        resourceUserHistory.setFirstName( daoUtil.getString( ++nPos ) );
        resourceUserHistory.setLastName( daoUtil.getString( ++nPos ) );
        resourceUserHistory.setRealm( daoUtil.getString( ++nPos ) );

        return resourceUserHistory;
    }

    /**
     * Build filter SQL query header
     * 
     * @param filter
     *            the filter
     * @param strSelectSQL
     *            the beginning of sql query
     * @param strOrderBy
     *            the SQL query for order by
     * @return the SQL query
     */
    private String buildFilterQuerydHeader( ResourceHistoryFilter filter, String strSelectSQL )
    {
        List<String> listStrFilter = new ArrayList<>( );

        if ( filter.getIdWorkflow( ) > 0 )
        {
            listStrFilter.add( SQL_FILTER_ID_WORKFLOW );
        }

        if ( filter.getIdAction( ) > 0 )
        {
            listStrFilter.add( SQL_FILTER_ID_ACTION );
        }

        if ( !StringUtils.isEmpty( filter.getResourceType( ) ) )
        {
            listStrFilter.add( SQL_FILTER_RESOURCE_TYPE );
        }

        if ( !StringUtils.isEmpty( filter.getUserAccessCode( ) ) )
        {
            listStrFilter.add( SQL_FILTER_USER_ACCESS_CODE );
        }

        if ( CollectionUtils.isNotEmpty( filter.getListIdResources( ) ) )
        {
            StringBuilder sb = new StringBuilder( );
            sb.append( SQL_FILTER_LIST_RESOURCES );
            sb.append( filter.getListIdResources( ).stream( ).map( i -> "?" ).collect( Collectors.joining( "," ) ) );
            sb.append( SQL_CLOSE_PARENTHESIS );
            listStrFilter.add( sb.toString( ) );
        }
        return WorkflowUtils.buildRequestWithFilter( strSelectSQL, listStrFilter, null );
    }

    /**
     * Build filter SQL query footer
     * 
     * @param daoUtil
     *            the DaoUtil
     * @param filter
     *            the filter
     * @param lListIdState
     *            the list of ids state
     * @param nPos
     *            the parameter position
     * @return the doaUtil
     */
    private int buildFilterQuerydFooter( ResourceHistoryFilter filter, DAOUtil daoUtil, Integer nPos )
    {

        if ( filter.getIdWorkflow( ) > 0 )
        {
            daoUtil.setInt( ++nPos, filter.getIdWorkflow( ) );
        }
        if ( filter.getIdAction( ) > 0 )
        {
            daoUtil.setInt( ++nPos, filter.getIdAction( ) );
        }

        if ( !StringUtils.isEmpty( filter.getResourceType( ) ) )
        {
            daoUtil.setString( ++nPos, filter.getResourceType( ) );
        }

        if ( !StringUtils.isEmpty( filter.getUserAccessCode( ) ) )
        {
            daoUtil.setString( ++nPos, filter.getUserAccessCode( ) );
        }

        if ( CollectionUtils.isNotEmpty( filter.getListIdResources( ) ) )
        {
            List<Integer> listIdResource = filter.getListIdResources( );
            for ( int i = 0; i < listIdResource.size( ); i++ )
            {
                daoUtil.setInt( ++nPos, listIdResource.get( i ) );
            }
        }
        return nPos;
    }
}
