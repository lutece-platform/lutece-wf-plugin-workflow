package fr.paris.lutece.plugins.workflow.modules.state.business;

import fr.paris.lutece.util.sql.DAOUtil;

public class ChooseStateTaskInformationDAO implements IChooseStateTaskInformationDAO
{

    private static final String SQL_QUERY_SELECT = "SELECT id_history, id_task, new_state FROM workflow_task_choose_state_information WHERE id_history = ? AND id_task = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO workflow_task_choose_state_information ( id_history, id_task, new_state ) VALUES (?,?,?) ";

    @Override
    public void insert( ChooseStateTaskInformation taskInformation )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT ) )
        {
            int index = 1;
            daoUtil.setInt( index++, taskInformation.getIdHistory( ) );
            daoUtil.setInt( index++, taskInformation.getIdTask( ) );
            daoUtil.setString( index++, taskInformation.getState( ) );

            daoUtil.executeUpdate( );
        }

    }

    @Override
    public ChooseStateTaskInformation load( int nIdHistory, int nIdTask )
    {
        ChooseStateTaskInformation res = null;
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT ) )
        {
            int index = 1;
            daoUtil.setInt( index++, nIdHistory );
            daoUtil.setInt( index++, nIdTask );

            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                index = 1;
                res = new ChooseStateTaskInformation( );
                res.setIdHistory( daoUtil.getInt( index++ ) );
                res.setIdTask( daoUtil.getInt( index++ ) );
                res.setNewState( daoUtil.getString( index++ ) );
            }
        }
        return res;
    }

}
