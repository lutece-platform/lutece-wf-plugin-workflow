package fr.paris.lutece.plugins.workflow.modules.state.business;

public interface IChooseStateTaskInformationDAO
{

    /**
     * Inserts a new record in the table.
     * 
     * @param taskInformation
     *            the task information to insert
     */
    void insert( ChooseStateTaskInformation taskInformation );

    /**
     * Loads the data from the table
     * 
     * @param nIdHistory
     *            The history id
     * @param nIdTask
     *            The task id
     * @return The task information
     */
    ChooseStateTaskInformation load( int nIdHistory, int nIdTask );
}
