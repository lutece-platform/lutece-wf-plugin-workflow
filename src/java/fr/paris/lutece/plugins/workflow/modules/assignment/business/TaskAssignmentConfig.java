/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.assignment.business;

import fr.paris.lutece.plugins.workflowcore.business.config.TaskConfig;

import java.util.List;


/**
 *
 *TaskCommentConfig
 *
 */
public class TaskAssignmentConfig extends TaskConfig
{
    public static final String ASSIGMENT_TYPE_ADMIN_USER = "TYPE_ADMIN_USER";
    public static final String ASSIGMENT_TYPE_WORKGROUP = "TYPE_WORKGROUP";
    private boolean _bMultipleOwner;
    private String _strTitle;
    private List<WorkgroupConfig> _workgroups;
    private String _strMessage;
    private boolean _bNotify;
    private String _strSubject;
    private boolean _bUseUserName;

    /**
     * @return the _bUseUserName
     */
    public boolean isUseUserName(  )
    {
        return _bUseUserName;
    }

    /**
     * @param useUserName the _bUseUserName to set
     */
    public void setUseUserName( boolean useUserName )
    {
        _bUseUserName = useUserName;
    }

    /**
     * @return the _strSubject
     */
    public String getSubject(  )
    {
        return _strSubject;
    }

    /**
     * @param subject the _strSubject to set
     */
    public void setSubject( String subject )
    {
        _strSubject = subject;
    }

    /**
     * @return the _bNotification
     */
    public boolean isNotify(  )
    {
        return _bNotify;
    }

    /**
     * @param bNotify the _bNotify to set
     */
    public void setNotify( boolean bNotify )
    {
        _bNotify = bNotify;
    }

    /**
     * @return the _strMessage
     */
    public String getMessage(  )
    {
        return _strMessage;
    }

    /**
     * @param message the _strMessage to set
     */
    public void setMessage( String message )
    {
        _strMessage = message;
    }

    /**
     *
     * @return the title of the field insert in tasks form
     */
    public String getTitle(  )
    {
        return _strTitle;
    }

    /**
     * set  the title of the field insert in tasks form
     * @param title the title of the field insert in tasks form
     */
    public void setTitle( String title )
    {
        _strTitle = title;
    }

    /**
     * @return a list wich contains the differents workgroups to displayed in task form
     */
    public List<WorkgroupConfig> getWorkgroups(  )
    {
        return _workgroups;
    }

    /**
     * Set a list wich contains the differents workgroups to displayed in task form
     * @param worgroups the list of workgroups
     */
    public void setWorkgroups( List<WorkgroupConfig> worgroups )
    {
        _workgroups = worgroups;
    }

    /**
     * @return true if the user can choose multiple entity
     */
    public boolean isMultipleOwner(  )
    {
        return _bMultipleOwner;
    }

    /**
     * set true if the user can choose multiple entity
     * @param multipleOwner true if the user can choose multiple entity
     */
    public void setMultipleOwner( boolean multipleOwner )
    {
        _bMultipleOwner = multipleOwner;
    }
}
