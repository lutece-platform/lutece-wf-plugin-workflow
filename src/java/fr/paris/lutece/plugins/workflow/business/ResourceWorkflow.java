/*
 * Copyright (c) 2002-2009, Mairie de Paris
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

import fr.paris.lutece.portal.business.workflow.State;
import fr.paris.lutece.portal.business.workflow.Workflow;

import java.util.List;


/**
 *
 * ResourceWorkflow
 *
 */
public class ResourceWorkflow
{
    private int _nIdResource;
    private String _strResourceType;
    private Workflow _workflow;
    private State _state;
    private Integer _nIdExternalParent = null;
    private List<String> _workgroups;
    private boolean _isAssociatedWithWorkgroup;

    /**
    *
    * @return the workflow associated
    */
    public Workflow getWorkflow(  )
    {
        return _workflow;
    }

    /**
     * set the the workflow associated
     * @param workflow the workflow associated
     */
    public void setWorkFlow( Workflow workflow )
    {
        _workflow = workflow;
    }

    /**
     * Returns the State of the document
     * @return The State
     */
    public State getState(  )
    {
        return _state;
    }

    /**
      * Set the State of the document
      * @param state The State
    */
    public void setState( State state )
    {
        _state = state;
    }

    /**
     * return the id of the resource
     * @return the id of the resource
     */
    public int getIdResource(  )
    {
        return _nIdResource;
    }

    /**
     * set the id of the resource
     * @param idResource  the id of the resource
     */
    public void setIdResource( int idResource )
    {
        _nIdResource = idResource;
    }

    /**
     * return the resource type
     * @return resource type
     */
    public String getResourceType(  )
    {
        return _strResourceType;
    }

    /**
     * set the resource type
     * @param resourceType the resource type
     */
    public void setResourceType( String resourceType )
    {
        _strResourceType = resourceType;
    }

    /**
     *
     * @return the workgroups associated to the resource
     */
    public List<String> getWorkgroups(  )
    {
        return _workgroups;
    }

    /**
     * set the the workgroups associated to the resource
     * @param worgroups the list of workgroups
     */
    public void setWorkgroups( List<String> worgroups )
    {
        _workgroups = worgroups;
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

    /**
     * set to true if the workgroup is associate with workgroups 
     * @param _isAssociatedWithWorkgroup
     */
	public void setAssociatedWithWorkgroup(boolean _isAssociatedWithWorkgroup) {
		this._isAssociatedWithWorkgroup = _isAssociatedWithWorkgroup;
	}
	/**
	 * 
	 * @return true if the workgroup is associate with workgroups
	 */
	public boolean isAssociatedWithWorkgroup() {
		return _isAssociatedWithWorkgroup;
	}
}
