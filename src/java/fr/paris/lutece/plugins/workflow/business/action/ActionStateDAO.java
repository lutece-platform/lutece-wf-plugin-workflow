package fr.paris.lutece.plugins.workflow.business.action;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.IActionStateDAO;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.util.sql.DAOUtil;

public class ActionStateDAO implements IActionStateDAO 
{
	private static final String SQL_QUERY_SELECT_ALL = "SELECT id_action,id_state_before ";
    private static final String SQL_QUERY_FIND_BY_ID_ACTION = "SELECT id_state_before FROM workflow_action_state_before WHERE id_action= ? ";
	private static final String SQL_QUERY_INSERT = "INSERT INTO workflow_action_state_before "
            + "(id_action,id_state_before)"
            + " VALUES(?,?)";
	private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_action_state_before  WHERE id_action=? ";
	private static final String SQL_QUERY_UPDATE = "UPDATE workflow_action_state_before  SET id_action=?,id_state_before=? "
            + " WHERE id_action=? AND id_state_before = ?";
	
	/**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( int nIdAction, int nIdState )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;
            daoUtil.setInt( ++nPos, nIdAction );
            daoUtil.setInt( ++nPos, nIdState );

            daoUtil.executeUpdate( );

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( int nNewIdAction, int nNewIdState, int nOldIdAction, int nOldIdState )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, WorkflowUtils.getPlugin( ) ) )
        {
            int nPos = 0;

            daoUtil.setInt( ++nPos, nNewIdAction );
            daoUtil.setInt( ++nPos, nNewIdState );

            daoUtil.setInt( ++nPos, nOldIdAction );
            daoUtil.setInt( ++nPos, nOldIdState );

            daoUtil.executeUpdate( );
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

	@Override
	public void delete(int nIdAction) {
		try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdAction );
            daoUtil.executeUpdate( );
        }
		
	}

}
