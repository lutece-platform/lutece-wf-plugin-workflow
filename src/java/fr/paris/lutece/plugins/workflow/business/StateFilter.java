/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.business;


/**
 *
 * class WorkflowFilter
 *
 */
public class StateFilter
{
    public static final int ALL_INT = -1;
    public static final int FILTER_FALSE = 0;
    public static final int FILTER_TRUE = 1;
    private int _nIsInitialState = ALL_INT;
    private int _nIdWorkFlow = ALL_INT;

    /**
     *
     * @return 1 if the state return must be the initial state
     *          0 if the state return must not be the initial state
     */
    public int getIsInitialState(  )
    {
        return _nIsInitialState;
    }

    /**
     * Set 1 if the state return must be the initial state
     *            0 if the state return must not be the initial state
     * @param idState 1 if the state return must be the initial state
     *                0 if the state return must not be the initial state
     */
    public void setIsInitialState( int idState )
    {
        _nIsInitialState = idState;
    }

    /**
     *
     * @return true if the filter contain workflow state
     */
    public boolean containsIsInitialState(  )
    {
        return ( _nIsInitialState != ALL_INT );
    }

    /**
    *
    * @return  the id of workflow insert in the filter
    */
    public int getIdWorkflow(  )
    {
        return _nIdWorkFlow;
    }

    /**
     * set the id of workflow in the filter
     * @param idWorkflow the id of workflow to insert in the filter
     */
    public void setIdWorkflow( int idWorkflow )
    {
        _nIdWorkFlow = idWorkflow;
    }

    /**
     *
     * @return true if the filter contain an id of workflow
     *
     */
    public boolean containsIdWorkflow(  )
    {
        return ( _nIdWorkFlow != ALL_INT );
    }
}
