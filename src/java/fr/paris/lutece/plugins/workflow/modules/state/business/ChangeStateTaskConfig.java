package fr.paris.lutece.plugins.workflow.modules.state.business;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import fr.paris.lutece.plugins.workflowcore.business.config.TaskConfig;

public class ChangeStateTaskConfig extends TaskConfig
{
    @NotNull
    @Min( 1 )
    private int _nIdNextState;

    /**
     * @return the nIdNextState
     */
    public int getIdNextState( )
    {
        return _nIdNextState;
    }

    /**
     * @param nIdNextState the nIdNextState to set
     */
    public void setIdNextState( int nIdNextState )
    {
        _nIdNextState = nIdNextState;
    }
    
}
