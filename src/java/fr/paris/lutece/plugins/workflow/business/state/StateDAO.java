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
package fr.paris.lutece.plugins.workflow.business.state;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.icon.Icon;
import fr.paris.lutece.plugins.workflowcore.business.state.IStateDAO;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.util.sql.DAOUtil;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * StateDAO
 *
 */
public class StateDAO implements IStateDAO
{
    private static final String SQL_SELECT_ALL = "SELECT s.id_state,s.name,s.description,s.id_workflow,s.is_initial_state,s.is_required_workgroup_assigned,s.display_order,s.id_icon ";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = SQL_SELECT_ALL + " FROM workflow_state s WHERE s.id_state=?";
    private static final String SQL_QUERY_FIND_BY_RESSOURCE = SQL_SELECT_ALL
            + " FROM workflow_state s INNER JOIN workflow_resource_workflow r ON (r.id_state = s.id_state) WHERE r.id_resource=? AND r.id_workflow=? AND r.resource_type=?";
    private static final String SQL_QUERY_SELECT_STATE_BY_FILTER = SQL_SELECT_ALL + " FROM workflow_state s ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO  workflow_state "
            + "(name,description,id_workflow,is_initial_state,is_required_workgroup_assigned,display_order,id_icon)VALUES(?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_state SET name=?,description=?,id_workflow=?,is_initial_state=?,is_required_workgroup_assigned=?, display_order=?, id_icon=? WHERE id_state=?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_state  WHERE id_state=? ";
    private static final String SQL_FILTER_ID_WORKFLOW = " s.id_workflow = ? ";
    private static final String SQL_FILTER_IS_INITIAL_STATE = " s.is_initial_state = ? ";
    private static final String SQL_ORDER_BY_STATE_ORDER = " ORDER BY display_order ";
    private static final String SQL_QUERY_FIND_MAXIMUM_ORDER_BY_WORKFLOW = "SELECT MAX(display_order) FROM workflow_state WHERE id_workflow=?";
    private static final String SQL_QUERY_DECREMENT_ORDER = "UPDATE workflow_state SET display_order = display_order - 1 WHERE display_order > ? AND id_workflow=? ";
    private static final String SQL_QUERY_STATES_WITH_ORDER_BETWEEN = SQL_SELECT_ALL
            + " FROM workflow_state s WHERE (s.display_order BETWEEN ? AND ?) AND s.id_workflow=?";
    private static final String SQL_QUERY_STATES_AFTER_ORDER = SQL_SELECT_ALL + " FROM workflow_state s WHERE s.display_order >=? AND s.id_workflow=?";
    private static final String SQL_QUERY_SELECT_STATE_FOR_ORDER_INIT = SQL_SELECT_ALL + " FROM workflow_state s WHERE s.id_workflow=? ORDER BY s.id_state";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( State state )
    {

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;
            daoUtil.setString( ++nPos, state.getName( ) );
            daoUtil.setString( ++nPos, state.getDescription( ) );
            daoUtil.setInt( ++nPos, state.getWorkflow( ).getId( ) );
            daoUtil.setBoolean( ++nPos, state.isInitialState( ) );
            daoUtil.setBoolean( ++nPos, state.isRequiredWorkgroupAssigned( ) );
            daoUtil.setInt( ++nPos, state.getOrder( ) );

            if ( ( state.getIcon( ) == null ) || ( state.getIcon( ).getId( ) == -1 ) )
            {
                daoUtil.setIntNull( ++nPos );
            }
            else
            {
                daoUtil.setInt( ++nPos, state.getIcon( ).getId( ) );
            }

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                state.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( State state )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;

            daoUtil.setString( ++nPos, state.getName( ) );
            daoUtil.setString( ++nPos, state.getDescription( ) );
            daoUtil.setInt( ++nPos, state.getWorkflow( ).getId( ) );
            daoUtil.setBoolean( ++nPos, state.isInitialState( ) );
            daoUtil.setBoolean( ++nPos, state.isRequiredWorkgroupAssigned( ) );
            daoUtil.setInt( ++nPos, state.getOrder( ) );

            if ( ( state.getIcon( ) == null ) || ( state.getIcon( ).getId( ) == -1 ) || ( state.getIcon( ).getId( ) == 0 ) )
            {
                daoUtil.setIntNull( ++nPos );
            }
            else
            {
                daoUtil.setInt( ++nPos, state.getIcon( ).getId( ) );
            }

            daoUtil.setInt( ++nPos, state.getId( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public State load( int nIdState )
    {
        State state = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdState );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                state = dataToObject( daoUtil );
            }
        }
        return state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public State findByResource( int nIdResource, String strResourceType, int nIdWorkflow )
    {
        State state = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_RESSOURCE, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdResource );
            daoUtil.setInt( 2, nIdWorkflow );
            daoUtil.setString( 3, strResourceType );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                state = dataToObject( daoUtil );
            }
        }
        return state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdState )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdState );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findMaximumOrderByWorkflowId( int nWorkflowId )
    {
        int nMaximumOrder = 0;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_MAXIMUM_ORDER_BY_WORKFLOW, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nWorkflowId );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                nMaximumOrder = daoUtil.getInt( 1 );
            }
        }
        return nMaximumOrder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<State> selectStatesByFilter( StateFilter filter )
    {
        List<State> listState = new ArrayList<>( );
        List<String> listStrFilter = new ArrayList<>( );

        if ( filter.containsIdWorkflow( ) )
        {
            listStrFilter.add( SQL_FILTER_ID_WORKFLOW );
        }

        if ( filter.containsIsInitialState( ) )
        {
            listStrFilter.add( SQL_FILTER_IS_INITIAL_STATE );
        }

        String strSQL = WorkflowUtils.buildRequestWithFilter( SQL_QUERY_SELECT_STATE_BY_FILTER, listStrFilter, SQL_ORDER_BY_STATE_ORDER );

        try ( DAOUtil daoUtil = new DAOUtil( strSQL, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;
            if ( filter.containsIdWorkflow( ) )
            {
                daoUtil.setInt( ++nPos, filter.getIdWorkflow( ) );
            }

            if ( filter.containsIsInitialState( ) )
            {
                daoUtil.setInt( ++nPos, filter.getIsInitialState( ) );
            }

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                nPos = 0;
                State state = dataToObject( daoUtil );
                listState.add( state );
            }

        }

        return listState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void decrementOrderByOne( int nOrder, int nIdWorkflow )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DECREMENT_ORDER, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nOrder );
            daoUtil.setInt( 2, nIdWorkflow );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<State> findStatesBetweenOrders( int nOrder1, int nOrder2, int nIdWorkflow )
    {
        List<State> listResult = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_STATES_WITH_ORDER_BETWEEN, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nOrder1 );
            daoUtil.setInt( 2, nOrder2 );
            daoUtil.setInt( 3, nIdWorkflow );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                State state = dataToObject( daoUtil );
                listResult.add( state );
            }
        }
        return listResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<State> findStatesAfterOrder( int nOrder, int nIdWorkflow )
    {
        List<State> listResult = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_STATES_AFTER_ORDER, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;
            daoUtil.setInt( ++nPos, nOrder );
            daoUtil.setInt( ++nPos, nIdWorkflow );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                State state = dataToObject( daoUtil );
                listResult.add( state );
            }
        }
        return listResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<State> findStatesForOrderInit( int nIdWorkflow )
    {
        List<State> listResult = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_STATE_FOR_ORDER_INIT, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdWorkflow );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                State state = dataToObject( daoUtil );
                listResult.add( state );
            }
        }
        return listResult;
    }

    private State dataToObject( DAOUtil daoUtil )
    {
        int nPos = 0;
        State state = new State( );
        state.setId( daoUtil.getInt( ++nPos ) );
        state.setName( daoUtil.getString( ++nPos ) );
        state.setDescription( daoUtil.getString( ++nPos ) );

        Workflow workflow = new Workflow( );
        workflow.setId( daoUtil.getInt( ++nPos ) );
        state.setWorkflow( workflow );
        state.setInitialState( daoUtil.getBoolean( ++nPos ) );
        state.setRequiredWorkgroupAssigned( daoUtil.getBoolean( ++nPos ) );
        state.setOrder( daoUtil.getInt( ++nPos ) );

        Icon icon = new Icon( );
        icon.setId( daoUtil.getInt( ++nPos ) );
        state.setIcon( icon );

        return state;
    }
}
