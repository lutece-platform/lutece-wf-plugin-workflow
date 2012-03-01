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

import fr.paris.lutece.portal.business.workflow.Action;
import fr.paris.lutece.portal.business.workflow.Workflow;

import java.sql.Timestamp;


/**
 *
 * ResourceHistory
 *
 */
public class ResourceHistory
{
    private int _nId;
    private int _nIdResource;
    private String _strResourceType;
    private Workflow _workflow;
    private Action _action;
    private Timestamp _tCreationDate;
    private String _strUserAccessCode;

    /**
     * return the id of the resource history
     * @return the id of the resource history
     */
    public int getId(  )
    {
        return _nId;
    }

    /**
     * set the id of the resource history
     * @param idHistory  the id of the resource history
     */
    public void setId( int idHistory )
    {
        _nId = idHistory;
    }

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
     * Returns the action performed on the resource
     * @return The action performed on the resource
     */
    public Action getAction(  )
    {
        return _action;
    }

    /**
      * Set the action performed on the resource
      * @param action The action
    */
    public void setAction( Action action )
    {
        _action = action;
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
    * @return the creation date
    */
    public Timestamp getCreationDate(  )
    {
        return _tCreationDate;
    }

    /**
     * set the creation date
     * @param dateCreation the creation date
     */
    public void setCreationDate( Timestamp dateCreation )
    {
        _tCreationDate = dateCreation;
    }

    /**
     * Returns the user access code
     *
     * @return The User acces code
     */
    public String getUserAccessCode(  )
    {
        return _strUserAccessCode;
    }

    /**
     * Sets the User acces code
     *
     * @param strEventUser The User acces code
     */
    public void setUserAccessCode( String strEventUser )
    {
        _strUserAccessCode = strEventUser;
    }
}
