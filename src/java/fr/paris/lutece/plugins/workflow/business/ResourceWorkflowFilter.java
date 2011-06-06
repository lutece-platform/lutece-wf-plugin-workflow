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

import fr.paris.lutece.util.ReferenceList;


/**
 *
 * class WorkflowFilter
 *
 */
public class ResourceWorkflowFilter
{
    public static final int ALL_INT = -1;
    public static final String EMPTY_STRING = "";
    private String _strResourceType = EMPTY_STRING;
    private int _nIdWorkFlow = ALL_INT;
    private int _nIdState = ALL_INT;
    private Integer _nIdExternalParent = null;
    private ReferenceList _workgroupKeyList;

    /**
     *
     * @return the resource type
     */
    public String getResourceType(  )
    {
        return _strResourceType;
    }

    /**
     * Set the resource type
     * @param strResourceType the resource type
     */
    public void setResourceType( String strResourceType )
    {
        _strResourceType = strResourceType;
    }

    /**
     *
     * @return true if the filter contain resource type
     */
    public boolean containsResourceType(  )
    {
        return ( _strResourceType != EMPTY_STRING );
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

    /**
    *
    * @return  the id of State insert in the filter
    */
    public int getIdState(  )
    {
        return _nIdState;
    }

    /**
     * set the id of State in the filter
     * @param idState the state id to insert in the filter
     */
    public void setIdState( int idState )
    {
        _nIdState = idState;
    }

    /**
     *
     * @return true if the filter contain an id of State
     *
     */
    public boolean containsIdState(  )
    {
        return ( _nIdState != ALL_INT );
    }

    /**
     * The workgroup key list to set
     * @param workgroupKeyList The workgroup key list
     */
    public void setWorkgroupKeyList( ReferenceList workgroupKeyList )
    {
        this._workgroupKeyList = workgroupKeyList;
    }

    /**
     * Get workgroup key list
     * @return The workgroup key list
     */
    public ReferenceList getWorkgroupKeyList(  )
    {
        return _workgroupKeyList;
    }

    /**
    *
    * @return true if the filter contain workgroup key list
    */
    public boolean containsWorkgroupKeyList(  )
    {
        return ( _workgroupKeyList != null );
    }

    /**
     * Test if filter contains an external parent id
     * @return true if contains an external parent id
     */
    public boolean containsExternalParentId(  )
    {
        return ( _nIdExternalParent != null );
    }

    /**
     * Get the external parent id key
     * @return the external parent id key
     */
    public Integer getExternalParentId(  )
    {
        return _nIdExternalParent;
    }

    /**
     * Set external parent id key
     * @param nIdExternalParent the external parent id to set
     */
    public void setExternalParentId( Integer nIdExternalParent )
    {
        _nIdExternalParent = nIdExternalParent;
    }
}
