/*
 * Copyright (c) 2002-2025, City of Paris
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

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.IActionStateDAO;
import fr.paris.lutece.util.sql.DAOUtil;

public class ActionStateDAO implements IActionStateDAO 
{
	private static final String SQL_QUERY_SELECT_ALL = "SELECT id_action,id_state_before ";
    private static final String SQL_QUERY_FIND_BY_ID_ACTION = "SELECT id_state_before FROM workflow_action_state_before WHERE id_action= ? ";
    private static final String SQL_QUERY_FIND_BY_UID_ACTION = "SELECT uid_state FROM workflow_action_state_before asb, workflow_state s, workflow_action a WHERE asb.id_state_before = s.id_state AND a.id_action = asb.id_action and a.uid_action = ?";
	private static final String SQL_QUERY_INSERT = "INSERT INTO workflow_action_state_before "
            + "(id_action,id_state_before)"
            + " VALUES(?,?)";
	private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_action_state_before  WHERE id_action=? ";
	
	/**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( int nIdAction, List<Integer> listIdStateBefore )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, WorkflowUtils.getPlugin( ) ) )
        {
        	for ( Integer nIdStateBefore : listIdStateBefore )
        	{
	            int nPos = 0;
	            daoUtil.setInt( ++nPos, nIdAction );
	            daoUtil.setInt( ++nPos, nIdStateBefore );
	            daoUtil.addBatch( );
        	}
            //daoUtil.executeUpdate( );
        	daoUtil.executeBatch( );
            
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> load( int nIdAction )
    {
        List<Integer> listState = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_ID_ACTION, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdAction );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
            	int nPos = 0;
                listState.add( Integer.valueOf(daoUtil.getInt( ++nPos ) ) );
            }

        }
        return listState;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> load( String strUidAction )
    {
        List<String> listState = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_UID_ACTION, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setString( 1, strUidAction );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
            	int nPos = 0;
                listState.add( String.valueOf(daoUtil.getString( ++nPos ) ) );
            }

        }
        return listState;
    }

	@Override
	public void delete(int nIdAction) {
		try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdAction );
            daoUtil.executeUpdate( );
        }
		
	}

}
