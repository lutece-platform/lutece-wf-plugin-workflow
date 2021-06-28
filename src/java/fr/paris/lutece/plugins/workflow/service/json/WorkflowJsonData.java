/*
 * Copyright (c) 2002-2021, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
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
     * @param workflow
     *            the workflow to set
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
     * @param stateList
     *            the stateList to set
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
     * @param actionList
     *            the actionList to set
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
     * @param taskList
     *            the taskList to set
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
     * @param prerequisiteList
     *            the prerequisiteList to set
     */
    public void setPrerequisiteList( List<Prerequisite> prerequisiteList )
    {
        _prerequisiteList = new ArrayList<>( prerequisiteList );
    }
}
