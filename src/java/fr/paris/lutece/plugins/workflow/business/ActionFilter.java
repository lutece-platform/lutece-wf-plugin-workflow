/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
 * class ActionFilter
 *
 */
public class ActionFilter
{
    public static final int ALL_INT = -1;
    private static final int TRUE = 1;
    private static final int FALSE = 0;
    private int _nIdWorkFlow = ALL_INT;
    private int _nIdStateBefore = ALL_INT;
    private int _nIdStateAfter = ALL_INT;
    private int _nIdIcon = ALL_INT;
    private int _nIsAutomaticState = ALL_INT;
    private int _nIsMassAction= ALL_INT;

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

    /**
     *
     * @return  the initial state insert in the filter
     */
    public int getIdStateBefore(  )
    {
        return _nIdStateBefore;
    }

    /**
     * set the id of the initial state in the filter
     * @param idStateBefore the id of state to insert in the filter
     */
    public void setIdStateBefore( int idStateBefore )
    {
        _nIdStateBefore = idStateBefore;
    }

    /**
     *
     * @return true if the filter contain an id of  the initial state
     *
     */
    public boolean containsIdStateBefore(  )
    {
        return ( _nIdStateBefore != ALL_INT );
    }

    /**
    *
    * @return the id of  the  state after doing the action insert in the filter
    */
    public int getIdStateAfter(  )
    {
        return _nIdStateAfter;
    }

    /**
     * set the id of the  state after doing the action insert in the filter
     * @param idStateAfter the id of the state to insert in the filter
     */
    public void setIdStateAfter( int idStateAfter )
    {
        _nIdStateAfter = idStateAfter;
    }

    /**
     *
     * @return true if the filter contain the id of the  state after doing the action
     *
     */
    public boolean containsIdStateAfter(  )
    {
        return ( _nIdStateAfter != ALL_INT );
    }

    /**
    *
    * @return the id of  the  icon insert in the filter
    */
    public int getIdIcon(  )
    {
        return _nIdIcon;
    }

    /**
     * set the id of the icon insert in the filter
     * @param idIcon the id of the icon insert in the filter
     */
    public void setIdIcon( int idIcon )
    {
        _nIdIcon = idIcon;
    }

    /**
     *
     * @return true if the filter contain the id of the icon
     *
     */
    public boolean containsIdIcon(  )
    {
        return ( _nIdIcon != ALL_INT );
    }

    /**
     *
     * @return 1 if the state return must be the automatic action
     *          0 if the state return must not be the automatic action
     */
    public int getIsAutomaticState(  )
    {
        return _nIsAutomaticState;
    }

    /**
     * Set 1 if the state return must be the automatic action
     *            0 if the state return must not be the automatic action
     * @param idState 1 if the state return must be the automatic action
     *                0 if the state return must not be the automatic action
     */
    public void setIsAutomaticState( int isAutomaticState )
    {
        _nIsAutomaticState = isAutomaticState;
    }

    /**
     *
     * @return true if the filter contain automatic action
     */
    public boolean containsIsAutomaticState(  )
    {
        return ( _nIsAutomaticState != ALL_INT );
    }
    
    /**
     *
     * @return 1 if the state return must be the mass action
     *          0 if the state return must not be the mass action
     */
    public int getIsMassAction(  )
    {
        return _nIsMassAction;
    }

    /**
     * Set 1 if the state return must be the mass action
     *            0 if the state return must not be the mass action
     * @param idState 1 if the state return must be the mass action
     *                0 if the state return must not be the mass action
     */
    public void setIsMassAction( boolean bIsMassAction )
    {
        _nIsMassAction = bIsMassAction ? TRUE : FALSE;
    }

    /**
     *
     * @return true if the filter contain mass action
     */
    public boolean containsIsMassAction(  )
    {
        return ( _nIsMassAction != ALL_INT );
    }
}
