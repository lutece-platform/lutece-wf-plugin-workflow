/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * TaskAssignmentConfigDAO
 *
 */
public class WorkgroupConfigDAO implements IWorkgroupConfigDAO
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT workgroup_key,id_mailing_list FROM workflow_workgroup_cf   WHERE id_task=? and workgroup_key=? ";
    private static final String SQL_QUERY_DELETE_BY_CONFIG = "DELETE FROM workflow_workgroup_cf   WHERE id_task=? ";
    private static final String SQL_QUERY_SELECT_BY_CONFIG = "SELECT workgroup_key,id_mailing_list FROM workflow_workgroup_cf   WHERE id_task=? ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO workflow_workgroup_cf(id_task,workgroup_key,id_mailing_list)  VALUES(?,?,?) ";

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkgroupConfig load( int nIdTask, String strWorkgroupKey, Plugin plugin )
    {
        WorkgroupConfig workgroupConfig = null;
        int nPos;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );

        daoUtil.setInt( 1, nIdTask );
        daoUtil.setString( 2, strWorkgroupKey );

        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            nPos = 0;
            workgroupConfig = new WorkgroupConfig(  );
            workgroupConfig.setIdTask( nIdTask );
            workgroupConfig.setWorkgroupKey( daoUtil.getString( ++nPos ) );
            workgroupConfig.setIdMailingList( daoUtil.getInt( ++nPos ) );
        }

        daoUtil.free(  );

        return workgroupConfig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WorkgroupConfig> selectByConfig( int nIdTask, Plugin plugin )
    {
        WorkgroupConfig workgroupConfig = null;
        List<WorkgroupConfig> listWorkgroupConfig = new ArrayList<WorkgroupConfig>(  );
        int nPos;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_CONFIG, plugin );

        daoUtil.setInt( 1, nIdTask );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            nPos = 0;
            workgroupConfig = new WorkgroupConfig(  );
            workgroupConfig.setIdTask( nIdTask );
            workgroupConfig.setWorkgroupKey( daoUtil.getString( ++nPos ) );
            workgroupConfig.setIdMailingList( daoUtil.getInt( ++nPos ) );

            listWorkgroupConfig.add( workgroupConfig );
        }

        daoUtil.free(  );

        return listWorkgroupConfig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByTask( int nIdTask, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_CONFIG, plugin );
        daoUtil.setInt( 1, nIdTask );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert( WorkgroupConfig workgroupConf, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        int nPos = 0;
        daoUtil.setInt( ++nPos, workgroupConf.getIdTask(  ) );
        daoUtil.setString( ++nPos, workgroupConf.getWorkgroupKey(  ) );

        if ( workgroupConf.getIdMailingList(  ) != WorkflowUtils.CONSTANT_ID_NULL )
        {
            daoUtil.setInt( ++nPos, workgroupConf.getIdMailingList(  ) );
        }
        else
        {
            daoUtil.setIntNull( ++nPos );
        }

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
}
