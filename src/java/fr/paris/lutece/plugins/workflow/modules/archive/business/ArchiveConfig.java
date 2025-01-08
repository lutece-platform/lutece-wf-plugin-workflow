/*
 * Copyright (c) 2002-2022, City of Paris
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
package fr.paris.lutece.plugins.workflow.modules.archive.business;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import fr.paris.lutece.plugins.workflow.modules.archive.ArchivalType;
import fr.paris.lutece.plugins.workflowcore.business.config.TaskConfig;

/**
 * Config for Archival Task.
 *
 */
@Dependent
@Named( "workflow.taskArchiveConfig" )
public class ArchiveConfig extends TaskConfig
{
    @NotNull
    @Min( 1 )
    private int _nNextState;

    private ArchivalType _typeArchival;
    // Number of days before archival
    private int _delayArchival;

    public ArchivalType getTypeArchival( )
    {
        return _typeArchival;
    }

    public void setTypeArchival( ArchivalType typeArchival )
    {
        _typeArchival = typeArchival;
    }

    public int getDelayArchival( )
    {
        return _delayArchival;
    }

    public void setDelayArchival( int delayArchival )
    {
        _delayArchival = delayArchival;
    }

    /**
     * @return the nextState
     */
    public int getNextState( )
    {
        return _nNextState;
    }

    /**
     * @param nNextState
     *            the nextState to set
     */
    public void setNextState( int nNextState )
    {
        _nNextState = nNextState;
    }

    /**
     * @return nNextState as a string
     */
    public String getStrNextState( )
    {
        return String.valueOf( _nNextState );
    }
}
