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
package fr.paris.lutece.plugins.workflow.business.prerequisite;

import fr.paris.lutece.plugins.workflowcore.business.prerequisite.Prerequisite;

/**
 * Business class of prerequisites
 */
public class PrerequisiteDTO extends Prerequisite
{
    private String _strTitle;
    private boolean _bHasConfiguration;

    /**
     * Creates a new prerequisite DAO with parameters initialized
     * 
     * @param prerequisite
     *            the prerequisite to copy
     * @param strTitle
     *            The title of the type of the underlying prerequisite
     * @param bHasConfiguration
     *            True if the prerequisite has configuration, false otherwise
     */
    public PrerequisiteDTO( Prerequisite prerequisite, String strTitle, boolean bHasConfiguration )
    {
        this.setIdPrerequisite( prerequisite.getIdPrerequisite( ) );
        this.setIdAction( prerequisite.getIdAction( ) );
        this.setPrerequisiteType( prerequisite.getPrerequisiteType( ) );
        this._strTitle = strTitle;
        this._bHasConfiguration = bHasConfiguration;
    }

    /**
     * Get the title of the type of the underlying prerequisite
     * 
     * @return The title of the type of the underlying prerequisite
     */
    public String getTitle( )
    {
        return _strTitle;
    }

    /**
     * Set the title of the type of the underlying prerequisite
     * 
     * @param strTitle
     *            The title of the type of the underlying prerequisite
     */
    public void setTitle( String strTitle )
    {
        this._strTitle = strTitle;
    }

    /**
     * Check if the prerequisite type associated with the underlying prerequisite has a configuration
     * 
     * @return True if it has a configuration, false otherwise
     */
    public boolean getHasConfiguration( )
    {
        return _bHasConfiguration;
    }

    /**
     * Set a boolean value that indicates that the type associated with the underlying prerequisite has a configuration or not
     * 
     * @param bHasConfiguration
     *            True if it has a configuration, false otherwise
     */
    public void setHasConfiguration( boolean bHasConfiguration )
    {
        this._bHasConfiguration = bHasConfiguration;
    }
}
