package fr.paris.lutece.plugins.workflow.service.json;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.prerequisite.Prerequisite;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;

public class WorkflowJsonData
{

    private Workflow _workflow;
    
    private List<State> _stateList;
    
    private List<Action> _actionList;
    
    private List<ITask> _taskList;
    
    private List<Prerequisite> _prerequisiteList;

    /**
     * @return the workflow
     */
    public Workflow getWorkflow( )
    {
        return _workflow;
    }

    /**
     * @param workflow the workflow to set
     */
    public void setWorkflow( Workflow workflow )
    {
        _workflow = workflow;
    }

    /**
     * @return the stateList
     */
    public List<State> getStateList( )
    {
        return new ArrayList<>( _stateList );
    }

    /**
     * @param stateList the stateList to set
     */
    public void setStateList( List<State> stateList )
    {
        this._stateList = new ArrayList<>( stateList );
    }

    /**
     * @return the actionList
     */
    public List<Action> getActionList( )
    {
        return new ArrayList<>( _actionList );
    }

    /**
     * @param actionList the actionList to set
     */
    public void setActionList( List<Action> actionList )
    {
        _actionList = new ArrayList<>( actionList );
    }

    /**
     * @return the taskList
     */
    public List<ITask> getTaskList( )
    {
        return new ArrayList<>( _taskList );
    }

    /**
     * @param taskList the taskList to set
     */
    public void setTaskList( List<ITask> taskList )
    {
        _taskList = new ArrayList<>( taskList );
    }

    /**
     * @return the prerequisiteList
     */
    public List<Prerequisite> getPrerequisiteList( )
    {
        return new ArrayList<>( _prerequisiteList );
    }

    /**
     * @param prerequisiteList the prerequisiteList to set
     */
    public void setPrerequisiteList( List<Prerequisite> prerequisiteList )
    {
        _prerequisiteList = new ArrayList<>( prerequisiteList );
    }
}
