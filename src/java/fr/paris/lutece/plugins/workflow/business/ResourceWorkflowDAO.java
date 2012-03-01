/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.business;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.portal.business.workflow.State;
import fr.paris.lutece.portal.business.workflow.Workflow;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 *
 *class  ResourceWorkflowDAO
 *
 */
public class ResourceWorkflowDAO implements IResourceWorkflowDAO
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_resource,resource_type,id_state,id_workflow,id_external_parent " +
        "FROM workflow_resource_workflow  WHERE id_resource=? AND resource_type=? AND id_workflow=?";
    private static final String SQL_QUERY_SELECT_ID_STATE = "SELECT id_resource,id_state FROM workflow_resource_workflow " +
        " WHERE id_workflow =? AND resource_type = ? AND id_external_parent = ? AND id_resource IN (?";
    private static final String SQL_QUERY_SELECT_BY_WORKFLOW = "SELECT id_resource,resource_type,id_state,id_workflow  " +
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

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.IResourceWorkflowDAO#insert(fr.paris.lutece.plugins.workflow.business.ResourceWorkflow, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public synchronized void insert( ResourceWorkflow resourceWorkflow, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

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

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.IResourceWorkflowDAO#store(fr.paris.lutece.plugins.workflow.business.ResourceWorkflow, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void store( ResourceWorkflow resourceWorkflow, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

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

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.IResourceWorkflowDAO#load(int, java.lang.String, int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public ResourceWorkflow load( int nIdResource, String strResourceType, int nIdWorkflow, Plugin plugin )
    {
        ResourceWorkflow documentWorkflow = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
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

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.IResourceWorkflowDAO#selectResourceWorkflowByWorkflow(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public List<ResourceWorkflow> selectResourceWorkflowByWorkflow( int nIdWorkflow, Plugin plugin )
    {
        List<ResourceWorkflow> lisResourceWorkflow = new ArrayList<ResourceWorkflow>(  );
        ResourceWorkflow resourceWorkflow = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_WORKFLOW, plugin );
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

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.IResourceWorkflowDAO#selectResourceWorkflowByState(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public List<ResourceWorkflow> selectResourceWorkflowByState( int nIdState, Plugin plugin )
    {
        List<ResourceWorkflow> lisResourceWorkflow = new ArrayList<ResourceWorkflow>(  );
        ResourceWorkflow resourceWorkflow = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_STATE, plugin );
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

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.IResourceWorkflowDAO#delete(fr.paris.lutece.plugins.workflow.business.ResourceWorkflow, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void delete( ResourceWorkflow resourceWorkflow, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );

        int nPos = 0;
        daoUtil.setInt( ++nPos, resourceWorkflow.getIdResource(  ) );
        daoUtil.setString( ++nPos, resourceWorkflow.getResourceType(  ) );
        daoUtil.setInt( ++nPos, resourceWorkflow.getWorkflow(  ).getId(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.IResourceWorkflowDAO#selectWorkgroups(fr.paris.lutece.plugins.workflow.business.ResourceWorkflow, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public List<String> selectWorkgroups( ResourceWorkflow resourceWorkflow, Plugin plugin )
    {
        int nPos = 0;
        List<String> listWorkgroups = new ArrayList<String>(  );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_WORKGROUPS, plugin );

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

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.IResourceWorkflowDAO#deleteWorkgroups(fr.paris.lutece.plugins.workflow.business.ResourceWorkflow, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void deleteWorkgroups( ResourceWorkflow resourceWorkflow, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_WORKGROUP, plugin );
        int nPos = 0;

        daoUtil.setInt( ++nPos, resourceWorkflow.getIdResource(  ) );
        daoUtil.setString( ++nPos, resourceWorkflow.getResourceType(  ) );
        daoUtil.setInt( ++nPos, resourceWorkflow.getWorkflow(  ).getId(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.IResourceWorkflowDAO#removeWorkgroupsByListIdResource(java.util.List, java.lang.String, java.lang.Integer, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void removeWorkgroupsByListIdResource( List<Integer> listIdResource, String strResourceType,
        Integer nIdWorflow, Plugin plugin )
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

            DAOUtil daoUtil = new DAOUtil( sbSQL.toString(  ), plugin );
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

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.IResourceWorkflowDAO#removeByListIdResource(java.util.List, java.lang.String, java.lang.Integer, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void removeByListIdResource( List<Integer> listIdResource, String strResourceType, Integer nIdWorflow,
        Plugin plugin )
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

            DAOUtil daoUtil = new DAOUtil( sbSQL.toString(  ), plugin );
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

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.IResourceWorkflowDAO#insertWorkgroup(fr.paris.lutece.plugins.workflow.business.ResourceWorkflow, java.lang.String, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void insertWorkgroup( ResourceWorkflow resourceWorkflow, String strWorkgroup, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_WORKGROUP, plugin );

        int nPos = 0;

        daoUtil.setInt( ++nPos, resourceWorkflow.getIdResource(  ) );
        daoUtil.setString( ++nPos, resourceWorkflow.getResourceType(  ) );
        daoUtil.setInt( ++nPos, resourceWorkflow.getWorkflow(  ).getId(  ) );
        daoUtil.setString( ++nPos, strWorkgroup );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.IResourceWorkflowDAO#getListResourceWorkflowByFilter(fr.paris.lutece.plugins.workflow.business.ResourceWorkflowFilter, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public List<ResourceWorkflow> getListResourceWorkflowByFilter( ResourceWorkflowFilter filter, Plugin plugin )
    {
        List<ResourceWorkflow> listResourceWorkflow = new ArrayList<ResourceWorkflow>(  );
        ResourceWorkflow resourceWorkflow = null;

        String strSQL = buildFilterQuerydHeader( filter, null, SQL_QUERY_SELECT_STATE_BY_FILTER, null );

        if ( filter.containsWorkgroupKeyList(  ) )
        {
            ReferenceList workgroupList = filter.getWorkgroupKeyList(  );
            filter.setWorkgroupKeyList( null );

            StringBuffer bufSQL = new StringBuffer(  );
            bufSQL.append( strSQL ).append( SQL_CLOSE_UNION )
                  .append( buildFilterQuerydHeader( filter, null, SQL_QUERY_SELECT_STATE_BY_FILTER, null ) )
                  .append( SQL_FILTER_NOT_ASSOCIATE_WITH_WORKGROUP );
            filter.setWorkgroupKeyList( workgroupList );
            strSQL = bufSQL.toString(  );
        }

        int nPos = 0;

        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
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
     * Select All resourceWorkflow associated to the workflow
     * @param nIdWorkflow workflow id
     * @param plugin the plugin
     * @return List of Id resource
     */
    public List<Integer> selectResourceIdByWorkflow( int nIdWorkflow, Plugin plugin )
    {
        List<Integer> lisResourceWorkflow = new ArrayList<Integer>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ID_RESOURCE_BY_WORKFLOW, plugin );
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

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.IResourceWorkflowDAO#getListResourceWorkflowIdByFilter(fr.paris.lutece.plugins.workflow.business.ResourceWorkflowFilter, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public List<Integer> getListResourceWorkflowIdByFilter( ResourceWorkflowFilter filter, Plugin plugin )
    {
        List<Integer> listResourceWorkflowId = new ArrayList<Integer>(  );

        String strSQL = buildFilterQuerydHeader( filter, null, SQL_QUERY_SELECT_STATE_ID_BY_FILTER, null );

        if ( filter.containsWorkgroupKeyList(  ) )
        {
            ReferenceList workgroupList = filter.getWorkgroupKeyList(  );
            filter.setWorkgroupKeyList( null );

            StringBuffer bufSQL = new StringBuffer(  );
            bufSQL.append( strSQL ).append( SQL_CLOSE_UNION )
                  .append( buildFilterQuerydHeader( filter, null, SQL_QUERY_SELECT_STATE_ID_BY_FILTER, null ) )
                  .append( SQL_FILTER_NOT_ASSOCIATE_WITH_WORKGROUP );
            filter.setWorkgroupKeyList( workgroupList );
            strSQL = bufSQL.toString(  );
        }

        int nPos = 0;

        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );

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

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.IResourceWorkflowDAO#getListResourceWorkflowIdByFilter(fr.paris.lutece.plugins.workflow.business.ResourceWorkflowFilter, java.util.List, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public List<Integer> getListResourceWorkflowIdByFilter( ResourceWorkflowFilter filter,
        List<Integer> lListIdWorkflowState, Plugin plugin )
    {
        List<Integer> listResourceWorkflowId = new ArrayList<Integer>(  );

        filter.setIdState( ResourceWorkflowFilter.ALL_INT );

        String strSQL = buildFilterQuerydHeader( filter, lListIdWorkflowState, SQL_QUERY_SELECT_STATE_ID_BY_FILTER,
                SQL_ORDER_BY_ID_STATE );

        if ( filter.containsWorkgroupKeyList(  ) )
        {
            ReferenceList workgroupList = filter.getWorkgroupKeyList(  );
            filter.setWorkgroupKeyList( null );

            StringBuffer bufSQL = new StringBuffer(  );
            bufSQL.append( strSQL ).append( SQL_CLOSE_UNION )
                  .append( buildFilterQuerydHeader( filter, lListIdWorkflowState, SQL_QUERY_SELECT_STATE_ID_BY_FILTER,
                    null ) ).append( SQL_FILTER_NOT_ASSOCIATE_WITH_WORKGROUP );
            filter.setWorkgroupKeyList( workgroupList );
            strSQL = bufSQL.toString(  );
        }

        Integer nPos = 0;

        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );

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
     * Build filter SQL query footer
     * @param daoUtil the DaoUtil
     * @param filter the filter
     * @param nPos the parameter position
     * @return the doaUtil
     */
    private Integer buildFilterQuerydFooter( DAOUtil daoUtil, ResourceWorkflowFilter filter,
        List<Integer> lListIdState, Integer nPos )
    {
        if ( filter.containsIdWorkflow(  ) )
        {
            daoUtil.setInt( ++nPos, filter.getIdWorkflow(  ) );
        }

        if ( ( lListIdState == null ) && filter.containsIdState(  ) )
        {
            daoUtil.setInt( ++nPos, filter.getIdState(  ) );
        }

        if ( filter.containsResourceType(  ) )
        {
            daoUtil.setString( ++nPos, filter.getResourceType(  ) );
        }

        if ( filter.containsExternalParentId(  ) )
        {
            daoUtil.setInt( ++nPos, filter.getExternalParentId(  ) );
        }

        if ( lListIdState != null )
        {
            int nSize = lListIdState.size(  );

            for ( int i = 0; i < nSize; i++ )
            {
                daoUtil.setInt( i + nPos + 1, lListIdState.get( i ) );
            }

            nPos += nSize;
        }

        if ( filter.containsWorkgroupKeyList(  ) )
        {
            ReferenceList list = filter.getWorkgroupKeyList(  );
            int nSize = list.size(  );

            for ( int i = 0; i < nSize; i++ )
            {
                daoUtil.setString( i + nPos + 1, list.get( i ).getCode(  ) );
            }

            nPos += nSize;
        }

        return nPos;
    }

    /**
     * Build filter SQL query header
     * @param filter the filter
     * @param strSelectSQL The beginning of sql query
     * @return the SQL query
     */
    private String buildFilterQuerydHeader( ResourceWorkflowFilter filter, List<Integer> lListIdState,
        String strSelectSQL, String strOderBy )
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
            ReferenceList list = filter.getWorkgroupKeyList(  );
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
            strSQL = WorkflowUtils.buildRequestWithFilter( strSelectSQL, listStrFilter, strOderBy );
        }

        return strSQL;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.IResourceWorkflowDAO#getListIdStateByListId(java.util.List, int, java.lang.String, java.lang.Integer, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public HashMap<Integer, Integer> getListIdStateByListId( List<Integer> lListIdRessource, int nIdWorflow,
        String strResourceType, Integer nIdExternalParentId, Plugin plugin )
    {
        HashMap<Integer, Integer> result = new HashMap<Integer, Integer>(  );
        int nlistIdResourceSize = lListIdRessource.size(  );

        if ( nlistIdResourceSize > 0 )
        {
            StringBuffer sbSQL = new StringBuffer( SQL_QUERY_SELECT_ID_STATE );

            for ( int i = 1; i < nlistIdResourceSize; i++ )
            {
                sbSQL.append( SQL_ADITIONAL_PARAMETER );
            }

            sbSQL.append( SQL_CLOSE_PARENTHESIS );

            DAOUtil daoUtil = new DAOUtil( sbSQL.toString(  ), plugin );
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
}
