package fr.paris.lutece.plugins.workflow.modules.state.business;

import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.util.sql.DAOUtil;

public class ChooseStateTaskConfigDao implements ITaskConfigDAO<ChooseStateTaskConfig>
{

    // Queries
    private static final String SQL_INSERT = "INSERT INTO workflow_task_choose_state_config (id_task, controller_name, id_state_ok, id_state_ko) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE workflow_task_choose_state_config SET controller_name = ?, id_state_ok = ?, id_state_ko = ? WHERE id_task = ?";
    private static final String SQL_DELETE = "DELETE FROM workflow_task_choose_state_config WHERE id_task = ?";
    private static final String SQL_SELECT = "SELECT id_task, controller_name, id_state_ok, id_state_ko FROM workflow_task_choose_state_config WHERE id_task = ?";

    @Override
    public void insert( ChooseStateTaskConfig config )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_INSERT ) )
        {
            int nPos = 1;
            daoUtil.setInt( nPos++, config.getIdTask( ) );
            daoUtil.setString( nPos++, config.getControllerName( ) );
            daoUtil.setInt( nPos++, config.getIdStateOK( ) );
            daoUtil.setInt( nPos++, config.getIdStateKO( ) );
            daoUtil.executeUpdate( );
        }

    }

    @Override
    public void store( ChooseStateTaskConfig config )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_UPDATE ) )
        {
            int nPos = 1;
            daoUtil.setString( nPos++, config.getControllerName( ) );
            daoUtil.setInt( nPos++, config.getIdStateOK( ) );
            daoUtil.setInt( nPos++, config.getIdStateKO( ) );
            daoUtil.setInt( nPos++, config.getIdTask( ) );

            daoUtil.executeUpdate( );
        }

    }

    @Override
    public ChooseStateTaskConfig load( int nIdTask )
    {
        ChooseStateTaskConfig config = null;

        try( DAOUtil daoUtil = new DAOUtil( SQL_SELECT ) )
        {
            int nPos = 1;
            daoUtil.setInt( nPos++, nIdTask );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                int nIndex = 1;
                config = new ChooseStateTaskConfig( );
                config.setIdTask( daoUtil.getInt( nIndex++ ) );
                config.setControllerName( daoUtil.getString( nIndex++ ) );
                config.setIdStateOK( daoUtil.getInt( nIndex++ ) );
                config.setIdStateKO( daoUtil.getInt( nIndex++ ) );
            }
        }
        return config;
    }

    @Override
    public void delete( int nIdTask )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_DELETE ) )
        {
            int nPos = 1;
            daoUtil.setInt( nPos++, nIdTask );

            daoUtil.executeUpdate( );
        }
    }
}
