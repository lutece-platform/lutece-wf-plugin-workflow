package fr.paris.lutece.plugins.workflow.modules.state.business;

import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.util.sql.DAOUtil;

public class ChangeStateTaskConfigDao implements ITaskConfigDAO<ChangeStateTaskConfig>
{

    // Queries
    private static final String SQL_INSERT = "INSERT INTO workflow_task_change_state_config (id_task, id_next_state) VALUES (?, ?)";
    private static final String SQL_UPDATE = "UPDATE workflow_task_change_state_config SET id_next_state = ? WHERE id_task = ?";
    private static final String SQL_DELETE = "DELETE FROM workflow_task_change_state_config WHERE id_task = ?";
    private static final String SQL_SELECT = "SELECT id_task, id_next_state FROM workflow_task_change_state_config WHERE id_task = ?";
    
    @Override
    public void insert( ChangeStateTaskConfig config )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_INSERT ) )
        {
            int nPos = 0;
            daoUtil.setInt( ++nPos, config.getIdTask( ) );
            daoUtil.setInt( ++nPos, config.getIdNextState( ) );
            daoUtil.executeUpdate( );
        }
        
    }

    @Override
    public void store( ChangeStateTaskConfig config )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_UPDATE ) )
        {
            int nPos = 0;
            daoUtil.setInt( ++nPos, config.getIdNextState( ) );
            daoUtil.setInt( ++nPos, config.getIdTask( ) );

            daoUtil.executeUpdate( );
        }
        
    }

    @Override
    public ChangeStateTaskConfig load( int nIdTask )
    {
        ChangeStateTaskConfig config = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_SELECT ) )
        {
            daoUtil.setInt( 1, nIdTask );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                int nIndex = 0;
                config = new ChangeStateTaskConfig( );
                config.setIdTask( daoUtil.getInt( ++nIndex ) );
                config.setIdNextState( daoUtil.getInt( ++nIndex ) );
            }
        }
        return config;
    }

    @Override
    public void delete( int nIdTask )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_DELETE ) )
        {
            daoUtil.setInt( 1, nIdTask );

            daoUtil.executeUpdate( );
        }
    }

}
