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
import fr.paris.lutece.plugins.workflowcore.business.resource.IResourceWorkflowDAO;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflowFilter;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * ResourceWorkflowDAO
 *
 */
public class ResourceWorkflowDAO implements IResourceWorkflowDAO
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_resource,resource_type,id_state,id_workflow,id_external_parent " +
        "FROM workflow_resource_workflow  WHERE id_resource=? AND resource_type=? AND id_workflow=?";
    private static final String SQL_QUERY_SELECT_ID_STATE = "SELECT id_resource,id_state FROM workflow_resource_workflow " +
        " WHERE id_workflow =? AND resource_type = ? AND id_external_parent = ? AND id_resource IN (?";
    private static final String SQL_QUERY_SELECT_BY_WORKFLOW = "SELECT id_resource,resource_type,id_state,id_workflow,id_external_parent  " +
        "FROM workflow_resource_workflow  WHERE id_workflow=?";
    private static final String SQL_QUERY_SELECT_ID_RESOURCE_BY_WORKFLOW = "SELECT id_resource  " +
        "FROM workflow_resource_workflow  WHERE id_workflow=?";
    private static final String SQL_QUERY_SELECT_BY_STATE = "SELECT id_resource,resource_type,id_state,id_workflow  " +
        "FROM workflow_resource_workflow  WHERE id_state=?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO  workflow_resource_workflow " +
        "(id_resource,resource_type,id_state,id_workflow,id_external_parent,is_associated_workgroups)VALUES(?,?,?,?,?,?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_resource_workflow  SET id_resource=?,resource_type=?,id_state=?,id_workflow=?, " +
        " id_external_parent= ?,is_associated_workgroups= ? WHERE id_resource=? AND resource_type=? AND id_workflow=?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_resource_workflow WHERE id_resource=? AND resource_type=? AND id_workflow=? ";
    private static final String SQL_QUERY_DELETE_WORKGROUP = "DELETE FROM workflow_resource_workgroup WHERE id_resource=? AND resource_type=? AND id_workflow=?";
    private static final String SQL_QUERY_DELETE_BY_LIST_ID_RESOURCE = "DELETE FROM workflow_resource_workflow WHERE id_workflow =? AND resource_type = ? AND id_resource IN (? ";
    private static final String SQL_QUERY_DELETE_WORKGROUP_BY_LIST_ID_RESOURCE = "DELETE FROM workflow_resource_workgroup WHERE id_workflow =? AND resource_type = ? AND id_resource IN (? ";
    private static final String SQL_QUERY_SELECT_WORKGROUPS = "SELECT workgroup_key FROM workflow_resource_workgroup WHERE id_resource=? AND resource_type=? AND id_workflow=?";
    private static final String SQL_QUERY_INSERT_WORKGROUP = "INSERT INTO workflow_resource_workgroup (id_resource,resource_type,id_workflow, workgroup_key) VALUES(?,?,?,?) ";
    private static final String SQL_QUERY_SELECT_STATE_BY_FILTER = "SELECT r.id_resource,r.resource_type,r.id_state,r.id_workflow " +
        " FROM workflow_resource_workflow r ";
    private static final String SQL_QUERY_SELECT_STATE_ID_BY_FILTER = "SELECT r.id_resource " +
        " FROM workflow_resource_workflow r ";
    private static final String SQL_QUERY_WITH_WORKGROUP = ",workflow_resource_workgroup w  ";

    //private static final String SQL_QUERY_WITH_WORKGROUP = " LEFT JOIN workflow_resource_workgroup w ON (r.id_resource = w.id_resource AND r.resource_type = w.resource_type AND r.id_workflow = w.id_workflow) ";
    private static final String SQL_FILTER_EXTERNAL_PARENT_ID = " r.id_external_parent = ? ";
    private static final String SQL_FILTER_ID_WORKFLOW = " r.id_workflow = ? ";
    private static final String SQL_FILTER_ID_STATE = " r.id_state = ? ";
    private static final String SQL_FILTER_RESOURCE_TYPE = " r.resource_type = ? ";
    private static final String SQL_FILTER_WORKGROUP_KEY = " r.id_resource = w.id_resource AND r.resource_type = w.resource_type AND r.id_workflow = w.id_workflow AND (w.workgroup_key IN (? ";
    private static final String SQL_FILTER_LIST_STATE = " r.id_state IN (? ";
    private static final String SQL_ORDER_BY_ID_STATE = " ORDER BY r.id_state ";
    private static final String SQL_CLOSE_PARENTHESIS = " ) ";
    private static final String SQL_CLOSE_UNION = " UNION ";
    private static final String SQL_ADITIONAL_PARAMETER = ",?";
    private static final String SQL_FILTER_NOT_ASSOCIATE_WITH_WORKGROUP = " AND r.is_associated_workgroups=0 ";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( ResourceWorkflow resourceWorkflow )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, WorkflowUtils.getPlugin(  ) );

        int nPos = 0;

        daoUtil.setInt( ++nPos, resourceWorkflow.getIdResource(  ) );
        daoUtil.setString( ++nPos, resourceWorkflow.getResourceType(  ) );
        daoUtil.setInt( ++nPos, resourceWorkflow.getState(  ).getId(  ) );
        daoUtil.setInt( ++nPos, resourceWorkflow.getWorkflow(  ).getId(  ) );

        if ( resourceWorkflow.getExternalParentId(  ) != null )
        {
            daoUtil.setInt( ++nPos, resourceWorkflow.getExternalParentId(  ) );
        }
        else
        {
            daoUtil.setIntNull( ++nPos );
        }

        daoUtil.setBoolean( ++nPos, resourceWorkflow.isAssociatedWithWorkgroup(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( ResourceWorkflow resourceWorkflow )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, WorkflowUtils.getPlugin(  ) );

        int nPos = 0;

        daoUtil.setInt( ++nPos, resourceWorkflow.getIdResource(  ) );
        daoUtil.setString( ++nPos, resourceWorkflow.getResourceType(  ) );
        daoUtil.setInt( ++nPos, resourceWorkflow.getState(  ).getId(  ) );
        daoUtil.setInt( ++nPos, resourceWorkflow.getWorkflow(  ).getId(  ) );

        if ( resourceWorkflow.getExternalParentId(  ) != null )
        {
            daoUtil.setInt( ++nPos, resourceWorkflow.getExternalParentId(  ) );
        }
        else
        {
            daoUtil.setIntNull( ++nPos );
        }

        daoUtil.setBoolean( ++nPos, resourceWorkflow.isAssociatedWithWorkgroup(  ) );
        daoUtil.setInt( ++nPos, resourceWorkflow.getIdResource(  ) );
        daoUtil.setString( ++nPos, resourceWorkflow.getResourceType(  ) );
        daoUtil.setInt( ++nPos, resourceWorkflow.getWorkflow(  ).getId(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceWorkflow load( int nIdResource, String strResourceType, int nIdWorkflow )
    {
        ResourceWorkflow documentWorkflow = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, WorkflowUtils.getPlugin(  ) );
        int nPos = 0;

        daoUtil.setInt( ++nPos, nIdResource );
        daoUtil.setString( ++nPos, strResourceType );
        daoUtil.setInt( ++nPos, nIdWorkflow );

        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            nPos = 0;

            documentWorkflow = new ResourceWorkflow(  );

            documentWorkflow.setIdResource( daoUtil.getInt( ++nPos ) );
            documentWorkflow.setResourceType( daoUtil.getString( ++nPos ) );

            State state = new State(  );
            state.setId( daoUtil.getInt( ++nPos ) );
            documentWorkflow.setState( state );

            Workflow workflow = new Workflow(  );
            workflow.setId( daoUtil.getInt( ++nPos ) );
            documentWorkflow.setWorkFlow( workflow );

            ++nPos;

            if ( daoUtil.getObject( nPos ) != null )
            {
                documentWorkflow.setExternalParentId( daoUtil.getInt( nPos ) );
            }
            else
            {
                documentWorkflow.setExternalParentId( null );
            }
        }

        daoUtil.free(  );

        return documentWorkflow;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceWorkflow> selectResourceWorkflowByWorkflow( int nIdWorkflow )
    {
        List<ResourceWorkflow> lisResourceWorkflow = new ArrayList<ResourceWorkflow>(  );
        ResourceWorkflow resourceWorkflow = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_WORKFLOW, WorkflowUtils.getPlugin(  ) );
        int nPos = 0;

        daoUtil.setInt( ++nPos, nIdWorkflow );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            nPos = 0;

            resourceWorkflow = new ResourceWorkflow(  );

            resourceWorkflow.setIdResource( daoUtil.getInt( ++nPos ) );
            resourceWorkflow.setResourceType( daoUtil.getString( ++nPos ) );

            State state = new State(  );
            state.setId( daoUtil.getInt( ++nPos ) );
            resourceWorkflow.setState( state );

            Workflow workflow = new Workflow(  );
            workflow.setId( daoUtil.getInt( ++nPos ) );
            resourceWorkflow.setWorkFlow( workflow );

            resourceWorkflow.setExternalParentId( daoUtil.getInt( ++nPos ) );

            lisResourceWorkflow.add( resourceWorkflow );
        }

        daoUtil.free(  );

        return lisResourceWorkflow;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceWorkflow> selectResourceWorkflowByState( int nIdState )
    {
        List<ResourceWorkflow> lisResourceWorkflow = new ArrayList<ResourceWorkflow>(  );
        ResourceWorkflow resourceWorkflow = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_STATE, WorkflowUtils.getPlugin(  ) );
        int nPos = 0;

        daoUtil.setInt( ++nPos, nIdState );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            nPos = 0;

            resourceWorkflow = new ResourceWorkflow(  );

            resourceWorkflow.setIdResource( daoUtil.getInt( ++nPos ) );
            resourceWorkflow.setResourceType( daoUtil.getString( ++nPos ) );

            State state = new State(  );
            state.setId( daoUtil.getInt( ++nPos ) );
            resourceWorkflow.setState( state );

            Workflow workflow = new Workflow(  );
            workflow.setId( daoUtil.getInt( ++nPos ) );
            resourceWorkflow.setWorkFlow( workflow );

            resourceWorkflow.setExternalParentId( daoUtil.getInt( ++nPos ) );

            lisResourceWorkflow.add( resourceWorkflow );
        }

        daoUtil.free(  );

        return lisResourceWorkflow;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( ResourceWorkflow resourceWorkflow )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowUtils.getPlugin(  ) );

        int nPos = 0;
        daoUtil.setInt( ++nPos, resourceWorkflow.getIdResource(  ) );
        daoUtil.setString( ++nPos, resourceWorkflow.getResourceType(  ) );
        daoUtil.setInt( ++nPos, resourceWorkflow.getWorkflow(  ).getId(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> selectWorkgroups( ResourceWorkflow resourceWorkflow )
    {
        int nPos = 0;
        List<String> listWorkgroups = new ArrayList<String>(  );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_WORKGROUPS, WorkflowUtils.getPlugin(  ) );

        daoUtil.setInt( ++nPos, resourceWorkflow.getIdResource(  ) );
        daoUtil.setString( ++nPos, resourceWorkflow.getResourceType(  ) );
        daoUtil.setInt( ++nPos, resourceWorkflow.getWorkflow(  ).getId(  ) );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            nPos = 0;
            listWorkgroups.add( daoUtil.getString( ++nPos ) );
        }

        daoUtil.free(  );

        return listWorkgroups;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteWorkgroups( ResourceWorkflow resourceWorkflow )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_WORKGROUP, WorkflowUtils.getPlugin(  ) );
        int nPos = 0;

        daoUtil.setInt( ++nPos, resourceWorkflow.getIdResource(  ) );
        daoUtil.setString( ++nPos, resourceWorkflow.getResourceType(  ) );
        daoUtil.setInt( ++nPos, resourceWorkflow.getWorkflow(  ).getId(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeWorkgroupsByListIdResource( List<Integer> listIdResource, String strResourceType,
        Integer nIdWorflow )
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
    public void removeByListIdResource( List<Integer> listIdResource, String strResourceType, Integer nIdWorflow )
    {
        int nlistIdResourceSize = listIdResource.size(  );

        if ( nlistIdResourceSize > 0 )
        {
            StringBuffer sbSQL = new StringBuffer( SQL_QUERY_DELETE_WORKGROUP_BY_LIST_ID_RESOURCE );

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
    public void insertWorkgroup( ResourceWorkflow resourceWorkflow, String strWorkgroup )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_WORKGROUP, WorkflowUtils.getPlugin(  ) );

        int nPos = 0;

        daoUtil.setInt( ++nPos, resourceWorkflow.getIdResource(  ) );
        daoUtil.setString( ++nPos, resourceWorkflow.getResourceType(  ) );
        daoUtil.setInt( ++nPos, resourceWorkflow.getWorkflow(  ).getId(  ) );
        daoUtil.setString( ++nPos, strWorkgroup );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceWorkflow> getListResourceWorkflowByFilter( ResourceWorkflowFilter filter )
    {
        List<ResourceWorkflow> listResourceWorkflow = new ArrayList<ResourceWorkflow>(  );
        ResourceWorkflow resourceWorkflow = null;

        String strSQL = buildFilterQuerydHeader( filter, null, SQL_QUERY_SELECT_STATE_BY_FILTER, null );

        if ( filter.containsWorkgroupKeyList(  ) )
        {
            Map<String, String> workgroupList = filter.getWorkgroupKeyList(  );
            filter.setWorkgroupKeyList( null );

            StringBuffer bufSQL = new StringBuffer(  );
            bufSQL.append( strSQL ).append( SQL_CLOSE_UNION )
                  .append( buildFilterQuerydHeader( filter, null, SQL_QUERY_SELECT_STATE_BY_FILTER, null ) )
                  .append( SQL_FILTER_NOT_ASSOCIATE_WITH_WORKGROUP );
            filter.setWorkgroupKeyList( workgroupList );
            strSQL = bufSQL.toString(  );
        }

        int nPos = 0;

        DAOUtil daoUtil = new DAOUtil( strSQL, WorkflowUtils.getPlugin(  ) );
        nPos = buildFilterQuerydFooter( daoUtil, filter, null, nPos );

        if ( filter.containsWorkgroupKeyList(  ) )
        {
            filter.setWorkgroupKeyList( null );
            buildFilterQuerydFooter( daoUtil, filter, null, nPos );
        }

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            nPos = 0;
            resourceWorkflow = new ResourceWorkflow(  );
            resourceWorkflow.setIdResource( daoUtil.getInt( ++nPos ) );
            resourceWorkflow.setResourceType( daoUtil.getString( ++nPos ) );

            State state = new State(  );
            state.setId( daoUtil.getInt( ++nPos ) );
            resourceWorkflow.setState( state );

            Workflow workflow = new Workflow(  );
            workflow.setId( daoUtil.getInt( ++nPos ) );
            resourceWorkflow.setWorkFlow( workflow );

            if ( filter.containsExternalParentId(  ) )
            {
                resourceWorkflow.setExternalParentId( daoUtil.getInt( ++nPos ) );
            }

            listResourceWorkflow.add( resourceWorkflow );
        }

        daoUtil.free(  );

        return listResourceWorkflow;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> selectResourceIdByWorkflow( int nIdWorkflow )
    {
        List<Integer> lisResourceWorkflow = new ArrayList<Integer>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ID_RESOURCE_BY_WORKFLOW, WorkflowUtils.getPlugin(  ) );
        int nPos = 0;

        daoUtil.setInt( ++nPos, nIdWorkflow );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            nPos = 0;
            lisResourceWorkflow.add( daoUtil.getInt( ++nPos ) );
        }

        daoUtil.free(  );

        return lisResourceWorkflow;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getListResourceWorkflowIdByFilter( ResourceWorkflowFilter filter )
    {
        List<Integer> listResourceWorkflowId = new ArrayList<Integer>(  );

        String strSQL = buildFilterQuerydHeader( filter, null, SQL_QUERY_SELECT_STATE_ID_BY_FILTER, null );

        if ( filter.containsWorkgroupKeyList(  ) )
        {
            Map<String, String> workgroupList = filter.getWorkgroupKeyList(  );
            filter.setWorkgroupKeyList( null );

            StringBuffer bufSQL = new StringBuffer(  );
            bufSQL.append( strSQL ).append( SQL_CLOSE_UNION )
                  .append( buildFilterQuerydHeader( filter, null, SQL_QUERY_SELECT_STATE_ID_BY_FILTER, null ) )
                  .append( SQL_FILTER_NOT_ASSOCIATE_WITH_WORKGROUP );
            filter.setWorkgroupKeyList( workgroupList );
            strSQL = bufSQL.toString(  );
        }

        int nPos = 0;

        DAOUtil daoUtil = new DAOUtil( strSQL, WorkflowUtils.getPlugin(  ) );

        nPos = buildFilterQuerydFooter( daoUtil, filter, null, nPos );

        if ( filter.containsWorkgroupKeyList(  ) )
        {
            filter.setWorkgroupKeyList( null );
            buildFilterQuerydFooter( daoUtil, filter, null, nPos );
        }

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            listResourceWorkflowId.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return listResourceWorkflowId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getListResourceWorkflowIdByFilter( ResourceWorkflowFilter filter,
        List<Integer> lListIdWorkflowState )
    {
        List<Integer> listResourceWorkflowId = new ArrayList<Integer>(  );

        filter.setIdState( ResourceWorkflowFilter.ALL_INT );

        String strSQL = buildFilterQuerydHeader( filter, lListIdWorkflowState, SQL_QUERY_SELECT_STATE_ID_BY_FILTER,
                SQL_ORDER_BY_ID_STATE );

        if ( filter.containsWorkgroupKeyList(  ) )
        {
            Map<String, String> workgroupList = filter.getWorkgroupKeyList(  );
            filter.setWorkgroupKeyList( null );

            StringBuffer bufSQL = new StringBuffer(  );
            bufSQL.append( strSQL ).append( SQL_CLOSE_UNION )
                  .append( buildFilterQuerydHeader( filter, lListIdWorkflowState, SQL_QUERY_SELECT_STATE_ID_BY_FILTER,
                    null ) ).append( SQL_FILTER_NOT_ASSOCIATE_WITH_WORKGROUP );
            filter.setWorkgroupKeyList( workgroupList );
            strSQL = bufSQL.toString(  );
        }

        Integer nPos = 0;

        DAOUtil daoUtil = new DAOUtil( strSQL, WorkflowUtils.getPlugin(  ) );

        nPos = buildFilterQuerydFooter( daoUtil, filter, lListIdWorkflowState, nPos );

        if ( filter.containsWorkgroupKeyList(  ) )
        {
            filter.setWorkgroupKeyList( null );
            buildFilterQuerydFooter( daoUtil, filter, lListIdWorkflowState, nPos );
        }

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            listResourceWorkflowId.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return listResourceWorkflowId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Integer, Integer> getListIdStateByListId( List<Integer> lListIdRessource, int nIdWorflow,
        String strResourceType, Integer nIdExternalParentId )
    {
        Map<Integer, Integer> result = new HashMap<Integer, Integer>(  );
        int nlistIdResourceSize = lListIdRessource.size(  );

        if ( nlistIdResourceSize > 0 )
        {
            StringBuffer sbSQL = new StringBuffer( SQL_QUERY_SELECT_ID_STATE );

            for ( int i = 1; i < nlistIdResourceSize; i++ )
            {
                sbSQL.append( SQL_ADITIONAL_PARAMETER );
            }

            sbSQL.append( SQL_CLOSE_PARENTHESIS );

            DAOUtil daoUtil = new DAOUtil( sbSQL.toString(  ), WorkflowUtils.getPlugin(  ) );
            daoUtil.setInt( 1, nIdWorflow );
            daoUtil.setString( 2, strResourceType );
            daoUtil.setInt( 3, nIdExternalParentId );

            for ( int i = 0; i < nlistIdResourceSize; i++ )
            {
                daoUtil.setInt( i + 4, lListIdRessource.get( i ) );
            }

            daoUtil.executeQuery(  );

            while ( daoUtil.next(  ) )
            {
                result.put( daoUtil.getInt( 1 ), daoUtil.getInt( 2 ) );
            }

            daoUtil.free(  );
        }

        return result;
    }

    /**
     * Build filter SQL query footer
     * @param daoUtil the DaoUtil
     * @param filter the filter
     * @param lListIdState the list of ids state
     * @param nPos the parameter position
     * @return the doaUtil
     */
    private Integer buildFilterQuerydFooter( DAOUtil daoUtil, ResourceWorkflowFilter filter,
        List<Integer> lListIdState, Integer nPos )
    {
        int nPosition = nPos;

        if ( filter.containsIdWorkflow(  ) )
        {
            daoUtil.setInt( ++nPosition, filter.getIdWorkflow(  ) );
        }

        if ( ( lListIdState == null ) && filter.containsIdState(  ) )
        {
            daoUtil.setInt( ++nPosition, filter.getIdState(  ) );
        }

        if ( filter.containsResourceType(  ) )
        {
            daoUtil.setString( ++nPosition, filter.getResourceType(  ) );
        }

        if ( filter.containsExternalParentId(  ) )
        {
            daoUtil.setInt( ++nPosition, filter.getExternalParentId(  ) );
        }

        if ( lListIdState != null )
        {
            int nSize = lListIdState.size(  );

            for ( int i = 0; i < nSize; i++ )
            {
                daoUtil.setInt( i + nPosition + 1, lListIdState.get( i ) );
            }

            nPosition += nSize;
        }

        if ( filter.containsWorkgroupKeyList(  ) )
        {
            ReferenceList list = ReferenceList.convert( filter.getWorkgroupKeyList(  ) );
            int nSize = list.size(  );

            for ( int i = 0; i < nSize; i++ )
            {
                daoUtil.setString( i + nPosition + 1, list.get( i ).getCode(  ) );
            }

            nPosition += nSize;
        }

        return nPosition;
    }

    /**
     * Build filter SQL query header
     * @param filter the filter
     * @param lListIdState the list of ids state
     * @param strSelectSQL the beginning of sql query
     * @param strOrderBy the SQL query for order by
     * @return the SQL query
     */
    private String buildFilterQuerydHeader( ResourceWorkflowFilter filter, List<Integer> lListIdState,
        String strSelectSQL, String strOrderBy )
    {
        List<String> listStrFilter = new ArrayList<String>(  );

        if ( filter.containsIdWorkflow(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_WORKFLOW );
        }

        if ( ( lListIdState == null ) && filter.containsIdState(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_STATE );
        }

        if ( filter.containsResourceType(  ) )
        {
            listStrFilter.add( SQL_FILTER_RESOURCE_TYPE );
        }

        if ( filter.containsExternalParentId(  ) )
        {
            listStrFilter.add( SQL_FILTER_EXTERNAL_PARENT_ID );
        }

        if ( lListIdState != null )
        {
            int nSize = lListIdState.size(  );

            if ( nSize > 0 )
            {
                StringBuffer sb = new StringBuffer(  );

                for ( int i = 0; i < nSize; i++ )
                {
                    if ( i < 1 )
                    {
                        sb.append( SQL_FILTER_LIST_STATE );
                    }
                    else
                    {
                        sb.append( SQL_ADITIONAL_PARAMETER );
                    }
                }

                sb.append( SQL_CLOSE_PARENTHESIS );
                listStrFilter.add( sb.toString(  ) );
            }
        }

        String strSQL = null;

        if ( filter.containsWorkgroupKeyList(  ) )
        {
            ReferenceList list = ReferenceList.convert( filter.getWorkgroupKeyList(  ) );
            int nSize = list.size(  );

            if ( nSize > 0 )
            {
                StringBuffer sb = new StringBuffer(  );

                for ( int i = 0; i < nSize; i++ )
                {
                    if ( i < 1 )
                    {
                        sb.append( SQL_FILTER_WORKGROUP_KEY );
                    }
                    else
                    {
                        sb.append( SQL_ADITIONAL_PARAMETER );
                    }
                }

                sb.append( SQL_CLOSE_PARENTHESIS + SQL_CLOSE_PARENTHESIS );
                listStrFilter.add( sb.toString(  ) );
            }

            strSQL = WorkflowUtils.buildRequestWithFilter( strSelectSQL + SQL_QUERY_WITH_WORKGROUP, listStrFilter, null );
        }
        else
        {
            strSQL = WorkflowUtils.buildRequestWithFilter( strSelectSQL, listStrFilter, strOrderBy );
        }

        return strSQL;
    }
}
