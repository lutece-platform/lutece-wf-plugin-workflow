package fr.paris.lutece.plugins.workflow.modules.state.business;

public class ChooseStateTaskInformation
{

    private int _nIdHistory;
    private int _nIdTask;
    private String _newState;

    /**
     * @return the _nIdHistory
     */
    public int getIdHistory( )
    {
        return _nIdHistory;
    }

    /**
     * @return the _nIdTask
     */
    public int getIdTask( )
    {
        return _nIdTask;
    }

    /**
     * @return the _newState
     */
    public String getState( )
    {
        return _newState;
    }

    /**
     * @param nIdHistory
     *            the nIdHistory to set
     */
    public void setIdHistory( int nIdHistory )
    {
        _nIdHistory = nIdHistory;
    }

    /**
     * @param nIdTask
     *            the nIdTask to set
     */
    public void setIdTask( int nIdTask )
    {
        _nIdTask = nIdTask;
    }

    /**
     * @param newState
     *            the newState to set
     */
    public void setNewState( String newState )
    {
        _newState = newState;
    }
}
