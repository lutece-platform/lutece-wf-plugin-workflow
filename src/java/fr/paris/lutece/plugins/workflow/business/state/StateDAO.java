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
package fr.paris.lutece.plugins.workflow.business.state;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.icon.Icon;
import fr.paris.lutece.plugins.workflowcore.business.state.IStateDAO;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * StateDAO
 *
 */
public class StateDAO implements IStateDAO
{
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_state ) FROM workflow_state";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_state,name,description,id_workflow,is_initial_state,is_required_workgroup_assigned,id_icon" +
        " FROM workflow_state WHERE id_state=?";
    private static final String SQL_QUERY_FIND_BY_RESSOURCE = "SELECT s.id_state,s.name,s.description,s.id_workflow,s.is_initial_state,s.is_required_workgroup_assigned,s.id_icon" +
        " FROM workflow_state s INNER JOIN workflow_resource_workflow r ON (r.id_state = s.id_state) WHERE r.id_resource=? AND r.id_workflow=? AND r.resource_type=?";
    private static final String SQL_QUERY_SELECT_STATE_BY_FILTER = "SELECT id_state,name,description,id_workflow,is_initial_state,is_required_workgroup_assigned,id_icon" +
        " FROM workflow_state ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO  workflow_state " +
        "(id_state,name,description,id_workflow,is_initial_state,is_required_workgroup_assigned,id_icon)VALUES(?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_state  SET id_state=?,name=?,description=?,id_workflow=?,is_initial_state=?,is_required_workgroup_assigned=?,id_icon=?" +
        " WHERE id_state=?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_state  WHERE id_state=? ";
    private static final String SQL_FILTER_ID_WORKFLOW = " id_workflow = ? ";
    private static final String SQL_FILTER_IS_INITIAL_STATE = " is_initial_state = ? ";
    private static final String SQL_ORDER_BY_ID_STATE = " ORDER BY id_state ";

    /**
     * Generates a new primary key
     *
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
    public synchronized void insert( State state )
    {
        int nPos = 0;
        state.setId( newPrimaryKey( WorkflowUtils.getPlugin(  ) ) );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, WorkflowUtils.getPlugin(  ) );
        daoUtil.setInt( ++nPos, state.getId(  ) );
        daoUtil.setString( ++nPos, state.getName(  ) );
        daoUtil.setString( ++nPos, state.getDescription(  ) );
        daoUtil.setInt( ++nPos, state.getWorkflow(  ).getId(  ) );
        daoUtil.setBoolean( ++nPos, state.isInitialState(  ) );
        daoUtil.setBoolean( ++nPos, state.isRequiredWorkgroupAssigned(  ) );

        if ( ( state.getIcon(  ) == null ) || ( state.getIcon(  ).getId(  ) == -1 ) )
        {
            daoUtil.setIntNull( ++nPos );
        }
        else
        {
            daoUtil.setInt( ++nPos, state.getIcon(  ).getId(  ) );
        }

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( State state )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, WorkflowUtils.getPlugin(  ) );

        int nPos = 0;

        daoUtil.setInt( ++nPos, state.getId(  ) );
        daoUtil.setString( ++nPos, state.getName(  ) );
        daoUtil.setString( ++nPos, state.getDescription(  ) );
        daoUtil.setInt( ++nPos, state.getWorkflow(  ).getId(  ) );
        daoUtil.setBoolean( ++nPos, state.isInitialState(  ) );
        daoUtil.setBoolean( ++nPos, state.isRequiredWorkgroupAssigned(  ) );

        if ( ( state.getIcon(  ) == null ) || ( state.getIcon(  ).getId(  ) == -1 ) )
        {
            daoUtil.setIntNull( ++nPos );
        }
        else
        {
            daoUtil.setInt( ++nPos, state.getIcon(  ).getId(  ) );
        }

        daoUtil.setInt( ++nPos, state.getId(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public State load( int nIdState )
    {
        State state = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, WorkflowUtils.getPlugin(  ) );

        daoUtil.setInt( 1, nIdState );

        daoUtil.executeQuery(  );

        int nPos = 0;

        if ( daoUtil.next(  ) )
        {
            state = new State(  );
            state.setId( daoUtil.getInt( ++nPos ) );
            state.setName( daoUtil.getString( ++nPos ) );
            state.setDescription( daoUtil.getString( ++nPos ) );

            Workflow workflow = new Workflow(  );
            workflow.setId( daoUtil.getInt( ++nPos ) );
            state.setInitialState( daoUtil.getBoolean( ++nPos ) );
            state.setRequiredWorkgroupAssigned( daoUtil.getBoolean( ++nPos ) );
            state.setWorkflow( workflow );

            Icon icon = new Icon(  );
            icon.setId( daoUtil.getInt( ++nPos ) );
            state.setIcon( icon );
        }

        daoUtil.free(  );

        return state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public State findByResource( int nIdResource, String strResourceType, int nIdWorkflow )
    {
        State state = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_RESSOURCE, WorkflowUtils.getPlugin(  ) );
        daoUtil.setInt( 1, nIdResource );
        daoUtil.setInt( 2, nIdWorkflow );
        daoUtil.setString( 3, strResourceType );

        daoUtil.executeQuery(  );

        int nPos = 0;

        if ( daoUtil.next(  ) )
        {
            state = new State(  );
            state.setId( daoUtil.getInt( ++nPos ) );
            state.setName( daoUtil.getString( ++nPos ) );
            state.setDescription( daoUtil.getString( ++nPos ) );

            Workflow workflow = new Workflow(  );
            workflow.setId( daoUtil.getInt( ++nPos ) );
            state.setInitialState( daoUtil.getBoolean( ++nPos ) );
            state.setRequiredWorkgroupAssigned( daoUtil.getBoolean( ++nPos ) );
            state.setWorkflow( workflow );

            Icon icon = new Icon(  );
            icon.setId( daoUtil.getInt( ++nPos ) );
            state.setIcon( icon );
        }

        daoUtil.free(  );

        return state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdState )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowUtils.getPlugin(  ) );

        daoUtil.setInt( 1, nIdState );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<State> selectStatesByFilter( StateFilter filter )
    {
        List<State> listState = new ArrayList<State>(  );
        State state = null;
        List<String> listStrFilter = new ArrayList<String>(  );
        int nPos = 0;

        if ( filter.containsIdWorkflow(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_WORKFLOW );
        }

        if ( filter.containsIsInitialState(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_INITIAL_STATE );
        }

        String strSQL = WorkflowUtils.buildRequestWithFilter( SQL_QUERY_SELECT_STATE_BY_FILTER, listStrFilter,
                SQL_ORDER_BY_ID_STATE );

        DAOUtil daoUtil = new DAOUtil( strSQL, WorkflowUtils.getPlugin(  ) );

        if ( filter.containsIdWorkflow(  ) )
        {
            daoUtil.setInt( ++nPos, filter.getIdWorkflow(  ) );
        }

        if ( filter.containsIsInitialState(  ) )
        {
            daoUtil.setInt( ++nPos, filter.getIsInitialState(  ) );
        }

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            nPos = 0;
            state = new State(  );
            state.setId( daoUtil.getInt( ++nPos ) );
            state.setName( daoUtil.getString( ++nPos ) );
            state.setDescription( daoUtil.getString( ++nPos ) );

            Workflow workflow = new Workflow(  );
            workflow.setId( daoUtil.getInt( ++nPos ) );
            state.setWorkflow( workflow );
            state.setInitialState( daoUtil.getBoolean( ++nPos ) );
            state.setRequiredWorkgroupAssigned( daoUtil.getBoolean( ++nPos ) );

            Icon icon = new Icon(  );
            icon.setId( daoUtil.getInt( ++nPos ) );
            state.setIcon( icon );
            listState.add( state );
        }

        daoUtil.free(  );

        return listState;
    }
}
