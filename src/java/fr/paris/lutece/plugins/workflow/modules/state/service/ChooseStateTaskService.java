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
package fr.paris.lutece.plugins.workflow.modules.state.service;

import javax.inject.Inject;
import javax.inject.Named;

import fr.paris.lutece.plugins.workflow.modules.state.business.ChooseStateTaskConfig;
import fr.paris.lutece.plugins.workflow.modules.state.business.ChooseStateTaskInformation;
import fr.paris.lutece.plugins.workflow.modules.state.business.ChooseStateTaskInformationHome;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;

/**
 * Implements IChooseStateTaskService
 */
public class ChooseStateTaskService extends AbstractStateTaskService implements IChooseStateTaskService
{

    @Inject
    @Named( "workflow.chooseStateTaskConfigService" )
    private ITaskConfigService _taskConfigService;

    @Override
    public ChooseStateTaskConfig loadConfig( ITask task )
    {
        ChooseStateTaskConfig config = _taskConfigService.findByPrimaryKey( task.getId( ) );
        if ( config == null )
        {
            config = new ChooseStateTaskConfig( );
            config.setIdTask( task.getId( ) );
            _taskConfigService.create( config );
        }
        return config;
    }

    private IChooseStateController getController( ChooseStateTaskConfig config )
    {
        for ( IChooseStateController controller : getControllerList( ) )
        {
            if ( controller.getName( ).equals( config.getControllerName( ) ) )
            {
                return controller;
            }
        }
        return null;
    }

    @Override
    public void chooseNewState( int nIdResource, String strResourceType, ITask task, ChooseStateTaskConfig config, int nIdWorkflow, int oldState )
    {
        IChooseStateController controller = getController( config );
        if ( controller == null )
        {
            return;
        }

        int newState = -1;
        if ( controller.control( nIdResource, strResourceType ) )
        {
            newState = config.getIdStateOK( );
        }
        else
        {
            newState = config.getIdStateKO( );
        }

        if ( newState != -1 && newState != oldState )
        {
            doChangeState( task, nIdResource, strResourceType, nIdWorkflow, newState );
        }
    }

    @Override
    protected void saveTaskInformation( int nIdResourceHistory, ITask task, State state )
    {
        ChooseStateTaskInformation taskInformation = new ChooseStateTaskInformation( );
        taskInformation.setIdHistory( nIdResourceHistory );
        taskInformation.setIdTask( task.getId( ) );
        taskInformation.setNewState( state.getName( ) );

        ChooseStateTaskInformationHome.create( taskInformation );
    }
}
