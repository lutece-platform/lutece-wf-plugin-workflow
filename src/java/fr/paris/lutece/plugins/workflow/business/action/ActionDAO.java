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
package fr.paris.lutece.plugins.workflow.business.action;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.business.action.IActionDAO;
import fr.paris.lutece.plugins.workflowcore.business.icon.Icon;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.util.sql.DAOUtil;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/**
 *
 * ActionDAO
 *
 */
public class ActionDAO implements IActionDAO
{
    private static final String SQL_QUERY_SELECT_ALL = "SELECT a.id_action,a.name,a.description,a.id_workflow,a.id_state_before, "
            + " a.id_state_after,a.id_icon,a.is_automatic,a.is_mass_action,a.display_order,a.is_automatic_reflexive_action ";
    private static final String SQL_QUERY_SELECT_ICON = ",i.name,i.mime_type,i.file_value,i.width,i.height ";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = SQL_QUERY_SELECT_ALL + " FROM workflow_action a WHERE a.id_action=? ";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY_WITH_ICON = SQL_QUERY_SELECT_ALL + SQL_QUERY_SELECT_ICON
            + " FROM workflow_action a LEFT JOIN workflow_icon i ON (a.id_icon = i.id_icon) WHERE a.id_action=?";
    private static final String SQL_QUERY_SELECT_ACTION_BY_FILTER = SQL_QUERY_SELECT_ALL + SQL_QUERY_SELECT_ICON
            + " FROM workflow_action a LEFT JOIN workflow_icon i ON (a.id_icon = i.id_icon) ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO workflow_action "
            + "(name,description,id_workflow,id_state_before,id_state_after,id_icon,is_automatic,is_mass_action,display_order,is_automatic_reflexive_action)"
            + " VALUES(?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_action  SET id_action=?,name=?,description=?,"
            + "id_workflow=?,id_state_before=?,id_state_after=?,id_icon=?,is_automatic=?,is_mass_action=?, display_order=?, is_automatic_reflexive_action=? "
            + " WHERE id_action=?";
    private static final String SQL_QUERY_INSERT_LINKED_ACTION = " INSERT INTO workflow_action_action (id_action, id_linked_action) VALUES ( ?,? ) ";
    private static final String SQL_QUERY_REMOVE_LINKED_ACTION = " DELETE FROM workflow_action_action WHERE id_action = ? OR id_linked_action = ? ";
    private static final String SQL_QUERY_SELECT_LINKED_ACTION = " SELECT id_action FROM workflow_action_action WHERE id_linked_action = ?";
    private static final String SQL_QUERY_SELECT_LINKED_ACTION_2 = " SELECT id_linked_action FROM workflow_action_action WHERE id_action = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_action  WHERE id_action=? ";
    private static final String SQL_FILTER_ID_WORKFLOW = " a.id_workflow = ? ";
    private static final String SQL_FILTER_ID_STATE_BEFORE = " a.id_state_before= ? ";
    private static final String SQL_FILTER_ID_STATE_AFTER = " a.id_state_after = ? ";
    private static final String SQL_FILTER_ID_ICON = " a.id_icon = ? ";
    private static final String SQL_FILTER_IS_AUTOMATIC = " a.is_automatic = ? ";
    private static final String SQL_FILTER_IS_MASS_ACTION = " a.is_mass_action = ? ";
    private static final String SQL_FILTER_IS_AUTOMATIC_REFLEXIVE_ACTION = " a.is_automatic_reflexive_action = ? ";
    private static final String SQL_ORDER_BY_ORDER_DISPLAY = " ORDER BY display_order";
    private static final String SQL_QUERY_FIND_MAXIMUM_ORDER_BY_WORKFLOW = "SELECT MAX(display_order) FROM workflow_action WHERE id_workflow=?";
    private static final String SQL_QUERY_DECREMENT_ORDER = "UPDATE workflow_action SET display_order = display_order - 1 WHERE display_order > ? AND id_workflow=? ";
    private static final String SQL_QUERY_ACTIONS_WITH_ORDER_BETWEEN = SQL_QUERY_SELECT_ALL + SQL_QUERY_SELECT_ICON
            + " FROM workflow_action a LEFT JOIN workflow_icon i ON (a.id_icon = i.id_icon) WHERE (display_order BETWEEN ? AND ?) AND id_workflow=?";
    private static final String SQL_QUERY_ACTIONS_AFTER_ORDER = SQL_QUERY_SELECT_ALL + SQL_QUERY_SELECT_ICON
            + " FROM workflow_action a LEFT JOIN workflow_icon i ON (a.id_icon = i.id_icon) WHERE display_order >=? AND id_workflow=?";
    private static final String SQL_QUERY_SELECT_ACTION_FOR_ORDER_INIT = SQL_QUERY_SELECT_ALL + SQL_QUERY_SELECT_ICON
            + " FROM workflow_action a LEFT JOIN workflow_icon i ON (a.id_icon = i.id_icon) WHERE id_workflow=? AND is_automatic_reflexive_action = 0 ORDER BY id_action ";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( Action action )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;
            daoUtil.setString( ++nPos, action.getName( ) );
            daoUtil.setString( ++nPos, action.getDescription( ) );
            daoUtil.setInt( ++nPos, action.getWorkflow( ).getId( ) );
            daoUtil.setInt( ++nPos, action.getStateBefore( ).getId( ) );
            daoUtil.setInt( ++nPos, action.getStateAfter( ).getId( ) );
            daoUtil.setInt( ++nPos, action.getIcon( ).getId( ) );
            daoUtil.setBoolean( ++nPos, action.isAutomaticState( ) );
            daoUtil.setBoolean( ++nPos, action.isMassAction( ) );
            daoUtil.setInt( ++nPos, action.getOrder( ) );
            daoUtil.setBoolean( ++nPos, action.isAutomaticReflexiveAction( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                action.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( Action action )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;

            daoUtil.setInt( ++nPos, action.getId( ) );
            daoUtil.setString( ++nPos, action.getName( ) );
            daoUtil.setString( ++nPos, action.getDescription( ) );
            daoUtil.setInt( ++nPos, action.getWorkflow( ).getId( ) );
            daoUtil.setInt( ++nPos, action.getStateBefore( ).getId( ) );
            daoUtil.setInt( ++nPos, action.getStateAfter( ).getId( ) );
            daoUtil.setInt( ++nPos, action.getIcon( ).getId( ) );
            daoUtil.setBoolean( ++nPos, action.isAutomaticState( ) );
            daoUtil.setBoolean( ++nPos, action.isMassAction( ) );
            daoUtil.setInt( ++nPos, action.getOrder( ) );
            daoUtil.setBoolean( ++nPos, action.isAutomaticReflexiveAction( ) );

            daoUtil.setInt( ++nPos, action.getId( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action load( int nIdAction )
    {
        Action action = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdAction );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                action = dataToObject( daoUtil, false );
            }

        }
        return action;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action loadWithIcon( int nIdAction )
    {
        Action action = null;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY_WITH_ICON, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdAction );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                action = dataToObject( daoUtil, true );
            }
        }
        return action;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdAction )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdAction );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Action> selectActionsByFilter( ActionFilter filter )
    {
        List<Action> listAction = new ArrayList<>( );
        List<String> listStrFilter = new ArrayList<>( );

        if ( filter.containsIdWorkflow( ) )
        {
            listStrFilter.add( SQL_FILTER_ID_WORKFLOW );
        }

        if ( filter.containsIdStateBefore( ) )
        {
            listStrFilter.add( SQL_FILTER_ID_STATE_BEFORE );
        }

        if ( filter.containsIdStateAfter( ) )
        {
            listStrFilter.add( SQL_FILTER_ID_STATE_AFTER );
        }

        if ( filter.containsIdIcon( ) )
        {
            listStrFilter.add( SQL_FILTER_ID_ICON );
        }

        if ( filter.containsIsAutomaticState( ) )
        {
            listStrFilter.add( SQL_FILTER_IS_AUTOMATIC );
        }

        if ( filter.containsIsMassAction( ) )
        {
            listStrFilter.add( SQL_FILTER_IS_MASS_ACTION );
        }
        if ( filter.containsIsAutomaticReflexiveAction( ) )
        {
            listStrFilter.add( SQL_FILTER_IS_AUTOMATIC_REFLEXIVE_ACTION );
        }

        String strSQL = WorkflowUtils.buildRequestWithFilter( SQL_QUERY_SELECT_ACTION_BY_FILTER, listStrFilter, SQL_ORDER_BY_ORDER_DISPLAY );

        try ( DAOUtil daoUtil = new DAOUtil( strSQL, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;
            if ( filter.containsIdWorkflow( ) )
            {
                daoUtil.setInt( ++nPos, filter.getIdWorkflow( ) );
            }

            if ( filter.containsIdStateBefore( ) )
            {
                daoUtil.setInt( ++nPos, filter.getIdStateBefore( ) );
            }

            if ( filter.containsIdStateAfter( ) )
            {
                daoUtil.setInt( ++nPos, filter.getIdStateAfter( ) );
            }

            if ( filter.containsIdIcon( ) )
            {
                daoUtil.setInt( ++nPos, filter.getIdIcon( ) );
            }

            if ( filter.containsIsAutomaticState( ) )
            {
                daoUtil.setInt( ++nPos, filter.getIsAutomaticState( ) );
            }

            if ( filter.containsIsMassAction( ) )
            {
                daoUtil.setInt( ++nPos, filter.getIsMassAction( ) );
            }

            if ( filter.containsIsAutomaticReflexiveAction( ) )
            {
                daoUtil.setInt( ++nPos, filter.isAutomaticReflexiveAction( ) );
            }

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                Action action = dataToObject( daoUtil, true );
                listAction.add( action );
            }

        }

        return listAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insertLinkedActions( int nIdAction, int nIdLinkedAction )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_LINKED_ACTION, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;
            daoUtil.setInt( ++nPos, nIdAction );
            daoUtil.setInt( ++nPos, nIdLinkedAction );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeLinkedActions( int nIdAction )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_REMOVE_LINKED_ACTION, WorkflowUtils.getPlugin( ) ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, nIdAction );
            daoUtil.setInt( nIndex, nIdAction );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Integer> selectListIdsLinkedAction( int nIdAction )
    {
        Collection<Integer> listIdsLinkedAction = new LinkedHashSet<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_LINKED_ACTION, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdAction );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listIdsLinkedAction.add( daoUtil.getInt( 1 ) );
            }
        }

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_LINKED_ACTION_2, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdAction );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listIdsLinkedAction.add( daoUtil.getInt( 1 ) );
            }
        }
        return listIdsLinkedAction;
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
    public List<Action> findStatesBetweenOrders( int nOrder1, int nOrder2, int nIdWorkflow )
    {
        List<Action> listResult = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_ACTIONS_WITH_ORDER_BETWEEN, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nOrder1 );
            daoUtil.setInt( 2, nOrder2 );
            daoUtil.setInt( 3, nIdWorkflow );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                Action action = dataToObject( daoUtil, true );
                listResult.add( action );
            }
        }

        return listResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Action> findStatesAfterOrder( int nOrder, int nIdWorkflow )
    {
        List<Action> listResult = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_ACTIONS_AFTER_ORDER, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nOrder );
            daoUtil.setInt( 2, nIdWorkflow );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                Action action = dataToObject( daoUtil, true );
                listResult.add( action );
            }
        }
        return listResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Action> findActionsForOrderInit( int nIdWorkflow )
    {
        List<Action> listAction = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ACTION_FOR_ORDER_INIT, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdWorkflow );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                Action action = dataToObject( daoUtil, true );
                listAction.add( action );
            }
        }

        return listAction;
    }

    private Action dataToObject( DAOUtil daoUtil, boolean populateIcon )
    {
        int nPos = 0;
        Action action = new Action( );
        action.setId( daoUtil.getInt( ++nPos ) );
        action.setName( daoUtil.getString( ++nPos ) );
        action.setDescription( daoUtil.getString( ++nPos ) );

        Workflow workflow = new Workflow( );
        workflow.setId( daoUtil.getInt( ++nPos ) );
        action.setWorkflow( workflow );

        State stateBefore = new State( );
        stateBefore.setId( daoUtil.getInt( ++nPos ) );
        action.setStateBefore( stateBefore );

        State stateAfter = new State( );
        stateAfter.setId( daoUtil.getInt( ++nPos ) );
        action.setStateAfter( stateAfter );

        Icon icon = new Icon( );
        icon.setId( daoUtil.getInt( ++nPos ) );
        action.setIcon( icon );

        action.setAutomaticState( daoUtil.getBoolean( ++nPos ) );
        action.setMassAction( daoUtil.getBoolean( ++nPos ) );
        action.setOrder( daoUtil.getInt( ++nPos ) );
        action.setAutomaticReflexiveAction( daoUtil.getBoolean( ++nPos ) );

        if ( populateIcon )
        {
            icon.setName( daoUtil.getString( ++nPos ) );
            icon.setMimeType( daoUtil.getString( ++nPos ) );
            icon.setValue( daoUtil.getBytes( ++nPos ) );
            icon.setWidth( daoUtil.getInt( ++nPos ) );
            icon.setHeight( daoUtil.getInt( ++nPos ) );
        }
        return action;
    }
}
