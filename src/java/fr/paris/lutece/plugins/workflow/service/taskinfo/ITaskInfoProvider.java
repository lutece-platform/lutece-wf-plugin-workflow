/*
 * Copyright (c) 2002-2025, City of Paris
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
package fr.paris.lutece.plugins.workflow.service.taskinfo;

import fr.paris.lutece.plugins.workflowcore.business.task.ITaskType;

import org.springframework.beans.factory.InitializingBean;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * ITaskInfoProvider
 *
 */
public interface ITaskInfoProvider extends InitializingBean
{
    /**
     * Set the task type
     * 
     * @param taskType
     *            the task type
     */
    void setTaskType( ITaskType taskType );

    /**
     * Get the task type
     * 
     * @return the task type
     */
    ITaskType getTaskType( );

    /**
     * Get the task resource info
     * 
     * @param nIdHistory
     *            the id history
     * @param nIdTask
     *            the id task
     * @param request
     *            the HTTP request
     * @return the task resource info
     */
    String getTaskResourceInfo( int nIdHistory, int nIdTask, HttpServletRequest request );

    /**
     * Get the plugin name
     * 
     * @return the plugin name
     */
    String getPluginName( );
}
