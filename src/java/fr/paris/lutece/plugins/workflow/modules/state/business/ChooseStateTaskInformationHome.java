package fr.paris.lutece.plugins.workflow.modules.state.business;

import fr.paris.lutece.portal.service.spring.SpringContextService;

public final class ChooseStateTaskInformationHome
{

    private static IChooseStateTaskInformationDAO _dao = SpringContextService.getBean( "workflow.chooseStateTaskInformationDao" );

    /**
     * Private constructor
     */
    private ChooseStateTaskInformationHome( )
    {
        super( );
    }

    /**
     * Creates a task information
     * 
     * @param taskInformation
     *            The task information to create
     * @return The task information which has been created
     */
    public static ChooseStateTaskInformation create( ChooseStateTaskInformation taskInformation )
    {
        _dao.insert( taskInformation );

        return taskInformation;
    }

    /**
     * Finds the task information for the specified couple {history id, task id}
     * 
     * @param nIdHistory
     *            the history id
     * @param nIdTask
     *            the task id
     * @return the task information
     */
    public static ChooseStateTaskInformation find( int nIdHistory, int nIdTask )
    {
        return _dao.load( nIdHistory, nIdTask );
    }
    
    /**
     * Deletes the task information for the specified couple {history id, task id}
     * @param nIdHistory
     * @param nIdTask
     */
    public static void remove( int nIdHistory, int nIdTask )
    {
        _dao.delete( nIdHistory, nIdTask );
    }
}
