package fr.paris.lutece.plugins.workflow.business.action;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.IActionStateDAO;
import fr.paris.lutece.util.sql.DAOUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@ApplicationScoped
@Named( "workflow.actionStateDAO" )
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
