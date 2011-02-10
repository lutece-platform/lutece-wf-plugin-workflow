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
package fr.paris.lutece.plugins.workflow.modules.tasknotification.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;


/**
 *
 *class   TaskNotificationConfigDAO
 *
 */
public class TaskNotificationConfigDAO implements ITaskNotificationConfigDAO
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_task,id_mailing_list,sender_name,subject,message " +
        " FROM workflow_task_notification_cf WHERE id_task=?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO  workflow_task_notification_cf  " +
        "(id_task,id_mailing_list,sender_name,subject,message)VALUES(?,?,?,?,?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_task_notification_cf " +
        "SET id_task=?,id_mailing_list=?,sender_name=?,subject=?,message=?" + " WHERE id_task=?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_task_notification_cf WHERE id_task=? ";

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.tasknotification.business.ITaskNotificationConfigDAO#insert(fr.paris.lutece.plugins.workflow.modules.tasknotification.business.TaskNotificationtConfig, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public synchronized void insert( TaskNotificationtConfig config, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        int nPos = 0;

        daoUtil.setInt( ++nPos, config.getIdTask(  ) );
        daoUtil.setInt( ++nPos, config.getIdMailingList(  ) );
        daoUtil.setString( ++nPos, config.getSenderName(  ) );
        daoUtil.setString( ++nPos, config.getSubject(  ) );
        daoUtil.setString( ++nPos, config.getMessage(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.tasknotification.business.ITaskNotificationConfigDAO#store(fr.paris.lutece.plugins.workflow.modules.tasknotification.business.TaskNotificationtConfig, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void store( TaskNotificationtConfig config, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        int nPos = 0;

        daoUtil.setInt( ++nPos, config.getIdTask(  ) );
        daoUtil.setInt( ++nPos, config.getIdMailingList(  ) );
        daoUtil.setString( ++nPos, config.getSenderName(  ) );
        daoUtil.setString( ++nPos, config.getSubject(  ) );
        daoUtil.setString( ++nPos, config.getMessage(  ) );

        daoUtil.setInt( ++nPos, config.getIdTask(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.tasknotification.business.ITaskNotificationConfigDAO#load(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public TaskNotificationtConfig load( int nIdState, Plugin plugin )
    {
        TaskNotificationtConfig config = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );

        daoUtil.setInt( 1, nIdState );

        daoUtil.executeQuery(  );

        int nPos = 0;

        if ( daoUtil.next(  ) )
        {
            config = new TaskNotificationtConfig(  );
            config.setIdTask( daoUtil.getInt( ++nPos ) );
            config.setIdMailingList( daoUtil.getInt( ++nPos ) );
            config.setSenderName( daoUtil.getString( ++nPos ) );
            config.setSubject( daoUtil.getString( ++nPos ) );
            config.setMessage( daoUtil.getString( ++nPos ) );
        }

        daoUtil.free(  );

        return config;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.tasknotification.business.ITaskNotificationConfigDAO#delete(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void delete( int nIdState, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );

        daoUtil.setInt( 1, nIdState );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
}
